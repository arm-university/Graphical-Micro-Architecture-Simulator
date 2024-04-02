package com.arm.legv8simulator.client;

import com.arm.legv8simulator.client.cpu.ControlUnitConfiguration;
import com.arm.legv8simulator.client.instruction.Instruction;
import com.arm.legv8simulator.client.instruction.Mnemonic;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


// TODO
// Look at SingleCycleVis.java for an example of a finished implementation of a dynamically updating datapath 


/**
 * Draws the LEGv8 pipeline datapath as defined in Patterson and Hennessy ARM Edition.
 * 
 * @author Jonathan Wright, 2016
 */
public class PipelineVis {
	
	private static final int CANVAS_HEIGHT_REF = 720;
	private static final int CANVAS_WIDTH_REF = 960;
	
	private static final double[] MUX_PC_DIMENSIONS = {20, 60};
	private static final double[] MUX_PC_COORDS = {25, 360};
	private static final double[] PC_DIMENSIONS = {25, 40};
	private static final double[] PC_COORDS = {MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]+15, MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[1]/2-PC_DIMENSIONS[1]/2};
	private static final double[] INS_MEM_DIMENSIONS = {80, 120};
	private static final double[] INS_MEM_COORDS = {PC_COORDS[0]+PC_DIMENSIONS[0]+25, PC_COORDS[1]+PC_DIMENSIONS[1]/2-INS_MEM_DIMENSIONS[1]/10};
	private static final double[] ALU_PC_DIMENSIONS = {40, 65};
	private static final double[] ALU_PC_COORDS = {INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]/2-ALU_PC_DIMENSIONS[0]/2, INS_MEM_COORDS[1]-1.25*INS_MEM_DIMENSIONS[1]};
	private static final double[] IFID_REG_DIMENSIONS = {15, 400};
	private static final double[] IFID_REG_COORDS = {INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+15, INS_MEM_COORDS[1]+INS_MEM_DIMENSIONS[1]/2-IFID_REG_DIMENSIONS[1]/2};
	
	private static final double[] REG_FILE_DIMENSIONS = {110, 140};
	private static final double[] REG_FILE_COORDS = {330, 340};
	private static final double[] FLAGS_DIMENSIONS = {88, 20};
	private static final double[] FLAGS_COORDS = {REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]/2-FLAGS_DIMENSIONS[0]/2, REG_FILE_COORDS[1]+REG_FILE_DIMENSIONS[1]+10};
	private static final double[] MUX_REG2LOC_DIMENSIONS = {20, 60};
	private static final double[] MUX_REG2LOC_COORDS = {IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0]+32.5, REG_FILE_COORDS[1]+4*REG_FILE_DIMENSIONS[1]/10-MUX_REG2LOC_DIMENSIONS[1]/2};
	private static final double[] ALU_BRANCH_DIMENSIONS = {40, 65};
	private static final double[] ALU_BRANCH_COORDS = {REG_FILE_COORDS[0], ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2-13*ALU_BRANCH_DIMENSIONS[1]/16};
	private static final double[] SIGN_EXTEND_DIMENSIONS = {52.5, 60};
	private static final double[] SIGN_EXTEND_COORDS = {FLAGS_COORDS[0]-3*SIGN_EXTEND_DIMENSIONS[0]/4, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]+15};
	private static final double[] SHIFT_LEFT2_DIMENSIONS = {45, 50};
	private static final double[] SHIFT_LEFT2_COORDS = {REG_FILE_COORDS[0]-SHIFT_LEFT2_DIMENSIONS[0], REG_FILE_COORDS[1]-SHIFT_LEFT2_DIMENSIONS[1]};
	private static final double[] ZERO_TEST_DIMENSIONS = {20, 30};
	private static final double[] ZERO_TEST_COORDS = {REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+5, REG_FILE_COORDS[1]+REG_FILE_DIMENSIONS[1]/2-ZERO_TEST_DIMENSIONS[1]};
	private static final double[] BRANCH_OR_DIMENSIONS = {25, 30};
	private static final double[] BRANCH_OR_COORDS = {425, 10};
	private static final double[] FLAG_AND_DIMENSIONS = {20, 20};
	private static final double[] FLAG_AND_COORDS = {443, BRANCH_OR_COORDS[1]+100};
	private static final double[] ZERO_AND_DIMENSIONS = {20, 20};
	private static final double[] ZERO_AND_COORDS = {412, BRANCH_OR_COORDS[1]+100};
	private static final double[] IDEX_REG_DIMENSIONS = {15, 400};
	private static final double[] IDEX_REG_COORDS = {ZERO_TEST_COORDS[0]+ZERO_TEST_DIMENSIONS[0]+15, IFID_REG_COORDS[1]};
	private static final double[] IDEX_EX_DIMENSIONS = {15, 30};
	private static final double[] IDEX_EX_COORDS = {IDEX_REG_COORDS[0], IDEX_REG_COORDS[1]-IDEX_EX_DIMENSIONS[1]};
	private static final double[] IDEX_M_DIMENSIONS = {15, 30};
	private static final double[] IDEX_M_COORDS = {IDEX_REG_COORDS[0], IDEX_EX_COORDS[1]-IDEX_M_DIMENSIONS[1]};
	private static final double[] IDEX_WB_DIMENSIONS = {15, 30};
	private static final double[] IDEX_WB_COORDS = {IDEX_REG_COORDS[0], IDEX_M_COORDS[1]-IDEX_WB_DIMENSIONS[1]};
	private static final double[] CONTROL_DIMENSIONS = {60, 100};
	private static final double[] CONTROL_COORDS = {MUX_REG2LOC_COORDS[0], 125};
	private static final double[] HAZARD_DETECT_DIMENSIONS = {1.2*CONTROL_DIMENSIONS[0], 40};
	private static final double[] HAZARD_DETECT_COORDS = {CONTROL_COORDS[0]-5, CONTROL_COORDS[1]-50};
	private static final double[] MUX_CONTROL_DIMENSIONS = {20, 60};
	private static final double[] MUX_CONTROL_COORDS = {ZERO_AND_COORDS[0]-MUX_CONTROL_DIMENSIONS[0], IDEX_M_COORDS[1]-10};
	
	private static final double[] MUX_FORWARD_A_DIMENSIONS = {20, 60};
	private static final double[] MUX_FORWARD_A_COORDS = {IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0]+50, REG_FILE_COORDS[1]+REG_FILE_DIMENSIONS[1]/5-MUX_FORWARD_A_DIMENSIONS[0]/2};
	private static final double[] ALU_MAIN_DIMENSIONS = {50, 135};
	private static final double[] ALU_MAIN_COORDS = {650, MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[1]/2-3*ALU_MAIN_DIMENSIONS[1]/16};
	private static final double[] MUX_READ_REG_DIMENSIONS = {20, 60};
	private static final double[] MUX_READ_REG_COORDS = {ALU_MAIN_COORDS[0]-40, ALU_MAIN_COORDS[1]+13*ALU_MAIN_DIMENSIONS[1]/16-MUX_READ_REG_DIMENSIONS[1]/2};
	private static final double[] MUX_FORWARD_B_DIMENSIONS = {20, 60};
	private static final double[] MUX_FORWARD_B_COORDS = {MUX_FORWARD_A_COORDS[0], MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[0]/2-MUX_FORWARD_B_DIMENSIONS[1]/2};
	private static final double[] ALU_CONTROL_DIMENSIONS = {55, 70};
	private static final double[] ALU_CONTROL_COORDS = {MUX_READ_REG_COORDS[0], SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2-ALU_CONTROL_DIMENSIONS[1]/2};
	private static final double[] FORWARD_UNIT_DIMENSIONS = {1.2*CONTROL_DIMENSIONS[0], 35};
	private static final double[] FORWARD_UNIT_COORDS = {ALU_CONTROL_COORDS[0]-20, ALU_CONTROL_COORDS[1]+1.7*ALU_CONTROL_DIMENSIONS[1]};
	private static final double[] EXMEM_REG_DIMENSIONS = {15, 400};
	private static final double[] EXMEM_REG_COORDS = {ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]+20, IFID_REG_COORDS[1]};
	private static final double[] EXMEM_M_DIMENSIONS = {15, 30};
	private static final double[] EXMEM_M_COORDS = {EXMEM_REG_COORDS[0], EXMEM_REG_COORDS[1]-EXMEM_M_DIMENSIONS[1]};
	private static final double[] EXMEM_WB_DIMENSIONS = {15, 30};
	private static final double[] EXMEM_WB_COORDS = {EXMEM_REG_COORDS[0], EXMEM_M_COORDS[1]-EXMEM_WB_DIMENSIONS[1]};
	
	private static final double[] DATA_MEM_DIMENSIONS = {90, 130};
	private static final double[] DATA_MEM_COORDS = {EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0]+40, 400};
	private static final double[] MEMWB_REG_DIMENSIONS = {15, 400};
	private static final double[] MEMWB_REG_COORDS = {DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]+25, IFID_REG_COORDS[1]};
	private static final double[] MEMWB_WB_DIMENSIONS = {15, 30};
	private static final double[] MEMWB_WB_COORDS = {MEMWB_REG_COORDS[0], MEMWB_REG_COORDS[1]-MEMWB_WB_DIMENSIONS[1]};
	
	private static final double[] MUX_READ_DATA_MEM_DIMENSIONS = {20, 60};
	private static final double[] MUX_READ_DATA_MEM_COORDS = {MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0]+25, 
			MEMWB_REG_COORDS[1]+MEMWB_REG_DIMENSIONS[1]/2-MUX_READ_DATA_MEM_DIMENSIONS[1]/2};
	
	
//	private static final double PC_PCALU_VERTICAL_X = PC_COORDS[0]+PC_DIMENSIONS[0]+(INS_MEM_COORDS[0]-PC_COORDS[0]-PC_DIMENSIONS[0])/3;
	private static final double IFID_VERTICAL_X = IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0]+(MUX_REG2LOC_COORDS[0]-IFID_REG_COORDS[0]-IFID_REG_DIMENSIONS[0])/4;
