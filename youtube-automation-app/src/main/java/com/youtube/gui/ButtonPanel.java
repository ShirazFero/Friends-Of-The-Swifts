package com.youtube.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class ButtonPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton CheckStreambtn;
	private JButton StartIntBrdbtn;
	private JButton StopIntbtn;
	private JButton Streamsbtn;
	private JButton AddStreambtn;
	private JButton ReomveStreambtn;
	private ButtonListener btnListener;
	
	public ButtonPanel() {
		
		Border outerborder = BorderFactory.createTitledBorder("Menu");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
		setLayout(new GridBagLayout());
		startButtons();
		
		
	}
	public void setBtnListener(ButtonListener listener) {
		this.btnListener=listener;
	}
	
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent ev) {
		
		JButton jb = (JButton) ev.getSource();
		//System.out.println("btnpanl:"+jb.getLabel());
		btnListener.ButtonPressed(jb.getLabel());
	}
	
	private void startButtons() {
		CheckStreambtn = new JButton("Check Stream");
		StartIntBrdbtn = new JButton("Start Interval Broadcast");
		StopIntbtn = new JButton("Stop Interval Broadcast");
		Streamsbtn = new JButton("My Streams");
		AddStreambtn = new JButton("Add Stream");
		ReomveStreambtn = new JButton("Remove Stream");
		GridBagConstraints gc = new GridBagConstraints();
		//--- first button
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.LINE_START;
		add(CheckStreambtn,gc);
		//--- second button
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridx =	0;
		gc.gridy =	1;
		gc.anchor = GridBagConstraints.LINE_START;
		add(StartIntBrdbtn,gc);
		//--- third button
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridx =	0;
		gc.gridy =	2;
		gc.anchor = GridBagConstraints.LINE_START;
		add(StopIntbtn,gc);
		//--- fourth button
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridx =	0;
		gc.gridy =	3;
		gc.anchor = GridBagConstraints.LINE_START;
		add(Streamsbtn,gc);
		//--- fifth button
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridx =	0;
		gc.gridy =	4;
		gc.anchor = GridBagConstraints.LINE_START;
		add(AddStreambtn,gc);
		//--- sixth button
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridx =	0;
		gc.gridy =	5;
		gc.anchor = GridBagConstraints.LINE_START;
		add(ReomveStreambtn,gc);
		//-----------------------------
		CheckStreambtn.addActionListener(this);
		StartIntBrdbtn.addActionListener(this);
		StopIntbtn.addActionListener(this);
		Streamsbtn.addActionListener(this);
		AddStreambtn.addActionListener(this);
		ReomveStreambtn.addActionListener(this);
		
	}
}
