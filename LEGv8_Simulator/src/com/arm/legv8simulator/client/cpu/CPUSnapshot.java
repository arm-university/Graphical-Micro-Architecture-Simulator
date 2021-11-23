package com.arm.legv8simulator.client.cpu;

/**
 * <code>CPUSnpashot</code> provides a deep copy of the <code>CPU</code> state for use in the pipeline simulator.
 * 
 * @see CPU
 * 
 * @author Jonathan Wright, 2016
 */
public class CPUSnapshot {
	
	/**
	 * @param cpu	the <code>CPU</code> whose state is to be copied
	 */
	public CPUSnapshot(CPU cpu) {
		for (int i=0; i<registerFile.length; i++) {
			registerFile[i] = cpu.getRegister(i);
		}
		Nflag = cpu.getNflag();
		Zflag = cpu.getZflag();
		Cflag = cpu.getCflag();
		Vflag = cpu.getVflag();
	}
	
	/**
	 * @param index the register whose value to return, an integer in the range 0-31
	 * @return		the value stored in the register <code>index</code>
	 */
	public long getRegister(int index) {
		return registerFile[index];
	}
	
	/**
	 * @return	the value of the N flag
	 */
	public boolean getNflag() {
		return Nflag;
	}
	
	/**
	 * @return	the value of the Z flag
	 */
	public boolean getZflag() {
		return Zflag;
	}
	
	/**
	 * @return	the value of the C flag
	 */
	public boolean getCflag() {
		return Cflag;
	}
	
	/**
	 * @return	the value of the V flag
	 */
	public boolean getVflag() {
		return Vflag;
	}
	
	private long[] registerFile = new long[CPU.NUM_REGISTERS];
	private boolean Nflag;
	private boolean Zflag;
	private boolean Cflag;
	private boolean Vflag;
}
