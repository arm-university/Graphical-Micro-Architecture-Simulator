package com.arm.legv8simulator.client.executionmodes;

import java.util.ArrayList;

import com.arm.legv8simulator.client.cpu.CPU;
import com.arm.legv8simulator.client.cpu.CPUSnapshot;
import com.arm.legv8simulator.client.cpu.ControlUnitConfiguration;
import com.arm.legv8simulator.client.cpu.RegisterType;
import com.arm.legv8simulator.client.instruction.Instruction;
import com.arm.legv8simulator.client.instruction.Mnemonic;
import com.arm.legv8simulator.client.instruction.PipelineInstruction;
import com.arm.legv8simulator.client.lexer.TextLine;
import com.arm.legv8simulator.client.lexer.TokenType;

/**
 * The simulator used for the pipelined execution mode
 * 
 * @see LEGv8_Simulator
 * @author Jonathan Wright, 2016
 */
public class PipelinedSimulator extends LEGv8_Simulator {
	
		// All of the unused variables are control signal values - can be obtained from ControlUnitConfigureations class
		// will be necessary when visualising the pipeline
		
		// It could be argued strongly that each pipeline register should be represented by an 
		// object to make this a cleaner, more hierarchical structure!
		
		// values stored in IFID pipeline register
		private long IFID_PC4 = -1;
		private long IFID_Instruction = -1;
		private int IFID_Rn = -1;
		private int IFID_Rm = -1;
		private int IFID_Rd = -1;
		
		// values stored in IDEX pipeline register
		private boolean XIDEX_WB_RegWrite = false;
		private boolean XIDEX_WB_MemToReg = false;
		private boolean XIDEX_M_MemRead = false;
		private boolean XIDEX_M_MemWrite = false;
		private Integer XIDEX_EX_ALUOp = null;
		private boolean XIDEX_EX_ALUSrc = false;
		private Long XIDEX_ReadData1 = null;
		private Long XIDEX_ReadData2 = null;
		private int XIDEX_Rn = -1;
		private int XIDEX_Rm = -1;
		private int XIDEX_Rd = -1;
		
		/*private boolean DIDEX_WB_RegWrite = false;
		private boolean DIDEX_WB_MemToReg = false;
		private boolean DIDEX_M_MemRead = false;
		private boolean DIDEX_M_MemWrite = false;
		private Integer DIDEX_EX_ALUOp = null;
		private boolean DIDEX_EX_ALUSrc = false;
		private Double DIDEX_ReadData1 = null;
		private Double DIDEX_ReadData2 = null;
		private int DIDEX_Rn = -1;
		private int DIDEX_Rm = -1;
		private int DIDEX_Rd = -1;
		
		private boolean SIDEX_WB_RegWrite = false;
		private boolean SIDEX_WB_MemToReg = false;
		private boolean SIDEX_M_MemRead = false;
		private boolean SIDEX_M_MemWrite = false;
		private Integer SIDEX_EX_ALUOp = null;
		private boolean SIDEX_EX_ALUSrc = false;
		private Float SIDEX_ReadData1 = null;
		private Float SIDEX_ReadData2 = null;
		private int SIDEX_Rn = -1;
		private int SIDEX_Rm = -1;
		private int SIDEX_Rd = -1;*/
		
		// values stored in the EXMEM pipeline register
		private boolean EXMEM_WB_RegWrite = false;
		private boolean EXMEM_WB_MemToReg = false;
		private boolean EXMEM_M_MemRead =  false;
		private boolean EXMEM_M_MemWrite = false;
		private Long EXMEM_ALUResult = null;
		private Long EXMEM_WriteDataMem = null;
		private int EXMEM_Rd = -1;
		
		// values stored in the MEMWB pipeline refister
		private boolean MEMWB_WB_RegWrite = false;
		private boolean MEMWB_WB_MemToReg = false;
		private Long MEMWB_ReadDataMem = null;
		private Long MEMWB_ALUResult = null;
		private int MEMWB_Rd = -1;
		
		// data hazard variables
		private boolean dataHazardStall = false;
		private boolean branchDataHazardStall = false;
		private boolean EXHazard = false;
		private boolean MEMHazard = false;
		private boolean forwardA = false;
		private boolean forwardB = false;
		//control hazard variables
		private boolean controlHazardStall = false;
		private Instruction nextInstruction;
		
