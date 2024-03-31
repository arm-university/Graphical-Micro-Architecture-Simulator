package com.arm.legv8simulator.client.executionmodes;

import java.util.ArrayList;

import com.arm.legv8simulator.client.instruction.Instruction;
import com.arm.legv8simulator.client.lexer.TextLine;

/**
 * The simulator used for the simulation and single cycle visual execution modes
 * 
 * @see LEGv8_Simulator
 * @author Jonathan Wright, 2016
 */
public class SingleCycleSimulator extends LEGv8_Simulator {

	/**
	 * @param code	individual lines of source code from the text editor
	 */
	public SingleCycleSimulator(ArrayList<TextLine> code) {
		super(code);
	}

	/**
	 * Executes a single instruction updating the CPU state and the current instruction index
	 */
	public void executeInstruction() {
		previousInstructionIndex = currentInstructionIndex;
		currentInstructionIndex = cpu.getInstructionIndex();
		previousInstruction = currentInstruction;
		currentInstruction = cpuInstructions.get(cpu.getInstructionIndex());
		currentLineNumber = cpuInstructions.get(cpu.getInstructionIndex()).getLineNumber();
		runtimeError = cpu.executeInstruction(cpuInstructions, memory);
	}
	
	/**
	 * @return	the index of the instruction executed on the 2nd to last call to 
	 * <code>executeInstruction</code>, 0 if never previously called.
	 */
	public int getPreviousInsIndex() {
		return previousInstructionIndex;
	}
	
	/**
	 * @return	the index of the instruction executed on the last call to 
	 * <code>executeInstruction</code>, 0 if never previously called.
	 */
	public int getCurrentInsIndex() {
		return currentInstructionIndex;
	}
	
	/**
	 * @return	the instruction executed on the 2nd to last call to <code>executeInstruction</code>,
	 * <code>null</code> if never previously called.
	 */
	public Instruction getPreviousInstruction() {
		return previousInstruction;
	}
	
	/**
	 * @return	the instruction executed on the last call to <code>executeInstruction</code>, 
	 * <code>null</code> if never previously called.
	 */
	public Instruction getCurrentInstruction() {
		return currentInstruction;
	}
	
	/**
	 * @return	<code>true</code> if the last instruction executed by the <code>CPU</code> 
	 * was a branch instruction and the branch was taken. <code>false</code> otherwise.
	 */
	public boolean getBranchTaken() {
		return cpu.getBranchTaken();
	}
	
	/**
	 * @return	<code>true</code> if the last instruction executed by the <code>CPU</code> 
	 * was STXR and the store to memory succeeded. <code>false</code> otherwise.
	 */
	public boolean getSTXRSucceed() {
		return cpu.getSTXRSucceed();
	}
	
	private int currentInstructionIndex = 0;
	private int previousInstructionIndex = 0;
	private Instruction currentInstruction;
	private Instruction previousInstruction;
}
