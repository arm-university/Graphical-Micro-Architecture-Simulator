package com.arm.legv8simulator.client;

import java.util.ArrayList;

import com.arm.legv8simulator.client.executionmodes.LEGv8_Simulator;
import com.arm.legv8simulator.client.executionmodes.PipelinedSimulator;
import com.arm.legv8simulator.client.executionmodes.SingleCycleSimulator;
import com.arm.legv8simulator.client.instruction.Instruction;
import com.arm.legv8simulator.client.lexer.TextLine;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import edu.ycp.cs.dh.acegwt.client.ace.AceCommandDescription;
import edu.ycp.cs.dh.acegwt.client.ace.AceDefaultCommandLine;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;
import edu.ycp.cs.dh.acegwt.client.ace.AceMarkerType;
import edu.ycp.cs.dh.acegwt.client.ace.AceRange;

/**
 * GWT uses this class to start execution
 * <p>
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebApp implements EntryPoint {
	 
	public static final String SIMULATION = "Simulation";
	public static final String SINGLE_CYCLE_VISUAL = "Single Cycle";
	public static final String PIPELINE_VISUAL = "Pipeline";
	
	private static final int HORIZTONAL_PADDING = 20;
	private static final int VERTICAL_PADDING = 30;
	private static final int BANNER_HEIGHT = 50;
	private static final double ASPECT_RATIO = 1.432;
	
	/**
	 * This is the entry point method. Program execution starts here.
	 * <p>
	 * Launches the webapp and build the GUI
	 */
	public void onModuleLoad() {
		
		// resize the GUI when the browser window size changes
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int editorHeight;
				if (executionModes.getSelectedItemText().equals(SIMULATION)) {
					editorHeight = Window.getClientHeight()-controlPanel.getOffsetHeight()
							-BANNER_HEIGHT-VERTICAL_PADDING;
				} else {
					editorHeight = Window.getClientHeight()-controlPanel.getOffsetHeight()
							-registerPanel.getOffsetHeight()-BANNER_HEIGHT-VERTICAL_PADDING;	
				}
				if (editorHeight > 200) {
					editor.setHeight(editorHeight + "px");
				} else {
					editor.setHeight("200px");
				}
				if (scDatapath != null) {
					double width = event.getWidth();
					resizeSingleCycleDatapath(width);
				}
				if (pDatapath != null) {
					double width = event.getWidth();
					resizePipelineDatapath(width);
				}
			}
			
			private void resizeSingleCycleDatapath(double width) {
				if (width > 1600) {
					editor.setWidth("675px");
				} else {
					editor.setWidth(registerPanel.getOffsetWidth() + "px");
				}
				double datapathWidth = width-editorPanel.getOffsetWidth()-HORIZTONAL_PADDING;
				datapathPanel.remove(scDatapath.getCanvas());
				scDatapath = new SingleCycleVis(datapathWidth, datapathWidth/ASPECT_RATIO);
				if (singleCycleSim != null && singleCycleSim.getCurrentInstruction() != null) {
					scDatapath.updateDatapath(singleCycleSim.getCurrentInstruction(), 
							singleCycleSim.getBranchTaken(), singleCycleSim.getSTXRSucceed(), 
							singleCycleSim.getCurrentInsIndex(), 
							code.get(singleCycleSim.getCurrentInstruction().getLineNumber()).getArgs().get(0));
				}
				datapathPanel.add(scDatapath.getCanvas());
			}
			
			private void resizePipelineDatapath(double width) {
				if (width > 1700) {
					editor.setWidth("675px");
				} else {
					editor.setWidth(registerPanel.getOffsetWidth() + "px");
				}
				double datapathWidth = width-editorPanel.getOffsetWidth()-HORIZTONAL_PADDING;
				datapathPanel.remove(pDatapath.getCanvas());
				pDatapath = new PipelineVis(datapathWidth, datapathWidth/ASPECT_RATIO);
				datapathPanel.add(pDatapath.getCanvas());
			}
		});
		
		//create LEGv8 source code editor
		editor = new AceEditor();
		editor.setWidth("600px"); // dummy values, size adjusted later based on window size
		editor.setHeight("350px"); 
		
		// create cpuLog
		cpuLog = new AceEditor();
		cpuLog.setWidth("600px");
		cpuLog.setHeight("350px");
		
		// build the UI
		initUIComponents();
		buildSingleCycleUI();
		//buildSimulationUI();
		
		// start the source code editor and set its theme and mode
		editor.startEditor(); // must be called before calling setTheme/setMode/etc.
		editor.setTheme(AceEditorTheme.COBALT);
		editor.setFontSize(14);
		editor.setTabSize(10);
		editor.focus();
		editor.setShowGutter(true);

		// start the cpuLog and set its theme and mode
		cpuLog.startEditor();
		cpuLog.setTheme(AceEditorTheme.MONOKAI);
		cpuLog.setReadOnly(true);
		cpuLog.setShowGutter(false);
		
		// code from here to end of method is not used but program breaks if it is removed...
		editor.initializeCommandLine(new AceDefaultCommandLine(commandLine));
		editor.addCommand(new AceCommandDescription("increaseFontSize", 
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(AceEditor editor) {
				int fontSize = editor.getFontSize();
				editor.setFontSize(fontSize + 1);
				return null;
			}
		}).withBindKey("Ctrl-=|Ctrl-+"));
		editor.addCommand(new AceCommandDescription("decreaseFontSize", 
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(AceEditor editor) {
				int fontSize = editor.getFontSize();
				fontSize = Math.max(fontSize - 1, 1);
				editor.setFontSize(fontSize);
				return null;
			}
		}).withBindKey("Ctrl+-|Ctrl-_"));
		editor.addCommand(new AceCommandDescription("resetFontSize", 
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(AceEditor editor) {
				editor.setFontSize(12);
				return null;
			}
		}).withBindKey("Ctrl+0|Ctrl-Numpad0"));
		AceCommandDescription gotolineCmd = editor.getCommandDescription("gotoline");
		editor.addCommand(
				new AceCommandDescription("gotoline2", gotolineCmd.getExec())
				.withBindKey("Alt-1").withReadOnly(true));
	}

	// Builds the GUI for the simulation execution mode