		private StringBuilder pipelineLog = new StringBuilder("");
		private CPUSnapshot visibleState;
		// array representing the pipeline
		private PipelineInstruction[] pipeline = new PipelineInstruction[PIPELINE_SIZE];
	
	public static final int PIPELINE_SIZE = 5;

	/**
	 * @param code	individual lines of source code from the text editor
	 */
	public PipelinedSimulator(ArrayList<TextLine> code) {
		super(code);
		visibleState = new CPUSnapshot(cpu);
	}

	/**
	 * Clocks the pipeline
	 */
	public void clock() {
		if (cpu.getInstructionIndex() == cpuInstructions.size()) {
			// if no more new instructions to insert into pipeline execute those left in pipeline; or if empty, do nothing
			pipelineLog.append("We are at the last instruction \n");
			currentLineNumber = cpuInstructions.get(cpuInstructions.size()-1).getLineNumber();
			if (!isPipelineEmpty()) {
				pipelineLog.append("The pipeline is not yet empty \n");
				if (dataHazardStallRequired()) {
					insertBubble(2);
				} else {
					pipelineLog.append("Moving the pipeline along \n");
					// insert bubble at IF position - no more instructions to insert
					updatePipeline(null);
				}
			} else 	pipelineLog.append("Execution has ended \n");
		} else {
			// come here if still new instructions to enter pipeline
			if (dataHazardStallRequired()) {
				insertBubble(2);
			} 
			else {
				if (pipeline[0] != null && pipeline[0].getBranchTaken()) {
					// inserts branch delay instruction to pipeline - will be flushed on next clock cycle
					updatePipeline(new PipelineInstruction(nextInstruction, null, null, pipeline[0].getPC()+4, false));
				} else {
					long currentPC = cpu.getPC();
					Instruction currentInstruction = cpuInstructions.get(cpu.getInstructionIndex());
					// keep track of next instruction incase needed as branch delay instruction
					if (cpu.getInstructionIndex()+1 < cpuInstructions.size()) {
						nextInstruction = cpuInstructions.get(cpu.getInstructionIndex()+1);
					} else {
						nextInstruction = null;
					}
					currentLineNumber = currentInstruction.getLineNumber();
					// record state before instruction executed 
					CPUSnapshot before = new CPUSnapshot(cpu);
					// execute instruction and catch runtime error if present - will be displayed in editor
					runtimeError = cpu.executeInstruction(cpuInstructions, memory);
					if (controlHazardStall && !branchDataHazardStall) {
						// flush IF stage of pipeline when branch taken
						pipeline[0] = null;
						controlHazardStall = false;
					}
					updatePipeline(new PipelineInstruction(currentInstruction, before, new CPUSnapshot(cpu), currentPC, cpu.getBranchTaken()));
				}
			}
		}
		// check for hazards
		detectControlHazard();
		detectDataHazard();
	}
	
	// true if there is a data hazard present
	private boolean dataHazardStallRequired() {
		return dataHazardStall || branchDataHazardStall;
	}
	
	// returns true if pipeline is empty
	private boolean isPipelineEmpty() {
		int nonEmptyCounter = 0;
		for(PipelineInstruction pi : pipeline) {
			if(pi != null)
				nonEmptyCounter++;
		}
		return nonEmptyCounter == 0;
	}
	
	// moves each instruction along one position in the pipeline and inserts the next instruction to the IF stage
	private void updatePipeline(PipelineInstruction nextInstruction) {
		for (int i=PIPELINE_SIZE-1; i>0; i--) {
			pipeline[i] = pipeline[i-1];
		}
		pipeline[0] = nextInstruction;
		if (pipeline[4] != null) {
			// update visible state to that of instruction in write-back stage
			visibleState = pipeline[4].getSnapshotAfter();
		}
		updatePipelineRegisters();
		logPipeline();
	}
	
	/*
	 * Inserts a stall at the specified position in the pipeline
	 * All instructions after this position move along one stage
	 */
	private void insertBubble(int pipelinePosition) {
		for (int i=PIPELINE_SIZE-1; i>pipelinePosition; i--) {
			pipeline[i] = pipeline[i-1];
		}
		pipeline[pipelinePosition] = null;
		if (pipeline[4] != null) {
			visibleState = pipeline[4].getSnapshotAfter();
		}
		updatePipelineRegisters();
		dataHazardStall = false;
		branchDataHazardStall = false;
		logPipeline();
	}
	
