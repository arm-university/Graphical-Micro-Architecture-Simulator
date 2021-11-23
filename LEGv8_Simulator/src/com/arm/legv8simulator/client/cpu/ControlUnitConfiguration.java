package com.arm.legv8simulator.client.cpu;

/**
 * The <code>ControlUnitConfiguration</code> enumeration defines the control signal values 
 * necessary to execute each different instruction type. A description of each control 
 * signal can be found in Patterson and Hennessy ARM Edition.
 * <p>
 * See LEGv8_Grammar.ppt for a description of each instruction group.
 * 
 * @author Jonathan Wright, 2016
 */
public enum ControlUnitConfiguration {
	
	RRR(false, false, false, false, false, false, false, false, false, 2, true),
	RRR_FLAGS(false, false, false, false, false, false, false, true, false, 2, true),
	RRI(false, false, false, false, false, false, false, false, true, 2, true),
	RRI_FLAGS(false, false, false, false, false, false, false, true, true, 2, true),
	RM_LOAD(null, false, false, false, false, true, true, false, true, 0, true),
	RM_STORE(true, false, false, false, false, null, true, false, false, 0, false),
	RRM(true, false, false, false, true, true, true, false, true, 0, true),
	RISI(false, false, false, false, false, false, false, false, true, 2, true),
	L(null, true, false, false, false, null, false, false, null, null, false),
	L_COND(null, false, true, false, false, null, false, false, null, null, false),
	RL(true, false, false, true, false, null, false, false, false, 1, false);
	
	/**
	 * @param reg2Loc		the value of the Reg2Loc control signal
	 * @param uncondBranch	the value of the UncondBranch control signal
	 * @param flagBranch	the value of the FlagBranch control signal
	 * @param zeroBranch	the value of the ZeroBranch control signal
	 * @param memRead		the value of the MemRead control signal
	 * @param memToReg		the value of the MemToReg control signal
	 * @param memWrite		the value of the MemWrite control signal
	 * @param flagWrite		the value of the FlagWrite control signal
	 * @param aluSrc		the value of the ALUSrc control signal
	 * @param aluOp			the value of the ALUOp control signal
	 * @param regWrite		the value of the RegWrite control signal
	 */
	private ControlUnitConfiguration(Boolean reg2Loc, Boolean uncondBranch, Boolean flagBranch, 
			Boolean zeroBranch, Boolean memRead, Boolean memToReg, Boolean memWrite,
			Boolean flagWrite, Boolean aluSrc, Integer aluOp, Boolean regWrite) {
		this.reg2Loc = reg2Loc;
		this.uncondBranch = uncondBranch;
		this.flagBranch = flagBranch;
		this.zeroBranch = zeroBranch;
		this.memRead = memRead;
		this.memToReg = memToReg;
		this.memWrite = memWrite;
		this.flagWrite = flagWrite;
		this.aluSrc = aluSrc;
		this.aluOp = aluOp;
		this.regWrite = regWrite;
	}
	
	/**
	 * @param signal	a control signal
	 * @return			a string representing the value of <code>signal</code>; 
	 * 					<code>"1"</code> if set, <code>"0"</code> if not
	 */
	public static String toString(Boolean signal) {
		if (signal == null) {
			return "";
		} else if (signal) {
			return "1";
		} else {
			return "0";
		}
	}
	
	/**
	 * @param aluOp	the value of the <code>aluOp</code> control signal
	 * @return		a string representing the 2-bit binary value of <code>aluOp</code>
	 */
	public static String toString(Integer aluOp) {
		if (aluOp == null) return ""; // this is very ugly... sorry if you are reading this :'(
		switch (aluOp) {
			case 0 : return "00";
			case 1 : return "01";
			case 2 : return "10";
			case 3 : return "11";
			default : return "";
		}
	}
	
	/**
	 * The value of the Reg2Loc control signal
	 */
	public final Boolean reg2Loc;
	
	/**
	 * The value of the UncondBranch control signal
	 */
	public final Boolean uncondBranch;
	
	/**
	 * The value of the FlagBranch control signal
	 */
	public final Boolean flagBranch;
	
	/**
	 * The value of the ZeroBranch control signal
	 */
	public final Boolean zeroBranch;
	
	/**
	 * The value of the MemRead control signal
	 */
	public final Boolean memRead;
	
	/**
	 * The value of the MemToReg control signal
	 */
	public final Boolean memToReg;
	
	/**
	 * The value of the MemWrite control signal
	 */
	public final Boolean memWrite;
	
	/**
	 * The value of the FlagWrite control signal
	 */
	public final Boolean flagWrite;
	
	/**
	 * The value of the ALUSrc control signal
	 */
	public final Boolean aluSrc;
	
	/**
	 * The value of the ALUOp control signal
	 */
	public final Integer aluOp;
	
	/**
	 * The value of the RegWrite control signal
	 */
	public final Boolean regWrite;
}
