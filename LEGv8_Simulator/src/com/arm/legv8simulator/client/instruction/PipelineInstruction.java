package com.arm.legv8simulator.client.instruction;

import com.arm.legv8simulator.client.cpu.CPU;
import com.arm.legv8simulator.client.cpu.CPUSnapshot;

/**
 * <code>PipelineInstruction</code> is a wrapper class for <code>Instruction</code>, used when executing in the 
 * pipelined simulation mode. The extra state is required so that registers and flags can be updated in the 
 * write-back stage rather than when the underlying single cycle <code>CPU</code> executes the instruction. 
 * 
 * @see Instruction
 * @see CPUSnpashot
 * @see CPU
 * 
 * @author Jonathan Wright, 2016
 */
public class PipelineInstruction {

	/**
	 * @param instruction	the <code>Instruction</code> to be executed
	 * @param before		the CPU state before <code>instruction</code> is executed
	 * @param after			the CPU state after <code>instruction</code> is executed
	 * @param PC			the value of the PC, which points to <code>instruction</code>
	 * @param branchTaken	whether a branch was taken on this instruction			
	 * 
	 * @see Instruction
	 * @see CPUSnapshot
	 */
	public PipelineInstruction(Instruction instruction, CPUSnapshot before, CPUSnapshot after, long PC, boolean branchTaken) {
		this.instruction = instruction;
		this.before = before;
		this.after = after;
		this.PC = PC;
		this.branchTaken = branchTaken;
	}
	
	/**
	 * @return	a reference to the <code>Instruction</code> object wrapped by this <code>PpiplineInstruction</code>
	 */
	public Instruction getInstruction() {
		return instruction;
	}
	
	/**
	 * @return	 a reference to the <code>CPUSnapshot</code> before this instruction is executed
	 */
	public CPUSnapshot getSnapshotBefore() {
		return before;
	}
	
	/**
	 * @return	a reference to the <code>CPUSnapshot</code> after this instruction is executed
	 */
	public CPUSnapshot getSnapshotAfter() {
		return after;
	}
	
	/**
	 * @return	the value of the PC as this <code>PipleineInstruction</code> entered the pipeline
	 */
	public long getPC() {
		return PC;
	}
	
	/**
	 * @return	whether a branch was taken on this instruction 
	 */
	public boolean getBranchTaken() {
		return branchTaken;
	}
	
	private Instruction instruction;
	private CPUSnapshot before;
	private CPUSnapshot after;
	private long PC;
	private boolean branchTaken; 
}
