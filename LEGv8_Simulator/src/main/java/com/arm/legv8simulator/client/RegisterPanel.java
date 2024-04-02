package com.arm.legv8simulator.client;

import com.arm.legv8simulator.client.cpu.CPU;
import com.arm.legv8simulator.client.cpu.RegisterType;
import com.arm.legv8simulator.client.memory.Memory;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A panel for the web GUI to show a register value. Also contains buttons to switch between hex and decimal representation
 * 
 * @author Jonathan Wright, 2016
 */
public class RegisterPanel extends HorizontalPanel {

	/**
	 * Initialises a register panel for the web GUI, value of register is initialised according to the register number.
	 * PC and SP registers are initialised to the values defined in Patternson and Hennessey ARM Edition. All other register initialised to 0.
	 * 
	 * @param reg	the register number. E.g. 15 for register X15
	 */
	public RegisterPanel(RegisterType type, int reg) {
		this.setHeight("20px");
		this.setWidth("250px");
		
		if (type == RegisterType.X && reg == CPU.SP) {
			regValue = Memory.STACK_BASE;	
			} else if (type == RegisterType.X && reg == -1) {
			regValue = Memory.TEXT_SEGMENT_OFFSET;
		} else {
			regValue = 0;
		}
		
		this.regType = type;
		
		hex = true;
		registerPanel = new HorizontalPanel();
		registerPanel.setHorizontalAlignment(ALIGN_CENTER);
		registerPanel.add(new Label(getRegStr(reg)));
		registerPanel.addStyleName("inactive");
		registerPanel.setWidth("30px");
		this.add(registerPanel);
		valueLab = new Label(convertToHex());
		regValuePanel = new HorizontalPanel();
		regValuePanel.setHorizontalAlignment(ALIGN_CENTER);
		regValuePanel.add(valueLab);
		regValuePanel.setWidth("150px");
		this.add(regValuePanel);
		hexButt = new Button("Hex");
		hexButt.setWidth("35px");
		hexButt.removeStyleName("gwt-Button");
		hexButt.addStyleName("active");
		hexButt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hex = true;
				valueLab.setText(convertToHex());
				hexButt.removeStyleName("inactive");
				hexButt.addStyleName("active");
				decButt.removeStyleName("active");
				decButt.addStyleName("inactive");
			}
		});
		this.add(hexButt);
		decButt = new Button("Dec");
		decButt.setWidth("35px");
		decButt.removeStyleName("gwt-Button");
		decButt.addStyleName("inactive");
		decButt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hex = false;
				valueLab.setText(convertToDecimal()); 
				decButt.removeStyleName("inactive");
				decButt.addStyleName("active");
				hexButt.removeStyleName("active");
				hexButt.addStyleName("inactive");
			}
		});
		this.add(decButt);
	}
	
	private String getRegStr(int reg) {
		
		if(this.regType == RegisterType.X) {
			switch (reg) {
			case CPU.FP : return "FP";
			case CPU.SP : return "SP";
			case CPU.LR : return "LR";
			case CPU.XZR : return "XZR";
			case -1 : return "PC";
			default : break;
			}
		}
		
		return this.regType.name() + String.valueOf(reg);
		
	}
	
	/**
	 * Update this register panel with the specified value, activity identifier colour is set to 'active'
	 * 
	 * @param newVal	the new value of the register to be displayed on this panel
	 */
	public void update(long newVal) {
		if (regValue == newVal) {
			registerPanel.removeStyleName("active");
			registerPanel.addStyleName("inactive");
		}
		else {
			regValue = newVal;
			if (hex) {
				valueLab.setText(convertToHex());
			} else {
				valueLab.setText(convertToDecimal());
			}
			registerPanel.removeStyleName("inactive");
			registerPanel.addStyleName("active");
		}
	}
	
	/**
	 * Resets the value of theis register panel to 0 and the activity identifier colour is set to 'inactive'
	 * 
	 * @param reg	the number of this register. E.g. 10 for X10
	 */
	public void reset(int reg) {
		if (this.regType == RegisterType.X && reg == 28) {
			regValue = Memory.STACK_BASE; // quadword aligned stack base to avoid manual adjustment every time (could break something, only done for convenience), SIMONE.DEIANA@studenti.units.it
		} else if (this.regType == RegisterType.X && reg == -1) {
			regValue = Memory.TEXT_SEGMENT_OFFSET;
		} else {
			regValue = 0;
		}
		registerPanel.removeStyleName("active");
		registerPanel.addStyleName("inactive");
		if (hex) {
			valueLab.setText(convertToHex());
		} else {
			valueLab.setText(convertToDecimal());
		}
	}
	
	private String convertToDecimal() {
		switch(this.regType) {
		case X: return Long.toString(regValue);
		case D: return Double.toString(Double.longBitsToDouble(regValue));
		case S: return Float.toString(Float.intBitsToFloat((int) regValue));
		default: return "";
		}
	}
	
	private String convertToHex() {
		if (regValue >= 0) {
			return "0x" + Long.toHexString(regValue);
		} else {
			String lower4Bytes = Long.toHexString(regValue & 0x00000000ffffffffL);
			if (lower4Bytes.length() < 8) {
				while (lower4Bytes.length() < 8) {
					lower4Bytes = "0" + lower4Bytes;
				}
			}
			return "0x" + Long.toHexString(regValue>>>32) + lower4Bytes;
		}
	}
	
	
	private long regValue;
	private RegisterType regType;
	private HorizontalPanel registerPanel;
	private HorizontalPanel regValuePanel;
	private Label valueLab;
	private boolean hex;
	private Button hexButt;
	private Button decButt;
}
