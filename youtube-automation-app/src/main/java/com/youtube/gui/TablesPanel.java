package com.youtube.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

public class TablesPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private IntervalPanel IP;
	private BroadcastPanel BP;
	private StreamPanel SP;
	
	public TablesPanel(IntervalPanel IP,BroadcastPanel BP,StreamPanel SP) {
		setLayout(new GridLayout(3,1));
		this.IP=IP;
		this.BP=BP;
		this.SP=SP;
		add(this.IP);
		add(this.SP);
		add(this.BP);
		
	}

	public BroadcastPanel getBP() {
		return BP;
	}

	public void setBP(BroadcastPanel bP) {
		BP = bP;
		add(BP);
	}

	public StreamPanel getSP() {
		return SP;
	}

	public IntervalPanel getIP() {
		return IP;
	}

	public void setIP(IntervalPanel iP) {
		IP = iP;
		add(IP);
	}

	public void setSP(StreamPanel sP) {
		SP = sP;
		add(SP);
	}
	
}
