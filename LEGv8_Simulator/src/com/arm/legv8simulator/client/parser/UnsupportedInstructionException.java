package com.arm.legv8simulator.client.parser;

/**
 * Thrown when the parser encounters a token which does not match any supported 
 * LEGv8 instruction mnemonic.
 * 
 * @author Jonathan Wright, 2016
 */
public class UnsupportedInstructionException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param state	the parser state when this 
	 * <code>UnsupportedInstructionException</code> was thrown
	 * @see Parser
	 * @see ParserState
	 */
	public UnsupportedInstructionException(ParserState state) {
		super();
		this.state = state;
	}
	
	/**
	 * @return	the parser state when this 
	 * <code>UnsupportedInstructionException</code> was thrown
	 */
	public ParserState getState() {
		return state;
	}
	
	private ParserState state;
}