//	private void buildSimulationUI() {
//		page.add(controlPanel);
//		if (contentPanel != null) page.remove(contentPanel);
//		contentPanel = new HorizontalPanel();
//		contentPanel.setHeight("100%");
//		contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
//		contentPanel.add(editorPanel);
//		int editorHeight = Window.getClientHeight()-controlPanel.getOffsetHeight()
//				-BANNER_HEIGHT-VERTICAL_PADDING;
//		if (editorHeight > 200) {
//			editor.setHeight(editorHeight + "px");
//		} else {
//			editor.setHeight("200px");
//		}
//		editor.setWidth("675px");
//		HorizontalPanel padding = new HorizontalPanel();
//		padding.setWidth("30px");
//		contentPanel.add(padding);
//		contentPanel.add(registerPanel);
//		resetRegisterPanel();
//		cpsrPanel.reset();
//		page.add(contentPanel);
//		//page.add(debugPanel);
//	}
	
	// builds the GUI for the single-cycle execution mode
	private void buildSingleCycleUI() {
		pDatapath = null;
		buildVisualisationUI();
		double width = Window.getClientWidth();
		if (width > 1700) {
			editor.setWidth("675px");
		} else {
			editor.setWidth(registerPanel.getOffsetWidth() + "px");
		}
		double datapathWidth = width-editorPanel.getOffsetWidth()-HORIZTONAL_PADDING;
		scDatapath = new SingleCycleVis(datapathWidth, datapathWidth/ASPECT_RATIO);
		datapathPanel.add(scDatapath.getCanvas());
		contentPanel.add(datapathPanel);
		//page.add(debugPanel);
	}
	
	// builds theGUI for the pipelined execution mode
	private void buildPipelineUI() {
		scDatapath = null;
		buildVisualisationUI();
		double width = Window.getClientWidth();
		if (width > 1700) {
			editor.setWidth("675px");
		} else {
			editor.setWidth(registerPanel.getOffsetWidth() + "px");
		}
		double datapathWidth = width-editorPanel.getOffsetWidth()-HORIZTONAL_PADDING;
		pDatapath = new PipelineVis(datapathWidth, datapathWidth/ASPECT_RATIO);
		contentPanel.add(datapathPanel);
		datapathPanel.add(debugPanel);
		datapathPanel.add(pDatapath.getCanvas());
		//debugPanel.add(datapathPanel);
		//page.add(datapathPanel);
	}
	
	// builds the GUI for the help page
		private void buildHelpPage() {
			page.add(controlPanel);
			if (contentPanel != null) page.remove(contentPanel);
			contentPanel = new HorizontalPanel();
			contentPanel.setHeight("100%");
			contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			writingPanel = new VerticalPanel();
			writingPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_JUSTIFY);
			writingPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
			contentPanel.add(writingPanel);
			
			Image sp1 = new Image("images/space.png");
			Label help1 = new Label("How to use the simulator");
			help1.addStyleName("title1Label");
			Image sp2 = new Image("images/space.png");
			Label help2 = new Label("Step 1: Select the execution mode you would like to observe; Single Cycle or Pipeline.");
			Image wp1 = new Image("images/selectModeHelp.jpeg");
			wp1.setPixelSize(600, 400);
			Image sp3 = new Image("images/space.png");
			Label help3 = new Label("Step 2: Enter your assembly code in the text editor.");
			Image wp2 = new Image("images/editorHelp.jpeg");
			wp2.setPixelSize(600, 400);
			Image sp4 = new Image("images/space.png");
			Label help4 = new Label("Step 3: Select the 'Assemble' button; this will check for errors in the assembly code and compile.");
			Image wp3 = new Image("images/assembleHelp.jpeg");
			wp3.setPixelSize(600, 400);
			Image sp5 = new Image("images/space.png");
			Label help5 = new Label("Step 4: Select the 'Execute Instruction' button if there are no errors in your code.");
			Image wp4 = new Image("images/executeHelp.jpeg");
			wp4.setPixelSize(600, 400);
			Image sp6 = new Image("images/space.png");
			Label help6 = new Label("Step 5: View the results by observing the registers, flags and the image of the datapath.");
			Image wp5 = new Image("images/outputHelp.jpeg");
			wp5.setPixelSize(600, 400);
			Image space1 = new Image("images/space.png");
			Label line1 = new Label("Background Information on the LEGv8 architecture");
			line1.addStyleName("title2Label");
			Label line2 = new Label("LEGv8 is a load/store architecture meaning that data has to be loaded from memory "
					+ "into registers before operations can be performed on it.The LEGv8 architecture has 32 registers, each having size 64 bits.");
			Image space2 = new Image("images/space.png");
			Image regTable = new Image("images/regTable.png");
			regTable.setPixelSize(624, 325);
			Image space3 = new Image("images/space.png");
			Label line3 = new Label("X8 is used by procedures that return a result via a pointer. Registers X16, X17, X28, X29 and X30 have special roles. "
					+ "In these roles they are labelled IP1, IP2, SP, FP, and LR respectively. These registers should only be referred to by their special "
					+ "names when they are being used to store addresses; the special name implies accessing the register as a 64-bit entity. In order to "
					+ "access the memory using an instruction, you must be aware that the Dynamic memory offset is 0x10000000 and the Stack base is 0x7ffffffffc. "
					+ "Thus you should store the value ' 0x10000000' into a register e.g X7 and use this register as the memory base. So memory access is Mem[X7 + 40].");
			Label line4 = new Label("Arithmetics instructions");
			line4.addStyleName("arithLabel");
			Image space4 = new Image("images/space.png");
			Image arithmetic = new Image("images/arithmetic.png");
			arithmetic.setPixelSize(568, 208);
			Image space5 = new Image("images/space.png");
			Label line5 = new Label("Data Transfer");
			line5.addStyleName("dataLabel");
			Image dataTransfer = new Image("images/dataTransfer.png");
			dataTransfer.setPixelSize(600, 260);
			Image space6 = new Image("images/space.png");
			Label line6 = new Label("Logical");
			line6.addStyleName("logicalLabel");
			Image logical = new Image("images/logical.png");
			logical.setPixelSize(595, 254);
			Image space7 = new Image("images/space.png");
			Label line7 = new Label("Conditional Branch");
			line7.addStyleName("condBLabel");
			Image condBranch = new Image("images/condBranch.png");
			condBranch.setPixelSize(650, 95);
			Image space8 = new Image("images/space.png");
			Label line8 = new Label("Unconditional Branch");
			line8.addStyleName("uncondBLabel");
			Image uncondBranch = new Image("images/uncondBranch.png");
			uncondBranch.setPixelSize(479, 96);
			Image space9 = new Image("images/space.png");
			Label line9 = new Label("Condition codes and Flags");
			line9.addStyleName("condCodesLabel");
			Image condCodes = new Image("images/condCodes.png");
			condCodes.setPixelSize(670, 233);
			Label moreInfo = new Label("Core Instruction Formats");
			moreInfo.addStyleName("moreInfo");
			Image instrFormat1 = new Image("images/instrFormat1.png");
			instrFormat1.setPixelSize(770, 550);
			Image instrFormat2 = new Image("images/instrFormat2.png");
			instrFormat2.setPixelSize(770, 615);
			Image space10 = new Image("images/space.png");
			Label line10 = new Label("Test Walkthrough for Pipelined Processor Simulation");
			line10.addStyleName("title3Label");
			Image testInstr = new Image("images/testInstr.png");
			testInstr.setPixelSize(600, 400);
			Label line11 = new Label("The instructions in the image above were added to the editor and then assembled, ready to be executed. "
					+ "The CPU Log shows the 5 pipeline stages separated by a '|'. So the stages are as follows: Instruction Fetch (IF), "
					+ "Instruction Decode (ID), Execute (EX), Memory Access (MEM), and Write Back (WB). The first instruction 'MOVZ' "
					+ "(Explained in the Data Transfer Instructions) is processed in the IF stage. After this, the second instruction "
					+ "will be processed in the IF stage while the first instruction will move to the ID stage.. and so on.");
			Image ex1 = new Image("images/ex1.png");
			ex1.setPixelSize(660, 202);
			Image space11 = new Image("images/space.png");
			Label line12 = new Label("Something interesting happens when the first arithmetic instrtuction, Subtract, reaches the EX stage."
					+ "The CPU Log shows the following:");
			Image ex2 = new Image("images/ex2.png");
			ex2.setPixelSize(315, 190);
			Label line13 = new Label("A data hazard occurs due to the data dependencies between the two 'MOVZ' instructions and the "
					+ "'SUB' instruction. The processor can either stall the pipeline and wait until the data is available or use logic "
					+ "to detect dependencies and then select where to read the operand from. This simulator has been programmed to do the latter.");
			Label line14 = new Label("Note that 'A' refers to the first operand i.e, the first register you choose to do the operation with; in this "
					+ "case, 'A' is x1. Likewise, 'B' refers to the second operand; in this case x2.");
			line14.addStyleName("note1");
			Label line15 = new Label("'Data Hazard: EX, forward B' means that the processor forwards the value for x2 from the second MOVZ instruction ( currently "
					+ "in the EX stage) to be used in the subtract instruction. 'Data Hazard: MEM, forward A' means that the processor forwards "
					+ "the value for x1 from the first MOVZ instruction (currently in the MEM stage) to be used in the subtract instruction.");
			Image space12 = new Image("images/space.png");
			Label line16 = new Label("Another thing to note is the method to store a value to memory. The two required instructions are MOVZ "
					+ "and LSL. Have a look at instruction 5 and 6. This effectively stores the value '0x10000000' which is the Dynamic memory offset into register x27. "
					+ "After this, the store instruction on line 7 accesses memory by going to the memory location specified by the addition of x27 and the immediate value 5. "
					+ "This would effectively be the fifth location of (accessible) memory.");
			Image space13 = new Image("images/space.png");
			Label line17 = new Label("Again, there is a data hazard as the Add instruction requires x0 (this value is forwarded from the SUB instruction in the EX stage) "
					+ "and x2 (this value is forwarded from the MOVZ instruction in the MEM stage).");
			Image ex3 = new Image("images/ex3.png");
			ex3.setPixelSize(330, 240);
			Image space14 = new Image("images/space.png");
			Label line18 = new Label("The last thing to note for this walkthrough is the an example of a control hazard and pipeline stall. Below, we have a Conditional Branch "
					+ "instruction which will branch if the value in register x6 is equal to zero. The CPU Log shows a line which says 'Control Hazard: Flushing Pipeline'. Note "
					+ "that this does not happen until the instruction is evaluated to be true. Furthermore, there is a data hazard which requires stalling of the pipeline. "
					+ "This is because the new value of register x6 is still being evaluated by the previous SUB instruction. Thus the pipeline is stalled until this value is ready "
					+ "and then the pipeline is flushed when the conditional branch is evaluated to be true.");
			Image ex4 = new Image("images/ex4.png");
			ex4.setPixelSize(295, 196);
			Image space15 = new Image("images/space.png");
			Label line19 = new Label("That concludes this walkthrough. Try out your own tests to check your understanding of both the Single Cycle and Pipelined processors!");
			line19.addStyleName("endLabel");
			line19.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			writingPanel.add(sp1);
			writingPanel.add(help1);
			writingPanel.add(sp2);
			writingPanel.add(help2);
			writingPanel.add(wp1);
			writingPanel.add(sp3);
			writingPanel.add(help3);
			writingPanel.add(wp2);
			writingPanel.add(sp4);
			writingPanel.add(help4);
			writingPanel.add(wp3);
			writingPanel.add(sp5);
			writingPanel.add(help5);
			writingPanel.add(wp4);
			writingPanel.add(sp6);
			writingPanel.add(help6);
			writingPanel.add(wp5);
			writingPanel.add(space1);
			writingPanel.add(line1);
			writingPanel.add(line2);
			writingPanel.add(space2);
			writingPanel.add(regTable);
			writingPanel.add(space3);
			writingPanel.add(line3);
			writingPanel.add(space4);
			writingPanel.add(line4);
			writingPanel.add(arithmetic);
			writingPanel.add(space5);
			writingPanel.add(line5);
			writingPanel.add(dataTransfer);
			writingPanel.add(space6);
			writingPanel.add(line6);
			writingPanel.add(logical);
			writingPanel.add(space7);
			writingPanel.add(line7);
			writingPanel.add(condBranch);
			writingPanel.add(space8);
			writingPanel.add(line8);
			writingPanel.add(uncondBranch);
			writingPanel.add(space9);
			writingPanel.add(line9);
			writingPanel.add(condCodes);
			writingPanel.add(moreInfo);
			writingPanel.add(instrFormat1);
			writingPanel.add(instrFormat2);
			writingPanel.add(space10);
			writingPanel.add(line10);
			writingPanel.add(testInstr);
			writingPanel.add(line11);
			writingPanel.add(ex1);
			writingPanel.add(space11);
			writingPanel.add(line12);
			writingPanel.add(ex2);
			writingPanel.add(line13);
			writingPanel.add(line14);
			writingPanel.add(line15);
			writingPanel.add(space12);
			writingPanel.add(line16);
			writingPanel.add(space13);
			writingPanel.add(line17);
			writingPanel.add(ex3);
			writingPanel.add(space14);
			writingPanel.add(line18);
			writingPanel.add(ex4);
			writingPanel.add(space15);
			writingPanel.add(line19);
			
			page.add(contentPanel);
		}

	private void buildVisualisationUI() {
		page.add(controlPanel);
		if (contentPanel != null) page.remove(contentPanel);
		contentPanel = new HorizontalPanel();
		contentPanel.setSize("100%", "100%");
		contentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		contentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		page.add(contentPanel);
		leftContentPanel = new VerticalPanel();
		leftContentPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		leftContentPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		leftContentPanel.add(editorPanel);
		leftContentPanel.add(registerPanel);
		resetRegisterPanel();
		cpsrPanel.reset();
		contentPanel.add(leftContentPanel);
		int editorHeight = Window.getClientHeight()-controlPanel.getOffsetHeight()
				-registerPanel.getOffsetHeight()-BANNER_HEIGHT-VERTICAL_PADDING;
		if (editorHeight > 200) {
			editor.setHeight(editorHeight + "px");
		} else {
			editor.setHeight("200px");
		}
		datapathPanel = new VerticalPanel();
		datapathPanel.addStyleName("datapathPanel");
		datapathPanel.setSize("100%", "100%");
		datapathPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);		
	}
	
	// initialises all required UI components
	private void initUIComponents() {
		initControlPanel();
		initRegisterPanel();
		initEditorPanel();
		initDebugPanel();
		page = new VerticalPanel();
		page.setWidth("100%");
		page.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		RootPanel.get().add(page);
	}
	
	// builds the control panel at the top of the GUI, must be added to the screen separately
	private void initControlPanel() {
		initAssembleButt();
		initExecModeDropdown();
		initExecuteButt();
		initHelpButt();
		buildControlPanel();
	}
	
	// initialises "Help" button in the control panel
	private void initHelpButt() {
		helpButt = new Button("Help");
		helpButt.setHeight("25px");
		helpButt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildHelpPage();
			}
		});
	}
	
	// initialises the "Assemble" button in the control panel
	private void initAssembleButt() {
		assembleButt = new Button("Assemble");
		assembleButt.setHeight("25px");
		assembleButt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentExMode = executionModes.getSelectedItemText();
				switch (currentExMode) {
//				case SIMULATION :
//					buildSimulationUI();
//					launchSingleCycleSim();
//					pipelineSim = null;
//					break;
				case SINGLE_CYCLE_VISUAL :
					buildSingleCycleUI();
					launchSingleCycleSim();
					pipelineSim = null;
					break;
				case PIPELINE_VISUAL :
					buildPipelineUI();
					launchPipelineSim();
					singleCycleSim = null;
					break;
				}
			}
		});
	}
	
	// initialises the dropdown box for the execution modes in the control panel
	private void initExecModeDropdown () {
		execModesLab = new Label("Execution Mode: ");
		execModesLab.addStyleName("controlLabel");
		executionModes = new ListBox();
		//executionModes.addItem(SIMULATION);
		executionModes.addItem(SINGLE_CYCLE_VISUAL);
		executionModes.addItem(PIPELINE_VISUAL);
		executionModes.setVisibleItemCount(1);
		executionModes.addStyleName("dropdownBox");
		currentExMode = SINGLE_CYCLE_VISUAL;
		executionModes.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				currentExMode = executionModes.getSelectedItemText();
				switch (currentExMode) {
//				case SIMULATION :
//					buildSimulationUI();
//					pipelineSim = null;
//					break;
				case SINGLE_CYCLE_VISUAL :
					buildSingleCycleUI();
					pipelineSim = null;
					break;
				case PIPELINE_VISUAL :
					buildPipelineUI();
					singleCycleSim = null;
					break;
				}
			}
		});
	}
	
	// initialises the "Execute Instruction" button in the control panel
	private void initExecuteButt() {
		executeButt = new Button("Execute Instruction");
		executeButt.setHeight("25px");
		executeButt.setEnabled(false);
		executeButt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (currentExMode.equals(executionModes.getSelectedItemText())) {
					switch (currentExMode) {
//					case SIMULATION : executeInstruction(false);
//					break;
					case SINGLE_CYCLE_VISUAL : executeInstruction(true);
					break;
					case PIPELINE_VISUAL : clockPipeline();
					break;
					}
				} else {
					executeButt.setEnabled(false);
				}
			}
		});
	}
	
	// adds all buttons and dropdown box to the control panel
	private void buildControlPanel() {
		controlPanel = new HorizontalPanel();
		controlPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		controlPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		controlPanel.setWidth("100%");
		controlPanel.setHeight("34px");
		controlPanel.addStyleName("controlPanel");
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonPanel.add(execModesLab);
		buttonPanel.add(executionModes);
		HorizontalPanel padding1 = new HorizontalPanel();
		padding1.setWidth("45px");
		buttonPanel.add(padding1);
		buttonPanel.add(assembleButt);
		HorizontalPanel padding2 = new HorizontalPanel();
		padding2.setWidth("40px");
		buttonPanel.add(padding2);
		buttonPanel.add(executeButt);
		HorizontalPanel padding3 = new HorizontalPanel();
		padding3.setWidth("40px");
		buttonPanel.add(padding3);
		buttonPanel.add(helpButt);
		controlPanel.add(buttonPanel);
	}
	
	// Initialises the register file part of the GUI, must be added to screen separately
	private void initRegisterPanel() {
		HorizontalPanel regFile = new HorizontalPanel();
		VerticalPanel leftRegPanel = new VerticalPanel();
		VerticalPanel rightRegPanel = new VerticalPanel();
		rightRegPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		pcPanel = new RegisterPanel(-1);
		pcPanel.addStyleName("pcReg");
		cpsrPanel = new CPSRPanel();
		cpsrPanel.addStyleName("cpsrReg");
		leftRegPanel.add(pcPanel);
		rightRegPanel.add(cpsrPanel);
		for (int i=0; i<32; i++) {
			regPanels[i] = new RegisterPanel(i);
			if (i < 16) {
				regPanels[i].setStyleName("individualReg");
				leftRegPanel.add(regPanels[i]);
			} else {
				rightRegPanel.add(regPanels[i]);
			}
		}
		regFile.add(leftRegPanel);
		regFile.add(rightRegPanel);
		registerPanel = new VerticalPanel();
		registerPanel.addStyleName("registerPanel");
		registerPanel.add(regFile);
	}
	
	// initialises the editor panel - contains the source code editor only
	private void initEditorPanel() {
		editorPanel = new VerticalPanel();
		editorPanel.setStyleName("editorPanel");
		editorPanel.add(editor);
	}
	
	// initialises the debug panel - contains the cpuLog
	private void initDebugPanel() {
		debugPanel = new VerticalPanel();
		debugPanel.add(new Label("CPU Log"));
		debugPanel.add(cpuLog);
	}
	
	// updates the register values in the GUI after an instruction has been executed
	private void updateRegisterLabels(LEGv8_Simulator sim) {
		for (int i=0; i<regPanels.length; i++) {
			regPanels[i].update(sim.getCPURegister(i));
		}
		pcPanel.update(sim.getPC());
	}
	
	// resets the register values in the GUI to those prior to executing any instructions
	private void resetRegisterPanel() {
		for (int i=0; i<regPanels.length; i++) {
			regPanels[i].reset(i);
		}
		pcPanel.reset(-1);
	}
	
	// updates the flag values in the GUI after an instruction has been executed
	private void updateFlagLabels(LEGv8_Simulator sim) {
		cpsrPanel.update(sim.getCPUZflag(), sim.getCPUNflag(), sim.getCPUCflag(), sim.getCPUVflag());
	}
	
	// starts a new SingleCycleSimulator object
	private void launchSingleCycleSim() {
		editor.clearAnnotations();
		splitIntoLines(editor.getText());
		singleCycleSim = new SingleCycleSimulator(code);
		String text = "";
		for (int i=0; i<singleCycleSim.getCode().size(); i++) {
			text += singleCycleSim.getCode().get(i).getLine() + "\n";
		}
		editor.setText(text);
		editor.removeAllMarkers();
		updateRegisterLabels(singleCycleSim);
		updateFlagLabels(singleCycleSim);
		compileErrors = singleCycleSim.getCompileErrorMsgs();
		if (compileErrors.size() != 0) {
			executeButt.setEnabled(false);
			setCompileErrors();
			editor.setAnnotations();
		} else {
			executeButt.setEnabled(true);
		}
	}
	
	// starts a new PipleineSimulator object
	private void launchPipelineSim() {
		editor.clearAnnotations();
		splitIntoLines(editor.getText());
		pipelineSim = new PipelinedSimulator(code);
		String text = "";
		for (int i=0; i<pipelineSim.getCode().size(); i++) {
			text += pipelineSim.getCode().get(i).getLine() + "\n";
		}
		editor.setText(text);
		editor.removeAllMarkers();
		updateRegisterLabels(pipelineSim);
		updateFlagLabels(pipelineSim);
		compileErrors = pipelineSim.getCompileErrorMsgs();
		if (compileErrors.size() != 0) {
			executeButt.setEnabled(false);
			setCompileErrors();
			editor.setAnnotations();
		} else {
			executeButt.setEnabled(true);
		}
	}
	
	// Executes an instruction when in simulation or single-cycle mode
	private void executeInstruction(boolean visual) {
		singleCycleSim.executeInstruction();
		editor.removeAllMarkers();
		editor.addMarker(AceRange.create(singleCycleSim.getCurrentLineNumber(), 0, singleCycleSim.getCurrentLineNumber(), 
				41), "ace_selection", AceMarkerType.FULL_LINE, false);
		cpuLog.setText(singleCycleSim.getCpuLog());
		updateRegisterLabels(singleCycleSim);
		updateFlagLabels(singleCycleSim);
		if (visual) {
			Timer t = new Timer() {
				private double i = 100;
			
				private void animate(Instruction previous, Instruction current) {
					if (i >= 0) {
						if (i > 50) {
							double j = i-50;
							scDatapath.getContext().setGlobalAlpha(1.0-(50-j)/50.0);
							if (previous == null) {
								scDatapath.datapathInit();
							} else {
								scDatapath.updateDatapath(previous, singleCycleSim.getBranchTaken(), singleCycleSim.getSTXRSucceed(),
										singleCycleSim.getPreviousInsIndex(), code.get(previous.getLineNumber()).getArgs().get(0));
							}
						} else {
							scDatapath.getContext().setGlobalAlpha((50-i)/50.0);
							scDatapath.updateDatapath(current, singleCycleSim.getBranchTaken(), singleCycleSim.getSTXRSucceed(),
									singleCycleSim.getCurrentInsIndex(), code.get(current.getLineNumber()).getArgs().get(0));
						}
						i-=5;
						this.schedule(25);
					} else { 
						this.cancel();
					}
				}
			
				@Override
				public void run() {
					animate(singleCycleSim.getPreviousInstruction(), singleCycleSim.getCurrentInstruction());
				}
			};
			t.run();
		}
		runtimeError = singleCycleSim.getRuntimeErrorMsg();
		if (runtimeError != null) {
			executeButt.setEnabled(false);
			setError(runtimeError.getMsg(), runtimeError.getLineNumber());
			editor.setAnnotations();
		}
	}
	
	// clocks the cpu in the pipeline execution mode
	private void clockPipeline() {
		pipelineSim.clock();
		editor.removeAllMarkers();
		if (pipelineSim.getCurrentLineNumber() != -1) {
		editor.addMarker(AceRange.create(pipelineSim.getCurrentLineNumber(), 0, pipelineSim.getCurrentLineNumber(), 
				41), "ace_selection", AceMarkerType.FULL_LINE, false);
		}
		cpuLog.setText(pipelineSim.getPipelineLog());
		updateRegisterLabels(pipelineSim);
		updateFlagLabels(pipelineSim);
		runtimeError = pipelineSim.getRuntimeErrorMsg();
		if (runtimeError != null) {
			executeButt.setEnabled(false);
			setError(runtimeError.getMsg(), runtimeError.getLineNumber());
			editor.setAnnotations();
		}
	}
	
	// adds the error messages the left side of the source code editor - seen as red box with white cross
	private void setCompileErrors() {
		for (int i=0; i<compileErrors.size(); i++) {
			setError(compileErrors.get(i).getMsg(), compileErrors.get(i).getLineNumber());
		}
	}
	
	private void setError(String message, int lineNumber) {
		editor.addAnnotation(lineNumber, 1, message, AceAnnotationType.ERROR);
	}
	
	// divides the string in the source code editor into individual lines
	// the resulting list of text lines will be passed to a derivative of LEGv8_Simulator for processing
	private void splitIntoLines(String editorText) {
		code = new ArrayList<TextLine>();
		int currentIndex = 0;
		int indexNextNewLineChar;
		while (currentIndex+"\n".length() < editorText.length()) {
			indexNextNewLineChar = editorText.indexOf("\n", currentIndex);
			if (indexNextNewLineChar == -1) {
				code.add(new TextLine(editorText.substring(currentIndex, editorText.length()).trim()));
				currentIndex = editorText.length();
			} else {
				code.add(new TextLine(editorText.substring(currentIndex, indexNextNewLineChar).trim()));
				currentIndex = indexNextNewLineChar+1;
			}
		}
	}
	
	// panels
	private VerticalPanel page;
	private HorizontalPanel controlPanel;
	private HorizontalPanel contentPanel;
	private VerticalPanel editorPanel;
	private VerticalPanel registerPanel;
	private VerticalPanel leftContentPanel;
	private VerticalPanel datapathPanel;
	private VerticalPanel debugPanel;
	private VerticalPanel writingPanel;
	
	private Label execModesLab;
	private String currentExMode;
	private ListBox executionModes;
	private SingleCycleVis scDatapath;
	private PipelineVis pDatapath;
	private Error runtimeError;
	private ArrayList<Error> compileErrors;
	private ArrayList<TextLine> code;
	private Button executeButt;
	private Button assembleButt;
	private Button helpButt;
	private RegisterPanel[] regPanels = new RegisterPanel[32];
	private CPSRPanel cpsrPanel;
	private RegisterPanel pcPanel;
	private SingleCycleSimulator singleCycleSim;
	private PipelinedSimulator pipelineSim;
	private AceEditor editor;
	private AceEditor cpuLog;
	private TextBox commandLine;
}
