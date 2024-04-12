package com.arm.legv8simulator.client;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Panel to hold the Current Program Status Register (CPSR) flags in the web GUI
 * 
 * @author Jonathan Wright, 2016
 */
public class CPSRPanel extends HorizontalPanel {
	
	/**
	 * Builds the CPSR panel for the web GUI, flag values are initialised to false by default
	 */
	public CPSRPanel() {
		this.setWidth("250px");
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		initZFlagPanel();
		initNFlagPanel();
		initCFlagPanel();
		initVFlagPanel();
	}
	
	private void initZFlagPanel() {
		zPanel = new HorizontalPanel();
		zPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		zPanel.add(new Label("Z"));
		zPanel.addStyleName("inactive");
		zPanel.setWidth("30px");
		zValue = new Label("0");
		HorizontalPanel zValuePanel = new HorizontalPanel();
		zValuePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		zValuePanel.add(zValue);
		zValuePanel.setWidth("30px");
		this.add(zPanel);
		this.add(zValuePanel);
	}
	
	private void initNFlagPanel() {
		nPanel = new HorizontalPanel();
		nPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		nPanel.add(new Label("N"));
		nPanel.addStyleName("inactive");
		nPanel.setWidth("30px");
		nValue = new Label("0");
		HorizontalPanel nValuePanel = new HorizontalPanel();
		nValuePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		nValuePanel.add(nValue);
		nValuePanel.setWidth("30px");
		this.add(nPanel);
		this.add(nValuePanel);
	}
	
	private void initCFlagPanel() {
		cPanel = new HorizontalPanel();
		cPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cPanel.add(new Label("C"));
		cPanel.addStyleName("inactive");
		cPanel.setWidth("30px");
		cValue = new Label("0");
		HorizontalPanel cValuePanel = new HorizontalPanel();
		cValuePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		cValuePanel.add(cValue);
		cValuePanel.setWidth("30px");
		this.add(cPanel);
		this.add(cValuePanel);
	}
	
	private void initVFlagPanel() {
		vPanel = new HorizontalPanel();
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(new Label("V"));
		vPanel.addStyleName("inactive");
		vPanel.setWidth("30px");
		vValue = new Label("0");
		HorizontalPanel vValuePanel = new HorizontalPanel();
		vValuePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vValuePanel.add(vValue);
		vValuePanel.setWidth("30px");
		this.add(vPanel);
		this.add(vValuePanel);
	}
	
	/**
	 * Set all flag values to 0 and activity identifier colours to 'inactive'
	 */
	public void reset() {
		zFlag = false;
		zValue.setText("0");
		zPanel.removeStyleName("active");
		zPanel.addStyleName("inactive");
		nFlag = false;
		nValue.setText("0");
		nPanel.removeStyleName("active");
		nPanel.addStyleName("inactive");
		cFlag = false;
		cValue.setText("0");
		cPanel.removeStyleName("active");
		cPanel.addStyleName("inactive");
		vFlag = false;
		vValue.setText("0");
		vPanel.removeStyleName("active");
		vPanel.addStyleName("inactive");
	}
	
	/**
	 * Updates flags with the specified values
	 * 
	 * @param z	new value of the Z flag
	 * @param n	new value of the N flag
	 * @param c	new value of the C flag
	 * @param v	new value of the V flag
	 */
	public void update(boolean z, boolean n, boolean c, boolean v) {
		updateZPanel(z);
		updateNPanel(n);
		updateCPanel(c);
		updateVPanel(v);
	}
	
	private void updateZPanel(boolean z) {
		if (zFlag == z) {
			zPanel.removeStyleName("active");
			zPanel.addStyleName("inactive");
		} else {
			zFlag = z;
			zValue.setText(boolToString(zFlag));
			zPanel.removeStyleName("inactive");
			zPanel.addStyleName("active");
		}
	}
	
	private void updateNPanel(boolean n) {
		if (nFlag == n) {
			nPanel.removeStyleName("active");
			nPanel.addStyleName("inactive");
		} else {
			nFlag = n;
			nValue.setText(boolToString(nFlag));
			nPanel.removeStyleName("inactive");
			nPanel.addStyleName("active");
		}
	}
	
	private void updateCPanel(boolean c) {
		if (cFlag == c) {
			cPanel.removeStyleName("active");
			cPanel.addStyleName("inactive");
		} else {
			cFlag = c;
			cValue.setText(boolToString(cFlag));
			cPanel.removeStyleName("inactive");
			cPanel.addStyleName("active");
		}
	}
	
	private void updateVPanel(boolean v) {
		if (vFlag == v) {
			vPanel.removeStyleName("active");
			vPanel.addStyleName("inactive");
		} else {
			vFlag = v;
			vValue.setText(boolToString(vFlag));
			vPanel.removeStyleName("inactive");
			vPanel.addStyleName("active");
		}
	}
	
	private String boolToString(boolean b) {
		return b ? "1" : "0";
	}
	
	private HorizontalPanel zPanel;
	private HorizontalPanel nPanel;
	private HorizontalPanel cPanel;
	private HorizontalPanel vPanel;

	private Label zValue;
	private Label nValue;
	private Label cValue;
	private Label vValue;
	
	private boolean zFlag;
	private boolean nFlag;
	private boolean cFlag;
	private boolean vFlag;
}
