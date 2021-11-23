package com.arm.legv8simulator.client.instruction;

/**
 * Thrown when an instruction immediate is outside of the permitted range.
 * <p>
 * Permitted values for the different immediates are defined in Patterson and Hennessy ARM Edition.
 * 
 * @author Jonathan Wright, 2016
 */
public class ImmediateOutOfBoundsException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param imm			string representation of the illegal immediate value
	 * @param lowerBound	the lower bound for this immediate type
	 * @param upperBound	the upper bound for this immediate type
	 */
	public ImmediateOutOfBoundsException(String imm, int lowerBound, int upperBound) {
		super();
		illegalImm = imm;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	/**
	 * 
	 * @param imm	string representation of the illegal immediate value
	 * @param cases	the permitted values for this immediate type
	 */
	public ImmediateOutOfBoundsException(String imm, int[] cases) {
		super();
		illegalImm = imm;
		this.cases = cases;	
	}
	
	/**
	 * 
	 * @param imm			string representation of the illegal immediate value
	 * @param allowedVal	the only allowed value for this immediate type
	 */
	public ImmediateOutOfBoundsException(String imm, int allowedVal) {
		super();
		illegalImm = imm;
		this.cases = new int[] {allowedVal};	
	}
	
	@Override
	public String getMessage() {
		if (cases == null) {
			return "Illegal immediate value: " + illegalImm + ". Permitted range: " + lowerBound + " - " + upperBound + " inclusive.";
		} else {
			String msg = "Illegal immediate value: " + illegalImm + ". Permitted values: ";
			for (int i=0; i<cases.length; i++) {
				msg += cases[i];
				if (i<cases.length-1) {
					msg += ", ";
				}	
			}
			return msg;
		}
	}
	
	private String illegalImm;
	private int lowerBound;
	private int upperBound;
	private int[] cases;
}
