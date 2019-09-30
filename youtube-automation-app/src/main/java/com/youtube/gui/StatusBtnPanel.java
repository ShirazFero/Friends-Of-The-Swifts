package com.youtube.gui;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class StatusBtnPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JButton refreshbtn;
	
	private JButton AddStreambtn;
	
	private JButton ReomveStreambtn;
	
	public StatusBtnPanel(){
		refreshbtn = new JButton("Refresh");
		AddStreambtn = new JButton("Add Stream");
		ReomveStreambtn = new JButton("Remove Streams");
		
		setLayout(new FlowLayout());
		add(refreshbtn);
		add(AddStreambtn);
		add(ReomveStreambtn);
		
	}

	public JButton getRefreshbtn() {
		return refreshbtn;
	}

	public JButton getAddStreambtn() {
		return AddStreambtn;
	}

	public JButton getReomveStreambtn() {
		return ReomveStreambtn;
	}

	
}
