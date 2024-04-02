package com.arm.legv8simulator.client.cpu;

/**
 * Thrown when the <code>CPU</code> attempts to execute an instruction at an address which is not word aligned.
 * <p>
 * Since the PC cannot be referenced directly by an instruction, the only way for the PC to become unaligned is
 * by calling the BR instruction with a register whose value is not 0 mod 4. 
 * 
 * @author Jonathan Wright, 2016
 */
public class PCAlignmentException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param address	the unaligned (illegal) address held by the PC
	 */
	public PCAlignmentException(long address) {
		super();
		this.address = address;
	}
	
	@Override
	public String getMessage() {
		return "The address 0x" + Long.toHexString(address) + " is not word aligned.";
	}
	
	private long address;
}
