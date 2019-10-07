package com.youtube.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

public class TablesPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private BroadcastPanel BP;
	private StreamPanel SP;
	
	public TablesPanel() {
		setLayout(new GridLayout(2,1));
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

	public void setSP(StreamPanel sP) {
		SP = sP;
		add(SP);
	}
	
}
