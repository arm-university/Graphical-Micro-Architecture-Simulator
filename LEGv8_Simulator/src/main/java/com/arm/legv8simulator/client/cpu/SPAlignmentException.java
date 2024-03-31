package com.arm.legv8simulator.client.cpu;

/**
 * Thrown when the <code>CPU</code> attempts to execute a data transfer instruction using an unaligned 
 * stack pointer as the base address.
 * <p>
 * The stack pointer is unaligned if it is not 0 mod 16.
 * 
 * @author Jonathan Wright, 2016
 */
public class SPAlignmentException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param address	the unaligned (illegal) address held by the SP
	 */
	public SPAlignmentException(long address) {
		super();
		this.address = address;
	}
	
	@Override
	public String getMessage() {
		return "Data transfer operation with misaligned SP: 0x" + Long.toHexString(address) + " is not quadword aligned.";
	}
	
	private long address;
}
