package com.arm.legv8simulator.client;

import com.arm.legv8simulator.client.cpu.ControlUnitConfiguration;
import com.arm.legv8simulator.client.instruction.Instruction;
import com.arm.legv8simulator.client.instruction.Mnemonic;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

/**
 * Draws the LEGv8 single cycle datapath as defined in Patterson and Hennessy ARM Edition.
 * 
 * @author Jonathan Wright, 2016
 */
public class SingleCycleVis {
	
	private static final int CANVAS_HEIGHT_REF = 625;
	private static final int CANVAS_WIDTH_REF = 895;
	
	// Coordinate positions  and dimensions for each of the components in the datapath
	// This is utterly hideous but  necessary
	// first number is width in pixels, second number is height in pixels
	private static final double[] INS_MEM_DIMENSIONS = {80, 120};
	// first number is x coordinate, second number is y coordinate in pixels
	private static final double[] INS_MEM_COORDS = {80, 335};
	private static final double[] PC_DIMENSIONS = {30, 60};
	private static final double[] PC_COORDS = {20, INS_MEM_COORDS[1]+INS_MEM_DIMENSIONS[1]/10-PC_DIMENSIONS[1]/2};
	private static final double[] REG_FILE_DIMENSIONS = {110, 140};
	private static final double[] REG_FILE_COORDS = {335, 325};
	private static final double[] DATA_MEM_DIMENSIONS = {90, 130};
	private static final double[] DATA_MEM_COORDS = {715, 385};
	private static final double[] ALU_PC_DIMENSIONS = {45, 75};
	private static final double[] ALU_PC_COORDS = {150, 20};
	private static final double[] ALU_MAIN_DIMENSIONS = {75, 110};
	private static final double[] ALU_MAIN_COORDS = {540, 340};
	private static final double[] MUX_PC_DIMENSIONS = {25, 80};
	private static final double[] MUX_PC_COORDS = {800, ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2-MUX_PC_DIMENSIONS[0]/2};
	private static final double[] MUX_REG2LOC_DIMENSIONS = {25, 65};
	private static final double[] MUX_REG2LOC_COORDS = {295, REG_FILE_COORDS[1]+4*REG_FILE_DIMENSIONS[1]/10-MUX_REG2LOC_DIMENSIONS[1]/2};
	private static final double[] MUX_READ_REG_DIMENSIONS = {25, 65};
	private static final double[] MUX_READ_REG_COORDS = {495, ALU_MAIN_COORDS[1]+13*ALU_MAIN_DIMENSIONS[1]/16-MUX_READ_REG_DIMENSIONS[1]/2};
	private static final double[] MUX_READ_DATA_MEM_DIMENSIONS = {25, 65};
	private static final double[] MUX_READ_DATA_MEM_COORDS = {835, 420};
	private static final double[] ALU_BRANCH_DIMENSIONS = {70, 90};
	private static final double[] ALU_BRANCH_COORDS = {595, MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[1]-MUX_PC_DIMENSIONS[0]/2-ALU_BRANCH_DIMENSIONS[1]/2};
	private static final double[] CONTROL_DIMENSIONS = {60, 160};
	private static final double[] CONTROL_COORDS = {290, 155};
	private static final double[] ALU_CONTROL_DIMENSIONS = {55, 70};
	private static final double[] ALU_CONTROL_COORDS = {510, 515};
	private static final double[] SIGN_EXTEND_DIMENSIONS = {55, 70};
	private static final double[] SIGN_EXTEND_COORDS = {375, 500};
	private static final double[] SHIFT_LEFT2_DIMENSIONS = {45, 50};
	private static final double[] SHIFT_LEFT2_COORDS = {525, ALU_BRANCH_COORDS[1]+13*ALU_BRANCH_DIMENSIONS[1]/16-SHIFT_LEFT2_DIMENSIONS[1]/2};
	private static final double[] FLAGS_DIMENSIONS = {88, 20};
	private static final double[] FLAGS_COORDS = {535, 310};
	private static final double[] FLAG_AND_DIMENSIONS = {20, 20};
	private static final double[] FLAG_AND_COORDS = {645, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]/2-4*FLAG_AND_DIMENSIONS[1]/5};
	private static final double[] ZERO_AND_DIMENSIONS = {20, 20};
	private static final double[] ZERO_AND_COORDS = {715, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]/2-4*ZERO_AND_DIMENSIONS[1]/5};
	
	private static final double CONTROL_OFFSET = 2.5;
	private static final double CONTROL_PADDING	= (CONTROL_DIMENSIONS[1]-2*CONTROL_OFFSET)/10;
	
	private static final double[] BRANCH_OR_DIMENSIONS = {30, 25};
	private static final double[] BRANCH_OR_COORDS = {765, CONTROL_COORDS[1]+CONTROL_OFFSET+CONTROL_PADDING-BRANCH_OR_DIMENSIONS[1]/5};
	
	private static final double PC_PCALU_VERTICAL_X = PC_COORDS[0]+PC_DIMENSIONS[0]+(INS_MEM_COORDS[0]-PC_COORDS[0]-PC_DIMENSIONS[0])/3;
	private static final double INS_MEM_VERTICAL_X = INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+(REG_FILE_COORDS[0]-INS_MEM_COORDS[0]-INS_MEM_DIMENSIONS[0])/12;
	private static final double SHIFT2VERT_X = REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+3*(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5;
	private static final double ZERO_AND_VERT_X = ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]+4*(DATA_MEM_COORDS[0]-ALU_MAIN_COORDS[0]-ALU_MAIN_DIMENSIONS[0])/5;
	
	private static final double[] INSTRUCTION_TEXT_COORDS = {PC_COORDS[0], ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1]};
	
	/**
	 * @param canvasWidth	the width of this single cycle datapath
	 * @param canvasHeight	the height of this single cycle datapath
	 */
	public SingleCycleVis(double canvasWidth, double canvasHeight) {
		canvas = Canvas.createIfSupported();
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		init();
	}
	
	/**
	 * Draws the single cycle datapath without any wire/component highlighting
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
	
	/**
	 * Redraws the datapath to visualise the specified instruction.
	 * 
	 * @param ins				the instruction to visualise on the datapath
	 * @param branchTaken		whether a branch was taken on this instruction
	 * @param stxrSucceed		if <code>ins</code> is STXR; whether the store to memory succeeded or not
	 * @param instructionIndex	the index of <code>ins</code>, used to calculate relative branch addresses
	 * @param label				the string label used in branch instructions
	 */
	/*
	 * Ideally this method would be overloaded to remove the unnecessary parameters
	 */
	public void updateDatapath(Instruction ins, boolean branchTaken, boolean stxrSucceed, int instructionIndex, String label) {
		clearCanvas();
		Mnemonic m = ins.getMnemonic();
		switch (m.type) {
		case MNEMONIC_RRR :
			drawDatapathRRR(m.equals(Mnemonic.ADDS) || m.equals(Mnemonic.SUBS) || m.equals(Mnemonic.ANDS), m);
			drawInstructionTextRRR(ins);
			break;
		case MNEMONIC_RRI : 
			drawDatapathRRI(m.equals(Mnemonic.ADDIS) || m.equals(Mnemonic.SUBIS) || m.equals(Mnemonic.ANDIS), m);
			if (m.equals(Mnemonic.LSL) || m.equals(Mnemonic.LSR)) {
				drawInstructionTextShift(ins);
			} else {
				drawInstructionTextRRI(ins);
			}
			break;
		case MNEMONIC_RISI :
			drawDatapathRRI(false, m);
			drawInstructionTextRISI(ins);
			break;
		case MNEMONIC_RM :
			if (m.equals(Mnemonic.STUR) || m.equals(Mnemonic.STURW) || m.equals(Mnemonic.STURH) || m.equals(Mnemonic.STURB)) {
				drawDatapathStore(m);
				drawInstructionTextRM(ins);
			} else {
				drawDatapathLoad(m);
				drawInstructionTextRM(ins);
			}
			break;
		case MNEMONIC_RRM :
			drawDatapathSTXR(stxrSucceed, m);
			drawInstructionTextRRM(ins);
			break;
		case MNEMONIC_RL :
			drawDatapathCB(branchTaken, m);
			drawInstructionTextRL(ins, instructionIndex, label);
			break;
		case MNEMONIC_L :
			if (m.equals(Mnemonic.B)) {
				drawDatapathB();
			} else {
				drawDatapathBcond(branchTaken);
			}
			drawInstructionTextL(ins, instructionIndex, label);
			break;
		default:
		}
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
	
	private void drawInstructionTextRRM(Instruction ins) {
		StringBuilder instruction = new StringBuilder("");
		instruction.append(ins.getMnemonic().nameUpper + "   ");
		instruction.append(getRegString(ins.getArgs()[0]) + ", ");
		instruction.append(getRegString(ins.getArgs()[1]) + ", [");
		instruction.append(getRegString(ins.getArgs()[2]) + ", ");
		instruction.append("#" + ins.getArgs()[3] + "]");
		String[][] fields = new String[5][2];
		fields[0][0] = ins.getMnemonic().opcode;
		fields[0][1] = "Opcode";
		fields[1][0] = getRegBinary(ins.getArgs()[0]);
		fields[1][1] = "Rs";
		fields[2][0] = getImmBinary(31, 6, false);
		fields[2][1] = "Rt2";
		fields[3][0] = getRegBinary(ins.getArgs()[2]);
		fields[3][1] = "Rn";
		fields[4][0] = getRegBinary(ins.getArgs()[1]);
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
	
	private void drawDataWiresInit() {
		draw4_ALUPC(DatapathGraphics.BLACK);
		drawPC_InsMem(DatapathGraphics.BLACK);
		drawPC_ALUPC(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawInsMem_Control(DatapathGraphics.BLACK);
		drawInsMem_RegFileRead1(DatapathGraphics.BLACK);
		drawInsMem_RegFileWrite(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_SignExtend(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawRegFile_ALUMain(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawALUPC_MuxPC(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxPC_PC(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.BLACK);
		drawMuxReadRegData_ALUMain(DatapathGraphics.BLACK);
		drawMuxReadMemData_RegFile(DatapathGraphics.BLACK);
		drawSignExtend_MuxReadRegData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
	}
	
	private void draw4_ALUPC(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, PC_PCALU_VERTICAL_X+2*(ALU_PC_COORDS[0]-PC_PCALU_VERTICAL_X)/3, 
				ALU_PC_COORDS[1]+13*ALU_PC_DIMENSIONS[1]/16, 
				ALU_PC_COORDS[0], color, false);
	}
	
	private void drawPC_InsMem(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, PC_COORDS[0]+PC_DIMENSIONS[0], 
				PC_COORDS[1]+PC_DIMENSIONS[1]/2, 
				INS_MEM_COORDS[0], color, false);
	}
	
	private void drawPC_ALUPC(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, PC_PCALU_VERTICAL_X, 
				ALU_PC_COORDS[1]+3*ALU_PC_DIMENSIONS[1]/16, 
				ALU_PC_COORDS[0], color, false);
		DatapathGraphics.drawHorizontalSegment(ctx, PC_COORDS[0]+PC_DIMENSIONS[0], 
				PC_COORDS[1]+PC_DIMENSIONS[1]/2, PC_PCALU_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, PC_PCALU_VERTICAL_X, 
				PC_COORDS[1]+PC_DIMENSIONS[1]/2, ALU_PC_COORDS[1]+3*ALU_PC_DIMENSIONS[1]/16, 
				color, true, false);
	}
	
	private void drawPC_ALUBranch(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, PC_COORDS[0]+PC_DIMENSIONS[0], 
				PC_COORDS[1]+PC_DIMENSIONS[1]/2, PC_PCALU_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, PC_PCALU_VERTICAL_X, 
				PC_COORDS[1]+PC_DIMENSIONS[1]/2, CONTROL_COORDS[1], color, true, false);
		DatapathGraphics.drawHorizontalSegment(ctx, PC_PCALU_VERTICAL_X, CONTROL_COORDS[1], 
				(INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+REG_FILE_COORDS[0])/2, color, true, false);
		DatapathGraphics.drawVerticalSegment(ctx, (INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+REG_FILE_COORDS[0])/2, 
				CONTROL_COORDS[1], ALU_BRANCH_COORDS[1]+3*ALU_BRANCH_DIMENSIONS[1]/16, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, (INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+REG_FILE_COORDS[0])/2, 
				ALU_BRANCH_COORDS[1]+3*ALU_BRANCH_DIMENSIONS[1]/16, ALU_BRANCH_COORDS[0], color, false);
	}
	
	private void drawInsMem_Control(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), INS_MEM_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X, INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), 
				CONTROL_COORDS[1]+(CONTROL_DIMENSIONS[1]/2), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_VERTICAL_X, CONTROL_COORDS[1]+(CONTROL_DIMENSIONS[1]/2), 
				CONTROL_COORDS[0], color, false);
	}
	
	private void drawInsMem_RegFileRead1(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), INS_MEM_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X, INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), 
				REG_FILE_COORDS[1]+(REG_FILE_DIMENSIONS[1]/10), color, false, true);
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_VERTICAL_X, REG_FILE_COORDS[1]+(REG_FILE_DIMENSIONS[1]/10), 
				REG_FILE_COORDS[0], color, false);
	}
	
	private void drawInsMem_RegFileWrite(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), INS_MEM_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X, INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), 
				REG_FILE_COORDS[1]+(7*REG_FILE_DIMENSIONS[1]/10), color, true, true);
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_VERTICAL_X, REG_FILE_COORDS[1]+(7*REG_FILE_DIMENSIONS[1]/10), 
				REG_FILE_COORDS[0], color, false);
	}
	
	private void drawInsMem_MuxReg2Loc1(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), INS_MEM_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X, INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), 
				REG_FILE_COORDS[1]+(7*REG_FILE_DIMENSIONS[1]/10), color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_VERTICAL_X, REG_FILE_COORDS[1]+(7*REG_FILE_DIMENSIONS[1]/10), 
				INS_MEM_VERTICAL_X+(REG_FILE_COORDS[0]-INS_MEM_VERTICAL_X)/2, color, true, false);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X+(REG_FILE_COORDS[0]-INS_MEM_VERTICAL_X)/2,
				REG_FILE_COORDS[1]+(7*REG_FILE_DIMENSIONS[1]/10),
				MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[1]-MUX_REG2LOC_DIMENSIONS[0]/2, 
				color, true, false);
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_VERTICAL_X+(REG_FILE_COORDS[0]-INS_MEM_VERTICAL_X)/2, 
				MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[1]-MUX_REG2LOC_DIMENSIONS[0]/2, 
				MUX_REG2LOC_COORDS[0], color, false);
	}
	
	private void drawInsMem_MuxReg2Loc2(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), INS_MEM_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X, INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), 
				MUX_REG2LOC_COORDS[1]+(MUX_REG2LOC_DIMENSIONS[0]/2), color, false, true);
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_VERTICAL_X, MUX_REG2LOC_COORDS[1]+(MUX_REG2LOC_DIMENSIONS[0]/2), 
				MUX_REG2LOC_COORDS[0], color, false);
	}
	
	private void drawInsMem_SignExtend(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), INS_MEM_VERTICAL_X,color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X, INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), 
				SIGN_EXTEND_COORDS[1]+(SIGN_EXTEND_DIMENSIONS[1]/2), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, INS_MEM_VERTICAL_X, SIGN_EXTEND_COORDS[1]+(SIGN_EXTEND_DIMENSIONS[1]/2), 
				SIGN_EXTEND_COORDS[0], color, false);
		DatapathGraphics.drawDiagSlash(ctx, REG_FILE_COORDS[0]+(SIGN_EXTEND_COORDS[0]-REG_FILE_COORDS[0])/2-4, 
				SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, color);
	}
	
	private void drawInsMem_ALUControl(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0], 
				INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), INS_MEM_VERTICAL_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, INS_MEM_VERTICAL_X, INS_MEM_COORDS[1]+(INS_MEM_DIMENSIONS[1]/2), 
				SIGN_EXTEND_COORDS[1]+(SIGN_EXTEND_DIMENSIONS[1]/2), color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, INS_MEM_VERTICAL_X, SIGN_EXTEND_COORDS[1]+(SIGN_EXTEND_DIMENSIONS[1]/2), 
				REG_FILE_COORDS[0], color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, REG_FILE_COORDS[0], ALU_CONTROL_COORDS[1]+1.25*ALU_CONTROL_DIMENSIONS[1], 
				SIGN_EXTEND_COORDS[1]+(SIGN_EXTEND_DIMENSIONS[1]/2), color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, REG_FILE_COORDS[0], ALU_CONTROL_COORDS[1]+1.25*ALU_CONTROL_DIMENSIONS[1], 
				SHIFT2VERT_X, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, SHIFT2VERT_X, ALU_CONTROL_COORDS[1]+1.25*ALU_CONTROL_DIMENSIONS[1], 
				ALU_CONTROL_COORDS[1]+(ALU_CONTROL_DIMENSIONS[1]/2), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, SHIFT2VERT_X, ALU_CONTROL_COORDS[1]+(ALU_CONTROL_DIMENSIONS[1]/2), 
				ALU_CONTROL_COORDS[0], color, false);
	}
	
	private void drawRegFile_ALUMain(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], ALU_MAIN_COORDS[1]+(3*ALU_MAIN_DIMENSIONS[1]/16), 
				ALU_MAIN_COORDS[0], color, false);
	}
	
	private void drawRegFile_MuxReadRegData(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], 
				MUX_READ_REG_COORDS[1]+(MUX_READ_REG_DIMENSIONS[0]/2), 
				MUX_READ_REG_COORDS[0], color, false);
	}
	
	private void drawRegFile_DataMem(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], 
				MUX_READ_REG_COORDS[1]+(MUX_READ_REG_DIMENSIONS[0]/2), 
				REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5, 
				DATA_MEM_COORDS[1]+(5*DATA_MEM_DIMENSIONS[1]/6), 
				MUX_READ_REG_COORDS[1]+(MUX_READ_REG_DIMENSIONS[0]/2), color, false, true);
		DatapathGraphics.drawRightArrow(ctx, 
				REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5, 
				DATA_MEM_COORDS[1]+(5*DATA_MEM_DIMENSIONS[1]/6), DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawDataMem_MuxReadMemData(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0],  
				MUX_READ_DATA_MEM_COORDS[1]+(MUX_READ_DATA_MEM_DIMENSIONS[0]/2), 
				MUX_READ_DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawALUPC_MuxPC(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_PC_COORDS[0]+ALU_PC_DIMENSIONS[0], ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2,
				ALU_PC_COORDS[0]+ALU_PC_DIMENSIONS[0]+(ALU_BRANCH_COORDS[0]-ALU_PC_COORDS[0]-ALU_PC_DIMENSIONS[0])/2,
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				ALU_PC_COORDS[0]+ALU_PC_DIMENSIONS[0]+(ALU_BRANCH_COORDS[0]-ALU_PC_COORDS[0]-ALU_PC_DIMENSIONS[0])/2, 
				ALU_PC_COORDS[1]+ALU_PC_DIMENSIONS[1]/2, MUX_PC_COORDS[1]+(MUX_PC_DIMENSIONS[0]/2), 
				color, false, false);
		DatapathGraphics.drawRightArrow(ctx, 
				ALU_PC_COORDS[0]+ALU_PC_DIMENSIONS[0]+(ALU_BRANCH_COORDS[0]-ALU_PC_COORDS[0]-ALU_PC_DIMENSIONS[0])/2, 
				MUX_PC_COORDS[1]+(MUX_PC_DIMENSIONS[0]/2), MUX_PC_COORDS[0], color, false);
	}
	
	private void drawALUBranch_MuxPC(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, ALU_BRANCH_COORDS[0]+ALU_BRANCH_DIMENSIONS[0], 
				ALU_BRANCH_COORDS[1]+(ALU_BRANCH_DIMENSIONS[1]/2), MUX_PC_COORDS[0], color, false);
	}
	
	private void drawALUMain_Flags(CssColor color) {
		DatapathGraphics.drawUpArrow(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]/2, 
				ALU_MAIN_COORDS[1]+ALU_MAIN_DIMENSIONS[1]/8, FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1], 
				color, false);
	}
	
	private void drawALUMain_DataMem(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0], 
				ALU_MAIN_COORDS[1]+(5*ALU_MAIN_DIMENSIONS[1]/8), DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawALUMain_MuxReadMemData(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0], 
				ALU_MAIN_COORDS[1]+(5*ALU_MAIN_DIMENSIONS[1]/8), ZERO_AND_VERT_X, color, false, true);
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_AND_VERT_X, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+15, 
				ALU_MAIN_COORDS[1]+(5*ALU_MAIN_DIMENSIONS[1]/8), color, false, true);
		DatapathGraphics.drawHorizontalSegment(ctx, ZERO_AND_VERT_X, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+15,
				DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]+(MUX_READ_DATA_MEM_COORDS[0]-DATA_MEM_COORDS[0]-DATA_MEM_DIMENSIONS[0])/2,
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]+(MUX_READ_DATA_MEM_COORDS[0]-DATA_MEM_COORDS[0]-DATA_MEM_DIMENSIONS[0])/2, 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+15, 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]-MUX_READ_DATA_MEM_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, 
				DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]+(MUX_READ_DATA_MEM_COORDS[0]-DATA_MEM_COORDS[0]-DATA_MEM_DIMENSIONS[0])/2, 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]-MUX_READ_DATA_MEM_DIMENSIONS[0]/2, 
				MUX_READ_DATA_MEM_COORDS[0], color, false);
	}
	
	private void drawMuxPC_PC(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0], MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[1]/2,
				MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]+ALU_PC_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]+ALU_PC_DIMENSIONS[0], MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[1]/2, 
				ALU_PC_COORDS[1]-ALU_PC_DIMENSIONS[1]/4, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, PC_COORDS[0]-PC_DIMENSIONS[0]/2, ALU_PC_COORDS[1]-ALU_PC_DIMENSIONS[1]/4,
				MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]+ALU_PC_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, PC_COORDS[0]-PC_DIMENSIONS[0]/2, PC_COORDS[1]+(PC_DIMENSIONS[1]/2), 
				ALU_PC_COORDS[1]-ALU_PC_DIMENSIONS[1]/4, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, PC_COORDS[0]-PC_DIMENSIONS[0]/2, 
				PC_COORDS[1]+(PC_DIMENSIONS[1]/2), PC_COORDS[0], color, false);
	}
	
	private void drawMuxReg2Loc_RegFileRead2(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], 
				MUX_REG2LOC_COORDS[1]+(MUX_REG2LOC_DIMENSIONS[1]/2), REG_FILE_COORDS[0], color, false);
	}
	
	private void drawMuxReadRegData_ALUMain(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, MUX_READ_REG_COORDS[0]+MUX_READ_REG_DIMENSIONS[0], 
				MUX_READ_REG_COORDS[1]+(MUX_READ_REG_DIMENSIONS[1]/2), ALU_MAIN_COORDS[0], color, false);
	}
	
	private void drawMuxReadMemData_RegFile(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0], 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]/2,
				MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]+PC_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]+PC_DIMENSIONS[0]/2, 
				ALU_CONTROL_COORDS[1]+(1.5*ALU_CONTROL_DIMENSIONS[1]), 
				MUX_READ_DATA_MEM_COORDS[1]+MUX_READ_DATA_MEM_DIMENSIONS[1]/2, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], 
				ALU_CONTROL_COORDS[1]+(1.5*ALU_CONTROL_DIMENSIONS[1]),
				MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]+PC_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], 
				ALU_CONTROL_COORDS[1]+(1.5*ALU_CONTROL_DIMENSIONS[1]), 
				REG_FILE_COORDS[1]+(9*REG_FILE_DIMENSIONS[1]/10), color, false, false);
		DatapathGraphics.drawRightArrow(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0], 
				REG_FILE_COORDS[1]+(9*REG_FILE_DIMENSIONS[1]/10), REG_FILE_COORDS[0], color, false);
	}
	
	private void drawSignExtend_MuxReadRegData(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0], 
				SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, SHIFT2VERT_X, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, SHIFT2VERT_X, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, 
				MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[1]-MUX_READ_REG_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, SHIFT2VERT_X, 
				MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[1]-MUX_READ_REG_DIMENSIONS[0]/2, 
				MUX_READ_REG_COORDS[0], color, true);
		DatapathGraphics.drawDiagSlash(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], 
				SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, color);
	}
	
	private void drawSignExtend_ShiftLeft2(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0], 
				SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, SHIFT2VERT_X, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, SHIFT2VERT_X, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, 
				SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1]/2, color, false, false);
		DatapathGraphics.drawRightArrow(ctx, SHIFT2VERT_X, 
				SHIFT_LEFT2_COORDS[1]+SHIFT_LEFT2_DIMENSIONS[1]/2, SHIFT_LEFT2_COORDS[0], color, false);
		DatapathGraphics.drawDiagSlash(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0], 
				SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2, color);
	}
	
	private void drawShiftLeft2_ALUBranch(CssColor color) {
		DatapathGraphics.drawRightArrow(ctx, SHIFT_LEFT2_COORDS[0]+SHIFT_LEFT2_DIMENSIONS[0], 
				ALU_BRANCH_COORDS[1]+(13*ALU_BRANCH_DIMENSIONS[1]/16), ALU_BRANCH_COORDS[0], color, false);
	}
	
	private void drawControlSignals() {
		drawReg2Loc(DatapathGraphics.CONTROL_BLUE);
		drawUnconditionalBranch(DatapathGraphics.CONTROL_BLUE);
		drawZeroBranch(DatapathGraphics.CONTROL_BLUE);
		drawFlagBranch(DatapathGraphics.CONTROL_BLUE);
		drawMemRead(DatapathGraphics.CONTROL_BLUE);
		drawMemToReg(DatapathGraphics.CONTROL_BLUE);
		drawMemWrite(DatapathGraphics.CONTROL_BLUE);
		drawFlagWrite(DatapathGraphics.CONTROL_BLUE);
		drawALUSrc(DatapathGraphics.CONTROL_BLUE);
		drawALUOp(DatapathGraphics.CONTROL_BLUE);
		drawRegWrite(DatapathGraphics.CONTROL_BLUE);
		drawBranchOr_MuxPC(DatapathGraphics.CONTROL_BLUE);
		drawFlag_FlagAnd(DatapathGraphics.CONTROL_BLUE);
		drawFlagAnd_BranchOR(DatapathGraphics.CONTROL_BLUE);
		drawALUMain_ZeroAnd(DatapathGraphics.CONTROL_BLUE);
		drawZeroAnd_BranchOR(DatapathGraphics.CONTROL_BLUE);
		drawALUControl_ALUMain(DatapathGraphics.CONTROL_BLUE);
	}
	
	private double getEllipseXIntersect(double xOffset, double y, double xRadius, double yRadius) {
		return xOffset + xRadius + xRadius*Math.sqrt(1-((y*y)/(yRadius*yRadius)));
	}

	private void drawReg2Loc(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET, CONTROL_DIMENSIONS[0]/2, CONTROL_DIMENSIONS[1]/2), 
				CONTROL_COORDS[1]+CONTROL_OFFSET, CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0], 
				CONTROL_COORDS[1]+CONTROL_OFFSET, CONTROL_COORDS[1]+CONTROL_OFFSET-2*CONTROL_PADDING, 
				color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, 
				INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+(INS_MEM_VERTICAL_X-INS_MEM_COORDS[0]-INS_MEM_DIMENSIONS[0])/2, 
				CONTROL_COORDS[1]+CONTROL_OFFSET-2*CONTROL_PADDING,
				CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+(INS_MEM_VERTICAL_X-INS_MEM_COORDS[0]-INS_MEM_DIMENSIONS[0])/2, 
				REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10, CONTROL_COORDS[1]+CONTROL_OFFSET-2*CONTROL_PADDING,
				color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, 
				INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]+(INS_MEM_VERTICAL_X-INS_MEM_COORDS[0]-INS_MEM_DIMENSIONS[0])/2, 
				REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10,
				MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]/2, 
				REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10, MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[1],
				color, false, false);
	}

	private void drawUnconditionalBranch(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+CONTROL_PADDING,BRANCH_OR_COORDS[0]+3, color, false, false);
	}
	
	private void drawFlagBranch(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-2*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+2*CONTROL_PADDING, 
				FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]+(FLAG_AND_COORDS[0]-FLAGS_COORDS[0]-FLAGS_DIMENSIONS[0])/2, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]+(FLAG_AND_COORDS[0]-FLAGS_COORDS[0]-FLAGS_DIMENSIONS[0])/2, 
				FLAG_AND_COORDS[1]+(FLAG_AND_DIMENSIONS[1]/5), 
				CONTROL_COORDS[1]+CONTROL_OFFSET+2*CONTROL_PADDING, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]+(FLAG_AND_COORDS[0]-FLAGS_COORDS[0]-FLAGS_DIMENSIONS[0])/2, 
				FLAG_AND_COORDS[1]+(FLAG_AND_DIMENSIONS[1]/5), 
				FLAG_AND_COORDS[0], color, false, false);
	}

	private void drawZeroBranch(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx,getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-3*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+3*CONTROL_PADDING, ZERO_AND_VERT_X, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_AND_VERT_X, ZERO_AND_COORDS[1]+(ZERO_AND_DIMENSIONS[1]/5), 
				CONTROL_COORDS[1]+CONTROL_OFFSET+3*CONTROL_PADDING, color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, ZERO_AND_VERT_X, ZERO_AND_COORDS[1]+(ZERO_AND_DIMENSIONS[1]/5), 
				ZERO_AND_COORDS[0], color, false, false);
	}

	private void drawMemRead(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-4*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+4*CONTROL_PADDING, 
				MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]+PC_DIMENSIONS[0], 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]+PC_DIMENSIONS[0], 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+PC_DIMENSIONS[0], CONTROL_COORDS[1]+CONTROL_OFFSET+4*CONTROL_PADDING,
				color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2, 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+PC_DIMENSIONS[0], 
				MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]+PC_DIMENSIONS[0], color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx,  DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2, 
				DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+PC_DIMENSIONS[0], DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1],
				color, false, false);
	}

	private void drawMemToReg(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-5*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+5*CONTROL_PADDING, 
				MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2, 
				MUX_READ_DATA_MEM_COORDS[1], CONTROL_COORDS[1]+CONTROL_OFFSET+5*CONTROL_PADDING, 
				color, false, false);
	}

	private void drawMemWrite(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-6*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), 
				CONTROL_COORDS[1]+CONTROL_OFFSET+6*CONTROL_PADDING, DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2, DATA_MEM_COORDS[1],
				CONTROL_COORDS[1]+CONTROL_OFFSET+6*CONTROL_PADDING , color, false, false);
	}

	private void drawFlagWrite(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-7*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+7*CONTROL_PADDING, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]/2, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]/2, FLAGS_COORDS[1],
				CONTROL_COORDS[1]+CONTROL_OFFSET+7*CONTROL_PADDING , color, false, false);
	}

	private void drawALUSrc(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-8*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+8*CONTROL_PADDING, 
				MUX_READ_REG_COORDS[0]+MUX_READ_REG_DIMENSIONS[0]/2, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_READ_REG_COORDS[0]+MUX_READ_REG_DIMENSIONS[0]/2, MUX_READ_REG_COORDS[1],
				CONTROL_COORDS[1]+CONTROL_OFFSET+8*CONTROL_PADDING , color, false, false);
	}

	private void drawALUOp(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-9*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+9*CONTROL_PADDING, 
				REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+2*(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5, 
				color, false, false, 3.5);
		DatapathGraphics.drawVerticalSegment(ctx, 
				REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+2*(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5, 
				ALU_CONTROL_COORDS[1]+1.375*ALU_CONTROL_DIMENSIONS[1], CONTROL_COORDS[1]+CONTROL_OFFSET+9*CONTROL_PADDING , 
				color, false, false, 3.5);
		DatapathGraphics.drawHorizontalSegment(ctx, 
				REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]+2*(MUX_READ_REG_COORDS[0]-REG_FILE_COORDS[0]-REG_FILE_DIMENSIONS[0])/5, 
				ALU_CONTROL_COORDS[1]+1.375*ALU_CONTROL_DIMENSIONS[1], ALU_CONTROL_COORDS[0]+ALU_CONTROL_DIMENSIONS[0]/2, 
				color, false, false, 3.5);
		DatapathGraphics.drawVerticalSegment(ctx, ALU_CONTROL_COORDS[0]+ALU_CONTROL_DIMENSIONS[0]/2, 
				ALU_CONTROL_COORDS[1]+1.375*ALU_CONTROL_DIMENSIONS[1], ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1], 
				color, false, false, 3.5);
	}

	private void drawRegWrite(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, getEllipseXIntersect(CONTROL_COORDS[0], 
				CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-CONTROL_COORDS[1]-CONTROL_OFFSET-10*CONTROL_PADDING, CONTROL_DIMENSIONS[0]/2, 
				CONTROL_DIMENSIONS[1]/2), CONTROL_COORDS[1]+CONTROL_OFFSET+10*CONTROL_PADDING, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]/2, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]/2, REG_FILE_COORDS[1],
				CONTROL_COORDS[1]+CONTROL_OFFSET+10*CONTROL_PADDING, color, false, false);
	}

	private void drawBranchOr_MuxPC(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, BRANCH_OR_COORDS[0]+BRANCH_OR_DIMENSIONS[0], 
				BRANCH_OR_COORDS[1]+BRANCH_OR_DIMENSIONS[1]/2, MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]/2, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]/2, 
				BRANCH_OR_COORDS[1]+BRANCH_OR_DIMENSIONS[1]/2, MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[1], color, false, false);
	}

	private void drawFlag_FlagAnd(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0], 
				FLAGS_COORDS[1]+FLAGS_DIMENSIONS[1]/2, FLAG_AND_COORDS[0], color, false, false);
	}

	private void drawFlagAnd_BranchOR(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0], 
				FLAG_AND_COORDS[1]+FLAG_AND_DIMENSIONS[1]/2,
				FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0]+(ZERO_AND_VERT_X-FLAG_AND_COORDS[0]-FLAG_AND_DIMENSIONS[0])/2, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0]+(ZERO_AND_VERT_X-FLAG_AND_COORDS[0]-FLAG_AND_DIMENSIONS[0])/2,
				FLAG_AND_COORDS[1]+FLAG_AND_DIMENSIONS[1]/2, BRANCH_OR_COORDS[1]+BRANCH_OR_DIMENSIONS[1]/2,
				color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, 
				FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0]+(ZERO_AND_VERT_X-FLAG_AND_COORDS[0]-FLAG_AND_DIMENSIONS[0])/2, 
				BRANCH_OR_COORDS[1]+BRANCH_OR_DIMENSIONS[1]/2, BRANCH_OR_COORDS[0]+5, color, false, false);
	}

	private void drawALUMain_ZeroAnd(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0], 
				ALU_MAIN_COORDS[1]+(3*ALU_MAIN_DIMENSIONS[1]/8), ZERO_AND_VERT_X, color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, ZERO_AND_VERT_X, ALU_MAIN_COORDS[1]+(3*ALU_MAIN_DIMENSIONS[1]/8), 
				ZERO_AND_COORDS[1]+(4*ZERO_AND_DIMENSIONS[1]/5), color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, ZERO_AND_VERT_X, ZERO_AND_COORDS[1]+(4*ZERO_AND_DIMENSIONS[1]/5), 
				ZERO_AND_COORDS[0], color, false, false);
	}

	private void drawZeroAnd_BranchOR(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ZERO_AND_COORDS[0]+ZERO_AND_DIMENSIONS[0], 
				ZERO_AND_COORDS[1]+ZERO_AND_DIMENSIONS[1]/2,
				ZERO_AND_COORDS[0]+ZERO_AND_DIMENSIONS[0]+(DATA_MEM_COORDS[0]+(DATA_MEM_DIMENSIONS[0]/2)-ZERO_AND_COORDS[0]-ZERO_AND_DIMENSIONS[0])/2, 
				color, false, false);
		DatapathGraphics.drawVerticalSegment(ctx, 
				ZERO_AND_COORDS[0]+ZERO_AND_DIMENSIONS[0]+(DATA_MEM_COORDS[0]+(DATA_MEM_DIMENSIONS[0]/2)-ZERO_AND_COORDS[0]-ZERO_AND_DIMENSIONS[0])/2,
				ZERO_AND_COORDS[1]+ZERO_AND_DIMENSIONS[1]/2, 
				BRANCH_OR_COORDS[1]+4*BRANCH_OR_DIMENSIONS[1]/5,
				color, false, false);
		DatapathGraphics.drawHorizontalSegment(ctx, 
				ZERO_AND_COORDS[0]+ZERO_AND_DIMENSIONS[0]+(DATA_MEM_COORDS[0]+(DATA_MEM_DIMENSIONS[0]/2)-ZERO_AND_COORDS[0]-ZERO_AND_DIMENSIONS[0])/2, 
				BRANCH_OR_COORDS[1]+4*BRANCH_OR_DIMENSIONS[1]/5, 
				BRANCH_OR_COORDS[0]+3, color, false, false);
	}

	private void drawALUControl_ALUMain(CssColor color) {
		DatapathGraphics.drawHorizontalSegment(ctx, ALU_CONTROL_COORDS[0]+ALU_CONTROL_DIMENSIONS[0], 
				ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1]/2, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]/2, 
				color, false, false, 3.5);
		DatapathGraphics.drawVerticalSegment(ctx, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]/2, 
				ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1]/2,
				ALU_MAIN_COORDS[1]+7*(ALU_MAIN_DIMENSIONS[1]/8), color, false, false, 3.5);
	}

	private void drawComponentsInit() {
		drawPC(false);
		drawInsMem(false, false);
		drawRegFile(false, false);
		drawDataMem(false, false);
		drawALUPC(false);
		drawALUBranch(false);
		drawALUMain(false);
		drawMuxPC(false, false);
		drawMuxReg2Loc(false, false);
		drawMuxReadRegData(false, false);
		drawMuxReadMemData(false,false);
		drawSignExtend(false);
		drawShiftLeft2(false);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(false);
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
		DatapathGraphics.drawAndGateHorizontal(ctx, FLAG_AND_COORDS[0], FLAG_AND_COORDS[1], 
				FLAG_AND_DIMENSIONS[0], FLAG_AND_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawZeroAnd() {
		DatapathGraphics.drawAndGateHorizontal(ctx, ZERO_AND_COORDS[0], ZERO_AND_COORDS[1], 
				ZERO_AND_DIMENSIONS[0], ZERO_AND_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
	}
	
	private void drawBranchOr() {
		DatapathGraphics.drawOrGateHorizontal(ctx, BRANCH_OR_COORDS[0], BRANCH_OR_COORDS[1], 
				BRANCH_OR_DIMENSIONS[0], BRANCH_OR_DIMENSIONS[1], DatapathGraphics.CONTROL_BLUE);
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
	
	private void drawDatapathRRR(boolean flags, Mnemonic m) {
		drawDataWiresRRR(flags);
		drawControlSignals();
		drawComponentsRRR(flags);
		if (flags) {
			drawControlSignalVals(ControlUnitConfiguration.RRR_FLAGS, "", "", "", "", "0", m.aluControlInput);
		} else {
			drawControlSignalVals(ControlUnitConfiguration.RRR, "", "", "", "", "0", m.aluControlInput);
		}
		drawStringsInit(true, true, false);
	}
	
	private void drawDataWiresRRR(boolean flags) {
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawSignExtend_MuxReadRegData(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawInsMem_SignExtend(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawInsMem_RegFileRead1(DatapathGraphics.RED);
		drawInsMem_RegFileWrite(DatapathGraphics.RED);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.RED);
		drawInsMem_ALUControl(DatapathGraphics.RED);
		drawRegFile_ALUMain(DatapathGraphics.RED);
		drawRegFile_MuxReadRegData(DatapathGraphics.RED);
		drawALUPC_MuxPC(DatapathGraphics.RED);
		if (flags) {
			drawALUMain_Flags(DatapathGraphics.RED);
		} else {
			drawALUMain_Flags(DatapathGraphics.BLACK);
		}
		drawALUMain_MuxReadMemData(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.RED);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
		drawMuxReadMemData_RegFile(DatapathGraphics.RED);
	}
	
	private void drawComponentsRRR(boolean flags) {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(true, true);
		drawDataMem(false, false);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(true);
		drawMuxPC(true, false);
		drawMuxReg2Loc(true, false);
		drawMuxReadRegData(true, false);
		drawMuxReadMemData(false, true);
		drawSignExtend(false);
		drawShiftLeft2(false);
		drawFlags(flags, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(true);
	}
	
	private void drawDatapathRRI(boolean flags, Mnemonic m) {
		drawDataWiresRRI(flags);
		drawControlSignals();
		drawComponentsRRI(flags);
		if (flags) {
			drawControlSignalVals(ControlUnitConfiguration.RRI_FLAGS, "", "", "", "", "0", m.aluControlInput);
		} else {
			drawControlSignalVals(ControlUnitConfiguration.RRI, "", "", "", "", "0", m.aluControlInput);
		}
		drawStringsInit(false, true, false);
	}
	
	private void drawDataWiresRRI(boolean flags) {
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawInsMem_SignExtend(DatapathGraphics.RED);
		drawSignExtend_MuxReadRegData(DatapathGraphics.RED);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawInsMem_RegFileRead1(DatapathGraphics.RED);
		drawInsMem_RegFileWrite(DatapathGraphics.RED);
		drawInsMem_ALUControl(DatapathGraphics.RED);
		drawRegFile_ALUMain(DatapathGraphics.RED);
		drawALUPC_MuxPC(DatapathGraphics.RED);
		if (flags) {
			drawALUMain_Flags(DatapathGraphics.RED);
		} else {
			drawALUMain_Flags(DatapathGraphics.BLACK);
		}
		drawALUMain_MuxReadMemData(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
		drawMuxReadMemData_RegFile(DatapathGraphics.RED);
	}
	
	private void drawComponentsRRI(boolean flags) {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(true, true);
		drawDataMem(false, false);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(true);
		drawMuxPC(true, false);
		drawMuxReg2Loc(false, false);
		drawMuxReadRegData(false, true);
		drawMuxReadMemData(false, true);
		drawSignExtend(true);
		drawShiftLeft2(false);
		drawFlags(flags, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(true);
	}
	
	private void drawDatapathB() {
		drawDataWiresB();
		drawControlSignals();
		drawComponentsB();
		drawControlSignalVals(ControlUnitConfiguration.L, "", "", "", "", "1", "");
		drawStringsInit(true, true, false);
	}
	
	private void drawDataWiresB() {
		draw4_ALUPC(DatapathGraphics.BLACK);
		drawPC_ALUPC(DatapathGraphics.BLACK);
		drawInsMem_RegFileRead1(DatapathGraphics.BLACK);
		drawInsMem_RegFileWrite(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawRegFile_ALUMain(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawALUPC_MuxPC(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.BLACK);
		drawMuxReadRegData_ALUMain(DatapathGraphics.BLACK);
		drawMuxReadMemData_RegFile(DatapathGraphics.BLACK);
		drawSignExtend_MuxReadRegData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.RED);
		drawShiftLeft2_ALUBranch(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawPC_ALUBranch(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawALUBranch_MuxPC(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
	}
	
	private void drawComponentsB() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(false, false);
		drawDataMem(false, false);
		drawALUPC(false);
		drawALUBranch(true);
		drawALUMain(false);
		drawMuxPC(false, true);
		drawMuxReg2Loc(false, false);
		drawMuxReadRegData(false, false);
		drawMuxReadMemData(false, false);
		drawSignExtend(true);
		drawShiftLeft2(true);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(false);
	}
	
	private void drawDatapathBcond(boolean taken) {
		if (taken) {
			drawDataWiresBcondTrue();
			drawControlSignals();
			drawComponentsBcondTrue();
			drawControlSignalVals(ControlUnitConfiguration.L_COND, "1", "1", "", "", "1", "");
		} else {
			drawDataWiresBcondFalse();
			drawControlSignals();
			drawComponentsBcondFalse();
			drawControlSignalVals(ControlUnitConfiguration.L_COND, "0", "0", "", "", "0", "");
		}
		drawStringsInit(true, true, false);
	}
	
	private void drawDataWiresBcondTrue() {
		draw4_ALUPC(DatapathGraphics.BLACK);
		drawPC_ALUPC(DatapathGraphics.BLACK);
		drawInsMem_RegFileRead1(DatapathGraphics.BLACK);
		drawInsMem_RegFileWrite(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawRegFile_ALUMain(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawALUPC_MuxPC(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.BLACK);
		drawMuxReadRegData_ALUMain(DatapathGraphics.BLACK);
		drawMuxReadMemData_RegFile(DatapathGraphics.BLACK);
		drawSignExtend_MuxReadRegData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.RED);
		drawShiftLeft2_ALUBranch(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawPC_ALUBranch(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawALUBranch_MuxPC(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
	}
	
	private void drawComponentsBcondTrue() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(false, false);
		drawDataMem(false, false);
		drawALUPC(false);
		drawALUBranch(true);
		drawALUMain(false);
		drawMuxPC(false, true);
		drawMuxReg2Loc(false, false);
		drawMuxReadRegData(false, false);
		drawMuxReadMemData(false, false);
		drawSignExtend(true);
		drawShiftLeft2(true);
		drawFlags(false, true);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(false);
	}
	
	private void drawDataWiresBcondFalse() {
		drawInsMem_RegFileRead1(DatapathGraphics.BLACK);
		drawInsMem_RegFileWrite(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawRegFile_ALUMain(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.BLACK);
		drawMuxReadRegData_ALUMain(DatapathGraphics.BLACK);
		drawMuxReadMemData_RegFile(DatapathGraphics.BLACK);
		drawSignExtend_MuxReadRegData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawInsMem_SignExtend(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawInsMem_SignExtend(DatapathGraphics.BLACK);
		drawALUPC_MuxPC(DatapathGraphics.RED);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
	}
	
	private void drawComponentsBcondFalse() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(false, false);
		drawDataMem(false, false);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(false);
		drawMuxPC(true, false);
		drawMuxReg2Loc(false, false);
		drawMuxReadRegData(false, false);
		drawMuxReadMemData(false, false);
		drawSignExtend(false);
		drawShiftLeft2(false);
		drawFlags(false, true);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(false);
	}
	
	private void drawDatapathCB(boolean taken, Mnemonic m) {
		if (taken) {
			drawDataWiresCBTaken();
			drawControlSignals();
			drawComponentsCBTaken();
			drawControlSignalVals(ControlUnitConfiguration.RL, "", "", "1", "1", "1", m.aluControlInput);
		} else {
			drawDataWiresCBNotTaken();
			drawControlSignals();
			drawComponentsCBNotTaken();
			drawControlSignalVals(ControlUnitConfiguration.RL, "", "", "0", "0", "0", m.aluControlInput);
		}
		if (m.equals(Mnemonic.CBZ)) {
			drawStringsInit(true, true, false);
		} else {
			drawStringsInit(true, false, false);
		}
	}
	
	private void drawDataWiresCBTaken() {
		drawALUPC_MuxPC(DatapathGraphics.BLACK);
		draw4_ALUPC(DatapathGraphics.BLACK);
		drawPC_ALUPC(DatapathGraphics.BLACK);
		drawInsMem_RegFileRead1(DatapathGraphics.BLACK);
		drawInsMem_RegFileWrite(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawRegFile_ALUMain(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxReadMemData_RegFile(DatapathGraphics.BLACK);
		drawSignExtend_MuxReadRegData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.RED);
		drawShiftLeft2_ALUBranch(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
		drawALUBranch_MuxPC(DatapathGraphics.RED);
		drawPC_ALUBranch(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawRegFile_MuxReadRegData(DatapathGraphics.RED);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.RED);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
	}
	
	private void drawComponentsCBTaken() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(false, true);
		drawDataMem(false, false);
		drawALUPC(false);
		drawALUBranch(true);
		drawALUMain(true);
		drawMuxPC(false, true);
		drawMuxReg2Loc(false, true);
		drawMuxReadRegData(true, false);
		drawMuxReadMemData(false, false);
		drawSignExtend(true);
		drawShiftLeft2(true);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(false);
	}
	
	private void drawDataWiresCBNotTaken() {
		drawInsMem_RegFileRead1(DatapathGraphics.BLACK);
		drawInsMem_RegFileWrite(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawRegFile_ALUMain(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_DataMem(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxReadMemData_RegFile(DatapathGraphics.BLACK);
		drawSignExtend_MuxReadRegData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawInsMem_SignExtend(DatapathGraphics.BLACK);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.RED);
		drawRegFile_MuxReadRegData(DatapathGraphics.RED);
		drawALUPC_MuxPC(DatapathGraphics.RED);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.RED);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
	}
	
	private void drawComponentsCBNotTaken() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(false, true);
		drawDataMem(false, false);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(true);
		drawMuxPC(true, false);
		drawMuxReg2Loc(false, true);
		drawMuxReadRegData(true, false);
		drawMuxReadMemData(false, false);
		drawSignExtend(false);
		drawShiftLeft2(false);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(false);
	}
	
	private void drawDatapathLoad(Mnemonic m) {
		drawDataWiresLoad();
		drawControlSignals();
		drawComponentsLoad();
		drawStringsInit(true, true, false);
		drawControlSignalVals(ControlUnitConfiguration.RM_LOAD, "", "", "", "", "0", m.aluControlInput);
	}
	
	private void drawDataWiresLoad() {
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.BLACK);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawInsMem_RegFileRead1(DatapathGraphics.RED);
		drawInsMem_RegFileWrite(DatapathGraphics.RED);
		drawRegFile_ALUMain(DatapathGraphics.RED);
		drawDataMem_MuxReadMemData(DatapathGraphics.RED);
		drawALUMain_DataMem(DatapathGraphics.RED);
		drawMuxReadMemData_RegFile(DatapathGraphics.RED);
		drawSignExtend_MuxReadRegData(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawALUPC_MuxPC(DatapathGraphics.RED);
	}
	
	private void drawComponentsLoad() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(true, true);
		drawDataMem(false, true);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(true);
		drawMuxPC(true, false);
		drawMuxReg2Loc(false, false);
		drawMuxReadRegData(false, true);
		drawMuxReadMemData(true, false);
		drawSignExtend(true);
		drawShiftLeft2(false);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(false);
	}

	private void drawDatapathStore(Mnemonic m) {
		drawDataWiresStore();
		drawControlSignals();
		drawComponentsStore();
		drawStringsInit(true, true, false);
		drawControlSignalVals(ControlUnitConfiguration.RM_STORE, "", "", "", "", "0", m.aluControlInput);
	}
	
	private void drawDataWiresStore() {
		drawInsMem_MuxReg2Loc2(DatapathGraphics.BLACK);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.BLACK);
		drawMuxReadMemData_RegFile(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawInsMem_RegFileWrite(DatapathGraphics.BLACK);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.RED);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.RED);
		drawRegFile_DataMem(DatapathGraphics.RED);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawInsMem_RegFileRead1(DatapathGraphics.RED);
		drawRegFile_ALUMain(DatapathGraphics.RED);
		drawALUMain_DataMem(DatapathGraphics.RED);
		drawSignExtend_MuxReadRegData(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawALUPC_MuxPC(DatapathGraphics.RED);
	}
	
	private void drawComponentsStore() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(false, true);
		drawDataMem(true, false);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(true);
		drawMuxPC(true, false);
		drawMuxReg2Loc(false, true);
		drawMuxReadRegData(false, true);
		drawMuxReadMemData(false, false);
		drawSignExtend(true);
		drawShiftLeft2(false);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(true);
	}
	
	private void drawDatapathSTXR(boolean succeed, Mnemonic m) {
		if (succeed) {
			drawDataWiresSTXRSucceed();
			drawControlSignals();
			drawComponentsSTXRSucceed();
			drawControlSignalVals(ControlUnitConfiguration.RRM, "", "", "", "", "0", m.aluControlInput);
		} else {
			drawDataWiresSTXRFail();
			drawControlSignals();
			drawComponentsSTXRFail();
			drawControlSignalVals(ControlUnitConfiguration.RRM, "", "", "", "", "0", m.aluControlInput);
		}
		drawStringsInit(true, true, true);
	}
	
	private void drawDataWiresSTXRSucceed() {
		drawInsMem_MuxReg2Loc2(DatapathGraphics.RED);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.RED);
		drawMuxReadMemData_RegFile(DatapathGraphics.RED);
		drawInsMem_RegFileWrite(DatapathGraphics.RED);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.RED);
		drawRegFile_DataMem(DatapathGraphics.RED);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawInsMem_RegFileRead1(DatapathGraphics.RED);
		drawRegFile_ALUMain(DatapathGraphics.RED);
		drawALUMain_DataMem(DatapathGraphics.RED);
		drawSignExtend_MuxReadRegData(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawALUPC_MuxPC(DatapathGraphics.RED);
	}
	
	private void drawComponentsSTXRSucceed() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(true, true);
		drawDataMem(true, true);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(true);
		drawMuxPC(true, false);
		drawMuxReg2Loc(true, false);
		drawMuxReadRegData(false, true);
		drawMuxReadMemData(true, false);
		drawSignExtend(true);
		drawShiftLeft2(false);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(true);
	}
	
	private void drawDataWiresSTXRFail() {
		drawInsMem_MuxReg2Loc2(DatapathGraphics.RED);
		drawInsMem_ALUControl(DatapathGraphics.BLACK);
		drawALUMain_Flags(DatapathGraphics.BLACK);
		drawALUMain_MuxReadMemData(DatapathGraphics.BLACK);
		drawSignExtend_ShiftLeft2(DatapathGraphics.BLACK);
		drawShiftLeft2_ALUBranch(DatapathGraphics.BLACK);
		drawPC_ALUBranch(DatapathGraphics.BLACK);
		drawALUBranch_MuxPC(DatapathGraphics.BLACK);
		drawInsMem_MuxReg2Loc1(DatapathGraphics.BLACK);
		drawRegFile_MuxReadRegData(DatapathGraphics.BLACK);
		drawRegFile_DataMem(DatapathGraphics.BLACK);
		drawDataMem_MuxReadMemData(DatapathGraphics.RED);
		drawMuxReadMemData_RegFile(DatapathGraphics.RED);
		drawInsMem_RegFileWrite(DatapathGraphics.RED);
		drawMuxReg2Loc_RegFileRead2(DatapathGraphics.RED);
		drawMuxReadRegData_ALUMain(DatapathGraphics.RED);
		drawPC_InsMem(DatapathGraphics.RED);
		drawInsMem_Control(DatapathGraphics.RED);
		drawMuxPC_PC(DatapathGraphics.RED);
		drawInsMem_RegFileRead1(DatapathGraphics.RED);
		drawRegFile_ALUMain(DatapathGraphics.RED);
		drawALUMain_DataMem(DatapathGraphics.RED);
		drawSignExtend_MuxReadRegData(DatapathGraphics.RED);
		drawInsMem_SignExtend(DatapathGraphics.RED);
		draw4_ALUPC(DatapathGraphics.RED);
		drawPC_ALUPC(DatapathGraphics.RED);
		drawALUPC_MuxPC(DatapathGraphics.RED);
	}
	
	private void drawComponentsSTXRFail() {
		drawPC(true);
		drawInsMem(false, true);
		drawRegFile(true, true);
		drawDataMem(false, true);
		drawALUPC(true);
		drawALUBranch(false);
		drawALUMain(true);
		drawMuxPC(true, false);
		drawMuxReg2Loc(true, false);
		drawMuxReadRegData(false, true);
		drawMuxReadMemData(true, false);
		drawSignExtend(true);
		drawShiftLeft2(false);
		drawFlags(false, false);
		drawFlagAnd();
		drawZeroAnd();
		drawBranchOr();
		drawControl();
		drawALUControl(true);
	}
	
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
		ctx.setFont("13px arial");
		drawPC4_TXT();
		drawSE32_TXT();
		drawSE64_TXT();
		ctx.setFont("12px arial");
		drawInstructionFields_TXT();
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
		ctx.setFont("bold 14px arial");
		drawControl_TXT();
		drawALUControl_TXT();
		ctx.setFont("12px arial");
		drawReg2Loc_C_TXT();
		drawUncondBranch_C_TXT();
		drawFlagBranch_C_TXT();
		drawZeroBranch_C_TXT();
		drawMemRead_C_TXT();
		drawMemToReg_C_TXT();
		drawMemWrite_C_TXT();
		drawFlagWrite_C_TXT();
		drawALUSrc_C_TXT();
		drawALUOp_C_TXT();
		drawRegWrite_C_TXT();
		drawALUZero_TXT(zero);
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
	
	private void drawRegFile_TXT() {
		TextMetrics t = ctx.measureText("Registers");
		ctx.fillText("Registers", REG_FILE_COORDS[0]+2*REG_FILE_DIMENSIONS[0]/3-t.getWidth()/2, REG_FILE_COORDS[1]+19*REG_FILE_DIMENSIONS[1]/20);
	}
	
	private void drawSignExtend_TXT() {
		TextMetrics t = ctx.measureText("Sign-");
		ctx.fillText("Sign-", SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0]/2-t.getWidth()/2, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/3+10);
		t = ctx.measureText("extend");
		ctx.fillText("extend", SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0]/2-t.getWidth()/2, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/3+25);
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
		ctx.fillText("ALU", ALU_MAIN_COORDS[0]+2*ALU_MAIN_DIMENSIONS[0]/5+2.5-t.getWidth()/2, ALU_MAIN_COORDS[1]+ALU_MAIN_DIMENSIONS[1]/2+5);
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
		ctx.fillText("Add", ALU_BRANCH_COORDS[0]+2*ALU_BRANCH_DIMENSIONS[0]/5+2.5-t.getWidth()/2, ALU_BRANCH_COORDS[1]+ALU_BRANCH_DIMENSIONS[1]/2+5);
	}
	
	private void drawDataMem_TXT() {
		TextMetrics t = ctx.measureText("Data");
		ctx.fillText("Data", DATA_MEM_COORDS[0]+6.5*DATA_MEM_DIMENSIONS[0]/10-t.getWidth()/2, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]/2+10);
		t = ctx.measureText("memory");
		ctx.fillText("memory", DATA_MEM_COORDS[0]+6.5*DATA_MEM_DIMENSIONS[0]/10-t.getWidth()/2, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]/2+25);
	}

	private void drawPC4_TXT() {
		TextMetrics t = ctx.measureText("4");
		ctx.fillText("4", PC_PCALU_VERTICAL_X+(ALU_PC_COORDS[0]-PC_PCALU_VERTICAL_X)/2-t.getWidth()/2, ALU_PC_COORDS[1]+13*ALU_PC_DIMENSIONS[1]/16+5);
	}
	
	private void drawSE32_TXT() {
		TextMetrics t = ctx.measureText("32");
		ctx.fillText("32", REG_FILE_COORDS[0]+(SIGN_EXTEND_COORDS[0]-REG_FILE_COORDS[0])/2-3*t.getWidth()/4, 
				SIGN_EXTEND_COORDS[1]+2*SIGN_EXTEND_DIMENSIONS[1]/5);
	}
	
	private void drawSE64_TXT() {
		TextMetrics t = ctx.measureText("64");
		ctx.fillText("64", SIGN_EXTEND_COORDS[0]+SIGN_EXTEND_DIMENSIONS[0]+(SIGN_EXTEND_COORDS[0]-REG_FILE_COORDS[0])/2-3*t.getWidth()/4, 
				SIGN_EXTEND_COORDS[1]+2*SIGN_EXTEND_DIMENSIONS[1]/5);
	}
	
	private void drawInstructionFields_TXT() {
		TextMetrics t = ctx.measureText("Instruction");
		ctx.fillText("Instruction", INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]-4-t.getWidth(), INS_MEM_COORDS[1]+INS_MEM_DIMENSIONS[1]/2);
		t = ctx.measureText("[31-0]");
		ctx.fillText("[31-0]", INS_MEM_COORDS[0]+INS_MEM_DIMENSIONS[0]-4-t.getWidth(), INS_MEM_COORDS[1]+INS_MEM_DIMENSIONS[1]/2+10);
		ctx.fillText("Instruction [31-21]", INS_MEM_VERTICAL_X+5, CONTROL_COORDS[1]+CONTROL_DIMENSIONS[1]/2-5);
		ctx.fillText("Instruction [9-5]", INS_MEM_VERTICAL_X+5, REG_FILE_COORDS[1]+REG_FILE_DIMENSIONS[1]/10-5);
		ctx.fillText("Instruction [20-16]", INS_MEM_VERTICAL_X+5, MUX_REG2LOC_COORDS[1]+MUX_REG2LOC_DIMENSIONS[0]/2-5);
		ctx.fillText("Instruction [4-0]", INS_MEM_VERTICAL_X+5,REG_FILE_COORDS[1]+7*REG_FILE_DIMENSIONS[1]/10+15);
		ctx.fillText("Instruction [31-0]", INS_MEM_VERTICAL_X+5, SIGN_EXTEND_COORDS[1]+SIGN_EXTEND_DIMENSIONS[1]/2-5);
		ctx.fillText("Instruction [31-21]", REG_FILE_COORDS[0]+5, ALU_CONTROL_COORDS[1]+1.25*ALU_CONTROL_DIMENSIONS[1]-5);
	}
	
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
		ctx.fillText("Read", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), ALU_MAIN_COORDS[1]+3*ALU_MAIN_DIMENSIONS[1]/16);
		t = ctx.measureText("data 1");
		ctx.fillText("data 1", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), ALU_MAIN_COORDS[1]+3*ALU_MAIN_DIMENSIONS[1]/16+10);
	}
	
	private void drawReadData2_TXT() {
		TextMetrics t = ctx.measureText("Read");
		ctx.fillText("Read", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[0]/2);
		t = ctx.measureText("data 1");
		ctx.fillText("data 2", REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]-4-t.getWidth(), MUX_READ_REG_COORDS[1]+MUX_READ_REG_DIMENSIONS[0]/2+10);
	}
	
	private void drawDataMemAddress_TXT() {
		ctx.fillText("Address", DATA_MEM_COORDS[0]+3, ALU_MAIN_COORDS[1]+5*ALU_MAIN_DIMENSIONS[1]/8+4);
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
	
	private void drawALUZero_TXT(boolean zero) {
		if (zero) {
			TextMetrics t = ctx.measureText("Zero");
			ctx.fillText("Zero", ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]-4-t.getWidth(), ALU_MAIN_COORDS[1]+3*ALU_MAIN_DIMENSIONS[1]/8+2.5);
		} else {
			TextMetrics t = ctx.measureText("!Zero");
			ctx.fillText("!Zero", ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]-4-t.getWidth(), ALU_MAIN_COORDS[1]+3*ALU_MAIN_DIMENSIONS[1]/8+2.5);
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
	
	private void drawReg2Loc_C_TXT() {
		ctx.fillText("Reg2Loc", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5);
	}
	
	private void drawUncondBranch_C_TXT() {
		ctx.fillText("UncondBranch", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+CONTROL_PADDING);
	}
	
	private void drawFlagBranch_C_TXT() {
		ctx.fillText("FlagBranch", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+2*CONTROL_PADDING);
	}
	
	private void drawZeroBranch_C_TXT() {
		ctx.fillText("ZeroBranch", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+3*CONTROL_PADDING);
	}
	
	private void drawMemRead_C_TXT() {
		ctx.fillText("MemRead", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+4*CONTROL_PADDING);
	}
	
	private void drawMemToReg_C_TXT() {
		ctx.fillText("MemToReg", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+5*CONTROL_PADDING);
	}
	
	private void drawMemWrite_C_TXT() {
		ctx.fillText("MemWrite", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+6*CONTROL_PADDING);
	}
	
	private void drawFlagWrite_C_TXT() {
		ctx.fillText("FlagWrite", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+7*CONTROL_PADDING);
	}
	
	private void drawALUSrc_C_TXT() {
		ctx.fillText("ALUSrc", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+8*CONTROL_PADDING);
	}
	
	private void drawALUOp_C_TXT() {
		ctx.fillText("ALUOp", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+9*CONTROL_PADDING);
	}
	
	private void drawRegWrite_C_TXT() {
		ctx.fillText("RegWrite", CONTROL_COORDS[0]+CONTROL_DIMENSIONS[0]+5, CONTROL_COORDS[1]+CONTROL_OFFSET-2.5+10*CONTROL_PADDING);
	}
	
	private void drawControlSignalVals(ControlUnitConfiguration c, String flags_flagAnd, 
			String flagAnd_branchOr, String ALUMain_ZeroAnd, String zeroAnd_branchOr, 
			String branchOr_PCMux, String ALUMain) {
		ctx.setFont("bold 11px arial");
		ctx.setFillStyle(DatapathGraphics.CONTROL_BLUE);
		TextMetrics t = ctx.measureText("0");
		ctx.fillText(ControlUnitConfiguration.toString(c.reg2Loc), MUX_REG2LOC_COORDS[0]+MUX_REG2LOC_DIMENSIONS[0]/2-t.getWidth()-1, REG_FILE_COORDS[1]+9*REG_FILE_DIMENSIONS[1]/10-2);
		ctx.fillText(ControlUnitConfiguration.toString(c.uncondBranch), BRANCH_OR_COORDS[0]-7.5, BRANCH_OR_COORDS[1]+3);
		ctx.fillText(ControlUnitConfiguration.toString(c.flagBranch), FLAG_AND_COORDS[0]-t.getWidth()-2, FLAG_AND_COORDS[1]+2.5);
		ctx.fillText(ControlUnitConfiguration.toString(c.zeroBranch), ZERO_AND_COORDS[0]-t.getWidth()-2, ZERO_AND_COORDS[1]+2.5);
		ctx.fillText(ControlUnitConfiguration.toString(c.memRead), DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2-t.getWidth()-1, DATA_MEM_COORDS[1]-3);
		ctx.fillText(ControlUnitConfiguration.toString(c.memToReg), MUX_READ_DATA_MEM_COORDS[0]+MUX_READ_DATA_MEM_DIMENSIONS[0]/2-t.getWidth()-1, MUX_READ_DATA_MEM_COORDS[1]-3);
		ctx.fillText(ControlUnitConfiguration.toString(c.memWrite), DATA_MEM_COORDS[0]+DATA_MEM_DIMENSIONS[0]/2-t.getWidth()-1, DATA_MEM_COORDS[1]+DATA_MEM_DIMENSIONS[1]+10);
		ctx.fillText(ControlUnitConfiguration.toString(c.flagWrite), FLAGS_COORDS[0]+FLAGS_DIMENSIONS[0]/2-t.getWidth()-1, FLAGS_COORDS[1]-3);
		ctx.fillText(ControlUnitConfiguration.toString(c.aluSrc), MUX_READ_REG_COORDS[0]+MUX_READ_REG_DIMENSIONS[0]/2-t.getWidth()-1, MUX_READ_REG_COORDS[1]-3);
		ctx.fillText(ControlUnitConfiguration.toString(c.regWrite), REG_FILE_COORDS[0]+REG_FILE_DIMENSIONS[0]/2+2, REG_FILE_COORDS[1]-3);
		ctx.fillText(flags_flagAnd, FLAG_AND_COORDS[0]-t.getWidth()-2, FLAG_AND_COORDS[1]+FLAG_AND_DIMENSIONS[1]/2+5);
		ctx.fillText(flagAnd_branchOr, FLAG_AND_COORDS[0]+FLAG_AND_DIMENSIONS[0]+7, FLAG_AND_COORDS[1]+FLAG_AND_DIMENSIONS[1]/2-1);
		ctx.fillText(ALUMain_ZeroAnd, ZERO_AND_COORDS[0]-t.getWidth()-2, ZERO_AND_COORDS[1]+ZERO_AND_DIMENSIONS[1]/2+5);
		ctx.fillText(zeroAnd_branchOr, ZERO_AND_COORDS[0]+ZERO_AND_DIMENSIONS[0]+5, ZERO_AND_COORDS[1]+ZERO_AND_DIMENSIONS[1]/2-1);
		ctx.fillText(branchOr_PCMux, MUX_PC_COORDS[0]+MUX_PC_DIMENSIONS[0]/2-t.getWidth()-1, MUX_PC_COORDS[1]+MUX_PC_DIMENSIONS[1]+10);
		t = ctx.measureText(ControlUnitConfiguration.toString(c.aluOp));
		ctx.fillText(ControlUnitConfiguration.toString(c.aluOp), ALU_CONTROL_COORDS[0]+ALU_CONTROL_DIMENSIONS[0]/2-t.getWidth()-3, ALU_CONTROL_COORDS[1]+ALU_CONTROL_DIMENSIONS[1]+10);
		ctx.fillText(ALUMain, ALU_MAIN_COORDS[0]+ALU_MAIN_DIMENSIONS[0]/2+3, ALU_MAIN_COORDS[1]+7*ALU_MAIN_DIMENSIONS[1]/8+10);
	}
	
	private double canvasWidth;
	private double canvasHeight;
	private Canvas canvas;
	private Context2d ctx;
}
