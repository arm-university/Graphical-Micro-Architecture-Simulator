package com.arm.legv8simulator.client.lexer;

/**
 * The <code>TokenType</code> enumeration uses regular expressions to define
 * all valid lexical tokens in the LEGv8 assembly language.
 * 
 * @author Jonathan Wright, 2016
 */

/* Instruction mnemonics and immediates which contain letters from the English alphabet 
 * must contain either all upper case letters or all lower case letters; a mixture 
 * is not permitted. 
 */
public enum TokenType {
	
	// The order in which these enums are defined is important to get the longest RegExp match
	LBRACKET("\\[", 1, "["),
	RBRACKET("\\]", 2, "]"),
	COMMA("\\,", 3, ","),
	IMMEDIATE("#?-?0[xX][0-9a-fA-F]+|#?-?[0-9]+", 4, "IMMEDIATE"),
	REGISTER("[Xx][12][0-9]|[Xx]30|[Xx][0-9]|XZR|xzr|SP|sp|LR|lr|FP|fp|IP[01]|ip[01]", 5, "REGISTER"),
	MNEMONIC_R("BR[ \t]+|br[ \t]+", 6, "MNEMONIC"),
	MNEMONIC_RISI("MOV[ZK][ \t]+|mov[zk][ \t]+", 7, "MNEMONIC"),
	MNEMONIC_RI("CMPI[ \t]+|cmpi[ \t]+", 8, "MNEMONIC"),
	MNEMONIC_RR("CMP[ \t]+|MOV[ \t]+|cmp[ \t]+|mov[ \t]+", 9, "MNEMONIC"),
	MNEMONIC_RRI("ADDIS?[ \t]+|SUBIS?[ \t]+|ANDIS?[ \t]+|ORRI[ \t]+|EORI[ \t]+|LS[LR][ \t]+|addis?[ \t]+|subis?[ \t]+|andis?[ \t]+|orri[ \t]+|eori[ \t]+|ls[lr][ \t]+", 10, "MNEMONIC"),
	MNEMONIC_RRR("ADDS?[ \t]+|SUBS?[ \t]+|ANDS?[ \t]+|ORR[ \t]+|EOR[ \t]+|adds?[ \t]+|subs?[ \t]+|ands?[ \t]+|orr[ \t]+|eor[ \t]+", 11, "MNEMONIC"),
	MNEMONIC_RM("LDURSW[ \t]+|LDUR[HB]?[ \t]+|LDXR[ \t]+|STUR[WHB]?[ \t]+|ldursw[ \t]+|ldur[hb]?[ \t]+|ldxr[ \t]+|stur[whb]?[ \t]+", 12, "MNEMONIC"),
	MNEMONIC_RRM("STXR[ \t]+|stxr[ \t]+", 13, "MNEMONIC"),
	MNEMONIC_L("B\\.[GNL]E[ \t]+|B\\.[HL]S[ \t]+|B\\.[LG]T[ \t]+|B\\.LO[ \t]+|B\\.[MH]I[ \t]+|B\\.EQ[ \t]+|B\\.V[SC][ \t]+|B\\.PL[ \t]+|B[RL]?[ \t]+|b\\.[gnl]e[ \t]+|b\\.[hl]s[ \t]+|b\\.[lg]t[ \t]+|b\\.lo[ \t]+|b\\.[mh]i[ \t]+|b\\.eq[ \t]+|b\\.v[sc][ \t]+|b\\.pl[ \t]+|b[rl]?[ \t]+", 14, "MNEMONIC"),
	MNEMONIC_RL("CBN?Z[ \t]+|cbn?z[ \t]+", 15, "MNEMONIC"),
	LABEL("[A-Za-z0-9_]+:", 16, "LABEL"),
	IDENTIFIER("[A-Za-z0-9_]+", 17, "IDENTIFIER"),
	WHITESPACE("[ \t]+", 18, "WHITESPACE"),
	ERROR("[^\\s]+", 19, "ERROR");

	/**
	 * The regular expression pattern used to specify this <code>TokenType</code>
	 */
	public final String pattern;
	
	/**
	 * The String representation of this <code>TokenType</code> to be presented to 
	 * the user in <code>Lexer</code> and <code>Parser</code> error messages.
	 * 
	 * @see Lexer
	 * @see Parser
	 */
	public final String tokenStr;
	
	/**
	 * The ID number used by the <code>Lexer</code> to identify each <code>TokenType</code>
	 */
	public final int groupNumber;
	
	/**
	 * @param pattern		The regular expression pattern used to specify this <code>TokenType</code>
	 * @param groupNumber	The String representation of this <code>TokenType</code> to be presented to 
	 *						the user in <code>Lexer</code> and <code>Parser</code> error messages.
	 * @param tokenStr		The ID number used by the <code>Lexer</code> to identify each <code>TokenType</code>
	 */
	private TokenType(String pattern, int groupNumber, String tokenStr) {
		this.pattern = pattern;
		this.groupNumber = groupNumber;
		this.tokenStr = tokenStr;
	}
	
	/**
	 * @return	tokenStr
	 * @see 	tokenStr
	 */
	@Override
	public String toString() {
		return tokenStr;
	} 
}
