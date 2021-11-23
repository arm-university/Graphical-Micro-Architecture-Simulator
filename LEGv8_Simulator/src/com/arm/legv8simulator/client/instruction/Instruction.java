package com.arm.legv8simulator.client.instruction;

import com.arm.legv8simulator.client.cpu.ControlUnitConfiguration;

/**
 * An <code>Instruction</code> object is used to represent each LEGv8 instruction in the user's program.
 * <p>
 * The <code>CPU</code> class executes the operation defined by each <code>Instruction</code>
 * 
 * @see CPU
 * 
 * @author Jonathan Wright, 2016
 */
public class Instruction {
	
	/**
	 * @param mnemonic			the instruction mnemonic
	 * @param args				the array of arguments for this instruction i.e. register indices and immediates
	 * @param editorLineNumber	the line in the code editor of this instruction
	 * @param controlSignals	the control signals required to execute this instruction
	 * 
	 * @see Mnemonic
	 * @see ControlUnitConfiguration
	 * @see CPU
	 */
	public Instruction(Mnemonic mnemonic, int[] args, int editorLineNumber, 
			ControlUnitConfiguration controlSignals) {
		this.mnemonic = mnemonic;
		this.args = args;
		this.editorLineNumber = editorLineNumber;
		this.controlSignals = controlSignals;
	}
	
	/**
	 * @return	the instruction mnemonic
	 */
	public Mnemonic getMnemonic() {
		return mnemonic;
	}
	
	/**
	 * @return	the array of arguments for this instruction i.e. registers and immediates
	 */
	public int[] getArgs() {
		return args;
	}
	
	/**
	 * @return	the line in the code editor of this instruction
	 */
	public int getLineNumber() {
		return editorLineNumber;
	}
	
	/**
	 * @return	the control signals required to execute this instruction
	 */
	public ControlUnitConfiguration getControlSignals() {
		return controlSignals;
	}
	
	private Mnemonic mnemonic;
	private int[] args;
	private int editorLineNumber;
	private ControlUnitConfiguration controlSignals;
}
