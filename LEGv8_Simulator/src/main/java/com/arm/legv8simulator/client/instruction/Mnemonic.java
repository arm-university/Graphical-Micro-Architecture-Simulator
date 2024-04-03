package com.arm.legv8simulator.client.instruction;

import com.arm.legv8simulator.client.lexer.TokenType;

/**
 * The <code>Mnemonic</code> enumeration defines all supported LEGv8 instruction mnemonics;  
 * as well as their opcodes and ALU control bits.
 * 
 * @author Jonathan Wright, 2016
 */
public enum Mnemonic {
	ADD("ADD", "add", TokenType.XMNEMONIC_RRR, "10001011000", "0010"),
	ADDS("ADDS", "adds", TokenType.XMNEMONIC_RRR, "10101011000", "0010"),
	ADDI("ADDI", "addi", TokenType.XMNEMONIC_RRI, "1001000100", "0010"),
	ADDIS("ADDIS", "addis", TokenType.XMNEMONIC_RRI, "1011000111", "0010"),
	MUL("MUL", "mul", TokenType.XMNEMONIC_RRR, "10011011000", "0010"),		// added MUL mnemonic, SIMONE.DEIANA@studenti.units.it
	SMULH("SMULH", "smulh", TokenType.XMNEMONIC_RRR, "10011011010", "0010"),	// added SMULH mnemonic, SIMONE.DEIANA@studenti.units.it
	UMULH("UMULH", "umulh", TokenType.XMNEMONIC_RRR, "10011011110", "0010"),	// added UMULH mnemonic, SIMONE.DEIANA@studenti.units.it
	SDIV("SDIV", "sdiv", TokenType.XMNEMONIC_RRR, "10011010110", "0010"),	// added SDIV mnemonic, SIMONE.DEIANA@studenti.units.it
	UDIV("UDIV", "udiv", TokenType.XMNEMONIC_RRR, "10011010110", "0010"),	// added UDIV mnemonic, SIMONE.DEIANA@studenti.units.it
	SUB("SUB", "sub", TokenType.XMNEMONIC_RRR, "11001011000", "0110"),
	SUBS("SUBS", "subs", TokenType.XMNEMONIC_RRR, "11101011000", "0110"),
	SUBI("SUBI", "subi", TokenType.XMNEMONIC_RRI, "1101000100", "0110"),
	SUBIS("SUBIS", "subis", TokenType.XMNEMONIC_RRI, "1111000100", "0110"),
	AND("AND", "and", TokenType.XMNEMONIC_RRR, "10001010000", "0000"),
	ANDS("ANDS", "ands", TokenType.XMNEMONIC_RRR, "11101010000", "0000"),
	ANDI("ANDI", "andi", TokenType.XMNEMONIC_RRI, "1001001000", "0000"),
	ANDIS("ANDIS", "andis", TokenType.XMNEMONIC_RRI, "1111001000", "0000"),
	ORR("ORR", "orr", TokenType.XMNEMONIC_RRR, "10101010000", "0001"),
	ORRI("ORRI", "orri", TokenType.XMNEMONIC_RRI, "1011001000", "0001"),
	EOR("EOR", "eor", TokenType.XMNEMONIC_RRR, "11001010000", "1011"),
	EORI("EORI", "eori", TokenType.XMNEMONIC_RRI, "1101001000", "1011"),
	LSL("LSL", "lsl", TokenType.XMNEMONIC_RRI, "11010011011", "1101"),
	LSR("LSR", "lsr", TokenType.XMNEMONIC_RRI, "11010011010", "1110"),	
	LDUR("LDUR", "ldur", TokenType.XMNEMONIC_RM, "11111000010", "0010"),
	STUR("STUR", "stur", TokenType.XMNEMONIC_RM, "11111000000", "0010"),
	LDURSW("LDURSW", "ldursw", TokenType.XMNEMONIC_RM, "10111000100", "0010"),
	STURW("STURW", "sturw", TokenType.XMNEMONIC_RM, "10111000000", "0010"),
	LDURH("LDURH", "ldurh", TokenType.XMNEMONIC_RM, "01111000010", "0010"),
	STURH("STURH", "sturh", TokenType.XMNEMONIC_RM, "01111000000", "0010"),
	LDURB("LDURB", "ldurb", TokenType.XMNEMONIC_RM, "00111000010", "0010"),
	STURB("STURB", "sturb", TokenType.XMNEMONIC_RM, "00111000000", "0010"),
	LDXR("LDXR", "ldxr", TokenType.XMNEMONIC_RM, "11001000010", "0010"),
	STXR("STXR", "stxr", TokenType.XMNEMONIC_RRM, "11001000000", "0010"),
	MOVZ("MOVZ", "movz", TokenType.XMNEMONIC_RISI, "110100101", "0001"),
	MOVK("MOVK", "movk", TokenType.XMNEMONIC_RISI, "111100101", "0001"),
	CBZ("CBZ", "cbz", TokenType.XMNEMONIC_RL, "10110100", "0111"),
	CBNZ("CBNZ", "cbnz", TokenType.XMNEMONIC_RL, "10110101", "0111"),
	LDA("LDA", "lda", TokenType.XMNEMONIC_RL, null, null),
	BEQ("B.EQ", "b.eq", TokenType.MNEMONIC_L, "01010100", null),
	BNE("B.NE", "b.ne", TokenType.MNEMONIC_L, "01010100", null),
	BHS("B.HS", "b.hs", TokenType.MNEMONIC_L, "01010100", null),
	BLO("B.LO", "b.lo", TokenType.MNEMONIC_L, "01010100", null),
	BHI("B.HI", "b.hi", TokenType.MNEMONIC_L, "01010100", null),
	BLS("B.LS", "b.ls", TokenType.MNEMONIC_L, "01010100", null),
	BGE("B.GE", "b.ge", TokenType.MNEMONIC_L, "01010100", null),
	BLT("B.LT", "b.lt", TokenType.MNEMONIC_L, "01010100", null),
	BGT("B.GT", "b.gt", TokenType.MNEMONIC_L, "01010100", null),
	BLE("B.LE", "b.le", TokenType.MNEMONIC_L, "01010100", null),
	BMI("B.MI", "b.mi", TokenType.MNEMONIC_L, "01010100", null),
	BPL("B.PL", "b.pl", TokenType.MNEMONIC_L, "01010100", null),
	BVS("B.VS", "b.vs", TokenType.MNEMONIC_L, "01010100", null),
	BVC("B.VC", "b.vc", TokenType.MNEMONIC_L, "01010100", null),
	B("B", "b", TokenType.MNEMONIC_L, "00101", null),
	BR("BR", "br", TokenType.XMNEMONIC_R, "11010110000", null),
	BL("BL", "bl", TokenType.MNEMONIC_L, "100101", null), 
	CMP("CMP", "cmp", TokenType.XMNEMONIC_RR, null, null),
	CMPI("CMPI", "cmpi", TokenType.XMNEMONIC_RI, null, null),
	MOV("MOV", "mov", TokenType.XMNEMONIC_RR, null, null),
	
