package com.arm.legv8simulator.client.lexer;

/**
 * The <code>Token</code> class represents the lexical tokens returned by the <code>Lexer</code>
 * after processing a line of LEGv8 source code. These tokens can be parsed by the <code>Parser</code>.
 * 
 * @see Lexer
 * @see	Parser
 * 
 * @author Jonathan Wright, 2016
 */
public class Token {
	
	/**
	 * @param type	the type of this lexical <code>Token</code>
	 * @param data	the string recognised as this lexical <code>Token</code>
	 * @see		TokenType
	 */
	public Token(TokenType type, String data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * @return	the type of this lexical <code>Token</code>
	 * @see		TokenType
	 */
	public TokenType getType() {
		return type;
	}
	
	/**
	 * @return	the string forming this lexical <code>Token</code>
	 */
	public String getData() {
		return data;
	}
	
	/**
	 * @return	the string representation of this <code>Token</code> enclosed by 
	 * vertical bars to enable whitespace visibility
	 */
	@Override
	public String toString() {
		return type.name() + " |" + data + "|";
	}
	
	private TokenType type;
	private String data;
}
