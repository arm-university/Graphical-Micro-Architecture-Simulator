package com.arm.legv8simulator.client.instruction;

import java.util.ArrayList;
import java.util.HashMap;

import com.arm.legv8simulator.client.cpu.CPU;
import com.arm.legv8simulator.client.cpu.ControlUnitConfiguration;
import com.google.gwt.core.client.JavaScriptException;

/**
 * The <code>Decoder</code> is used to generate <code>Instruction</code>s from the LEGv8 source code.
 * 
 * @see Instruction
 * 
 * @author Jonathan Wright, 2016
 */
public class Decoder {
	
	public static final int ARITHMETIC_IMM_LOWER_BOUND = 0;
	public static final int ARITHMETIC_IMM_UPPER_BOUND = 4095;
	public static final int OFFSET_IMM_LOWER_BOUND = -256;
	public static final int OFFSET_IMM_UPPER_BOUND = 255;
	public static final int SHIFT_IMM_LOWER_BOUND = 0;
	public static final int SHIFT_IMM_UPPER_BOUND = 63;
	public static final int WIDE_IMM_LOWER_BOUND = 0;
	public static final int WIDE_IMM_UPPER_BOUND = 65535;
	public static final int LOGICAL_IMM_LOWER_BOUND = ARITHMETIC_IMM_LOWER_BOUND;
	public static final int LOGICAL_IMM_UPPER_BOUND = ARITHMETIC_IMM_UPPER_BOUND;
	public static final int[] WIDE_SHIFT_IMM = {0, 16, 32, 48};
	public static final int EXCLUSIVE_IMM = 0;