	/*
	 * Detects whether a control hazards is present
	 * If hazard present, set controlHazardStall = true;
	 */
	private void detectControlHazard() {
		if (pipeline[1] != null) {
			if (pipeline[1].getInstruction().getMnemonic().equals(Mnemonic.B)) {
				controlHazardStall = true;
				pipelineLog.append("Control Hazard: Flushing pipeline \n");
			} else if (pipeline[1].getInstruction().getMnemonic().type.equals(TokenType.MNEMONIC_L) || 
					pipeline[1].getInstruction().getMnemonic().type.equals(TokenType.XMNEMONIC_RL)) {
				if (pipeline[1].getBranchTaken()) {
					controlHazardStall = true;
					pipelineLog.append("Control Hazard: Flushing pipeline \n");
				}
			}
		}
	}
	
	/*
	 * Detects whether a data hazard is present
	 * If hazard present, set dataHazardStall = true 
	 */
	private void detectDataHazard() {
		if (ConditionalBranchHazard()) {
			branchDataHazardStall = true;
			pipelineLog.append("Data Hazard: Stalling pipeline \n");
		}
		if (BCondHazard()) {
			branchDataHazardStall = true;
			pipelineLog.append("Data Hazard: Stalling pipeline \n");
		}
		if (loadHazard()) {
			dataHazardStall = true;
			pipelineLog.append("Data Hazard: Stalling pipeline \n");
		}
		if (dataHazardEXRn()) {
			EXHazard = true;
			forwardA = true;
			pipelineLog.append("Data Hazard: EX, forward A \n");
		} else {
			EXHazard = false;
			forwardA = false;
		}
		if (dataHazardEXRm()) {
			EXHazard = true;
			forwardB = true;
			pipelineLog.append("Data Hazard: EX, forward B \n");
		} else {
			EXHazard = false;
			forwardB = false;
		}
		if (dataHazardMEMRn()) {
			MEMHazard = true;
			forwardA = true;
			pipelineLog.append("Data Hazard: MEM, forward A \n");
		} else {
			MEMHazard = false;
			forwardA = false;
		}
		if (dataHazardMEMRm()) {
			MEMHazard = true;
			forwardA = true;
			pipelineLog.append("Data Hazard: MEM, forward B \n");
		} else {
			MEMHazard = false;
			forwardA = false;
		}
	}
	
	/*
	 * Detect whether a data hazard is present when evaluating the condition register of a CBZ or CBNZ instruction
	 */
	private boolean ConditionalBranchHazard() {
		boolean hazardPresent = false;
		if (pipeline[1] != null) {
			if (pipeline[1].getInstruction().getMnemonic().type.equals(TokenType.XMNEMONIC_RL)) {
				hazardPresent = (pipeline[1].getInstruction().getArgs()[0] == XIDEX_Rd || 
						pipeline[1].getInstruction().getArgs()[0] == EXMEM_Rd);
			}
		}
		return hazardPresent;
	}
	
	/*
	 * Detects whether data hazard is present when evaluating the flag values for a B.cond instruction
	 * by determining whether there are instructions in the pipeline which are going to set the flags
	 * 
	 */
	private boolean BCondHazard() {
		boolean hazardPresent = false;
		if (pipeline[1] != null) {
			if (pipeline[1].getInstruction().getMnemonic().type.equals(TokenType.MNEMONIC_L) && 
					!pipeline[1].getInstruction().getMnemonic().equals(Mnemonic.B)) {
				if (pipeline[2] != null) {
					hazardPresent = setsFlags(pipeline[2].getInstruction().getMnemonic());
				}
				if (pipeline[3] != null) {
					hazardPresent = setsFlags(pipeline[3].getInstruction().getMnemonic());
				}
			}
		}
		return hazardPresent;
	}
	
	// returns true if the specified instruction sets the flag values
	private boolean setsFlags (Mnemonic m) {
		return (m.equals(Mnemonic.ADDS) || m.equals(Mnemonic.SUBS) || m.equals(Mnemonic.ANDS) || 
				m.equals(Mnemonic.ADDIS) || m.equals(Mnemonic.SUBIS) || m.equals(Mnemonic.ANDIS) ||
				m.equals(Mnemonic.FCMPD) || m.equals(Mnemonic.FCMPS) || m.equals(Mnemonic.CMP) || m.equals(Mnemonic.CMPI));
	}
	
