package com.arm.legv8simulator.client.parser;

/**
 * Thrown when the parser is in an accepting state but then encounters an 
 * unexpected token
 * 
 * @author Jonathan Wright, 2016
 */
public class UnexpectedTokenException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnexpectedTokenException() {
		super();
	}
}