	// floating point mnemonics
	FADDS("FADDS", "fadds", TokenType.SMNEMONIC_RRR, "00011110001", "0110"),
	FADDD("FADDD", "faddd", TokenType.DMNEMONIC_RRR, "00011110011", "0110"),
	FCMPS("FCMPS", "fcmps", TokenType.SMNEMONIC_RR, "00011110001", "0110"),
	FCMPD("FCMPD", "fcmpd", TokenType.DMNEMONIC_RR, "00011110011", "0110"),
	FDIVS("FDIVS", "fdvis", TokenType.SMNEMONIC_RRR, "00011110001", "0000"),
	FDIVD("FDIVD", "fdivd", TokenType.DMNEMONIC_RRR, "00011110011", "0000"),
	FMULS("FMULS", "fmuls", TokenType.SMNEMONIC_RRR, "00011110001", "0000"),
	FMULD("FMULD", "fmuld", TokenType.DMNEMONIC_RRR, "00011110011", "0000"),
	FSUBS("FSUBS", "fsubs", TokenType.SMNEMONIC_RRR, "00011110001", "0001"),
	FSUBD("FSUBD", "fsubd", TokenType.DMNEMONIC_RRR, "00011110011", "0001"),
	STURS("STURS", "ldurs", TokenType.SMNEMONIC_RM, "10111100010", "0010"),
	LDURS("LDURS", "ldurs", TokenType.SMNEMONIC_RM, "10111100000", "0010"),
	STURD("STURD", "ldurd", TokenType.DMNEMONIC_RM, "11111100000", "0010"),
	LDURD("LDURD", "ldurd", TokenType.DMNEMONIC_RM, "11111100010", "0010");
	
	/**
	 * @param nameUpper			lower case string representation of the instruction mnemonic 
	 * @param nameLower			upper case string representation of the instruction mnemonic
	 * @param type				the type of the instruction represented by this <code>Mnemonic</code>. 
	 * 							See LEGv8_Grammar.ppt for instruction groups
	 * @param opcode			the opcode for the instruction represented by this <code>Mnemonic</code> - 
	 * 							defined in Patterson and Hennessy ARM Edition. <code>null</code if this 
	 * 							mnemonic represents a pseudo instruction.
	 * @param aluControlInput	the control bits to determine the operation of the ALU. <code>null</code> if not 
	 * 							applicable for the instruction represented by this mnemonic.
	 * 
	 * @see TokenType
	 */
	/*
	 * Upper and lower case names required because LEGv8 assembler allows instructions written 
	 * in either upper or lower case but not mixed case. String.equalsIgnoreCase() method would not test for mixed case.
	 */
	private Mnemonic(String nameUpper, String nameLower, TokenType type, String opcode, String aluControlInput) {
		this.nameUpper = nameUpper;
		this.nameLower = nameLower;
		this.type = type;
		this.opcode = opcode;
		this.aluControlInput = aluControlInput;
	}
	
	/** 
	 * @param name	a string representation of a LEGv8 instruction mnemonic
	 * @return		the <code>Mnemonic</code> with the specified <code>name</code>
	 */
	public static Mnemonic fromString(String name) {
	    if (name != null) {
	    	for (Mnemonic m : Mnemonic.values()) {
	    		if (name.equals(m.nameUpper) || name.equals(m.nameLower)) {
	    			return m;
	    		}
	    	}
	    }
	    throw new IllegalArgumentException(name + " instruction mnemonic does not exist in LEGv8");
	}
	
	/**
	 * Lower case string representation of the instruction mnemonic
	 */
	public final String nameUpper;
	
	/**
	 * Upper case string representation of the instruction mnemonic
	 */
	public final String nameLower;
	
	/**
	 * The type of the instruction represented by this <code>Mnemonic</code>. 
	 * See LEGv8_Grammar.ppt for instruction groups
	 */
	public final TokenType type;
	
	/**
	 * The opcode for the instruction represented by this <code>Mnemonic</code> - defined in Patterson and Hennessy ARM Edition.
	 * <code>null</code if this mnemonic represents a pseudo instruction.
	 */
	public final String opcode;
	
	/**
	 * The control bits to determine the operation of the ALU. <code>null</code> if not 
	 * applicable for the instruction represented by this <code>Mnemonic</code>.
	 */
	public final String aluControlInput;
}
