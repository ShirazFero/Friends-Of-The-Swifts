package com.youtube.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class ButtonPanel extends JPanel implements ActionListener {

	/**
	 * panel which holds all operating buttons of the GUI
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton setIntervalbtn;
	private JButton StartIntBrdbtn;
	private JButton StopIntbtn;
	private JButton StartBrdbtn;
	private JButton StopBrdbtn;
	private JButton Testbtn;
	private ButtonListener btnListener;
	
	

	public ButtonPanel() {
		
		Border outerborder = BorderFactory.createTitledBorder("Menu");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
		setLayout(new GridLayout(6,1));
		startButtons();
		getStopIntbtn().setEnabled(false);
		getStopBrdbtn().setEnabled(false);
//		getStartIntBrdbtn().setEnabled(false);
		
	}
	public void setBtnListener(ButtonListener listener) {
		this.btnListener=listener;
	}
	
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent ev) {
		JButton jb = (JButton) ev.getSource();
		btnListener.ButtonPressed(jb.getLabel());
	}
	
	private void startButtons() {
		setIntervalbtn = new JButton("Set Interval");
		StartIntBrdbtn = new JButton("Start Interval Broadcast");
		StopIntbtn = new JButton("Stop Interval Broadcast");
		StartBrdbtn = new JButton("Start Broadcast");
		StopBrdbtn = new JButton("Stop Broadcast");
		Testbtn = new JButton("Test");
		
		add(setIntervalbtn);
		add(StartIntBrdbtn);
		add(StopIntbtn);
		add(StartBrdbtn);
		add(StopBrdbtn);
		add(Testbtn);
		
		setIntervalbtn.addActionListener(this);
		StartIntBrdbtn.addActionListener(this);
		StopIntbtn.addActionListener(this);
		StartBrdbtn.addActionListener(this);
		StopBrdbtn.addActionListener(this);
		Testbtn.addActionListener(this);
		
	}
	
	//-----button getters----------------
	public JButton getsetIntervalbtn() {
		return setIntervalbtn;
	}
	public JButton getStartIntBrdbtn() {
		return StartIntBrdbtn;
	}
	public JButton getStopIntbtn() {
		return StopIntbtn;
	}
	public JButton getStartBrdbtn() {
		return StartBrdbtn;
	}
	public JButton getStopBrdbtn() {
		return StopBrdbtn;
	}
	public JButton getTestbtn() {
		return Testbtn;
	}
}
