package com.youtube.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.youtube.utils.Constants;

import java.awt.Font;

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
	private JButton LiveStreamsbtn;
	private JButton Studiobtn;
	private ButtonListener btnListener;
	
	private static ButtonPanel instance;
	
	

	/**
	 * @param instance the instance to set
	 */
	public  void setInstance(ButtonPanel instance) {
		ButtonPanel.instance = instance;
	}
	public ButtonPanel() {
		
		Border outerborder = BorderFactory.createTitledBorder("Menu");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
		setLayout(new GridLayout(7,1));
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
		setIntervalbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		StartIntBrdbtn = new JButton("Start Interval Broadcast");
		StartIntBrdbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		StopIntbtn = new JButton("Stop Interval Broadcast");
		StopIntbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		StartBrdbtn = new JButton("Start Broadcast");
		StartBrdbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		StopBrdbtn = new JButton("Stop Broadcast");
		StopBrdbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		LiveStreamsbtn = new JButton(Constants.OYLS);
		LiveStreamsbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		Studiobtn = new JButton("Open YouTube Studio");
		Studiobtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		add(setIntervalbtn);
		add(StartIntBrdbtn);
		add(StopIntbtn);
		add(StartBrdbtn);
		add(StopBrdbtn);
		add(LiveStreamsbtn);
		add(Studiobtn);
		
		setIntervalbtn.addActionListener(this);
		StartIntBrdbtn.addActionListener(this);
		StopIntbtn.addActionListener(this);
		StartBrdbtn.addActionListener(this);
		StopBrdbtn.addActionListener(this);
		LiveStreamsbtn.addActionListener(this);
		Studiobtn.addActionListener(this);
		
	}
	
	//-----button getters----------------
	
	public static ButtonPanel getInstance() {
		if(instance==null)
			instance=new ButtonPanel();
		return instance;
	}
	
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
	public JButton getLiveStreamsbtn() {
		return LiveStreamsbtn;
	}
	public JButton getStudiobtn() {
		return Studiobtn;
	}
}