	/**
	 * @param mnemonic		the instruction mnemonic
	 * @param args			the array of arguments (in string form) for this instruction i.e. register indices and immediates
	 * @param lineNumber	the line in the code editor of this instruction
	 * @param branchTable	table mapping labels to instruction indices used to implement branch instructions
	 * @return				an <code>Instruction</code> able to be executed by the <code>CPU</code> class
	 * 
	 * @throws UndefinedLabelException			when a label (instruction argument) is not defined in the <code>branchTable</code>
	 * @throws ImmediateOutOfBoundsException	when an immediate (instruction argument) is not within the permitted range
	 * 
	 * @see CPU
	 * @see Mnemonic
	 * @see Instruction
	 */
	public static Instruction getInstruction(Mnemonic mnemonic, ArrayList<String> args, 
			int lineNumber, HashMap<String, Integer> branchTable) 
					throws UndefinedLabelException, ImmediateOutOfBoundsException {
		switch (mnemonic) {
		case ADD :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR);
		case ADDS :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR_FLAGS);
		case ADDI :
			return new Instruction(mnemonic, decodeRRIArithmeticArgs(args), lineNumber, ControlUnitConfiguration.RRI);
		case ADDIS :
			return new Instruction(mnemonic, decodeRRIArithmeticArgs(args), lineNumber, ControlUnitConfiguration.RRI_FLAGS);
		case SUB :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR);
		case SUBS :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR_FLAGS);
		case SUBI :
			return new Instruction(mnemonic, decodeRRIArithmeticArgs(args), lineNumber, ControlUnitConfiguration.RRI);
		case SUBIS :
			return new Instruction(mnemonic, decodeRRIArithmeticArgs(args), lineNumber, ControlUnitConfiguration.RRI_FLAGS);
		case AND :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR);
		case ANDS :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR_FLAGS);
		case ANDI :
			return new Instruction(mnemonic, decodeRRILogicalArgs(args), lineNumber, ControlUnitConfiguration.RRI);
		case ANDIS :
			return new Instruction(mnemonic, decodeRRILogicalArgs(args), lineNumber, ControlUnitConfiguration.RRI_FLAGS);
		case ORR :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR);
		case ORRI :
			return new Instruction(mnemonic, decodeRRILogicalArgs(args), lineNumber, ControlUnitConfiguration.RRI);
		case EOR :
			return new Instruction(mnemonic, decodeRRRArgs(args), lineNumber, ControlUnitConfiguration.RRR);
		case EORI :
			return new Instruction(mnemonic, decodeRRILogicalArgs(args), lineNumber, ControlUnitConfiguration.RRI);
		case LSL :
			return new Instruction(mnemonic, decodeRRIShiftArgs(args), lineNumber, ControlUnitConfiguration.RRI);
		case LSR :
			return new Instruction(mnemonic, decodeRRIShiftArgs(args), lineNumber, ControlUnitConfiguration.RRI);
		case LDUR :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_LOAD);
		case STUR :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_STORE);
		case LDURSW :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_LOAD);
		case STURW :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_STORE);
		case LDURH :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_LOAD);
		case STURH :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_STORE);
		case LDURB :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_LOAD);
		case STURB :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_STORE);
		case LDXR :
			return new Instruction(mnemonic, decodeRMArgs(args), lineNumber, ControlUnitConfiguration.RM_LOAD);
		case STXR :
			return new Instruction(mnemonic, decodeRRMArgs(args), lineNumber, ControlUnitConfiguration.RRM);
		case MOVZ :
			return new Instruction(mnemonic, decodeRISIArgs(args), lineNumber, ControlUnitConfiguration.RISI);
		case MOVK :
			return new Instruction(mnemonic, decodeRISIArgs(args), lineNumber, ControlUnitConfiguration.RISI);
		case CBZ :
			return new Instruction(mnemonic, decodeRLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.RL);
		case CBNZ :
			return new Instruction(mnemonic, decodeRLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.RL);
		case BEQ :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BNE :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BHS :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BLO :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BHI :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BLS :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BGE :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BLT :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BGT :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BLE :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BMI :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BPL :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BVS :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case BVC :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L_COND);
		case B :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, ControlUnitConfiguration.L);
		case BR :
			return new Instruction(mnemonic, decodeRArgs(args), lineNumber, null);
		case BL :
			return new Instruction(mnemonic, decodeLArgs(args, branchTable), lineNumber, null);
		case CMP :
			return new Instruction(Mnemonic.SUBS, decodeCMPArgs(args), lineNumber, null);
		case CMPI :
			return new Instruction(Mnemonic.SUBIS, decodeRIArgs(args), lineNumber, null);
		case MOV :
			return new Instruction(Mnemonic.ORR, decodeMOVArgs(args), lineNumber, null);
		default : return null;
		}
	}

	private static int[] decodeRArgs(ArrayList<String> args) {
		int[] operands = new int[1];
		operands[0] = decodeRegister(args.get(0));
		return operands;
	}
	
	private static int[] decodeCMPArgs(ArrayList<String> args) {
		int[] operands = new int[3];
		operands[0] = decodeRegister("XZR");
		operands[1] = decodeRegister(args.get(0));
		operands[2] = decodeRegister(args.get(1));
		return operands;
	}
	
	private static int[] decodeMOVArgs(ArrayList<String> args) {
		int[] operands = new int[3];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeRegister("XZR");
		operands[2] = decodeRegister(args.get(1));
		return operands;
	}
	
	private static int[] decodeRRRArgs(ArrayList<String> args) {
		int[] operands = new int[2];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeRegister(args.get(1));
		operands[2] = decodeRegister(args.get(2));
		return operands;
	}
	
	private static int[] decodeRIArgs(ArrayList<String> args) throws ImmediateOutOfBoundsException {
		int[] operands = new int[3];
		operands[0] = decodeRegister("XZR");
		operands[1] = decodeRegister(args.get(0));
		operands[2] = decodeArithmeticImmediate(args.get(0));
		return operands;
	}
	
	private static int[] decodeRRIArithmeticArgs(ArrayList<String> args) throws ImmediateOutOfBoundsException {
		int[] operands = new int[3];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeRegister(args.get(1));
		operands[2] = decodeArithmeticImmediate(args.get(2));
		return operands;
	}
	
	private static int[] decodeRRILogicalArgs(ArrayList<String> args) throws ImmediateOutOfBoundsException {
		int[] operands = new int[3];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeRegister(args.get(1));
		operands[2] = decodeLogicalImmediate(args.get(2));
		return operands;
	}
	
	private static int[] decodeRRIShiftArgs(ArrayList<String> args) throws ImmediateOutOfBoundsException {
		int[] operands = new int[3];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeRegister(args.get(1));
		operands[2] = decodeShiftImmediate(args.get(2));
		return operands;
	}
	
	private static int[] decodeRMArgs(ArrayList<String> args) throws ImmediateOutOfBoundsException {
		int[] operands = new int[3];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeRegister(args.get(1));
		if (args.size() == 3) {
			operands[2] = decodeOffsetImmediate(args.get(2));
		}
		return operands;
	}
	
	private static int[] decodeRRMArgs(ArrayList<String> args) throws ImmediateOutOfBoundsException {
		int[] operands = new int[4];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeRegister(args.get(1));
		operands[2] = decodeRegister(args.get(2));
		if (args.size() == 4) {
			operands[3] = decodeExclusiveImmediate(args.get(3));
		}
		return operands;
	}
	
	private static int[] decodeRISIArgs(ArrayList<String> args) throws ImmediateOutOfBoundsException {
		int[] operands = new int[3];
		operands[0] = decodeRegister(args.get(0));
		operands[1] = decodeWideImmediate(args.get(1));
		if (args.size() == 4) {
			operands[2] = decodeWideOffsetImmediate(args.get(3));
		}
		return operands;
	}
	
	private static int[] decodeRLArgs(ArrayList<String> args, HashMap<String, Integer> branchTable) 
			throws UndefinedLabelException {
		int[] operands = new int[2];
		operands[0] = decodeRegister(args.get(0));
		try {
			operands[1] = branchTable.get(args.get(1));
		} catch (NullPointerException npe) {
			throw new UndefinedLabelException(args.get(1));
		} catch (JavaScriptException jse) {
			throw new UndefinedLabelException(args.get(1));
		}
		return operands;
	}
	
	private static int[] decodeLArgs(ArrayList<String> args, HashMap<String, Integer> branchTable) 
			throws UndefinedLabelException {
		int[] instructionIndex = new int[1];
		try {
			instructionIndex[0] = branchTable.get(args.get(0));
		} catch (NullPointerException npe) {
			throw new UndefinedLabelException(args.get(0));
		} catch (JavaScriptException jse) {
			throw new UndefinedLabelException(args.get(0));
		}
		return instructionIndex;
	}
	
	private static int decodeRegister(String reg) {
		switch (reg) {
		case "XZR" : return CPU.XZR;
		case "xzr" : return CPU.XZR;
		case "IP0" : return CPU.IP0;
		case "ip0" : return CPU.IP0;
		case "IP1" : return CPU.IP1;
		case "ip1" : return CPU.IP1;
		case "SP" : return CPU.SP;
		case "sp" : return CPU.SP;
		case "FP" : return CPU.FP;
		case "fp" : return CPU.FP;
		case "LR" : return CPU.LR;
		case "lr" : return CPU.LR;
		default : return Integer.parseInt(reg.substring(1));
		}
	}
	
	private static int parseImmediate(String imm) {
		if (imm.startsWith("#")) {
			 return Integer.decode(imm.substring(1));
		}
		return Integer.decode(imm);
	}
	
	private static int decodeImmediate(String imm, int lowerBound, int upperBound) throws ImmediateOutOfBoundsException {
		int result;
		try {
			result = parseImmediate(imm);
		} catch (NumberFormatException nfe) {
			throw new ImmediateOutOfBoundsException(imm, lowerBound, upperBound);
		}
		if (result >= lowerBound && result <= upperBound) {
			return result;
		} else {
			throw new ImmediateOutOfBoundsException(imm, lowerBound, upperBound);
		}
	}
	
	private static int decodeArithmeticImmediate(String imm) throws ImmediateOutOfBoundsException {
		return decodeImmediate(imm, ARITHMETIC_IMM_LOWER_BOUND, ARITHMETIC_IMM_UPPER_BOUND);
	}
	
	private static int decodeLogicalImmediate(String imm) throws ImmediateOutOfBoundsException {
		return decodeImmediate(imm, LOGICAL_IMM_LOWER_BOUND, LOGICAL_IMM_UPPER_BOUND);
	}
	
	private static int decodeShiftImmediate(String imm) throws ImmediateOutOfBoundsException {
		return decodeImmediate(imm, SHIFT_IMM_LOWER_BOUND, SHIFT_IMM_UPPER_BOUND);
	}
	
	private static int decodeOffsetImmediate(String imm) throws ImmediateOutOfBoundsException {
		return decodeImmediate(imm, OFFSET_IMM_LOWER_BOUND, OFFSET_IMM_UPPER_BOUND);
	}
	
	private static int decodeWideImmediate(String imm) throws ImmediateOutOfBoundsException {
		return decodeImmediate(imm, WIDE_IMM_LOWER_BOUND, WIDE_IMM_UPPER_BOUND);
	}
	
	private static int decodeWideOffsetImmediate(String imm) throws ImmediateOutOfBoundsException {
		int result;
		try {
			result =  parseImmediate(imm);
		} catch (NumberFormatException nfe) {
			throw new ImmediateOutOfBoundsException(imm, 0, 48);
		}
		for (int i=0; i<WIDE_SHIFT_IMM.length; i++) {
			if (result == WIDE_SHIFT_IMM[i]) {
				return result;
			}
		}
		throw new ImmediateOutOfBoundsException(imm, WIDE_SHIFT_IMM);
	}
	
	private static int decodeExclusiveImmediate(String imm) throws ImmediateOutOfBoundsException {
		int result;
		try {
			result =  parseImmediate(imm);
		} catch (NumberFormatException nfe) {
			throw new ImmediateOutOfBoundsException(imm, EXCLUSIVE_IMM);
		}
		if (result == EXCLUSIVE_IMM) {
			return result;
		}
		throw new ImmediateOutOfBoundsException(imm, EXCLUSIVE_IMM);
	}
}
