package com.arm.legv8simulator.client;

/**
 * Generic class to hold runtime and compile time errors with their messages to be displayed to the user.
 * 
 * @author Jonathan Wright, 2016
 */
public class Error {
	
	/**
	 * @param errorMsg		the error message that will be displayed to the user
	 * @param lineNumber	the line number in the editor of the offending code
	 */
	public Error(String errorMsg, int lineNumber) {
		this.errorMsg = errorMsg;
		this.lineNumber = lineNumber;
	}
	
	/**
	 * @return	the error message that will be displayed to the user
	 */
	public String getMsg() {
		return errorMsg;
	}
	
	/**
	 * @return	the line number in the editor of the offending code
	 */
	public int getLineNumber() {
		return lineNumber;
	}
	
	private String errorMsg;
	private int lineNumber;
}