//	private static final double SHIFT2VERT_X = REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+3*(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5;
//	private static final double ZERO_AND_VERT_X = ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]+4*(DATA_MEM_COORDS[0]-ALU_MAIN_COORDS[0]-ALU_MAIN_DIMENSIONS[0])/5;
	
	private static final double[] INSTRUCTION_TEXT_COORDS = {PC_COORDS[0], ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1]};
	
	/**
	 * 
	 * @param canvasWidth	the width of this pipeline datapath
	 * @param canvasHeight	the height of this pipeline datapath
	 */
	public PipelineVis(double canvasWidth, double canvasHeight) {
		canvas = Canvas.createIfSupported();
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		init();
	}
	
	/**
	 * Draws the pipeline datapath without any wire/component highlighting
	 */
	public void init(){
		canvas.setWidth(canvasWidth + "px");
		canvas.setHeight(canvasHeight + "px");
		canvas.setCoordinateSpaceWidth((int) canvasWidth);
		canvas.setCoordinateSpaceHeight((int) canvasHeight);
		ctx = canvas.getContext2d();
		ctx.scale((double)canvasWidth/CANVAS_WIDTH_REF, (double)canvasHeight/CANVAS_HEIGHT_REF);
		datapathInit();
	}
	
	/**
	 * @return	the HTML5 Canvas element on which the datapath is drawn
	 */
	public Canvas getCanvas() {
		return canvas;
	}
	
	/**
	 * @return	the context of this HTML5 canvas
	 */
	public Context2d getContext() {
		return ctx;
	}
	
	
	public void updateDatapath(Instruction ins, boolean branchTacken, boolean stxrSucceed, int instructionIndex, String label) {
		// TODO
		clearCanvas();
		Mnemonic m = ins.getMnemonic();
		switch(m.type) {
		case MNEMONIC_RRR :
			drawDatapathRRR(m.equals(Mnemonic.ADDS)||m.equals(Mnemonic.SUBS)||m.equals(Mnemonic.ANDS), m);
			break;
		case MNEMONIC_RRI :
			break;
		case MNEMONIC_RISI :
			break;
		case MNEMONIC_RM :
			break;
		case MNEMONIC_RRM :
			break;
		case MNEMONIC_RL :
			break;
		case MNEMONIC_L :
			break;
		
		}
	}
	
	private void drawDatapathRRR(boolean flags, Mnemonic m) {
		drawDataWiresRRR(flags);
		drawControlSignals();
		drawComponentsRRR(flags);
		drawStringsInit();
	}
	
	private void drawDataWiresRRR(boolean flags) {
		
	}
	
	private void drawComponentsRRR(boolean flags) {
		
	}
	
	private void drawStringsInit() {
		
	}
	
	private void drawInstructionString(String instruction, String[][] fields) {
		double xPos = INSTRUCTION_TEXT_COORDS[0];
		double yPos = INSTRUCTION_TEXT_COORDS[1];
		ctx.setFont("bold 13px arial");
		ctx.setFillStyle(DatapathGraphics.BLACK);
		ctx.fillText(instruction, xPos, yPos-20);
		TextMetrics t;
		for (int i=0; i<fields.length; i++) {
			ctx.setFont("13px arial");
			ctx.setFillStyle(DatapathGraphics.ARM_BLUE);
			t = ctx.measureText(fields[i][0]);
			double f0Width = t.getWidth();
			ctx.fillText(fields[i][0], xPos, yPos);
			t = ctx.measureText(fields[i][1]);
			ctx.setFillStyle(DatapathGraphics.BLACK);
			ctx.fillText(fields[i][1], (xPos+xPos+f0Width)/2.0-t.getWidth()/2.0, yPos+17.5);
			xPos += f0Width + 7.5;
		}
	}
	
	private void drawInstructionTextRRR(Instruction ins) {
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(getRegString(ins.getArgs()[0]) + ", ");
		instruction.append(getRegString(ins.getArgs()[1]) + ", ");
		instruction.append(getRegString(ins.getArgs()[2]));
		String[][] fields = new String[5][2];
		fields[0][0] = ins.getMnemonic().opcode;
		fields[0][1] = "Opcode";
		fields[1][0] = getRegBinary(ins.getArgs()[2]);
		fields[1][1] = "Rm";
		fields[2][0] = getImmBinary(0, 6, false);
		fields[2][1] = "shamt";
		fields[3][0] = getRegBinary(ins.getArgs()[1]);
		fields[3][1] = "Rn";
		fields[4][0] = getRegBinary(ins.getArgs()[0]);
		fields[4][1] = "Rd";
		drawInstructionString(instruction.toString(), fields);
	}
	
	private void drawInstructionTextShift(Instruction ins) {
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(getRegString(ins.getArgs()[0]) + ", ");
		instruction.append(getRegString(ins.getArgs()[1]) + ", ");
		instruction.append("#" + ins.getArgs()[2]);
		String[][] fields = new String[5][2];
		fields[0][0] = ins.getMnemonic().opcode;
		fields[0][1] = "Opcode";
		fields[1][0] = getImmBinary(0, 5, false);
		fields[1][1] = "Rm";
		fields[2][0] = getImmBinary(ins.getArgs()[2], 6, false);
		fields[2][1] = "shamt";
		fields[3][0] = getRegBinary(ins.getArgs()[1]);
		fields[3][1] = "Rn";
		fields[4][0] = getRegBinary(ins.getArgs()[0]);
		fields[4][1] = "Rd";
		drawInstructionString(instruction.toString(), fields);
	}
	
	private void drawInstructionTextRRI(Instruction ins) {
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(getRegString(ins.getArgs()[0]) + ", ");
		instruction.append(getRegString(ins.getArgs()[1]) + ", ");
		instruction.append("#" + ins.getArgs()[2]);
		String[][] fields = new String[4][2];
		fields[0][0] = ins.getMnemonic().opcode;
		fields[0][1] = "Opcode";
		fields[1][0] = getImmBinary(ins.getArgs()[2], 12, false);
		fields[1][1] = "ALU_immediate";
		fields[2][0] = getRegBinary(ins.getArgs()[1]);
		fields[2][1] = "Rn";
		fields[3][0] = getRegBinary(ins.getArgs()[0]);
		fields[3][1] = "Rd";
		drawInstructionString(instruction.toString(), fields);
	}
	
	private void drawInstructionTextRM(Instruction ins) {
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(getRegString(ins.getArgs()[0]) + ", [");
		instruction.append(getRegString(ins.getArgs()[1]) + ", ");
		instruction.append("#" + ins.getArgs()[2] + "]");
		String[][] fields = new String[5][2];
		fields[0][0] = ins.getMnemonic().opcode;
		fields[0][1] = "Opcode";
		fields[1][0] = getImmBinary(ins.getArgs()[2], 12, true);
		fields[1][1] = "DT_address";
		fields[2][0] = "00";
		fields[2][1] = "op2";
		fields[3][0] = getRegBinary(ins.getArgs()[1]);
		fields[3][1] = "Rn";
		fields[4][0] = getRegBinary(ins.getArgs()[0]);
		fields[4][1] = "Rt";
		drawInstructionString(instruction.toString(), fields);
	}
	
	private void drawInstructionTextRISI(Instruction ins) {
		String shift = "";
		switch (ins.getArgs()[2]) {
			case 0 : shift = "00";
			break;
			case 16 : shift = "01";
			break;
			case 32 : shift = "10";
			break;
			case 48 : shift = "11";
			break;
		}
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(getRegString(ins.getArgs()[0]) + ", ");
		instruction.append("#" + ins.getArgs()[1] + ", LSL");
		instruction.append("#" + ins.getArgs()[2]);
		String[][] fields = new String[3][2];
		fields[0][0] = ins.getMnemonic().opcode + shift;
		fields[0][1] = "Opcode";
		fields[1][0] = getImmBinary(ins.getArgs()[1], 16, false);
		fields[1][1] = "MOV_immediate";
		fields[2][0] = getRegBinary(ins.getArgs()[0]);
		fields[2][1] = "Rd";
		drawInstructionString(instruction.toString(), fields);
	}
	
	private void drawInstructionTextRL(Instruction ins, int instructionIndex, String label) {
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(getRegString(ins.getArgs()[0]) + ", ");
		instruction.append(label);
		String[][] fields = new String[3][2];
		fields[0][0] = ins.getMnemonic().opcode;
		fields[0][1] = "Opcode";
		fields[1][0] = getImmBinary(ins.getArgs()[1]-instructionIndex, 19, true);
		fields[1][1] = "COND_BR_address";
		fields[2][0] = getRegBinary(ins.getArgs()[0]);
		fields[2][1] = "Rt";
		drawInstructionString(instruction.toString(), fields);
	}
	
	private void drawInstructionTextL(Instruction ins, int instructionIndex, String label) {
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(label);
		String[][] fields = new String[2][2];
		fields[0][0] = ins.getMnemonic().opcode;
		fields[0][1] = "Opcode";
		fields[1][0] = getImmBinary(ins.getArgs()[0]-instructionIndex, 26, true);
		fields[1][1] = "BR_address";
		drawInstructionString(instruction.toString(), fields);
	}
	
	private String getRegString(int regNum) {
		switch (regNum) {
		case 31 : return "XZR";
		case 30 : return "LR";
		case 29 : return "FP";
		case 28 : return "SP";
		default : return "X" + regNum;
		}
	} 
	
	private String getRegBinary(int regNum) {
		String regBinary = Integer.toBinaryString(regNum);
		while (regBinary.length() < 5) {
			regBinary = "0" + regBinary;
		}
		return regBinary;
	}
	
	private String getImmBinary(int value, int numBits, boolean signed) {
		String immBinary = null;
		if (signed) {
			if (value < 0) {
				immBinary = Integer.toBinaryString(value & 0x0fffffff);
				while (immBinary.length() > numBits) {
					immBinary = immBinary.substring(1);
				}
				return immBinary;
			}
		}
		immBinary = Integer.toBinaryString(value);
		while (immBinary.length() < numBits) {
			immBinary = "0" + immBinary;
		}
		return immBinary;
	}
	
	/**
	 * Clears the canvas and draws the original, un-highlighted datapath
	 */
	public void datapathInit() {
		clearCanvas();
		drawDataWiresInit();
		drawControlSignals();
		drawComponentsInit();
		drawStringsInit(true, true, false);
	}
	
	private void clearCanvas() {
		ctx.clearRect(0, 0, canvasWidth*CANVAS_WIDTH_REF, canvasHeight*CANVAS_HEIGHT_REF);
	}
	
	//-------------------------------------- Drawing Datapath wires (START) --------------------------------------------//
	
	private void drawDataWiresInit() {
		drawMuxPC_PC(DatapathGraphics.BLACK);
		draw4_ALUPC(DatapathGraphics.BLACK);
		drawPC_InsMem(DatapathGraphics.BLACK);
		drawPC_ALUPC(DatapathGraphics.BLACK);
		drawALUPC_MuxPC(DatapathGraphics.BLACK);
		drawALUPC_IFIDReg(DatapathGraphics.BLACK);
		drawInsMem_IFID(DatapathGraphics.BLACK);
		drawIFID_ALUBranch(DatapathGraphics.BLACK);
		drawIFID_Control(DatapathGraphics.BLACK);
		drawIFID_RegFileRead(DatapathGraphics.BLACK);
		drawIFID_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawIFID_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawIFID_MuxReg2LocSel(DatapathGraphics.BLACK);
		drawIFID_SignExtend(DatapathGraphics.BLACK);
		drawIFID_HAZARDDETECT(DatapathGraphics.BLACK);
		drawIFID_IDEX1(DatapathGraphics.BLACK);
		drawIFID_IDEX2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.BLACK);
		drawMuxReg2Loc_IDEX(DatapathGraphics.BLACK);
		drawSignExtend_IDEX(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawRegFile_IDEX1(DatapathGraphics.BLACK);
		drawRegFile_ZeroTest(DatapathGraphics.BLACK);
		drawRegFile_IDEX2(DatapathGraphics.BLACK);
		
		drawIDEX_EXMEM(DatapathGraphics.BLACK);
		drawIDEX_HAZARDDETECT(DatapathGraphics.BLACK);
		drawEXMEM_MEMWB2(DatapathGraphics.BLACK);
		drawEXMEM_MEMWB3(DatapathGraphics.BLACK);
		drawEXMEM_FORWARDINGUNIT(DatapathGraphics.BLACK);
		drawMEMWB_FORWARDINGUNIT(DatapathGraphics.BLACK);
		
		drawIDEX_MuxForwardA(DatapathGraphics.BLACK);
		drawIDEX_MuxForwardB(DatapathGraphics.BLACK);
		drawIDEX_ALUControl(DatapathGraphics.BLACK);
		drawIDEX_MuxReadRegData(DatapathGraphics.BLACK);
		drawIDEX_ForwardingUnit1(DatapathGraphics.BLACK);
		drawIDEX_ForwardingUnit2(DatapathGraphics.BLACK);
		drawMuxForwardA_ALUMain(DatapathGraphics.BLACK);
		drawMuxForwardB_MuxReadRegData(DatapathGraphics.BLACK);
		drawMuxForwardB_EXMEM(DatapathGraphics.BLACK);
		drawMuxReadRegData_ALUMain(DatapathGraphics.BLACK);
		
		drawALUMain_EXMEM1(DatapathGraphics.BLACK);
		drawALUMain_EXMEM2(DatapathGraphics.BLACK);
		drawEXMEM_DataMem1(DatapathGraphics.BLACK);
		drawEXMEM_DataMem2(DatapathGraphics.BLACK);
		drawEXMEM_MEMWB1(DatapathGraphics.BLACK);
		drawEXMEM_MuxForwardA(DatapathGraphics.BLACK);
		drawEXMEM_MuxForwardB(DatapathGraphics.BLACK);
		drawDataMem_MEMWB(DatapathGraphics.BLACK);
		drawMEMWB_MuxReadDataMem1(DatapathGraphics.BLACK);
		drawMEMWB_MuxReadDataMem2(DatapathGraphics.BLACK);
		drawMuxReadDataMem_WriteData(DatapathGraphics.BLACK);
		drawMEMWB_Flags(DatapathGraphics.BLACK);
		drawMEMWB_WriteReg(DatapathGraphics.BLACK);
		drawMuxReadDataMem_MuxForwardA(DatapathGraphics.BLACK);
		drawMuxReadDataMem_MuxForwardB(DatapathGraphics.BLACK);
	}
	
	private void drawMuxPC_PC(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0], 
				MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[1]/2, PC_COORDS[0], color, false);
	}
	
	private void draw4_ALUPC(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_COORDS[0], 
				ALU_PC_COORDS[1]+13*ALU_PC_DIMENSIONS[1]/16, 
				ALU_PC_COORDS[0], color, false);
	}
	
	private void drawPC_InsMem(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, PC_COORDS[0]+PC_DIMENSIONS[0], 
				PC_COORDS[1]+PC_DIMENSIONS[1]/2, 
				INS_MEM_COORDS[0], color, false);
	}
	
	private void drawPC_ALUPC(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, PC_COORDS[0]+PC_DIMENSIONS[0]+(INS_MEM_COORDS[0]-PC_COORDS[0]-PC_DIMENSIONS[0])/4, 
				ALU_PC_COORDS[1]+3*ALU_PC_DIMENSIONS[1]/16, 
				ALU_PC_COORDS[0], color, false);
		DatapathGraphics.drawHorizontalSegment(ctx, PC_COORDS[0]+PC_DIMENSIONS[0], PC_COORDS[1]+PC_DIMENSIONS[1]/2, 
				PC_COORDS[0]+PC_DIMENSIONS[0]+(INS_MEM_COORDS[0]-PC_COORDS[0]-PC_DIMENSIONS[0])/4, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, PC_COORDS[0]+PC_DIMENSIONS[0]+(INS_MEM_COORDS[0]-PC_COORDS[0]-PC_DIMENSIONS[0])/4, 
				PC_COORDS[1]+PC_DIMENSIONS[1]/2, ALU_PC_COORDS[1]+3*ALU_PC_DIMENSIONS[1]/16, 
				color, true, false);
	}
	
	private void drawALUPC_IFIDReg(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, ALU_PC_COORDS[0]+ALU_PC_DIMENSIONS[0], 
				ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2, IFID_REG_COORDS[0], color, false);
	}
	
	private void drawInsMem_IFID(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+INS_MEM_DIMENSIONS[1]/2, IFID_REG_COORDS[0], color, false);
	}
	
	private void drawIFID_Control(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				IFID_REG_COORDS[1]+IFID_REG_DIMENSIONS[1]/2, IFID_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, IFID_REG_COORDS[1]+(IFID_REG_DIMENSIONS[1]/2), 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2, color, false, true);
		DatapathGraphics.drawRightArrow(ctx, IFID_VERTICAL_X, CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2, 
				CONTROL_COORDS[0], color, false);
	}
	
	private void drawIFID_RegFileRead(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				IFID_REG_COORDS[1]+IFID_REG_DIMENSIONS[1]/2, IFID_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, IFID_REG_COORDS[1]+(IFID_REG_DIMENSIONS[1]/2), 
				REG_FILE_COORDS[1]+(REG_FILE_DIMENSIONS[1]/10), color, false, true);
		DatapathGraphics.drawRightArrow(ctx, IFID_VERTICAL_X, REG_FILE_COORDS[1]+(REG_FILE_DIMENSIONS[1]/10), 
				REG_FILE_COORDS[0], color, false);
	}
	
	private void drawIFID_MuxReg2Loc2(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				IFID_REG_COORDS[1]+IFID_REG_DIMENSIONS[1]/2, IFID_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, IFID_REG_COORDS[1]+(IFID_REG_DIMENSIONS[1]/2), 
				MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[0]/2, color, false, true);
		DatapathGraphics.drawRightArrow(ctx, IFID_VERTICAL_X, MUX_REG2LOC_COORDS[1]+(MUX_REG2LOC_DIMENSIONS[0]/2), 
				MUX_REG2LOC_COORDS[0], color, false);
	}
	
	private void drawIFID_MuxReg2Loc1(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				IFID_REG_COORDS[1]+IFID_REG_DIMENSIONS[1]/2, IFID_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, IFID_REG_COORDS[1]+(IFID_REG_DIMENSIONS[1]/2), 
				MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[1]-MUX_REG2LOC_DIMENSIONS[0]/2, color, false, true);
		DatapathGraphics.drawRightArrow(ctx, IFID_VERTICAL_X, MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[1]-MUX_REG2LOC_DIMENSIONS[0]/2, 
				MUX_REG2LOC_COORDS[0], color, false);
	}
	
	private void drawIFID_MuxReg2LocSel(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_VERTICAL_X, SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1]-10, 
				MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]/2, color, true, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]/2, MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[1], 
				SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1]-10, color, false, false);
	}
	
	private void drawIFID_SignExtend(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				IFID_REG_COORDS[1]+IFID_REG_DIMENSIONS[1]/2, IFID_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2,
				IFID_REG_COORDS[1]+(IFID_REG_DIMENSIONS[1]/2), color, false, true);
		DatapathGraphics.drawRightArrow(ctx, IFID_VERTICAL_X, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, 
				SIGN_EXTEND_COORDS[0], color, true);
	}
	
	private void drawIFID_HAZARDDETECT(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2, HAZARD_DETECT_COORDS[1]+HAZARD_DETECT_DIMENSIONS[1]/2, 
				color, false, false);
		DatapathGraphics.drawRightArrow(ctx, IFID_VERTICAL_X, HAZARD_DETECT_COORDS[1]+HAZARD_DETECT_DIMENSIONS[1]/2, 
				HAZARD_DETECT_COORDS[0], color, false);
	}
	
	private void drawIFID_IDEX1(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				IFID_REG_COORDS[1]+IFID_REG_DIMENSIONS[1]/2, IFID_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, IFID_REG_COORDS[1]+(IFID_REG_DIMENSIONS[1]/2), 
				REG_FILE_COORDS[1]+(REG_FILE_DIMENSIONS[1]/10), color, false, true);
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_VERTICAL_X, REG_FILE_COORDS[1]+(REG_FILE_DIMENSIONS[1]/10), 
				(IFID_VERTICAL_X+MUX_REG2LOC_COORDS[0])/2-2.5, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, (IFID_VERTICAL_X+MUX_REG2LOC_COORDS[0])/2-2.5, IDEX_REG_COORDS[1]+92*IDEX_REG_DIMENSIONS[1]/100,
				REG_FILE_COORDS[1]+(REG_FILE_DIMENSIONS[1]/10), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, (IFID_VERTICAL_X+MUX_REG2LOC_COORDS[0])/2-2.5, IDEX_REG_COORDS[1]+92*IDEX_REG_DIMENSIONS[1]/100, 
				IDEX_REG_COORDS[0], color, false);
	}
	
	private void drawIFID_IDEX2(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				IFID_REG_COORDS[1]+IFID_REG_DIMENSIONS[1]/2, IFID_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_VERTICAL_X, IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100,
				IFID_REG_COORDS[1]+(IFID_REG_DIMENSIONS[1]/2), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, IFID_VERTICAL_X, IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, 
				IDEX_REG_COORDS[0], color, false);
	}
	
	private void drawSignExtend_ShiftLeft2(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0], SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, 
				SIGN_EXTEND_COORDS[0]+1.5*SIGN_EXTEND_DIMENSIONS[0], color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, SIGN_EXTEND_COORDS[0]+1.5*SIGN_EXTEND_DIMENSIONS[0], SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2,
				(FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]+SIGN_EXTEND_COORDS[1])/2, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, SHIFT_LEFT2_COORDS[0]+SHIFT_LEFT2_DIMENSIONS[0]/2, (FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]+SIGN_EXTEND_COORDS[1])/2, 
				SIGN_EXTEND_COORDS[0]+1.5*SIGN_EXTEND_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawUpArrow(ctx, SHIFT_LEFT2_COORDS[0]+SHIFT_LEFT2_DIMENSIONS[0]/2, (FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]+SIGN_EXTEND_COORDS[1])/2, 
				SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1], color, false);
	}
	
	private void drawALUBranch_MuxPC(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_BRANCH_COORDS[0]+ALU_BRANCH_DIMENSIONS[0], ALU_BRANCH_COORDS[1]+ALU_BRANCH_DIMENSIONS[1]/2, 
				ALU_BRANCH_COORDS[0]+1.15*ALU_BRANCH_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, ALU_BRANCH_COORDS[0]+1.15*ALU_BRANCH_DIMENSIONS[0], 
				ALU_BRANCH_COORDS[1]+ALU_BRANCH_DIMENSIONS[1]/2, ALU_BRANCH_COORDS[1]-2.4*ALU_BRANCH_DIMENSIONS[1], 
				color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_PC_COORDS[0]-4*MUX_PC_DIMENSIONS[0]/4, 
				ALU_BRANCH_COORDS[1]-2.4*ALU_BRANCH_DIMENSIONS[1], ALU_BRANCH_COORDS[0]+1.15*ALU_BRANCH_DIMENSIONS[0], 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_PC_COORDS[0]-4*MUX_PC_DIMENSIONS[0]/4, 
				MUX_PC_COORDS[1]+2*MUX_PC_DIMENSIONS[0], ALU_BRANCH_COORDS[1]-2.4*ALU_BRANCH_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_PC_COORDS[0]-4*MUX_PC_DIMENSIONS[0]/4, 
				MUX_PC_COORDS[1]+2*MUX_PC_DIMENSIONS[0], MUX_PC_COORDS[0], color, false);
	}
	
	private void drawSignExtend_IDEX(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0], SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, 
				IDEX_REG_COORDS[0], color, false);
	}
	
	private void drawRegFile_IDEX1(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[0]/2, 
				IDEX_REG_COORDS[0], color, false);
	}
	
	private void drawRegFile_ZeroTest(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[0]/2, 
				ZERO_TEST_COORDS[0]+ZERO_TEST_DIMENSIONS[0]/2, color, false, true);
		DatapathGraphics.drawUpArrow(ctx, ZERO_TEST_COORDS[0]+ZERO_TEST_DIMENSIONS[0]/2, MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[0]/2, 
				 ZERO_TEST_COORDS[1]+ZERO_TEST_DIMENSIONS[1], color, false);
	}
	
	private void drawRegFile_IDEX2(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[0]/2, 
				IDEX_REG_COORDS[0], color, false);
	}
	
	private void drawIDEX_EXMEM(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0], IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, 
				EXMEM_REG_COORDS[0], color, false);
	}
	
	private void drawIDEX_HAZARDDETECT(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0]+15, FORWARD_UNIT_COORDS[1]+0.17*FORWARD_UNIT_DIMENSIONS[1], 
				IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, color, false, true);
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0]+15, FORWARD_UNIT_COORDS[1]+0.17*FORWARD_UNIT_DIMENSIONS[1],
				MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]+12, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]+12, FORWARD_UNIT_COORDS[1]+0.17*FORWARD_UNIT_DIMENSIONS[1], 
				SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]+12, SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1], 
				(IFID_VERTICAL_X+MUX_REG2LOC_COORDS[0])/2-2.5, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, (IFID_VERTICAL_X+MUX_REG2LOC_COORDS[0])/2-2.5, SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1], 
				HAZARD_DETECT_COORDS[1]+0.9*HAZARD_DETECT_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawRightArrow(ctx, (IFID_VERTICAL_X+MUX_REG2LOC_COORDS[0])/2-2.5, HAZARD_DETECT_COORDS[1]+0.9*HAZARD_DETECT_DIMENSIONS[1], 
				HAZARD_DETECT_COORDS[0], color, false);
	}
	
	private void drawALUPC_MuxPC(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_PC_COORDS[0]+ALU_PC_DIMENSIONS[0], ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2,
				ALU_PC_COORDS[0]+1.25*ALU_PC_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				ALU_PC_COORDS[0]+1.25*ALU_PC_DIMENSIONS[0], 
				ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2, ALU_PC_COORDS[1]-(ALU_PC_DIMENSIONS[0]/4), 
				color, true, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_PC_COORDS[0]-3*MUX_PC_DIMENSIONS[0]/4, ALU_PC_COORDS[1]-(ALU_PC_DIMENSIONS[0]/4),
				ALU_PC_COORDS[0]+1.25*ALU_PC_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_PC_COORDS[0]-3*MUX_PC_DIMENSIONS[0]/4, 
				MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[0]/2, ALU_PC_COORDS[1]-(ALU_PC_DIMENSIONS[0]/4), 
				color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_PC_COORDS[0]-3*MUX_PC_DIMENSIONS[0]/4, 
				MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[0]/2, MUX_PC_COORDS[0], color, false);
	}
	
	private void drawALUMain_EXMEM1(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0], 
				ALU_MAIN_COORDS[1]+ALU_MAIN_DIMENSIONS[1]/2, EXMEM_REG_COORDS[0], color, false);
	}
	
	private void drawALUMain_EXMEM2(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]/2, ALU_MAIN_COORDS[1]+15, ALU_MAIN_COORDS[1]-50, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]/2, 
				ALU_MAIN_COORDS[1]-50, EXMEM_REG_COORDS[0], color, false);
	}
	
	private void drawEXMEM_DataMem1(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0], 
				ALU_MAIN_COORDS[1]+ALU_MAIN_DIMENSIONS[1]/2, DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawEXMEM_DataMem2(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0], 
				(MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[1]+ALU_CONTROL_COORDS[1])/2, 
				DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawEXMEM_MEMWB1(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx,  DATA_MEM_COORDS[0]-20, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+30,
				ALU_MAIN_COORDS[1]+ALU_MAIN_DIMENSIONS[1]/2, color, false, true);
		DatapathGraphics.drawRightArrow(ctx, DATA_MEM_COORDS[0]-20, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+30, 
				MEMWB_REG_COORDS[0], color, false);
	}
	
	private void drawEXMEM_MEMWB2(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0], 
				IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, MEMWB_REG_COORDS[0], color, false);
	}
	
	private void drawEXMEM_MEMWB3(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, EXMEM_REG_COORDS[0], ALU_MAIN_COORDS[1]-50, MEMWB_REG_COORDS[0], color, false);
	}
	
	private void drawEXMEM_MuxForwardA(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, DATA_MEM_COORDS[0], FORWARD_UNIT_COORDS[1]+1.25*FORWARD_UNIT_DIMENSIONS[1], 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+30, color, false, true);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_FORWARD_A_COORDS[0]-1.35*MUX_FORWARD_A_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+1.25*FORWARD_UNIT_DIMENSIONS[1], 
				DATA_MEM_COORDS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_FORWARD_A_COORDS[0]-1.35*MUX_FORWARD_A_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+1.25*FORWARD_UNIT_DIMENSIONS[1], 
				MUX_FORWARD_A_COORDS[1]+0.8*MUX_FORWARD_A_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_A_COORDS[0]-1.35*MUX_FORWARD_A_DIMENSIONS[0], MUX_FORWARD_A_COORDS[1]+0.8*MUX_FORWARD_A_DIMENSIONS[1], 
				MUX_FORWARD_A_COORDS[0], color, false);
	}
	
	private void drawEXMEM_MuxForwardB(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_A_COORDS[0]-1.35*MUX_FORWARD_A_DIMENSIONS[0], MUX_FORWARD_B_COORDS[1]+0.8*MUX_FORWARD_B_DIMENSIONS[1], 
				MUX_FORWARD_B_COORDS[0], color, true);
	}
	
	private void drawEXMEM_FORWARDINGUNIT(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0]+15, FORWARD_UNIT_COORDS[1]+0.17*FORWARD_UNIT_DIMENSIONS[1], 
				IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, color, false, true);
		DatapathGraphics.drawLeftArrow(ctx, EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0]+15, FORWARD_UNIT_COORDS[1]+0.17*FORWARD_UNIT_DIMENSIONS[1], 
				FORWARD_UNIT_COORDS[0]+FORWARD_UNIT_DIMENSIONS[0], color, false);
	}
	
	private void drawMEMWB_FORWARDINGUNIT(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0], IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, 
				MUX_READ_DATA_MEM_COORDS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0], FORWARD_UNIT_COORDS[1]+0.75*FORWARD_UNIT_DIMENSIONS[1], 
				IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, color, false, false);
		DatapathGraphics.drawLeftArrow(ctx, MUX_READ_DATA_MEM_COORDS[0], FORWARD_UNIT_COORDS[1]+0.75*FORWARD_UNIT_DIMENSIONS[1], 
				FORWARD_UNIT_COORDS[0]+FORWARD_UNIT_DIMENSIONS[0], color, false);
	}
	
	private void drawDataMem_MEMWB(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0], 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2, MEMWB_REG_COORDS[0], color, false);
	}
	
	private void drawMEMWB_MuxReadDataMem1(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0], 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2, MUX_READ_DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawMEMWB_MuxReadDataMem2(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0], 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+30, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0]+10, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0]+10, 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+30, 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]-MUX_READ_REG_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0]+10, 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]-MUX_READ_DATA_MEM_DIMENSIONS[0]/2, MUX_READ_DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawMuxReg2Loc_RegFileRead2(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], 
				MUX_REG2LOC_COORDS[1]+(MUX_REG2LOC_DIMENSIONS[1]/2), REG_FILE_COORDS[0], color, false);
	}
	
	private void drawMuxReg2Loc_IDEX(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], 
				MUX_REG2LOC_COORDS[1]+(MUX_REG2LOC_DIMENSIONS[1]/2), MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]+7.5, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]+7.5, 
				IDEX_REG_COORDS[1]+95*IDEX_REG_DIMENSIONS[1]/100, MUX_REG2LOC_COORDS[1]+(MUX_REG2LOC_DIMENSIONS[1]/2), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]+7.5, 
				IDEX_REG_COORDS[1]+95*IDEX_REG_DIMENSIONS[1]/100, IDEX_REG_COORDS[0], color, false);
	}
	
	private void drawIDEX_MuxForwardA(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0], 
				MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[0]/2, MUX_FORWARD_A_COORDS[0], color, false);
	}
	
	private void drawIDEX_MuxForwardB(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0], 
				MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[0]/2, MUX_FORWARD_B_COORDS[0], color, false);
	}
	
	private void drawIDEX_ALUControl(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0], 
				SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, ALU_CONTROL_COORDS[0], color, false);
	}
	
	private void drawIDEX_MuxReadRegData(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0], 
				SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, MUX_READ_REG_COORDS[0]-30, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_REG_COORDS[0]-30, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2,
				MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[1]-MUX_READ_REG_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_READ_REG_COORDS[0]-30, 
				MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[1]-MUX_READ_REG_DIMENSIONS[0]/2, MUX_READ_REG_COORDS[0], color, false);
	}
	
	private void drawIDEX_ForwardingUnit1(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_REG_COORDS[0]+IDEX_REG_DIMENSIONS[0], IDEX_REG_COORDS[1]+92*IDEX_REG_DIMENSIONS[1]/100, 
				MUX_FORWARD_B_COORDS[0]+MUX_FORWARD_B_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_FORWARD_B_COORDS[0]+MUX_FORWARD_B_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+FORWARD_UNIT_DIMENSIONS[1]/2, 
				IDEX_REG_COORDS[1]+92*IDEX_REG_DIMENSIONS[1]/100, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_B_COORDS[0]+MUX_FORWARD_B_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+FORWARD_UNIT_DIMENSIONS[1]/2, 
				FORWARD_UNIT_COORDS[0], color, false);
	}
	
	private void drawIDEX_ForwardingUnit2(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_REG_COORDS[0], IDEX_REG_COORDS[1]+95*IDEX_REG_DIMENSIONS[1]/100, 
				MUX_FORWARD_B_COORDS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_FORWARD_B_COORDS[0], FORWARD_UNIT_COORDS[1]+0.8*FORWARD_UNIT_DIMENSIONS[1], 
				IDEX_REG_COORDS[1]+95*IDEX_REG_DIMENSIONS[1]/100, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_B_COORDS[0], FORWARD_UNIT_COORDS[1]+0.8*FORWARD_UNIT_DIMENSIONS[1], 
				FORWARD_UNIT_COORDS[0], color, false);
	}
	
	private void drawMuxForwardA_ALUMain(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_A_COORDS[0]+MUX_FORWARD_A_DIMENSIONS[0], 
				MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[1]/2, ALU_MAIN_COORDS[0], color, false);
	}
	
	private void drawMuxForwardB_MuxReadRegData(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_B_COORDS[0]+MUX_FORWARD_B_DIMENSIONS[0], 
				MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[1]/2, MUX_READ_REG_COORDS[0], color, false);
	}
	
	private void drawMuxForwardB_EXMEM(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_REG_COORDS[0]-20, (MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[1]+ALU_CONTROL_COORDS[1])/2, 
				MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[0]/2, color, false, true);
		DatapathGraphics.drawRightArrow(ctx,  MUX_READ_REG_COORDS[0]-20, 
				(MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[1]+ALU_CONTROL_COORDS[1])/2, EXMEM_REG_COORDS[0], color, false);
	}
	
	private void drawMuxReadRegData_ALUMain(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_READ_REG_COORDS[0]+MUX_READ_REG_DIMENSIONS[0], 
				MUX_READ_REG_COORDS[1]+(MUX_READ_REG_DIMENSIONS[1]/2), ALU_MAIN_COORDS[0], color, false);
	}
	
	private void drawShiftLeft2_ALUBranch(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, SHIFT_LEFT2_COORDS[0]+SHIFT_LEFT2_DIMENSIONS[0]/2, SHIFT_LEFT2_COORDS[1], 
				ALU_BRANCH_COORDS[1]+(3*ALU_BRANCH_DIMENSIONS[1]/16), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, SHIFT_LEFT2_COORDS[0]+SHIFT_LEFT2_DIMENSIONS[0]/2, 
				ALU_BRANCH_COORDS[1]+(3*ALU_BRANCH_DIMENSIONS[1]/16), ALU_BRANCH_COORDS[0], color, false);
	}
	
	private void drawIFID_ALUBranch(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0], 
				ALU_BRANCH_COORDS[1]+(13*ALU_BRANCH_DIMENSIONS[1]/16), ALU_BRANCH_COORDS[0], color, false);
	}
	
	private void drawMuxReadDataMem_WriteData(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0], MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]/2, 
				MUX_READ_DATA_MEM_COORDS[0]+1.25*MUX_READ_DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+1.25*MUX_READ_DATA_MEM_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+1.5*FORWARD_UNIT_DIMENSIONS[1], 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]/2, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+1.5*FORWARD_UNIT_DIMENSIONS[1], 
				MUX_READ_DATA_MEM_COORDS[0]+1.25*MUX_READ_DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+1.5*FORWARD_UNIT_DIMENSIONS[1], 
				REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10, 
				REG_FILE_COORDS[0], color, false);
	}
	
	private void drawMEMWB_Flags(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0], ALU_MAIN_COORDS[1]-50, 
				MUX_READ_DATA_MEM_COORDS[0]+1.45*MUX_READ_DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+1.45*MUX_READ_DATA_MEM_DIMENSIONS[0], 
				FORWARD_UNIT_COORDS[1]+2.15*FORWARD_UNIT_DIMENSIONS[1], ALU_MAIN_COORDS[1]-50, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]/2, FORWARD_UNIT_COORDS[1]+2.15*FORWARD_UNIT_DIMENSIONS[1], 
				MUX_READ_DATA_MEM_COORDS[0]+1.45*MUX_READ_DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawUpArrow(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]/2, FORWARD_UNIT_COORDS[1]+2.15*FORWARD_UNIT_DIMENSIONS[1], 
				FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1], color, false);
	}
	
	private void drawMEMWB_WriteReg(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2, FORWARD_UNIT_COORDS[1]+1.75*FORWARD_UNIT_DIMENSIONS[1], 
				FORWARD_UNIT_COORDS[1]+0.75*FORWARD_UNIT_DIMENSIONS[1], color, false, true);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_REG2LOC_COORDS[0], FORWARD_UNIT_COORDS[1]+1.75*FORWARD_UNIT_DIMENSIONS[1], 
				DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_REG2LOC_COORDS[0], FORWARD_UNIT_COORDS[1]+1.75*FORWARD_UNIT_DIMENSIONS[1], 
				REG_FILE_COORDS[1]+7*REG_FILE_DIMENSIONS[1]/10, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_REG2LOC_COORDS[0], REG_FILE_COORDS[1]+7*REG_FILE_DIMENSIONS[1]/10, 
				REG_FILE_COORDS[0], color, false);
	}
	
	private void drawMuxReadDataMem_MuxForwardA(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, MUX_FORWARD_A_COORDS[0]-MUX_FORWARD_A_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+1.5*FORWARD_UNIT_DIMENSIONS[1], 
				MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[1]/2, color, true, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_A_COORDS[0]-MUX_FORWARD_A_DIMENSIONS[0], MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[1]/2, 
				MUX_FORWARD_A_COORDS[0], color, false);
	}
	
	private void drawMuxReadDataMem_MuxForwardB(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_FORWARD_A_COORDS[0]-MUX_FORWARD_A_DIMENSIONS[0], MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[1]/2, 
				MUX_FORWARD_B_COORDS[0], color, true);
	}
	
	
	//-------------------------------------- Drawing Datapath wires (END) --------------------------------------------//
	
	//-------------------------------------- Drawing Control Signals (START) --------------------------------------------//
	private void drawControlSignals() {
		drawControl_IFID(DatapathGraphics.CONTROL_BLUE);
		drawBranchOR_MuxPC(DatapathGraphics.CONTROL_BLUE);
		drawHazardDetect_PC(DatapathGraphics.CONTROL_BLUE);
		drawHazardDetect_IFID(DatapathGraphics.CONTROL_BLUE);
		drawControl_MuxControl(DatapathGraphics.CONTROL_BLUE);
		drawHazardDetect_MuxControl(DatapathGraphics.CONTROL_BLUE);
		drawHazardDetect_IDEXM(DatapathGraphics.CONTROL_BLUE);
		drawRegFile_MEMWBWB(DatapathGraphics.CONTROL_BLUE);
		drawRegFile_MuxControl(DatapathGraphics.CONTROL_BLUE);
		drawMuxControl_IDEXM(DatapathGraphics.CONTROL_BLUE);
		drawMuxControl_IDEXEX(DatapathGraphics.CONTROL_BLUE);
		drawMuxControl_IDEXWB(DatapathGraphics.CONTROL_BLUE);
		drawZero_ZeroAND(DatapathGraphics.CONTROL_BLUE);
		drawZeroAND_MuxControl(DatapathGraphics.CONTROL_BLUE);
		drawZeroAND_BranchOR(DatapathGraphics.CONTROL_BLUE);
		drawMuxControl_BranchOR(DatapathGraphics.CONTROL_BLUE);
		drawFlagAND_MuxControl(DatapathGraphics.CONTROL_BLUE);
		drawFlags_FlagAND(DatapathGraphics.CONTROL_BLUE);
		drawFlagAND_BranchOR(DatapathGraphics.CONTROL_BLUE);
		drawIDEXWB_EXMEMWB(DatapathGraphics.CONTROL_BLUE);
		drawIDEXM_EXMEMM(DatapathGraphics.CONTROL_BLUE);
		drawIDEXEX_MuxReadRegData(DatapathGraphics.CONTROL_BLUE);
		drawForwardUnit_MuxForwardA(DatapathGraphics.CONTROL_BLUE);
		drawForwardUnit_MuxForwardB(DatapathGraphics.CONTROL_BLUE);
		drawALUControl_ALUMain(DatapathGraphics.CONTROL_BLUE);
		drawALUControl_IDEXEX(DatapathGraphics.CONTROL_BLUE);
		drawEXMEMWB_MEMWBWB(DatapathGraphics.CONTROL_BLUE);
		drawEXMEMWB_ForwardUnit(DatapathGraphics.CONTROL_BLUE);
		drawEXMEMM_DataMemBottom(DatapathGraphics.CONTROL_BLUE);
		drawEXMEMM_DataMemTop(DatapathGraphics.CONTROL_BLUE);
		drawMEMWBWB_MuxReadDataMem(DatapathGraphics.CONTROL_BLUE);
		drawMEMWBWB_ForwardUnit(DatapathGraphics.CONTROL_BLUE);
	}

	private void drawControl_IFID(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, CONTROL_COORDS[0]+0.9*CONTROL_DIMENSIONS[0], CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/4, 
				CONTROL_COORDS[0]+1.5*CONTROL_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, CONTROL_COORDS[0]+1.5*CONTROL_DIMENSIONS[0], CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/4, 
				ALU_BRANCH_COORDS[1]-2.6*ALU_BRANCH_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_PC_COORDS[0]-MUX_PC_DIMENSIONS[0]/2, ALU_BRANCH_COORDS[1]-2.6*ALU_BRANCH_DIMENSIONS[1], 
				CONTROL_COORDS[0]+1.5*CONTROL_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_PC_COORDS[0]-MUX_PC_DIMENSIONS[0]/2, IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, 
				ALU_BRANCH_COORDS[1]-2.6*ALU_BRANCH_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_PC_COORDS[0]-MUX_PC_DIMENSIONS[0]/2, IDEX_REG_COORDS[1]+98*IDEX_REG_DIMENSIONS[1]/100, 
				IFID_REG_COORDS[0], color, false, false);
	}
	
	private void drawBranchOR_MuxPC(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, BRANCH_OR_COORDS[0]+BRANCH_OR_DIMENSIONS[0]/2, BRANCH_OR_COORDS[1]+BRANCH_OR_DIMENSIONS[1]/2, 
				BRANCH_OR_COORDS[1]-8, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]/2, BRANCH_OR_COORDS[1]-8, 
				BRANCH_OR_COORDS[0]+BRANCH_OR_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]/2, MUX_PC_COORDS[1], 
				BRANCH_OR_COORDS[1]-8, color, false, false);
	}
	
	private void drawHazardDetect_PC(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, PC_COORDS[0]+PC_DIMENSIONS[0]/2, HAZARD_DETECT_COORDS[1]+0.10*HAZARD_DETECT_DIMENSIONS[1], 
				HAZARD_DETECT_COORDS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, PC_COORDS[0]+PC_DIMENSIONS[0]/2, PC_COORDS[1], 
				HAZARD_DETECT_COORDS[1]+0.10*HAZARD_DETECT_DIMENSIONS[1], color, false, false);
	}
	
	private void drawHazardDetect_IFID(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0]/2, HAZARD_DETECT_COORDS[1]+0.25*HAZARD_DETECT_DIMENSIONS[1], 
				HAZARD_DETECT_COORDS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, IFID_REG_COORDS[0]+IFID_REG_DIMENSIONS[0]/2, IFID_REG_COORDS[1], 
				HAZARD_DETECT_COORDS[1]+0.25*HAZARD_DETECT_DIMENSIONS[1], color, false, false);
	}
	
	private void drawControl_MuxControl(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]/2, CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2, 
				MUX_CONTROL_COORDS[0], color, false);
	}
	
	private void drawHazardDetect_MuxControl(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, HAZARD_DETECT_COORDS[0]+HAZARD_DETECT_DIMENSIONS[0], HAZARD_DETECT_COORDS[1]+0.8*HAZARD_DETECT_DIMENSIONS[1], 
				MUX_CONTROL_COORDS[0]+MUX_CONTROL_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_CONTROL_COORDS[0]+MUX_CONTROL_DIMENSIONS[0]/2, MUX_CONTROL_COORDS[1], 
				HAZARD_DETECT_COORDS[1]+0.8*HAZARD_DETECT_DIMENSIONS[1], color, false, false);
	}
	
	private void drawHazardDetect_IDEXM(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, HAZARD_DETECT_COORDS[0]+HAZARD_DETECT_DIMENSIONS[0], HAZARD_DETECT_COORDS[1]+0.5*HAZARD_DETECT_DIMENSIONS[1], 
				IDEX_M_COORDS[0]+2*IDEX_M_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, IDEX_M_COORDS[0]+2*IDEX_M_DIMENSIONS[0], IDEX_M_COORDS[1]+IDEX_M_DIMENSIONS[1]/2, 
				HAZARD_DETECT_COORDS[1]+0.5*HAZARD_DETECT_DIMENSIONS[1], color, true, false);
	}
	
	private void drawRegFile_MEMWBWB(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]/2, REG_FILE_COORDS[1], 
				HAZARD_DETECT_COORDS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]/2, HAZARD_DETECT_COORDS[1], 
				MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2, MEMWB_WB_COORDS[1]+0.10*MEMWB_WB_DIMENSIONS[1], 
				HAZARD_DETECT_COORDS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MEMWB_WB_COORDS[0]+MEMWB_WB_DIMENSIONS[0], MEMWB_WB_COORDS[1]+0.10*MEMWB_WB_DIMENSIONS[1], 
				MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2, color, false, false);
	}
	
	private void drawRegFile_MuxControl(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]/2, MUX_CONTROL_COORDS[1]+0.8*MUX_CONTROL_DIMENSIONS[1], 
				MUX_CONTROL_COORDS[0], color, true);
	}
	
	private void drawMuxControl_IDEXM(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_CONTROL_COORDS[0]+MUX_CONTROL_DIMENSIONS[0], MUX_CONTROL_COORDS[1]+0.5*MUX_CONTROL_DIMENSIONS[1], 
				IDEX_M_COORDS[0], color, false);
	}
	
	private void drawMuxControl_IDEXEX(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0], IDEX_EX_COORDS[1]+IDEX_EX_DIMENSIONS[1]/2,
				MUX_CONTROL_COORDS[1]+0.5*MUX_CONTROL_DIMENSIONS[1], color, false, true);
		DatapathGraphics.drawRightArrow(ctx, FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0], IDEX_EX_COORDS[1]+IDEX_EX_DIMENSIONS[1]/2, IDEX_EX_COORDS[0], color, false);
	}

	private void drawMuxControl_IDEXWB(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0], MUX_CONTROL_COORDS[1]+0.5*MUX_CONTROL_DIMENSIONS[1], 
				IDEX_WB_COORDS[1]+IDEX_WB_DIMENSIONS[1]/2, color, true, false);
		DatapathGraphics.drawRightArrow(ctx, FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0], IDEX_WB_COORDS[1]+IDEX_WB_DIMENSIONS[1]/2, 
				IDEX_WB_COORDS[0], color, false);
	}
	
	private void drawZero_ZeroAND(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_AND_COORDS[0]+0.65*ZERO_AND_DIMENSIONS[0], SHIFT_LEFT2_COORDS[1], 
				ZERO_AND_COORDS[1]+ZERO_AND_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, ZERO_AND_COORDS[0]+0.65*ZERO_AND_DIMENSIONS[0], SHIFT_LEFT2_COORDS[1], 
				ZERO_TEST_COORDS[0]+0.5*ZERO_TEST_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_TEST_COORDS[0]+0.5*ZERO_TEST_DIMENSIONS[0], ZERO_TEST_COORDS[1], 
				SHIFT_LEFT2_COORDS[1], color, false, false);

	}
	
	private void drawZeroAND_MuxControl(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_AND_COORDS[0]+0.35*ZERO_AND_DIMENSIONS[0], MUX_CONTROL_COORDS[1]+0.5*MUX_CONTROL_DIMENSIONS[1], 
				ZERO_AND_COORDS[1]+ZERO_AND_DIMENSIONS[1], color, true, false);
	}
	
	private void drawZeroAND_BranchOR(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_AND_COORDS[0]+0.5*ZERO_AND_DIMENSIONS[0], ZERO_AND_COORDS[1], 
				BRANCH_OR_COORDS[1]+1.5*BRANCH_OR_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, ZERO_AND_COORDS[0]+0.5*ZERO_AND_DIMENSIONS[0], BRANCH_OR_COORDS[1]+1.5*BRANCH_OR_DIMENSIONS[1], 
				BRANCH_OR_COORDS[0]+0.35*BRANCH_OR_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, BRANCH_OR_COORDS[0]+0.35*BRANCH_OR_DIMENSIONS[0], BRANCH_OR_COORDS[1]+1.5*BRANCH_OR_DIMENSIONS[1], 
				BRANCH_OR_COORDS[1]+0.92*BRANCH_OR_DIMENSIONS[1], color, false, false);
	}
	
	private void drawMuxControl_BranchOR(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, BRANCH_OR_COORDS[0]+0.5*BRANCH_OR_DIMENSIONS[0], MUX_CONTROL_COORDS[1]+0.5*MUX_CONTROL_DIMENSIONS[1], 
				BRANCH_OR_COORDS[1]+0.92*BRANCH_OR_DIMENSIONS[1], color, true, false);
	}
	
	private void drawFlagAND_MuxControl(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, FLAG_AND_COORDS[0]+0.35*FLAG_AND_DIMENSIONS[0], MUX_CONTROL_COORDS[1]+0.5*MUX_CONTROL_DIMENSIONS[1], 
				FLAG_AND_COORDS[1]+FLAG_AND_DIMENSIONS[1], color, true, false);
	}
	
	private void drawFlags_FlagAND(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, FLAG_AND_COORDS[0]+0.65*FLAG_AND_DIMENSIONS[0], ALU_BRANCH_COORDS[1]+(13*ALU_BRANCH_DIMENSIONS[1]/16), 
				FLAG_AND_COORDS[1]+FLAG_AND_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, FLAG_AND_COORDS[0]+0.65*FLAG_AND_DIMENSIONS[0], ALU_BRANCH_COORDS[1]+(13*ALU_BRANCH_DIMENSIONS[1]/16), 
				ZERO_TEST_COORDS[0]+1.25*ZERO_TEST_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_TEST_COORDS[0]+1.25*ZERO_TEST_DIMENSIONS[0], FLAGS_COORDS[1]+0.5*FLAGS_DIMENSIONS[1],
				ALU_BRANCH_COORDS[1]+(13*ALU_BRANCH_DIMENSIONS[1]/16), color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0], FLAGS_COORDS[1]+0.5*FLAGS_DIMENSIONS[1], 
				ZERO_TEST_COORDS[0]+1.25*ZERO_TEST_DIMENSIONS[0], color, false, false);
	}
	
	private void drawFlagAND_BranchOR(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, FLAG_AND_COORDS[0]+0.5*FLAG_AND_DIMENSIONS[0], FLAG_AND_COORDS[1], 
				BRANCH_OR_COORDS[1]+1.5*BRANCH_OR_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, FLAG_AND_COORDS[0]+0.5*FLAG_AND_DIMENSIONS[0], BRANCH_OR_COORDS[1]+1.5*BRANCH_OR_DIMENSIONS[1], 
				BRANCH_OR_COORDS[0]+0.65*BRANCH_OR_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, BRANCH_OR_COORDS[0]+0.65*BRANCH_OR_DIMENSIONS[0], BRANCH_OR_COORDS[1]+1.5*BRANCH_OR_DIMENSIONS[1], 
				BRANCH_OR_COORDS[1]+0.92*BRANCH_OR_DIMENSIONS[1], color, false, false);
	}
	
	private void drawIDEXWB_EXMEMWB(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_WB_COORDS[0]+IDEX_WB_DIMENSIONS[0], IDEX_WB_COORDS[1]+0.5*IDEX_WB_DIMENSIONS[1], 
				EXMEM_WB_COORDS[0]-EXMEM_WB_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, EXMEM_WB_COORDS[0]-EXMEM_WB_DIMENSIONS[0], EXMEM_WB_COORDS[1]+0.5*EXMEM_WB_DIMENSIONS[1], 
				IDEX_WB_COORDS[1]+0.5*IDEX_WB_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, EXMEM_WB_COORDS[0]-EXMEM_WB_DIMENSIONS[0], EXMEM_WB_COORDS[1]+0.5*EXMEM_WB_DIMENSIONS[1], 
				EXMEM_WB_COORDS[0], color, false, false);
	}
	
	private void drawIDEXM_EXMEMM(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_M_COORDS[0]+IDEX_M_DIMENSIONS[0], IDEX_M_COORDS[1]+0.5*IDEX_M_DIMENSIONS[1], 
				EXMEM_M_COORDS[0]-2*EXMEM_M_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, EXMEM_M_COORDS[0]-2*EXMEM_M_DIMENSIONS[0], EXMEM_M_COORDS[1]+0.5*EXMEM_M_DIMENSIONS[1], 
				IDEX_M_COORDS[1]+0.5*IDEX_M_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, EXMEM_M_COORDS[0]-2*EXMEM_M_DIMENSIONS[0], EXMEM_M_COORDS[1]+0.5*EXMEM_M_DIMENSIONS[1], 
				EXMEM_M_COORDS[0], color, false, false);
	}
	
	private void drawIDEXEX_MuxReadRegData(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_EX_COORDS[0]+IDEX_EX_DIMENSIONS[0], IDEX_EX_COORDS[1]+0.5*IDEX_EX_DIMENSIONS[1], 
				MUX_READ_REG_COORDS[0]+0.5*MUX_READ_REG_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_REG_COORDS[0]+0.5*MUX_READ_REG_DIMENSIONS[0], MUX_READ_REG_COORDS[1], 
				IDEX_EX_COORDS[1]+0.5*IDEX_EX_DIMENSIONS[1], color, false, false);
	}
	
	private void drawForwardUnit_MuxForwardA(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_FORWARD_A_COORDS[0]+0.5*MUX_FORWARD_A_DIMENSIONS[0], MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[1], 
				MUX_FORWARD_A_COORDS[0]+1.5*MUX_FORWARD_A_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_FORWARD_A_COORDS[0]+1.5*MUX_FORWARD_A_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+0.10*FORWARD_UNIT_DIMENSIONS[1], 
				MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_FORWARD_A_COORDS[0]+1.5*MUX_FORWARD_A_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+0.10*FORWARD_UNIT_DIMENSIONS[1], 
				FORWARD_UNIT_COORDS[0], color, false, false);
	}
	
	private void drawForwardUnit_MuxForwardB(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, MUX_FORWARD_B_COORDS[0]+0.5*MUX_FORWARD_B_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+0.25*FORWARD_UNIT_DIMENSIONS[1], 
				MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_FORWARD_B_COORDS[0]+0.5*MUX_FORWARD_B_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+0.25*FORWARD_UNIT_DIMENSIONS[1], 
				FORWARD_UNIT_COORDS[0], color, false, false);
	}
	
	private void drawALUControl_ALUMain(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_CONTROL_COORDS[0]+ALU_CONTROL_DIMENSIONS[0], ALU_CONTROL_COORDS[1]+0.5*ALU_CONTROL_DIMENSIONS[1], 
				ALU_MAIN_COORDS[0]+0.5*ALU_MAIN_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, ALU_MAIN_COORDS[0]+0.5*ALU_MAIN_DIMENSIONS[0], ALU_CONTROL_COORDS[1]+0.5*ALU_CONTROL_DIMENSIONS[1], 
				ALU_MAIN_COORDS[1]+0.75*ALU_MAIN_DIMENSIONS[1], color, false, false);
	}
	
	private void drawALUControl_IDEXEX(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, ALU_CONTROL_COORDS[0]+0.5*ALU_CONTROL_DIMENSIONS[0], IDEX_REG_COORDS[1]+92*IDEX_REG_DIMENSIONS[1]/100, 
				ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_CONTROL_COORDS[0]+0.5*ALU_CONTROL_DIMENSIONS[0], IDEX_REG_COORDS[1]+92*IDEX_REG_DIMENSIONS[1]/100, 
				ALU_MAIN_COORDS[0]+1.2*ALU_MAIN_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, ALU_MAIN_COORDS[0]+1.2*ALU_MAIN_DIMENSIONS[0], IDEX_REG_COORDS[1]+92*IDEX_REG_DIMENSIONS[1]/100, 
				IDEX_EX_COORDS[1]+0.8*IDEX_EX_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, IDEX_EX_COORDS[0]+IDEX_EX_DIMENSIONS[0], IDEX_EX_COORDS[1]+0.8*IDEX_EX_DIMENSIONS[1], 
				ALU_MAIN_COORDS[0]+1.2*ALU_MAIN_DIMENSIONS[0], color, false, false);
	}
	
	private void drawEXMEMWB_MEMWBWB(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, EXMEM_WB_COORDS[0]+EXMEM_WB_DIMENSIONS[0], EXMEM_WB_COORDS[1]+0.5*EXMEM_WB_DIMENSIONS[1], 
				DATA_MEM_COORDS[0]+0.5*DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, DATA_MEM_COORDS[0]+0.5*DATA_MEM_DIMENSIONS[0], MEMWB_WB_COORDS[1]+0.5*MEMWB_WB_DIMENSIONS[1], 
				EXMEM_WB_COORDS[1]+0.5*EXMEM_WB_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, DATA_MEM_COORDS[0]+0.5*DATA_MEM_DIMENSIONS[0], MEMWB_WB_COORDS[1]+0.5*MEMWB_WB_DIMENSIONS[1], 
				MEMWB_WB_COORDS[0], color, false, false);
	}
	
	private void drawEXMEMWB_ForwardUnit(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0]+10, FORWARD_UNIT_COORDS[1]+0.35*FORWARD_UNIT_DIMENSIONS[1], 
				EXMEM_WB_COORDS[1]+0.5*EXMEM_WB_DIMENSIONS[1], color, false, true);
		DatapathGraphics.drawHorizontalSegment(ctx, FORWARD_UNIT_COORDS[0]+FORWARD_UNIT_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+0.35*FORWARD_UNIT_DIMENSIONS[1], 
				EXMEM_REG_COORDS[0]+EXMEM_REG_DIMENSIONS[0]+10, color, false, false);
	}
	
	private void drawEXMEMM_DataMemBottom(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, DATA_MEM_COORDS[0]+0.5*DATA_MEM_DIMENSIONS[0], DATA_MEM_COORDS[1]+1.45*DATA_MEM_DIMENSIONS[1], 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, DATA_MEM_COORDS[0]+0.5*DATA_MEM_DIMENSIONS[0], DATA_MEM_COORDS[1]+1.45*DATA_MEM_DIMENSIONS[1], 
				DATA_MEM_COORDS[0]+1.1*DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, DATA_MEM_COORDS[0]+1.1*DATA_MEM_DIMENSIONS[0], DATA_MEM_COORDS[1]+1.45*DATA_MEM_DIMENSIONS[1], 
				EXMEM_M_COORDS[1]+0.65*EXMEM_M_DIMENSIONS[1], color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, EXMEM_M_COORDS[0]+EXMEM_M_DIMENSIONS[0], EXMEM_M_COORDS[1]+0.65*EXMEM_M_DIMENSIONS[1], 
				DATA_MEM_COORDS[0]+1.1*DATA_MEM_DIMENSIONS[0], color, false, false);
	}
	
	private void drawEXMEMM_DataMemTop(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, EXMEM_M_COORDS[0]+EXMEM_M_DIMENSIONS[0], EXMEM_M_COORDS[1]+0.9*EXMEM_M_DIMENSIONS[1], 
				DATA_MEM_COORDS[0]+0.5*DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, DATA_MEM_COORDS[0]+0.5*DATA_MEM_DIMENSIONS[0], DATA_MEM_COORDS[1], 
				EXMEM_M_COORDS[1]+0.9*EXMEM_M_DIMENSIONS[1], color, false, false);
	}
	
	private void drawMEMWBWB_MuxReadDataMem(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MEMWB_WB_COORDS[0]+MEMWB_WB_DIMENSIONS[0], MEMWB_WB_COORDS[1]+0.5*MEMWB_WB_DIMENSIONS[1], 
				MUX_READ_DATA_MEM_COORDS[0]+0.5*MUX_READ_DATA_MEM_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+0.5*MUX_READ_DATA_MEM_DIMENSIONS[0], MUX_READ_DATA_MEM_COORDS[1], 
				MEMWB_WB_COORDS[1]+0.5*MEMWB_WB_DIMENSIONS[1], color, false, false);
	}
	
	private void drawMEMWBWB_ForwardUnit(CssColor color) {
		DatapathGraphics.drawVerticalSegment(ctx, MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0]+10, FORWARD_UNIT_COORDS[1]+0.55*FORWARD_UNIT_DIMENSIONS[1], 
				MEMWB_WB_COORDS[1]+0.10*MEMWB_WB_DIMENSIONS[1], color, false, true);
		DatapathGraphics.drawHorizontalSegment(ctx, FORWARD_UNIT_COORDS[0]+FORWARD_UNIT_DIMENSIONS[0], FORWARD_UNIT_COORDS[1]+0.55*FORWARD_UNIT_DIMENSIONS[1], 
				MEMWB_REG_COORDS[0]+MEMWB_REG_DIMENSIONS[0]+10, color, false, false);
	}
	
	//-------------------------------------- Drawing Control Signals (END) --------------------------------------------//
	
	//-------------------------------------- Drawing Components (START) --------------------------------------------//
	private void drawComponentsInit() {
		drawIFID(false,false);
		drawPC(false);
		drawInsMem(false, false);
		drawALUPC(false);
		drawMuxPC(false, false);
		
		drawIDEX(false,false);
		drawIDEX_EX();
		drawIDEX_M();
		drawIDEX_WB();
		drawRegFile(false, false);
		drawMuxReg2Loc(false, false);
		drawALUBranch(false);
		drawSignExtend(false);
		drawShiftLeft2(false);
		drawZeroTest(false);
		drawFlags(false, false);
		
		drawEXMEM(false,false);
		drawEXMEM_M();
		drawEXMEM_WB();
		drawALUMain(false);
		drawMuxReadRegData(false, false);
		drawMuxForwardA(false, false);
		drawMuxForwardB(false, false);
		
		drawMEMWB(false,false);
		drawMEMWB_WB();
		drawDataMem(false, false);
		drawMuxReadMemData(false,false);
		
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawMuxControl(false, false);
		drawHazardDetectionUnit();
		drawForwardingUnit();
		drawALUControl(false);
	}
	
	private void drawIFID(boolean highlightLeft, boolean highlightRight) {
		DatapathGraphics.drawCompRect(ctx, IFID_REG_COORDS[0], IFID_REG_COORDS[1], 
				IFID_REG_DIMENSIONS[0], IFID_REG_DIMENSIONS[1], highlightLeft, highlightRight);
	}
	
	private void drawIDEX(boolean highlightLeft, boolean highlightRight) {
		DatapathGraphics.drawCompRect(ctx, IDEX_REG_COORDS[0], IDEX_REG_COORDS[1], 
				IDEX_REG_DIMENSIONS[0], IDEX_REG_DIMENSIONS[1], highlightLeft, highlightRight);
	}
	
	private void drawIDEX_EX() {
		DatapathGraphics.drawCompRect(ctx, IDEX_EX_COORDS[0], IDEX_EX_COORDS[1], 
				IDEX_EX_DIMENSIONS[0], IDEX_EX_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawIDEX_M() {
		DatapathGraphics.drawCompRect(ctx, IDEX_M_COORDS[0], IDEX_M_COORDS[1], 
				IDEX_M_DIMENSIONS[0], IDEX_M_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawIDEX_WB() {
		DatapathGraphics.drawCompRect(ctx, IDEX_WB_COORDS[0], IDEX_WB_COORDS[1], 
				IDEX_WB_DIMENSIONS[0], IDEX_WB_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawEXMEM(boolean highlightLeft, boolean highlightRight) {
		DatapathGraphics.drawCompRect(ctx, EXMEM_REG_COORDS[0], EXMEM_REG_COORDS[1], 
				EXMEM_REG_DIMENSIONS[0], EXMEM_REG_DIMENSIONS[1], highlightLeft, highlightRight);
	}
	
	private void drawEXMEM_M() {
		DatapathGraphics.drawCompRect(ctx, EXMEM_M_COORDS[0], EXMEM_M_COORDS[1], 
				EXMEM_M_DIMENSIONS[0], EXMEM_M_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawEXMEM_WB() {
		DatapathGraphics.drawCompRect(ctx, EXMEM_WB_COORDS[0], EXMEM_WB_COORDS[1], 
				EXMEM_WB_DIMENSIONS[0], EXMEM_WB_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawMEMWB(boolean highlightLeft, boolean highlightRight) {
		DatapathGraphics.drawCompRect(ctx, MEMWB_REG_COORDS[0], MEMWB_REG_COORDS[1], 
				MEMWB_REG_DIMENSIONS[0], MEMWB_REG_DIMENSIONS[1], highlightLeft, highlightRight);
	}
	
	private void drawMEMWB_WB() {
		DatapathGraphics.drawCompRect(ctx, MEMWB_WB_COORDS[0], MEMWB_WB_COORDS[1], 
				MEMWB_WB_DIMENSIONS[0], MEMWB_WB_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawZeroTest(boolean highlight) {
		DatapathGraphics.drawCompEllipse(ctx, ZERO_TEST_COORDS[0], ZERO_TEST_COORDS[1], 
				ZERO_TEST_DIMENSIONS[0], ZERO_TEST_DIMENSIONS[1], highlight);
	}
	
	private void drawMuxForwardA(boolean highlightTop, boolean highlightBottom) {
		DatapathGraphics.drawMux(ctx, MUX_FORWARD_A_COORDS[0], MUX_FORWARD_A_COORDS[1], 
				MUX_FORWARD_A_DIMENSIONS[0], MUX_FORWARD_A_DIMENSIONS[1], highlightTop, highlightBottom);
	}
	
	private void drawMuxForwardB(boolean highlightTop, boolean highlightBottom) {
		DatapathGraphics.drawMux(ctx, MUX_FORWARD_B_COORDS[0], MUX_FORWARD_B_COORDS[1], 
				MUX_FORWARD_B_DIMENSIONS[0], MUX_FORWARD_B_DIMENSIONS[1], highlightTop, highlightBottom);
	}
	
	private void drawPC(boolean highlight) {
		DatapathGraphics.drawCompRect(ctx, PC_COORDS[0], PC_COORDS[1], 
				PC_DIMENSIONS[0], PC_DIMENSIONS[1], highlight, highlight);
	}
	
	private void drawInsMem(boolean highlightLeft, boolean highlightRight) {
		DatapathGraphics.drawCompRect(ctx, INS_MEM_COORDS[0], INS_MEM_COORDS[1], 
				INS_MEM_DIMENSIONS[0], INS_MEM_DIMENSIONS[1], highlightLeft, highlightRight);
	}
	
	private void drawRegFile(boolean highlightLeft, boolean highlightRight) {
		DatapathGraphics.drawCompRect(ctx, REG_FILE_COORDS[0], REG_FILE_COORDS[1], 
				REG_FILE_DIMENSIONS[0], REG_FILE_DIMENSIONS[1], highlightLeft, highlightRight);
	}
	
	private void drawDataMem(boolean highlightLeft, boolean highlightRight) {
		DatapathGraphics.drawCompRect(ctx, DATA_MEM_COORDS[0], DATA_MEM_COORDS[1], 
				DATA_MEM_DIMENSIONS[0], DATA_MEM_DIMENSIONS[1], highlightLeft, highlightRight);
	}
	
	private void drawALUPC(boolean highlight) {
		DatapathGraphics.drawALU(ctx, ALU_PC_COORDS[0], ALU_PC_COORDS[1], 
				ALU_PC_DIMENSIONS[0], ALU_PC_DIMENSIONS[1], highlight);
	}
	
	private void drawALUBranch(boolean highlight) {
		DatapathGraphics.drawALU(ctx, ALU_BRANCH_COORDS[0], ALU_BRANCH_COORDS[1], 
				ALU_BRANCH_DIMENSIONS[0], ALU_BRANCH_DIMENSIONS[1], highlight);
	}
	
	private void drawALUMain(boolean highlight) {
		DatapathGraphics.drawALU(ctx, ALU_MAIN_COORDS[0], ALU_MAIN_COORDS[1], 
				ALU_MAIN_DIMENSIONS[0], ALU_MAIN_DIMENSIONS[1], highlight);
	}
	
	private void drawMuxPC(boolean highlightTop, boolean highlightBottom) {
		DatapathGraphics.drawMux(ctx, MUX_PC_COORDS[0], MUX_PC_COORDS[1], 
				MUX_PC_DIMENSIONS[0], MUX_PC_DIMENSIONS[1], highlightTop, highlightBottom);
	}
	
	private void drawMuxReg2Loc(boolean highlightTop, boolean highlightBottom) {
		DatapathGraphics.drawMux(ctx, MUX_REG2LOC_COORDS[0], MUX_REG2LOC_COORDS[1], 
				MUX_REG2LOC_DIMENSIONS[0], MUX_REG2LOC_DIMENSIONS[1], highlightTop, highlightBottom);
	}
	
	private void drawMuxReadRegData(boolean highlightTop, boolean highlightBottom) {
		DatapathGraphics.drawMux(ctx, MUX_READ_REG_COORDS[0], MUX_READ_REG_COORDS[1], 
				MUX_READ_REG_DIMENSIONS[0], MUX_READ_REG_DIMENSIONS[1], highlightTop, highlightBottom);
	}
	
	private void drawMuxReadMemData(boolean highlightTop, boolean highlightBottom) {
		DatapathGraphics.drawMux(ctx, MUX_READ_DATA_MEM_COORDS[0], MUX_READ_DATA_MEM_COORDS[1], 
				MUX_READ_DATA_MEM_DIMENSIONS[0], MUX_READ_DATA_MEM_DIMENSIONS[1], highlightTop, highlightBottom);
	}
	
	private void drawControl() {
		DatapathGraphics.drawEllipse(ctx, CONTROL_COORDS[0], CONTROL_COORDS[1], 
				CONTROL_DIMENSIONS[0], CONTROL_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE, DatapathGraphics.WHITE);
	}
	
	private void drawMuxControl(boolean highlightTop, boolean highlightBottom) {
		DatapathGraphics.drawMux(ctx, MUX_CONTROL_COORDS[0], MUX_CONTROL_COORDS[1], MUX_CONTROL_DIMENSIONS[0], MUX_CONTROL_DIMENSIONS[1], highlightTop, highlightBottom);
	}
	
	private void drawALUControl(boolean highlight) {
		DatapathGraphics.drawEllipse(ctx, ALU_CONTROL_COORDS[0], ALU_CONTROL_COORDS[1], 
				ALU_CONTROL_DIMENSIONS[0], ALU_CONTROL_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE, DatapathGraphics.WHITE);
	}
	
	private void drawSignExtend(boolean highlight) {
		DatapathGraphics.drawCompEllipse(ctx, SIGN_EXTEND_COORDS[0], SIGN_EXTEND_COORDS[1], 
				SIGN_EXTEND_DIMENSIONS[0], SIGN_EXTEND_DIMENSIONS[1], highlight);
	}
	
	private void drawShiftLeft2(boolean highlight) {
		DatapathGraphics.drawCompEllipse(ctx, SHIFT_LEFT2_COORDS[0], SHIFT_LEFT2_COORDS[1], 
				SHIFT_LEFT2_DIMENSIONS[0], SHIFT_LEFT2_DIMENSIONS[1], highlight);
	}
	
	private void drawFlagAnd() {
		DatapathGraphics.drawAndGateVertical(ctx, FLAG_AND_COORDS[0], FLAG_AND_COORDS[1], 
				FLAG_AND_DIMENSIONS[0], FLAG_AND_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawZeroAnd() {
		DatapathGraphics.drawAndGateVertical(ctx, ZERO_AND_COORDS[0], ZERO_AND_COORDS[1], 
				ZERO_AND_DIMENSIONS[0], ZERO_AND_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawBranchOr() {
		DatapathGraphics.drawOrGateVertical(ctx, BRANCH_OR_COORDS[0], BRANCH_OR_COORDS[1], 
				BRANCH_OR_DIMENSIONS[0], BRANCH_OR_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawHazardDetectionUnit() {
		DatapathGraphics.drawCompRect(ctx, HAZARD_DETECT_COORDS[0], HAZARD_DETECT_COORDS[1], 
				HAZARD_DETECT_DIMENSIONS[0], HAZARD_DETECT_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawForwardingUnit() {
		DatapathGraphics.drawCompRect(ctx, FORWARD_UNIT_COORDS[0], FORWARD_UNIT_COORDS[1], 
				FORWARD_UNIT_DIMENSIONS[0], FORWARD_UNIT_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawFlags(boolean highlightWrite, boolean highlightRead) {
		DatapathGraphics.drawCompRect(ctx, FLAGS_COORDS[0], FLAGS_COORDS[1], 
				FLAGS_DIMENSIONS[0]/4, FLAGS_DIMENSIONS[1], highlightWrite, highlightRead);
		DatapathGraphics.drawCompRect(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]/4, FLAGS_COORDS[1], 
				FLAGS_DIMENSIONS[0]/4, FLAGS_DIMENSIONS[1], highlightWrite, highlightRead);
		DatapathGraphics.drawCompRect(ctx, FLAGS_COORDS[0]+2*FLAGS_DIMENSIONS[0]/4, FLAGS_COORDS[1], 
				FLAGS_DIMENSIONS[0]/4, FLAGS_DIMENSIONS[1], highlightWrite, highlightRead);
		DatapathGraphics.drawCompRect(ctx, FLAGS_COORDS[0]+3*FLAGS_DIMENSIONS[0]/4, FLAGS_COORDS[1], 
				FLAGS_DIMENSIONS[0]/4, FLAGS_DIMENSIONS[1], highlightWrite, highlightRead);
	}
	
	//-------------------------------------- Drawing Components (END) --------------------------------------------//
	
	//-------------------------------------- Drawing Text Strings (START) --------------------------------------------//
	private void drawStringsInit(boolean signExtend, boolean zero, boolean stxr) {
		ctx.setFillStyle(DatapathGraphics.BLACK);
		ctx.setFont("bold 14px arial");
		drawPC_TXT();
		drawPCALU_TXT();
		drawInsMem_TXT();
		drawMux_TXT(MUX_REG2LOC_COORDS, MUX_REG2LOC_DIMENSIONS, "0", "1");
		drawRegFile_TXT();
		if (signExtend) {
			drawSignExtend_TXT();
		} else {
			drawPad_TXT();
		} 
		drawShiftLeft2_TXT();
		drawMux_TXT(MUX_READ_REG_COORDS, MUX_READ_REG_DIMENSIONS, "0", "1");
		drawALUMain_TXT();
		drawFlag_TXT();
		drawALUBranch_TXT();
		drawDataMem_TXT();
		drawMux_TXT(MUX_READ_DATA_MEM_COORDS, MUX_READ_DATA_MEM_DIMENSIONS, "1", "0");
		drawMux_TXT(MUX_PC_COORDS, MUX_PC_DIMENSIONS, "0", "1");
		drawMux_TXT(MUX_CONTROL_COORDS, MUX_CONTROL_DIMENSIONS, "1", "0");
		drawMux_TXT(MUX_FORWARD_A_COORDS, MUX_FORWARD_A_DIMENSIONS, "", "");
		drawMux_TXT(MUX_FORWARD_B_COORDS, MUX_FORWARD_B_DIMENSIONS, "", "");
		drawEqZero_TXT();
		ctx.setFont("13px arial");
		drawPC4_TXT();
		drawSE32_TXT();
		drawSE64_TXT();
		ctx.setFont("12px arial");
//		drawInstructionFields_TXT();
		drawReadAddress_TXT();
		drawReadReg1_TXT();
		drawReadReg2_TXT();
		drawWriteReg_TXT();
		drawWriteData_TXT();
		drawReadData1_TXT();
		drawReadData2_TXT();
		drawDataMemAddress_TXT();
		drawDataMemWriteData_TXT();
		drawDataMemReadData_TXT(stxr);
		ctx.setFillStyle(DatapathGraphics.CONTROL_BLUE);
		ctx.setFont("11px arial");
		drawEXMWB_TXT(IDEX_EX_COORDS, IDEX_EX_DIMENSIONS, "EX");
		drawEXMWB_TXT(IDEX_M_COORDS, IDEX_M_DIMENSIONS, "M");
		drawEXMWB_TXT(IDEX_WB_COORDS, IDEX_WB_DIMENSIONS, "WB");
		drawEXMWB_TXT(EXMEM_M_COORDS, EXMEM_M_DIMENSIONS, "M");
		drawEXMWB_TXT(EXMEM_WB_COORDS, EXMEM_WB_DIMENSIONS, "WB");
		drawEXMWB_TXT(MEMWB_WB_COORDS, MEMWB_WB_DIMENSIONS, "WB");
		ctx.setFont("bold 14px arial");
		drawControl_TXT();
		drawALUControl_TXT();
		ctx.setFont("bold 12px arial");
		drawHazardDetectionUnit_TXT();
		drawForwardingUnit_TXT();
		//ctx.setFont("12px arial");
		//drawALUZero_TXT(zero);
		
	}
	
	private void drawPC_TXT() {
		TextMetrics t = ctx.measureText("PC");
		ctx.fillText("PC", PC_COORDS[0]+PC_DIMENSIONS[0]/2-t.getWidth()/2, PC_COORDS[1]+PC_DIMENSIONS[1]/2+5);
	}
	
	private void drawPCALU_TXT() {
		TextMetrics t = ctx.measureText("Add");
		ctx.fillText("Add", ALU_PC_COORDS[0]+3*ALU_PC_DIMENSIONS[0]/5-t.getWidth()/2, ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2+5);
	}
	
	private void drawInsMem_TXT() {
		TextMetrics t = ctx.measureText("Instruction");
		ctx.fillText("Instruction", INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]/2-t.getWidth()/2, INS_MEM_COORDS[1]+5*INS_MEM_DIMENSIONS[1]/6);
		t = ctx.measureText("memory");
		ctx.fillText("memory", INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]/2-t.getWidth()/2, INS_MEM_COORDS[1]+5*INS_MEM_DIMENSIONS[1]/6+12.5);
	}
	
	private void drawMux_TXT(double[] muxCoords, double[] muxDimensions, String top, String bottom) {
		TextMetrics t = ctx.measureText("M");
		ctx.fillText("M", muxCoords[0]+muxDimensions[0]/2-t.getWidth()/2, muxCoords[1]+muxDimensions[1]/2-5);
		t = ctx.measureText("u");
		ctx.fillText("u", muxCoords[0]+muxDimensions[0]/2-t.getWidth()/2, muxCoords[1]+muxDimensions[1]/2+5);
		t = ctx.measureText("x");
		ctx.fillText("x", muxCoords[0]+muxDimensions[0]/2-t.getWidth()/2, muxCoords[1]+muxDimensions[1]/2+15);
		ctx.setFont("13px arial");
		t = ctx.measureText(top);
		ctx.fillText(top, muxCoords[0]+muxDimensions[0]/2-t.getWidth()/2, muxCoords[1]+14);
		t = ctx.measureText(bottom);
		ctx.fillText(bottom, muxCoords[0]+muxDimensions[0]/2-t.getWidth()/2, muxCoords[1]+muxDimensions[1]-4);
		ctx.setFont("bold 14px arial");
	}
	
	private void drawEXMWB_TXT(double[] coord, double[] dim, String word) {
		TextMetrics t = ctx.measureText(word);
		ctx.fillText(word, coord[0]+dim[0]/2-t.getWidth()/2, coord[1]+dim[1]/2);
		
	}
	
	private void drawRegFile_TXT() {
		TextMetrics t = ctx.measureText("Registers");
		ctx.fillText("Registers", REG_FILE_COORDS[0]+2*REG_FILE_DIMENSIONS[0]/3-t.getWidth()/2, REG_FILE_COORDS[1]+19*REG_FILE_DIMENSIONS[1]/20);
	}
	
	private void drawSignExtend_TXT() {
		TextMetrics t = ctx.measureText("Sign-");
		ctx.fillText("Sign-", SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0]/2-t.getWidth()/2, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/3+5);
		t = ctx.measureText("extend");
		ctx.fillText("extend", SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0]/2-t.getWidth()/2, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/3+20);
	}
	
	private void drawPad_TXT() {
		TextMetrics t = ctx.measureText("Pad");
		ctx.fillText("Pad", SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0]/2-t.getWidth()/2, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2+5);
	}
	
	private void drawShiftLeft2_TXT() {
		TextMetrics t = ctx.measureText("Shift");
		ctx.fillText("Shift", SHIFT_LEFT2_COORDS[0]+SHIFT_LEFT2_DIMENSIONS[0]/2-t.getWidth()/2, SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1]/3+5);
		t = ctx.measureText("left 2");
		ctx.fillText("left 2", SHIFT_LEFT2_COORDS[0]+SHIFT_LEFT2_DIMENSIONS[0]/2-t.getWidth()/2, SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1]/3+20);
	}
	
	private void drawALUMain_TXT() {
		TextMetrics t = ctx.measureText("ALU");
		ctx.fillText("ALU", ALU_MAIN_COORDS[0]+3*ALU_MAIN_DIMENSIONS[0]/5-t.getWidth()/2, ALU_MAIN_COORDS[1]+ALU_MAIN_DIMENSIONS[1]/2+5);
	}
	
	private void drawFlag_TXT() {
		TextMetrics t = ctx.measureText("N");
		ctx.fillText("N", FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]/8-t.getWidth()/2, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]/2+5);
		t = ctx.measureText("Z");
		ctx.fillText("Z", FLAGS_COORDS[0]+3*FLAGS_DIMENSIONS[0]/8-t.getWidth()/2, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]/2+5);
		t = ctx.measureText("C");
		ctx.fillText("C", FLAGS_COORDS[0]+5*FLAGS_DIMENSIONS[0]/8-t.getWidth()/2, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]/2+5);
		t = ctx.measureText("V");
		ctx.fillText("V", FLAGS_COORDS[0]+7*FLAGS_DIMENSIONS[0]/8-t.getWidth()/2, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]/2+5);
	}
	
	private void drawALUBranch_TXT() {
		TextMetrics t = ctx.measureText("Add");
		ctx.fillText("Add", ALU_BRANCH_COORDS[0]+3*ALU_BRANCH_DIMENSIONS[0]/5-t.getWidth()/2, ALU_BRANCH_COORDS[1]+ALU_BRANCH_DIMENSIONS[1]/2+5);
	}
	
	private void drawDataMem_TXT() {
		TextMetrics t = ctx.measureText("Data");
		ctx.fillText("Data", DATA_MEM_COORDS[0]+6.5*DATA_MEM_DIMENSIONS[0]/10-t.getWidth()/2, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]/2+10);
		t = ctx.measureText("memory");
		ctx.fillText("memory", DATA_MEM_COORDS[0]+6.5*DATA_MEM_DIMENSIONS[0]/10-t.getWidth()/2, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]/2+25);
	}

	private void drawPC4_TXT() {
		TextMetrics t = ctx.measureText("4");
		ctx.fillText("4", INS_MEM_COORDS[0]-7.5-t.getWidth()/2, ALU_PC_COORDS[1]+13*ALU_PC_DIMENSIONS[1]/16+5);
	}
	
	private void drawSE32_TXT() {
		TextMetrics t = ctx.measureText("32");
		ctx.fillText("32", SIGN_EXTEND_COORDS[0]-10-t.getWidth()/2, 
				SIGN_EXTEND_COORDS[1]+2*SIGN_EXTEND_DIMENSIONS[1]/5);
	}
	
	private void drawSE64_TXT() {
		TextMetrics t = ctx.measureText("64");
		ctx.fillText("64", SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0]+10-t.getWidth()/2, 
				SIGN_EXTEND_COORDS[1]+2*SIGN_EXTEND_DIMENSIONS[1]/5);
	}
	
//	private void drawInstructionFields_TXT() {
//		TextMetrics t = ctx.measureText("Instruction");
//		ctx.fillText("Instruction", INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]-4-t.getWidth(), INS_MEM_COORDS[1]+INS_MEM_DIMENSIONS[1]/2);
//		t = ctx.measureText("[31-0]");
//		ctx.fillText("[31-0]", INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]-4-t.getWidth(), INS_MEM_COORDS[1]+INS_MEM_DIMENSIONS[1]/2+10);
//		ctx.fillText("Instruction [31-21]", IFID_VERTICAL_X+5, CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-5);
//		ctx.fillText("Instruction [9-5]", IFID_VERTICAL_X+5, REG_FILE_COORDS[1]+REG_FILE_DIMENSIONS[1]/10-5);
//		ctx.fillText("Instruction [20-16]", IFID_VERTICAL_X+5, MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[0]/2-5);
//		ctx.fillText("Instruction [4-0]", IFID_VERTICAL_X+5,REG_FILE_COORDS[1]+7*REG_FILE_DIMENSIONS[1]/10+15);
//		ctx.fillText("Instruction [31-0]", IFID_VERTICAL_X+5, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2-5);
//		ctx.fillText("Instruction [31-21]", REG_FILE_COORDS[0]+5, ALU_CONTROL_COORDS[1]+1.25*ALU_CONTROL_DIMENSIONS[1]-5);
//	}
	
	private void drawReadAddress_TXT() {
		ctx.fillText("Read", INS_MEM_COORDS[0]+3, INS_MEM_COORDS[1]+REG_FILE_DIMENSIONS[1]/10);
		ctx.fillText("address", INS_MEM_COORDS[0]+3, INS_MEM_COORDS[1]+REG_FILE_DIMENSIONS[1]/10+10);
	}
	
	private void drawReadReg1_TXT() {
		ctx.fillText("Read", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+REG_FILE_DIMENSIONS[1]/10);
		ctx.fillText("register 1", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+REG_FILE_DIMENSIONS[1]/10+10);
	}
	
	private void drawReadReg2_TXT() {
		ctx.fillText("Read", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+4*REG_FILE_DIMENSIONS[1]/10);
		ctx.fillText("register 2", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+4*REG_FILE_DIMENSIONS[1]/10+10);
	}
	
	private void drawWriteReg_TXT() {
		ctx.fillText("Write", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+7*REG_FILE_DIMENSIONS[1]/10);
		ctx.fillText("register", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+7*REG_FILE_DIMENSIONS[1]/10+10);
	}
	
	private void drawWriteData_TXT() {
		ctx.fillText("Write", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10);
		ctx.fillText("data", REG_FILE_COORDS[0]+3, REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10+10);
	}
	
	private void drawReadData1_TXT() {
		TextMetrics t = ctx.measureText("Read");
		ctx.fillText("Read", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[0]/2);
		t = ctx.measureText("data 1");
		ctx.fillText("data 1", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), MUX_FORWARD_A_COORDS[1]+MUX_FORWARD_A_DIMENSIONS[0]/2+10);
	}
	
	private void drawReadData2_TXT() {
		TextMetrics t = ctx.measureText("Read");
		ctx.fillText("Read", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[0]/2);
		t = ctx.measureText("data 1");
		ctx.fillText("data 2", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), MUX_FORWARD_B_COORDS[1]+MUX_FORWARD_B_DIMENSIONS[0]/2+10);
	}
	
	private void drawEqZero_TXT() {
		TextMetrics t = ctx.measureText("=0");
		ctx.fillText("=0", ZERO_TEST_COORDS[0]+ZERO_TEST_DIMENSIONS[0]/2-t.getWidth()/2, ZERO_TEST_COORDS[1]+ZERO_TEST_DIMENSIONS[1]/2+5);
	}
	
	private void drawDataMemAddress_TXT() {
		ctx.fillText("Address", DATA_MEM_COORDS[0]+3, ALU_MAIN_COORDS[1]+ALU_MAIN_DIMENSIONS[1]/2+4);
	}
	
	private void drawDataMemWriteData_TXT() {
		ctx.fillText("Write", DATA_MEM_COORDS[0]+3, DATA_MEM_COORDS[1]+5*DATA_MEM_DIMENSIONS[1]/6);
		ctx.fillText("Data", DATA_MEM_COORDS[0]+3, DATA_MEM_COORDS[1]+5*DATA_MEM_DIMENSIONS[1]/6+12);
	}
	
	private void drawDataMemReadData_TXT(boolean stxr) {
		if (!stxr) {
			TextMetrics t = ctx.measureText("Read");
			ctx.fillText("Read", DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]-4-t.getWidth(), MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2);
			t = ctx.measureText("data");
			ctx.fillText("data", DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]-4-t.getWidth(), MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2+10);
		} else {
			TextMetrics t = ctx.measureText("Store");
			ctx.fillText("Store", DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]-4-t.getWidth(), MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2);
			t = ctx.measureText("outcome");
			ctx.fillText("outcome", DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]-4-t.getWidth(), MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2+10);
		}
	}
	
	private void drawControl_TXT() {
		TextMetrics t = ctx.measureText("Control");
		ctx.fillText("Control", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]/2-t.getWidth()/2, CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2+5);
	}
	
	private void drawALUControl_TXT() {
		TextMetrics t = ctx.measureText("ALU");
		ctx.fillText("ALU", ALU_CONTROL_COORDS[0]+ALU_CONTROL_DIMENSIONS[0]/2-t.getWidth()/2, ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1]/3+10);
		t = ctx.measureText("control");
		ctx.fillText("control", ALU_CONTROL_COORDS[0]+ALU_CONTROL_DIMENSIONS[0]/2-t.getWidth()/2, ALU_CONTROL_COORDS[1]+2*ALU_CONTROL_DIMENSIONS[1]/3);
	}
	
	private void drawHazardDetectionUnit_TXT() {
		TextMetrics t = ctx.measureText("Hazard");
		ctx.fillText("Hazard", HAZARD_DETECT_COORDS[0]+HAZARD_DETECT_DIMENSIONS[0]/2-t.getWidth()/2, HAZARD_DETECT_COORDS[1]+HAZARD_DETECT_DIMENSIONS[1]/3);
		t = ctx.measureText("Detection");
		ctx.fillText("Detection", HAZARD_DETECT_COORDS[0]+HAZARD_DETECT_DIMENSIONS[0]/2-t.getWidth()/2, HAZARD_DETECT_COORDS[1]+HAZARD_DETECT_DIMENSIONS[1]/3+10);
		t = ctx.measureText("Unit");
		ctx.fillText("Unit", HAZARD_DETECT_COORDS[0]+HAZARD_DETECT_DIMENSIONS[0]/2-t.getWidth()/2, 1.2*HAZARD_DETECT_COORDS[1]+HAZARD_DETECT_DIMENSIONS[1]/3+5);
	}
	
	private void drawForwardingUnit_TXT() {
		TextMetrics t = ctx.measureText("Forwarding");
		ctx.fillText("Forwarding", FORWARD_UNIT_COORDS[0]+FORWARD_UNIT_DIMENSIONS[0]/2-t.getWidth()/2, FORWARD_UNIT_COORDS[1]+FORWARD_UNIT_DIMENSIONS[1]/3+5);
		t = ctx.measureText("Unit");
		ctx.fillText("Unit", FORWARD_UNIT_COORDS[0]+FORWARD_UNIT_DIMENSIONS[0]/2-t.getWidth()/2, FORWARD_UNIT_COORDS[1]+2*FORWARD_UNIT_DIMENSIONS[1]/3+5);
	}
	
	//-------------------------------------- Drawing Text Strings (END) --------------------------------------------//
	
	
	private void drawControlSignalVals() {
		// TODO
	}
	
	private double canvasWidth;
	private double canvasHeight;
	private Canvas canvas;
	private Context2d ctx;
}
