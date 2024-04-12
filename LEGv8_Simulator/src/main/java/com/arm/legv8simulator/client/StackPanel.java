package com.arm.legv8simulator.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * A panel for the web GUI to show a stack value. Also contains buttons to switch between hex and decimal representation
 * 
 * @author Simone Deiana, 2024 */
public class StackPanel extends HorizontalPanel {

	/**
	 * Initialises a stack panel for the web GUI, value of register is initialised according to the stack address.
	 * Stack is initialized to 0.
	 * 
	 * @param stackAddr	the address of the stack
	 */
	public StackPanel(long stackAddr) {
		this.setHeight("20px");
		this.setWidth("250px");
		stackValue = 0;
		hex = true;
		stackAddressPanel = new HorizontalPanel();
		stackAddressPanel.setHorizontalAlignment(ALIGN_CENTER);
		stackAddressPanel.add(new Label("0x"+Long.toHexString(stackAddr)+":"));
		stackAddressPanel.addStyleName("inactive");
		stackAddressPanel.setWidth("30px");
		this.add(stackAddressPanel);
		valueLab = new Label(convertToHex());
		stackValuePanel = new HorizontalPanel();
		stackValuePanel.setHorizontalAlignment(ALIGN_CENTER);
		stackValuePanel.add(valueLab);
		stackValuePanel.setWidth("150px");
		this.add(stackValuePanel);
		hexButt = new Button("Hex");
		hexButt.setWidth("35px");
		hexButt.removeStyleName("gwt-Button");
		hexButt.addStyleName("active");
		this.add(hexButt);
	}
	
	/**
	 * Update this stack panel with the specified value, activity identifier colour is set to 'active'
	 * 
	 * @param newVal	the new value of the register to be displayed on this panel
	 */
	public void update(long newVal) {
		if (stackValue == newVal) {
			stackAddressPanel.removeStyleName("active");
			stackAddressPanel.addStyleName("inactive");
		}
		else {
			stackValue = newVal;
			if (hex) {
				valueLab.setText(convertToHex());
			}/* else {
				valueLab.setText(convertToDecimal());
			}*/
			stackAddressPanel.removeStyleName("inactive");
			stackAddressPanel.addStyleName("active");
		}
	}
	
	/**
	 * Resets the value of this stack panel to 0 and the activity identifier colour is set to 'inactive'
	 * 
	 * @param stackAddr	the number of this register. 
	 */
	public void reset(long stackAddr) {
		stackValue = 0;
		stackAddressPanel.removeStyleName("active");
		stackAddressPanel.addStyleName("inactive");
		if (hex) {
			valueLab.setText(convertToHex());
		} /*else {
			valueLab.setText(convertToDecimal());
		}*/
	}
	
	/*private String convertToDecimal() {
		return Long.toString(stackValue);
	}*/
	
	private String convertToHex() {
		if (stackValue >= 0) {
			return "0x" + Long.toHexString(stackValue);
		} else {
			String lower4Bytes = Long.toHexString(stackValue & 0x00000000ffffffffL);
			if (lower4Bytes.length() < 8) {
				while (lower4Bytes.length() < 8) {
					lower4Bytes = "0" + lower4Bytes;
				}
			}
			return "0x" + Long.toHexString(stackValue>>>32) + lower4Bytes;
		}
	}
	
	
	private long stackValue;
	private HorizontalPanel stackAddressPanel;
	private HorizontalPanel stackValuePanel;
	private Label valueLab;
	private boolean hex;
	private Button hexButt;
	//private Button decButt;
}
