package com.arm.legv8simulator.client.cpu;

import java.math.BigInteger;
import java.util.ArrayList;

import com.arm.legv8simulator.client.Error;
import com.arm.legv8simulator.client.instruction.Instruction;
import com.arm.legv8simulator.client.memory.Memory;
import com.arm.legv8simulator.client.memory.SegmentFaultException;

/**
 * The <code>CPU</code> class is a single cycle emulator of the LEGv8 instruction set.
 * <p> 
 * It comprises a register file, N, Z, C, V flags and a PC. To execute an instruction, 
 * references to a list of instructions and a data memory must be supplied.  
 * 
 * @author Jonathan Wright, 2016
 */

/*
 * Ugly things...
 * 1) Hard coded strings for log message saying ignored attempt to assign to XZR 
 * 2) The strings in the cpuLog.append(...) code for each instruction are hideous. 
 * 	  This is because GWT does not support the java.util.Formatter class which provides C-style printf() string formatting
 */ 

public class CPU {
	
	public static final int INSTRUCTION_SIZE = 4;
	public static final int NUM_REGISTERS = 32;
	
	public static final BigInteger UNSIGNED_LONG_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);
	
	public static final int XZR = 31, D31 = 31, S31 = 31;
	public static final int LR = 30, D30 = 30, S30 = 30;
	public static final int FP = 29, D29 = 29, S29 = 29;
	public static final int SP = 28, D28 = 28, S28 = 28;
	public static final int X27 = 27, D27 = 27, S27 = 27;
	public static final int X26 = 26, D26 = 26, S26 = 26;
	public static final int X25 = 25, D25 = 25, S25 = 25;
	public static final int X24 = 24, D24 = 24, S24 = 24;
	public static final int X23 = 23, D23 = 23, S23 = 23;
	public static final int X22 = 22, D22 = 22, S22 = 22;
	public static final int X21 = 21, D21 = 21, S21 = 21;
	public static final int X20 = 20, D20 = 20, S20 = 20;
	public static final int X19 = 19, D19 = 19, S19 = 19;
	public static final int X18 = 18, D18 = 18, S18 = 18;
	public static final int IP1 = 17, D17 = 17, S17 = 17;
	public static final int IP0 = 16, D16 = 16, S16 = 16;
	public static final int X15 = 15, D15 = 15, S15 = 15;
	public static final int X14 = 14, D14 = 14, S14 = 14;
	public static final int X13 = 13, D13 = 13, S13 = 13;
	public static final int X12 = 12, D12 = 12, S12 = 12;
	public static final int X11 = 11, D11 = 11, S11= 11;
	public static final int X10 = 10, D10 = 10, S10 = 10;
	public static final int X9 = 9, D9 = 9, S9 = 9;
	public static final int X8 = 8, D8 = 9, S8 = 9;
	public static final int X7 = 7, D7 = 9, S7 = 9;
	public static final int X6 = 6, D6 = 9, S6 = 9;
	public static final int X5 = 5, D5 = 9, S5 = 9;
	public static final int X4 = 4, D4 = 9, S4 = 9;
	public static final int X3 = 3, D3 = 9, S3 = 9;
	public static final int X2 = 2, D2 = 9, S2 = 9;
	public static final int X1 = 1, D1 = 9, S1 = 9;
	public static final int X0 = 0, D0 = 9, S0 = 9;
	
	private boolean branchTaken = false;
	private boolean STXRSucceed = false;
	private StringBuilder cpuLog = new StringBuilder("");
	private Register[]	XRegisterFile;
	private Register[]	DRegisterFile;
	private long taggedAddress;
	private int instructionIndex;
	private boolean Nflag;
	private boolean Zflag;
	private boolean Cflag;
	private boolean Vflag;

	
	/**
	 * Constructs a new <code>CPU</code> object, initialising registers and flags to 0 and false respectively.
	 * The SP register is then set according the definition of the LEGv8 virtual address space in Patterson and Hennessy ARM Edition.
	 * 
	 * @see Memory
	 */
	public CPU() {
		Nflag = false;
		Zflag = false;
		Cflag = false;
		Vflag = false;
		
		XRegisterFile = new Register[NUM_REGISTERS];
		for (int i = 0; i < NUM_REGISTERS; i++)
			XRegisterFile[i] = new Register(RegisterType.X);
		XRegisterFile[SP].writeDoubleWord(Memory.STACK_BASE);
		
		DRegisterFile = new Register[NUM_REGISTERS];
		for (int i = 0; i < NUM_REGISTERS; i++)
			DRegisterFile[i] = new Register(RegisterType.D);
		
	}
	
	/**
	 * The <code>Instruction</code> executed is that pointed to by the PC.
	 * 
	 * @param cpuInstructions	the list of <code>Instruction</code>s in the LEGv8 assembly program
	 * @param memory			a reference to the data memory used in data transfer instructions
	 * @return					an <code>Error</code> object, <code>null</code> if no error occurs during execution
	 * 
	 * @see Instruction
	 * @see Memory
	 * @see Error
	 */
	/*
	 * The PC value is derived from the internal variable 'instructionIndex', which denotes the next instruction to execute
	 * in the cpuInstructions ArrayList
	 */
	public Error executeInstruction(ArrayList<Instruction> cpuInstructions, Memory memory) {
		try {
			execute(cpuInstructions.get(instructionIndex++), memory);
		} catch (SegmentFaultException sfe) {
			return new Error(sfe.getMessage(), cpuInstructions.get(instructionIndex-1).getLineNumber());
		} catch (PCAlignmentException pcae) {
			return new Error(pcae.getMessage(), cpuInstructions.get(instructionIndex-1).getLineNumber());
		} catch (SPAlignmentException spae) {
			return new Error(spae.getMessage(), cpuInstructions.get(instructionIndex-1).getLineNumber());
		}
		return null;
	}
	
	/**
	 * This method will execute the supplied LEGv8 assembly program in its entirety.
	 * 
	 * @param cpuInstructions	the list of <code>Instruction</code>s in the LEGv8 assembly program
	 * @param memory			a reference to the data memory used in data transfer instructions
	 * @return					an <code>Error</code> object, <code>null</code> if no error occurs during execution
	 */
	public Error run(ArrayList<Instruction> cpuInstructions, Memory memory) {
		try {
			while (instructionIndex < cpuInstructions.size()) {
				execute(cpuInstructions.get(instructionIndex++), memory);
			}
		} catch (SegmentFaultException sfe) {
			return new Error(sfe.getMessage(), cpuInstructions.get(instructionIndex-1).getLineNumber());
		} catch (PCAlignmentException pcae) {
			return new Error(pcae.getMessage(), cpuInstructions.get(instructionIndex-1).getLineNumber());
		} catch (SPAlignmentException spae) {
			return new Error(spae.getMessage(), cpuInstructions.get(instructionIndex-1).getLineNumber());
		}
		return null;
	}
	
	/**
	 * @param index	the register whose value to return, an integer in the range 0-31
	 * @return		the value stored in the register <code>index</code>
	 */
	public long getRegister(RegisterType type, int index) {
		switch(type) {
		case X: return XRegisterFile[index].readDoubleWord();
		case D: return DRegisterFile[index].readDoubleWord();
		case S: return DRegisterFile[index].readDoubleWord() & 0x00000000ffffffffL;
		default: return 0L;
		}
	}
	
	/**
	 * @return a string showing full CPU execution history
	 */
	public String getCpuLog() {
		return cpuLog.toString();
	}
	
	/**
	 * @return	<code>true</code> if the last instruction executed was a branch instruction and 
	 * the branch was taken. <code>false</code> otherwise.
	 */
	public boolean getBranchTaken() {
		return branchTaken;
	}
	
	/**
	 * @return	<code>true</code> if the last instruction executed was STXR and the store to 
	 * memory succeeded. <code>false</code> otherwise.
	 */
	public boolean getSTXRSucceed() {
		return STXRSucceed;
	}
	
	/**
	 * @return	the current value of PC.
	 */
	public long getPC() {
		return (long) instructionIndex * INSTRUCTION_SIZE + Memory.TEXT_SEGMENT_OFFSET;
	}
	
	/**
	 * @return	the value of <code>instructionIndex</code>, the index of the next instruction to execute.
	 */
	public int getInstructionIndex() {
		return instructionIndex;
	}
	
	/**
	 * @return	the value of the N flag.
	 */
	public boolean getNflag() {
		return Nflag;
	}
	
	/**
	 * @return	 the value of the Z flag.
	 */
	public boolean getZflag() {
		return Zflag;
	}
	
	/**
	 * @return	the value of the C flag.
	 */
	public boolean getCflag() {
		return Cflag;
	}
	
	/**
	 * @return	the value of the V flag.
	 */
	public boolean getVflag() {
		return Vflag;
	}
	
	private void setNflag(boolean set) {
		Nflag = set;
	}
	
	private void setZflag(boolean set) {
		Zflag = set;
	}
	
	private void setCflag(boolean set) {
		Cflag = set;
	}
	
	private void setCflag(long result, long op1, long op2) {
		Cflag = ((MSB(~result) + MSB(op1) + MSB(op2)) & 2L) != 0;
	}
	
	private void setVflag(boolean set) {
		Vflag = set;
	}
	
	private void setVflag(long result, long op1, long op2) {
		Vflag = (((op1^~op2) & (op1^result)) & (1<<63)) != 0;
	}
	
	// returns most significant bit of value passed in
	private long MSB(long value) {
		return value >>> 63;
	}
	
	private void ADDSetFlags(long result, long op1, long op2) {
		setNflag(result < 0);
		setZflag(result == 0);
		setCflag(result, op1, op2);
		setVflag(result, op1, op2);
	}
	
	private void SUBSetFlags(long result, long op1, long op2) {
		ADDSetFlags(result, op1, (~op2)+1);											// fixed behaviour of the function, SIMONE.DEIANA@studenti.units.it
	}
	
	private void ANDSetFlags(long result) {
		setNflag(result < 0);
		setZflag(result == 0);
		setCflag(false);
		setVflag(false);
	}
	
	private void FCMPSetFlags(int comparisonResult, boolean isNaN) {
		setNflag(comparisonResult < 0 && !isNaN);
		setZflag(comparisonResult == 0 && !isNaN);
		setCflag(comparisonResult >= 0 || isNaN);
		setVflag(isNaN);
	}
	
	private void clearExclusiveAccessTag(long address, int figureSize) {
		if (taggedAddress == 0) return;
		if ((address >= taggedAddress 
				&& address < taggedAddress+Memory.DOUBLEWORD_SIZE) 
				|| (address+figureSize-1 >= taggedAddress 
				&& address+figureSize-1 < taggedAddress+Memory.DOUBLEWORD_SIZE)) {
			taggedAddress = 0;
			cpuLog.append("Exclusive access address tag cleared \n");
		}
	}
	
	private void checkSPAlignment() throws SPAlignmentException {
		if (XRegisterFile[SP].readDoubleWord()%16 != 0) {
			cpuLog.append("SP misaligned\n");
			throw new SPAlignmentException(XRegisterFile[SP].readDoubleWord());
		}
		cpuLog.append("SP aligned correctly\n");
	}
	
	private void execute(Instruction ins, Memory memory) 
			throws SegmentFaultException, PCAlignmentException, SPAlignmentException {
		int[] args = ins.getArgs();
		branchTaken = false; // rather ugly but... set to false by default as most instructions are not branches. 
		//if a branch instruction is executed and the branch is taken, will be set to true in that instruction method 
		switch (ins.getMnemonic()) {
		case ADD :
			ADD(args[0], args[1], args[2]);
			break;
		case ADDS :
			ADDS(args[0], args[1], args[2]);
			break;
		case ADDI :
			ADDI(args[0], args[1], args[2]);
			break;
		case ADDIS :
			ADDIS(args[0], args[1], args[2]);
			break;
		case MUL :																		// added MUL execution, SIMONE.DEIANA@studenti.units.it
			MUL(args[0], args[1], args[2]);
			break;
		case UMULH :																	// added UMULH execution, SIMONE.DEIANA@studenti.units.it
			UMULH(args[0], args[1], args[2]);
			break;
		case SMULH :																	// added SMULH execution, SIMONE.DEIANA@studenti.units.it
			SMULH(args[0], args[1], args[2]);
			break;
		case SDIV :
			SDIV(args[0], args[1], args[2]);
			break;
		case UDIV :
			UDIV(args[0], args[1], args[2]);
			break;
		case SUB :
			SUB(args[0], args[1], args[2]);
			break;
		case SUBS :
			SUBS(args[0], args[1], args[2]);
			break;
		case SUBI :
			SUBI(args[0], args[1], args[2]);
			break;
		case SUBIS :
			SUBIS(args[0], args[1], args[2]);
			break;
		case AND :
			AND(args[0], args[1], args[2]);
			break;
		case ANDS :
			ANDS(args[0], args[1], args[2]);
			break;
		case ANDI :
			ANDI(args[0], args[1], args[2]);
			break;
		case ANDIS :
			ANDIS(args[0], args[1], args[2]);
			break;
		case ORR :
			ORR(args[0], args[1], args[2]);
			break;
		case ORRI :
			ORRI(args[0], args[1], args[2]);
			break;
		case EOR :
			EOR(args[0], args[1], args[2]);
			break;
		case EORI :
			EORI(args[0], args[1], args[2]);
			break;
		case LSL :
			LSL(args[0], args[1], args[2]);
			break;
		case LSR :
			LSR(args[0], args[1], args[2]);
			break;
		case LDUR :
			LDUR(args[0], args[1], args[2], memory);
			break;
		case STUR :
			STUR(args[0], args[1], args[2], memory);
			break;
		case LDURSW :
			LDURSW(args[0], args[1], args[2], memory);
			break;
		case STURW :
			STURW(args[0], args[1], args[2], memory);
			break;
		case LDURH :
			LDURH(args[0], args[1], args[2], memory);
			break;
		case STURH :
			STURH(args[0], args[1], args[2], memory);
			break;
		case LDURB :
			LDURB(args[0], args[1], args[2], memory);
			break;
		case STURB :
			STURB(args[0], args[1], args[2], memory);
			break;
		case LDXR :
			LDXR(args[0], args[1], args[2], memory);
			break;
		case STXR :
			STXR(args[0], args[1], args[2], args[3], memory);
			break;
		case MOVZ :
			MOVZ(args[0], args[1], args[2]);
			break;
		case MOVK :
			MOVK(args[0], args[1], args[2]);
			break;
		case CBZ :
			CBZ(args[0], args[1]);
			break;
		case CBNZ :
			CBNZ(args[0], args[1]);
			break;
		case BEQ :
			BEQ(args[0]);
			break;
		case BNE :
			BNE(args[0]);
			break;
		case BHS :
			BHS(args[0]);
			break;
		case BLO :
			BLO(args[0]);
			break;
		case BHI :
			BHI(args[0]);
			break;
		case BLS :
			BLS(args[0]);
			break;
		case BGE :
			BGE(args[0]);
			break;
		case BLT :
			BLT(args[0]);
			break;
		case BGT :
			BGT(args[0]);
			break;
		case BLE :
			BLE(args[0]);
			break;
		case BMI :
			BMI(args[0]);
			break;
		case BPL :
			BPL(args[0]);
			break;
		case BVS :
			BVS(args[0]);
			break;
		case BVC :
			BVC(args[0]);
			break;
		case B :
			B(args[0]);
			break;
		case BR :
			BR(args[0], memory);
			break;
		case BL :
			BL(args[0]);
			break;
		case FADDS :
			FADDS(args[0], args[1], args[2]);
			break;
		case FADDD :
			FADDD(args[0], args[1], args[2]);
			break;
		case FSUBS :
			FSUBS(args[0], args[1], args[2]);
			break;
		case FSUBD :
			FSUBD(args[0], args[1], args[2]);
			break;
		case FMULS :
			FMULS(args[0], args[1], args[2]);
			break;
		case FMULD :
			FMULD(args[0], args[1], args[2]);
			break;
		case FDIVS :
			FDIVS(args[0], args[1], args[2]);
			break;
		case FDIVD :
			FDIVD(args[0], args[1], args[2]);
			break;
		case FCMPS :
			FCMPS(args[0], args[1]);
			break;
		case FCMPD :
			FCMPD(args[0], args[1]);
			break;
		case STURD :
			STURD(args[0], args[1], args[2], memory);
			break;
		case LDURD :
			LDURD(args[0], args[1], args[2], memory);
			break;
		case STURS :
			STURS(args[0], args[1], args[2], memory);
			break;
		case LDURS :
			LDURS(args[0], args[1], args[2], memory);
			break;
		default : {}
		}
	}

	private void ADD(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() + XRegisterFile[op2Reg].readDoubleWord());;
			cpuLog.append("ADD \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}

	private void ADDS(int destReg, int op1Reg, int op2Reg) {
		long result = XRegisterFile[op1Reg].readDoubleWord() + XRegisterFile[op2Reg].readDoubleWord();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(result);;
			cpuLog.append("ADDS \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
		ADDSetFlags(result, XRegisterFile[op1Reg].readDoubleWord(), XRegisterFile[op2Reg].readDoubleWord());
		cpuLog.append("Set flags + \n");
	}

	private void ADDI(int destReg, int op1Reg, int op2Imm) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() + op2Imm);
			cpuLog.append("ADDI \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
	}

	private void ADDIS(int destReg, int op1Reg, int op2Imm) {
		long result = XRegisterFile[op1Reg].readDoubleWord() + op2Imm;
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(result);
			cpuLog.append("ADDIS \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
		ADDSetFlags(result, XRegisterFile[op1Reg].readDoubleWord(), op2Imm);
		cpuLog.append("Set flags + \n");
	}
	
	private void MUL(int destReg, int op1Reg, int op2Reg) {											// added MUL execution, SIMONE.DEIANA@studenti.units.it
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() * XRegisterFile[op2Reg].readDoubleWord());
			cpuLog.append("MUL \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}

	private void SUB(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() - XRegisterFile[op2Reg].readDoubleWord());
			cpuLog.append("SUB \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}

	private void SUBS(int destReg, int op1Reg, int op2Reg) {
		long result = XRegisterFile[op1Reg].readDoubleWord() - XRegisterFile[op2Reg].readDoubleWord();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(result);
			cpuLog.append("SUBS \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
		SUBSetFlags(result, XRegisterFile[op1Reg].readDoubleWord(), XRegisterFile[op2Reg].readDoubleWord());
		cpuLog.append("Set flags + \n");
	}

	private void SUBI(int destReg, int op1Reg, int op2Imm) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() - op2Imm);
			cpuLog.append("SUBI \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
	}

	private void SUBIS(int destReg, int op1Reg, int op2Imm) {
		long result = XRegisterFile[op1Reg].readDoubleWord() - op2Imm;
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(result);
			cpuLog.append("SUBIS \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
		SUBSetFlags(result, XRegisterFile[op1Reg].readDoubleWord(), op2Imm);
		cpuLog.append("Set flags + \n");
	}
	
	private void SDIV(int destReg, int op1Reg, int op2Reg) {	// added SDIV instruction, SIMONE.DEIANA@studenti.units.it
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() / XRegisterFile[op2Reg].readDoubleWord());
			cpuLog.append("SDIV \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}
	
	private void UDIV(int destReg, int op1Reg, int op2Reg) {	// added UDIV instruction, SIMONE.DEIANA@studenti.units.it
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			BigInteger dividend = BigInteger.valueOf(XRegisterFile[op1Reg].readDoubleWord()).and(UNSIGNED_LONG_MASK);
			BigInteger divisor = BigInteger.valueOf(XRegisterFile[op2Reg].readDoubleWord()).and(UNSIGNED_LONG_MASK);
			BigInteger quotient = dividend.divide(divisor);
			XRegisterFile[destReg].writeDoubleWord(quotient.longValue());
			cpuLog.append("UDIV \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}
	
	
	
	private void SMULH(int destReg, int op1Reg, int op2Reg) {	// added SMULH instruction, SIMONE.DEIANA@studenti.units.it
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			BigInteger fullResult = BigInteger.valueOf(XRegisterFile[op1Reg].readDoubleWord()).multiply(BigInteger.valueOf(XRegisterFile[op2Reg].readDoubleWord()));
			BigInteger shiftedResult = fullResult.bitLength() > 64 ? fullResult.shiftRight(64) : BigInteger.valueOf(0);
			XRegisterFile[destReg].writeDoubleWord(shiftedResult.longValue());;
			cpuLog.append("SMULH \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}
	
	private void UMULH(int destReg, int op1Reg, int op2Reg) {	// added UMULH instruction, SIMONE.DEIANA@studenti.units.it
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			BigInteger fullResult = BigInteger.valueOf(XRegisterFile[op1Reg].readDoubleWord()).and(UNSIGNED_LONG_MASK).multiply(BigInteger.valueOf(XRegisterFile[op2Reg].readDoubleWord()).and(UNSIGNED_LONG_MASK));
			BigInteger shiftedResult = fullResult.bitLength() > 64 ? fullResult.shiftRight(64) : BigInteger.valueOf(0);
			XRegisterFile[destReg].writeDoubleWord(shiftedResult.longValue());;
			cpuLog.append("UMULH \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		  }
		}


	private void AND(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() & XRegisterFile[op2Reg].readDoubleWord());
			cpuLog.append("AND \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}

	private void ANDS(int destReg, int op1Reg, int op2Reg) {
		long result = XRegisterFile[op1Reg].readDoubleWord() & XRegisterFile[op2Reg].readDoubleWord();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(result);;
			cpuLog.append("ANDS \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
		ANDSetFlags(result);
		cpuLog.append("Set flags + \n");
	}

	private void ANDI(int destReg, int op1Reg, int op2Imm) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() & op2Imm);
			cpuLog.append("ANDI \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
	}

	private void ANDIS(int destReg, int op1Reg, int op2Imm) {
		long result = XRegisterFile[op1Reg].readDoubleWord() & op2Imm;
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(result);
			cpuLog.append("ANDIS \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
		ANDSetFlags(result);
		cpuLog.append("Set flags + \n");
	}

	private void ORR(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() | XRegisterFile[op2Reg].readDoubleWord());
			cpuLog.append("ORR \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}

	private void ORRI(int destReg, int op1Reg, int op2Imm) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() | op2Imm);
			cpuLog.append("ORRI \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
	}

	private void EOR(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() ^ XRegisterFile[op2Reg].readDoubleWord());
			cpuLog.append("EOR \t X" + destReg + ", X" + op1Reg + ", X" + op2Reg + "\n");
		}
	}

	private void EORI(int destReg, int op1Reg, int op2Imm) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() ^ op2Imm);;
			cpuLog.append("EORI \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
	}

	private void LSL(int destReg, int op1Reg, int op2Imm) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() << op2Imm);
			cpuLog.append("LSL \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
	}

	private void LSR(int destReg, int op1Reg, int op2Imm) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[op1Reg].readDoubleWord() >>> op2Imm);
			cpuLog.append("LSR \t X" + destReg + ", X" + op1Reg + ", #" + op2Imm + "\n");
		}
	}

	private void LDUR(int destReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(memory.loadDoubleword(XRegisterFile[baseAddressReg].readDoubleWord()+offset));
			cpuLog.append("LDUR \t X" + destReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
		}
	}

	private void STUR(int valReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		memory.storeDoubleword(XRegisterFile[baseAddressReg].readDoubleWord()+offset, XRegisterFile[valReg].readDoubleWord());
		clearExclusiveAccessTag(XRegisterFile[baseAddressReg].readDoubleWord()+offset, Memory.DOUBLEWORD_SIZE);
		cpuLog.append("STUR \t X" + valReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
	}

	private void LDURSW(int destReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(memory.loadSignedWord(XRegisterFile[baseAddressReg].readDoubleWord()+offset));
			cpuLog.append("LDURSW \t X" + destReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
		}
	}

	private void STURW(int valReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		memory.storeWord(XRegisterFile[baseAddressReg].readDoubleWord()+offset, XRegisterFile[valReg].readDoubleWord());
		clearExclusiveAccessTag(XRegisterFile[baseAddressReg].readDoubleWord()+offset, Memory.WORD_SIZE);
		cpuLog.append("STURW \t X" + valReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
	}

	private void LDURH(int destReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(memory.loadHalfword(XRegisterFile[baseAddressReg].readDoubleWord()+offset));
			cpuLog.append("LDURH \t X" + destReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
		}
	}

	private void STURH(int valReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		memory.storeHalfword(XRegisterFile[baseAddressReg].readDoubleWord()+offset, XRegisterFile[valReg].readDoubleWord());
		clearExclusiveAccessTag(XRegisterFile[baseAddressReg].readDoubleWord()+offset, Memory.HALFWORD_SIZE);
		cpuLog.append("STURH \t X" + valReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
	}

	private void LDURB(int destReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(memory.loadByte(XRegisterFile[baseAddressReg].readDoubleWord()+offset));
			cpuLog.append("LDURB \t X" + destReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
		}
	}

	private void STURB(int valReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		memory.storeByte(XRegisterFile[baseAddressReg].readDoubleWord()+offset, XRegisterFile[valReg].readDoubleWord());
		clearExclusiveAccessTag(XRegisterFile[baseAddressReg].readDoubleWord()+offset, Memory.BYTE_SIZE);
		cpuLog.append("STURB \t X" + valReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
	}

	private void LDXR(int destReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		long address = XRegisterFile[baseAddressReg].readDoubleWord() + offset;
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(memory.loadDoubleword(address));
			taggedAddress = address;
			cpuLog.append("LDXR \t X" + destReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
		}
	}

	private void STXR(int valReg, int outcomeReg, int baseAddressReg, int offset, Memory memory)
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		long address = XRegisterFile[baseAddressReg].readDoubleWord() + offset;
		if (taggedAddress == address) {
			memory.storeDoubleword(address, XRegisterFile[valReg].readDoubleWord());
			XRegisterFile[outcomeReg].writeDoubleWord(0);
			taggedAddress = 0;
			STXRSucceed = true;
		} else {
			XRegisterFile[outcomeReg].writeDoubleWord(1);
			STXRSucceed = false;
		}
		cpuLog.append("STXR \t X" + valReg + ", X" + outcomeReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
	}

	private void MOVZ(int destReg, int immediate, int quadrantShift) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(immediate << quadrantShift);
			cpuLog.append("MOVZ \t X" + destReg + ", #" + immediate + ", LSL #" + quadrantShift + " \n");
		}
	}

	private void MOVK(int destReg, int immediate, int quadrantShift) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			XRegisterFile[destReg].writeDoubleWord(XRegisterFile[destReg].readDoubleWord() | (immediate << quadrantShift));
			cpuLog.append("MOVK \t X" + destReg + ", #" + immediate + ", LSL #" + quadrantShift + " \n");
		}
	}

	private void CBZ(int conditionReg, int branchIndex) {
		if (XRegisterFile[conditionReg].readDoubleWord() == 0) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("CBZ \t X" + conditionReg + ", " + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (XRegisterFile[conditionReg].readDoubleWord() == 0);
	}

	private void CBNZ(int conditionReg, int branchIndex) {
		if (XRegisterFile[conditionReg].readDoubleWord() != 0) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("CBNZ \t X" + conditionReg + ", " + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (XRegisterFile[conditionReg].readDoubleWord() != 0);
	}

	private void BEQ(int branchIndex) {
		if (Zflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.EQ \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (Zflag);
	}

	private void BNE(int branchIndex) {
		if (!Zflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.NE \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!Zflag);
	}

	private void BHS(int branchIndex) {
		if (Cflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.HS \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (Cflag);
	}

	private void BLO(int branchIndex) {
		if (!Cflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.LO \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!Cflag);
	}

	private void BHI(int branchIndex) {
		if (!Zflag && Cflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.HI \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!Zflag && Cflag);
	}

	private void BLS(int branchIndex) {
		if (!(!Zflag && Cflag)) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.LS \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!(!Zflag && Cflag));
	}

	private void BGE(int branchIndex) {
		if (Nflag == Vflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.GE \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (Nflag == Vflag);
	}

	private void BLT(int branchIndex) {
		if (Nflag != Vflag) {														// fixed jump condition acarini@units.it
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.LT \t" + "0x" + Long.toHexString(getPC()) + " \n");
	}

	private void BGT(int branchIndex) {
		if (!Zflag && Nflag == Vflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.GT \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!Zflag && Nflag == Vflag);
	}

	private void BLE(int branchIndex) {
		if (!(!Zflag && Nflag == Vflag)) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.LE \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!(!Zflag && Nflag == Vflag));
	}

	private void BMI(int branchIndex) {
		if (Nflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.MI \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (Nflag);
	}

	private void BPL(int branchIndex) {
		if (!Nflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.PL \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!Nflag);
	}

	private void BVS(int branchIndex) {
		if (Vflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.VS \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (Vflag);
	}

	private void BVC(int branchIndex) {
		if (!Vflag) {
			instructionIndex = branchIndex;
		}
		cpuLog.append("B.VC \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = (!Vflag);
	}

	private void B(int branchIndex) {
		instructionIndex = branchIndex;
		cpuLog.append("B \t" + "0x" + Long.toHexString(getPC()) + " \n");
		branchTaken = true;
	}

	private void BR(int branchReg, Memory memory) throws SegmentFaultException, PCAlignmentException {
		if (XRegisterFile[branchReg].readDoubleWord()%Memory.WORD_SIZE != 0) {
			throw new PCAlignmentException(XRegisterFile[branchReg].readDoubleWord());
		}
		if (XRegisterFile[branchReg].readDoubleWord() < Memory.TEXT_SEGMENT_OFFSET 
				|| XRegisterFile[branchReg].readDoubleWord() > memory.getStaticDataSegmentOffset()-Memory.WORD_SIZE) {
			throw new SegmentFaultException(XRegisterFile[branchReg].readDoubleWord(), "text");
		}
		instructionIndex = (int) (XRegisterFile[branchReg].readDoubleWord() - Memory.TEXT_SEGMENT_OFFSET) / INSTRUCTION_SIZE;
		cpuLog.append("BR \t X" + "0x" + Long.toHexString(getPC()) + " \n");
	}

	private void BL(int branchIndex) {
		XRegisterFile[LR].writeDoubleWord(instructionIndex * INSTRUCTION_SIZE + Memory.TEXT_SEGMENT_OFFSET);
		instructionIndex = branchIndex;
		cpuLog.append("BL \t" + "0x" + Long.toHexString(XRegisterFile[LR].readDoubleWord()) + " \n");
	}
	
	private void FADDS(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeWord(Float.floatToIntBits(
							Float.intBitsToFloat(DRegisterFile[op1Reg].readWord()) +
							Float.intBitsToFloat(DRegisterFile[op2Reg].readWord())
							));
			cpuLog.append("FADDS \t S" + destReg + ", S" + op1Reg + ", S" + op2Reg + "\n");
		}
	}
	
	private void FADDD(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeDoubleWord(Double.doubleToLongBits(
							Double.longBitsToDouble(DRegisterFile[op1Reg].readDoubleWord()) +
							Double.longBitsToDouble(DRegisterFile[op2Reg].readDoubleWord())
							));
			cpuLog.append("FADDD \t D" + destReg + ", D" + op1Reg + ", D" + op2Reg + "\n");
		}
	}
	
	private void FSUBS(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeWord(Float.floatToIntBits(
							Float.intBitsToFloat(DRegisterFile[op1Reg].readWord()) -
							Float.intBitsToFloat(DRegisterFile[op2Reg].readWord())
							));
			cpuLog.append("FSUBS \t S" + destReg + ", S" + op1Reg + ", S" + op2Reg + "\n");
		}
	}
	
	private void FSUBD(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeDoubleWord(Double.doubleToLongBits(
							Double.longBitsToDouble(DRegisterFile[op1Reg].readDoubleWord()) -
							Double.longBitsToDouble(DRegisterFile[op2Reg].readDoubleWord())
							));
			cpuLog.append("FSUBD \t D" + destReg + ", D" + op1Reg + ", D" + op2Reg + "\n");
		}
	}
	
	private void FMULS(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeWord(Float.floatToIntBits(
							Float.intBitsToFloat(DRegisterFile[op1Reg].readWord()) *
							Float.intBitsToFloat(DRegisterFile[op2Reg].readWord())
							));
			cpuLog.append("FMULS \t S" + destReg + ", S" + op1Reg + ", S" + op2Reg + "\n");
		}
	}
	
	private void FMULD(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeDoubleWord(Double.doubleToLongBits(
							Double.longBitsToDouble(DRegisterFile[op1Reg].readDoubleWord()) *
							Double.longBitsToDouble(DRegisterFile[op2Reg].readDoubleWord())
							));
			cpuLog.append("FMULD \t D" + destReg + ", D" + op1Reg + ", D" + op2Reg + "\n");
		}
	}
	
	private void FDIVS(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeWord(Float.floatToIntBits(
							Float.intBitsToFloat(DRegisterFile[op1Reg].readWord()) /
							Float.intBitsToFloat(DRegisterFile[op2Reg].readWord())
							));
			cpuLog.append("FDIVS \t S" + destReg + ", S" + op1Reg + ", S" + op2Reg + "\n");
		}
	}
	
	private void FDIVD(int destReg, int op1Reg, int op2Reg) {
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeDoubleWord(Double.doubleToLongBits(
							Double.longBitsToDouble(DRegisterFile[op1Reg].readDoubleWord()) /
							Double.longBitsToDouble(DRegisterFile[op2Reg].readDoubleWord())
							));
			cpuLog.append("FDIVD \t D" + destReg + ", D" + op1Reg + ", D" + op2Reg + "\n");
		}
	}
	
	private void FCMPS(int op1Reg, int op2Reg) {
		float op1f = Float.intBitsToFloat(DRegisterFile[op1Reg].readWord());
		float op2f = Float.intBitsToFloat(DRegisterFile[op2Reg].readWord());
		FCMPSetFlags(Float.compare(op1f, op2f), Float.isNaN(op1f) || Float.isNaN(op1f));	
		cpuLog.append("FCMPS \t S" + op1Reg + ", S" + op2Reg + "\n");
		cpuLog.append("Set flags + \n");
	}
	
	private void FCMPD(int op1Reg, int op2Reg) {
		double op1d = Double.longBitsToDouble(DRegisterFile[op1Reg].readDoubleWord());
		double op2d = Double.longBitsToDouble(DRegisterFile[op2Reg].readDoubleWord());
		FCMPSetFlags(Double.compare(op1d, op2d), Double.isNaN(op1d) || Double.isNaN(op2d));
		cpuLog.append("FCMPD \t D" + op1Reg + ", D" + op2Reg + "\n");	
		cpuLog.append("Set flags + \n");
	}
	
	private void STURD(int valReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		memory.storeDoubleword(XRegisterFile[baseAddressReg].readDoubleWord()+offset, DRegisterFile[valReg].readDoubleWord());
		clearExclusiveAccessTag(XRegisterFile[baseAddressReg].readDoubleWord()+offset, Memory.DOUBLEWORD_SIZE);
		cpuLog.append("STURD \t D" + valReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
	}
	
	private void LDURD(int destReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeDoubleWord(memory.loadDoubleword(XRegisterFile[baseAddressReg].readDoubleWord()+offset));
			cpuLog.append("LDURD \t D" + destReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
		}
	}
	
	private void STURS(int valReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		memory.storeWord(XRegisterFile[baseAddressReg].readDoubleWord()+offset, DRegisterFile[valReg].readDoubleWord());
		clearExclusiveAccessTag(XRegisterFile[baseAddressReg].readDoubleWord()+offset, Memory.DOUBLEWORD_SIZE);
		cpuLog.append("STURD \t S" + valReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
	}
	
	private void LDURS(int destReg, int baseAddressReg, int offset, Memory memory) 
			throws SegmentFaultException, SPAlignmentException {
		if (baseAddressReg == SP) checkSPAlignment();
		if (destReg == XZR) {
			cpuLog.append("Ignored attempted assignment to XZR. \n");
		} else {
			DRegisterFile[destReg].writeWord((int) memory.loadDoubleword(XRegisterFile[baseAddressReg].readDoubleWord()+offset));
			cpuLog.append("LDURD \t S" + destReg + ", [X" + baseAddressReg + ", #" + offset + "] \n");
		}
	}

}