	// returns true if register dependency exists for load instruction in EX stage of pipeline
	private boolean loadHazard() {
		boolean hazardPresent = false;
		if (pipeline[2] != null) {
			hazardPresent = pipeline[2].getInstruction().getControlSignals().equals(ControlUnitConfiguration.RM_LOAD) &&
					(XIDEX_Rd == IFID_Rn || XIDEX_Rd == IFID_Rm);
		}
		return hazardPresent;
	}
	
	// The logic conditions in the next four methods are defined in Hennessy and Patterson ARM edition pages 320, 322, and 324
	private boolean dataHazardEXRn() {
		return EXMEM_WB_RegWrite && EXMEM_Rd != CPU.XZR && EXMEM_Rd == XIDEX_Rn;
	}
	
	private boolean dataHazardEXRm() {
		return EXMEM_WB_RegWrite && EXMEM_Rd != CPU.XZR && EXMEM_Rd == XIDEX_Rm;
	}
	
	private boolean dataHazardMEMRn() {
		return MEMWB_WB_RegWrite && MEMWB_Rd != CPU.XZR && !(EXMEM_WB_RegWrite && EXMEM_Rd != CPU.XZR && EXMEM_Rd == XIDEX_Rn) && MEMWB_Rd == XIDEX_Rn;
	}
	
	private boolean dataHazardMEMRm() {
		return MEMWB_WB_RegWrite && MEMWB_Rd != CPU.XZR && !(EXMEM_WB_RegWrite && EXMEM_Rd != CPU.XZR && EXMEM_Rd == XIDEX_Rm) && MEMWB_Rd == XIDEX_Rm;
	}
	
	// Updates the values of all the data held in the four pipeline registers
	private void updatePipelineRegisters() {
		restoreDefaults();
		if (pipeline[1] != null) {
			updateIFID();
			//pipelineLog.append(logIFID());
		}
		if (pipeline[2] != null) {
			updateIDEX();
			//pipelineLog.append(logIDEX());
		}
		if (pipeline[3] != null) {
			updateEXMEM();
			//pipelineLog.append(logEXMEM());
		}
		if (pipeline[4] != null) {
			updateMEMWB();
			//pipelineLog.append(logMEMWB());
		}
	}
	
	// Sets default values for all data held in the pipeline register
	private void restoreDefaults() {
		IFID_PC4 = -1;
		IFID_Instruction = -1;
		IFID_Rn = -1;
		IFID_Rm = -1;
		IFID_Rd = -1;
		XIDEX_WB_RegWrite = false;
		XIDEX_WB_MemToReg = false;
		XIDEX_M_MemRead = false;
		XIDEX_M_MemWrite = false;
		XIDEX_EX_ALUOp = null;
		XIDEX_EX_ALUSrc = false;
		XIDEX_ReadData1 = null;
		XIDEX_ReadData2 = null;
		XIDEX_Rn = -1;
		XIDEX_Rm = -1;
		XIDEX_Rd = -1;
		EXMEM_WB_RegWrite = false;
		EXMEM_WB_MemToReg = false;
		EXMEM_M_MemRead =  false;
		EXMEM_M_MemWrite = false;
		EXMEM_ALUResult = null;
		EXMEM_WriteDataMem = null;
		EXMEM_Rd = -1;
		MEMWB_WB_RegWrite = false;
		MEMWB_WB_MemToReg = false;
		MEMWB_ReadDataMem = null;
		MEMWB_ALUResult = null;
		MEMWB_Rd = -1;
	}
	
	// Updates the values held in the IFID pipeline register 
	private void updateIFID() {
		IFID_PC4 = pipeline[1].getPC();
		TokenType insGroup = pipeline[1].getInstruction().getMnemonic().type;
		int[] a = pipeline[1].getInstruction().getArgs();
		switch (insGroup) {
		case XMNEMONIC_RRR :
			IFID_Rd = a[0];
			IFID_Rn = a[1];
			IFID_Rm = a[2];
			break;
		case XMNEMONIC_RRI :
			IFID_Rd = a[0];
			IFID_Rn = a[1];
			break;
		case XMNEMONIC_RM :
			if (pipeline[1].getInstruction().getControlSignals().equals(ControlUnitConfiguration.RM_LOAD)) {
				IFID_Rd = a[0];
				IFID_Rn = a[1];
			} else {
				IFID_Rn = a[1];
				IFID_Rm = a[0];
			}
			break;
		case XMNEMONIC_RRM :
			IFID_Rd = a[1];
			IFID_Rn = a[2];
			IFID_Rm = a[0];
			break;
		case XMNEMONIC_RISI :
			IFID_Rd = a[0];
			break;
		case XMNEMONIC_RL :
			IFID_Rm = a[0];
			break;
		default:
			break;
		}
	}
	
