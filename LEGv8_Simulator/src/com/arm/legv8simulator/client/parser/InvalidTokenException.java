package com.arm.legv8simulator.client.parser;

import com.arm.legv8simulator.client.instruction.Mnemonic;
import com.arm.legv8simulator.client.lexer.TokenType;

/**
 * Thrown when the parser reads a token that it is not expecting given the parser's 
 * current (non-accepting) state
 * 
 * @author Jonathan Wright, 2016
 */
public class InvalidTokenException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param expected	the lexical token expected - but not found - by the parser
	 */
	public InvalidTokenException(TokenType expected) {
		super();
		tokenExpected = expected;
	}
	
	/**
	 * @param tokenExpected		the lexical token expected - but not found - by the parser
	 * @param mnemonicExpected	the instruction mnemonic expected - but not found - by the parser
	 */
	public InvalidTokenException(TokenType tokenExpected, Mnemonic mnemonicExpected) {
		super();
		this.tokenExpected = tokenExpected;
		this.mnemonicExpected = mnemonicExpected;
	}
	
	/**
	 * @return	the lexical token expected - but not found - by the parser
	 */
	public TokenType getExpectedTokenType() {
		return tokenExpected;
	}
	
	/**
	 * @return	the instruction mnemonic expected - but not found - by the parser
	 */
	public Mnemonic getExpectedMnemonic() {
		return mnemonicExpected;
	}
	
	private TokenType tokenExpected;
	private Mnemonic mnemonicExpected;
}
