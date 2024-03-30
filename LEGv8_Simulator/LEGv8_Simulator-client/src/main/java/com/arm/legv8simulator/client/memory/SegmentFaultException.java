package com.arm.legv8simulator.client.memory;

/**
 * Thrown when a user attempts to access a memory location outside the bounds 
 * of the allowed segment(s)
 * 
 * @author Jonathan Wright, 2016
 */
public class SegmentFaultException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param address	The rogue memory address that caused the exception to be thrown
	 * @param segment	The memory segment(s) within which <code>address</code> should have been contained 
	 */
	public SegmentFaultException(long address, String segment) {
		super();
		this.address = address;
		this.segment = segment;
	}
	
	@Override
	public String getMessage() {
		return "Memory address out of bounds: 0x" + Long.toHexString(address) + " is not in the " + segment + " segment";
	}
	
	private String segment;
	private long address;
}