	// Updates the values held in the IDEX pipeline register
	private void updateIDEX() {
		ControlUnitConfiguration c = pipeline[2].getInstruction().getControlSignals();
		XIDEX_WB_RegWrite = c.regWrite;
		XIDEX_WB_MemToReg = c.memToReg;
		XIDEX_M_MemRead = c.memRead;
		XIDEX_M_MemWrite = c.memWrite;
		XIDEX_EX_ALUOp = c.aluOp;
		XIDEX_EX_ALUSrc = c.aluSrc;
		TokenType insGroup = pipeline[2].getInstruction().getMnemonic().type;
		int[] a = pipeline[2].getInstruction().getArgs();
		switch (insGroup) {
		case XMNEMONIC_RRR :
			XIDEX_Rd = a[0];
			XIDEX_Rn = a[1];
			XIDEX_Rm = a[2];
			XIDEX_ReadData1 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[1]);
			XIDEX_ReadData2 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[2]);
			break;
		case XMNEMONIC_RRI :
			XIDEX_Rd = a[0];
			XIDEX_Rn = a[1];
			XIDEX_ReadData1 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[1]);
			break;
		case XMNEMONIC_RM :
			if (XIDEX_WB_RegWrite) {
				XIDEX_Rd = a[0];
				XIDEX_Rn = a[1];
				XIDEX_ReadData1 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[1])+a[2];
			} else {
				XIDEX_Rn = a[1];
				XIDEX_Rm = a[0];
				XIDEX_ReadData1 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[1]);
				XIDEX_ReadData2 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[0]);
			}
			break;
		case XMNEMONIC_RRM :
			XIDEX_Rd = a[1];
			XIDEX_Rn = a[2];
			XIDEX_Rm = a[0];
			XIDEX_ReadData1 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[1]);
			XIDEX_ReadData2 = pipeline[2].getSnapshotBefore().getRegister(RegisterType.X, a[0]);
			break;
		case XMNEMONIC_RISI :
			XIDEX_Rd = a[0];
			break;
		case XMNEMONIC_RL :
			XIDEX_Rm = a[0];
			break;
		default:
			break;
		}
	}
	
	// Updates the values held in the EXMEM pipeline register
	private void updateEXMEM() {
		ControlUnitConfiguration c = pipeline[3].getInstruction().getControlSignals();
		EXMEM_WB_RegWrite = c.regWrite;
		EXMEM_WB_MemToReg = c.memToReg;
		EXMEM_M_MemRead = c.memRead;
		EXMEM_M_MemWrite = c.memWrite;
		int[] a = pipeline[3].getInstruction().getArgs();
		TokenType insGroup = pipeline[3].getInstruction().getMnemonic().type;
		switch (insGroup) {
		case XMNEMONIC_RRR :
			EXMEM_Rd = a[0];
			EXMEM_ALUResult = pipeline[3].getSnapshotAfter().getRegister(RegisterType.X, a[0]);
			break;
		case XMNEMONIC_RRI :
			EXMEM_Rd = a[0];
			EXMEM_ALUResult = pipeline[3].getSnapshotAfter().getRegister(RegisterType.X, a[0]);
			break;
		case XMNEMONIC_RM :
			if (EXMEM_WB_RegWrite) {
				EXMEM_Rd = a[0];
			} else {
				EXMEM_WriteDataMem = pipeline[3].getSnapshotBefore().getRegister(RegisterType.X, a[0]);
			}
			EXMEM_ALUResult = pipeline[3].getSnapshotBefore().getRegister(RegisterType.X, a[1])+a[2];
			break;
		case XMNEMONIC_RRM :
			EXMEM_ALUResult = pipeline[3].getSnapshotBefore().getRegister(RegisterType.X, a[2])+a[3];
			EXMEM_WriteDataMem = pipeline[3].getSnapshotBefore().getRegister(RegisterType.X, a[0]);
			EXMEM_Rd = a[1];
			break;
		case XMNEMONIC_RISI :
			EXMEM_Rd = a[0];
			EXMEM_ALUResult = pipeline[3].getSnapshotAfter().getRegister(RegisterType.X, a[0]);
			break;
		default:
			break;
		}
	}
	
	// Updates the values held in the MEMWB pipeline register
	private void updateMEMWB() {
		ControlUnitConfiguration c = pipeline[4].getInstruction().getControlSignals();
		MEMWB_WB_RegWrite = c.regWrite;
		MEMWB_WB_MemToReg = c.memToReg;
		int[] a = pipeline[4].getInstruction().getArgs();
		TokenType insGroup = pipeline[4].getInstruction().getMnemonic().type;
		switch (insGroup) {
		case XMNEMONIC_RRR :
			MEMWB_Rd = a[0];
			MEMWB_ALUResult = pipeline[4].getSnapshotAfter().getRegister(RegisterType.X, a[0]);
			break;
		case XMNEMONIC_RRI :
			MEMWB_Rd = a[0];
			MEMWB_ALUResult = pipeline[4].getSnapshotAfter().getRegister(RegisterType.X, a[0]);
			break;
		case XMNEMONIC_RM :
			if (MEMWB_WB_RegWrite) {
				MEMWB_Rd = a[0];
				MEMWB_ReadDataMem = pipeline[4].getSnapshotAfter().getRegister(RegisterType.X, a[0]);
			}
			MEMWB_ALUResult = pipeline[4].getSnapshotBefore().getRegister(RegisterType.X, a[1])+a[2];
			break;
		case XMNEMONIC_RRM :
			MEMWB_ALUResult = pipeline[4].getSnapshotBefore().getRegister(RegisterType.X, a[2])+a[3];
			MEMWB_ReadDataMem = pipeline[4].getSnapshotAfter().getRegister(RegisterType.X, a[1]);
			MEMWB_Rd = a[1];
			break;
		case XMNEMONIC_RISI :
			MEMWB_Rd = a[0];
			MEMWB_ALUResult = pipeline[4].getSnapshotAfter().getRegister(RegisterType.X, a[0]);
			break;
		default:
			break;
		}
	}
	
	@Override
	public long getCPURegister(RegisterType type, int index) {
		return visibleState.getRegister(type, index);
	}
	
	@Override
	public boolean getCPUZflag() {
		return visibleState.getZflag();
	}
	
	@Override
	public boolean getCPUNflag() {
		return visibleState.getNflag();
	}
	
	@Override
	public boolean getCPUCflag() {
		return visibleState.getCflag();
	}
	
	@Override
	public boolean getCPUVflag() {
		return visibleState.getVflag();
	}
	
	@Override
	public long getPC() {
		if (pipeline[0] != null && pipeline[0].getSnapshotBefore() != null) {
			return pipeline[0].getPC()+4;
		} else {
			return cpu.getPC();
		}
	}
	
	/**
	 * @return the contents of the pipeline log, useful for debugging
	 */
	public String getPipelineLog() {
		return pipelineLog.toString();
	}
	
	private String logIFID() {
		return "IFID_PC: " + IFID_PC4 + ", IFID_Instruction: " + IFID_Instruction + "\n";
	}
	
	private String logIDEX() {
		return "IDEX_WB_RegWrite: " + XIDEX_WB_RegWrite + ", IDEX_Rd: " + XIDEX_Rd + ", IDEX_Rn: " + XIDEX_Rn + ", IDEX_Rm: " + XIDEX_Rm + "\n";
	}
	
	private String logEXMEM() {
		return "EXMEM_WB_RegWrite: " + EXMEM_WB_RegWrite + ", EXMEM_Rd: " + EXMEM_Rd + "\n";
	}
	
	private String logMEMWB() {
		return "MEMWB_WB_RegWrite: " + MEMWB_WB_RegWrite + ", MEMWB_Rd: " + MEMWB_Rd + "\n";
	}
	
	// Creates a string representation of the pipeline
	private void logPipeline() {
		for (int i=0; i<PIPELINE_SIZE; i++) {
			if (pipeline[i] != null && pipeline[i].getInstruction() != null) {
				pipelineLog.append(pipeline[i].getInstruction().getMnemonic().nameUpper + " | ");
			} else {
				pipelineLog.append("bubble | ");
			}
		}
		pipelineLog.append("\n");
	}
	
	
}
