package com.arm.legv8simulator.client.instruction;

/**
 * Thrown when a label is used in an instruction; but is not defined in source code.
 * 
 * @author	Jonathan Wright, 2016
 */
public class UndefinedLabelException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param label	the undefined label in the LEGv8 source code
	 */
	public UndefinedLabelException(String label) {
		super();
		this.label = label;
	}

	@Override
	public String getMessage() {
		return "Undefined label: '" + label + "'";
	}
	
	private String label;
}
