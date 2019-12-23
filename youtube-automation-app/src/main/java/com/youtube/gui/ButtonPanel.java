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
import javax.swing.ImageIcon;
import java.awt.Color;

public class ButtonPanel extends JPanel implements ActionListener {

	/**
	 * panel which holds all operating buttons of the GUI
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton setIntervalbtn;
	private JButton StartIntBrdbtn;
	private JButton StopIntbtn;
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
		setLayout(null);
		startButtons();
		
		setIntervalbtn.setBackground(new Color(255, 255, 255));
		setIntervalbtn.setForeground(new Color(0, 204, 204));
		setIntervalbtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		setIntervalbtn.setIcon(new ImageIcon(getClass().getResource("/Time-Machine-icon.png")));
		setIntervalbtn.setBounds(10, 24, 238, 101);
		
		StopIntbtn.setForeground(new Color(178, 34, 34));
		StopIntbtn.setEnabled(false);
		StopIntbtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		StopIntbtn.setIcon(new ImageIcon(getClass().getResource("/stop-sign-icon.png")));
		StopIntbtn.setBounds(10, 261, 238, 114);
		
		
		StartIntBrdbtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		StartIntBrdbtn.setForeground(new Color(50, 205, 50));
		StartIntBrdbtn.setBackground(new Color(255, 255, 255));
		StartIntBrdbtn.setIcon(new ImageIcon(getClass().getResource("/Start-small.png")));
		StartIntBrdbtn.setBounds(10, 136, 238, 114);
		
		
		
		LiveStreamsbtn.setBounds(10, 379, 238, 114);
		LiveStreamsbtn.setIcon(new ImageIcon(getClass().getResource("/YouTube-icon.png")));
		LiveStreamsbtn.setBackground(Color.WHITE);
		LiveStreamsbtn.setForeground(Color.BLACK);
		

		Studiobtn.setIcon(new ImageIcon(getClass().getResource("/YouTube-icon.png")));
		Studiobtn.setBounds(10, 504, 238, 114);
		
		
		setBackground(new Color(255, 255, 255));
		setSize(258, 648);
		setLocation(0, 45);
		getStopIntbtn().setEnabled(false);
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
		setIntervalbtn = new JButton("<html>Set<br>Interval</html>");
		setIntervalbtn.setBackground(new Color(0, 204, 255));
		setIntervalbtn.setBounds(28, 25, 161, 38);
		setIntervalbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		StartIntBrdbtn = new JButton("<html>Start<br>Broadcast</html>");
		StartIntBrdbtn.setBounds(10, 74, 238, 92);
		StartIntBrdbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		StopIntbtn = new JButton("<html>Stop<br>Broadcast</html>");
		StopIntbtn.setBackground(Color.WHITE);
		StopIntbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		
		LiveStreamsbtn = new JButton("<html>YouTube<br>Live Streams</html>");
		LiveStreamsbtn.setBounds(28, 200, 220, 105);
		LiveStreamsbtn.setFont(new Font("Tahoma", Font.BOLD, 15));
	
		Studiobtn = new JButton("<html>YouTube<br>Studio</html>");
		Studiobtn.setBackground(new Color(255, 255, 255));
		Studiobtn.setFont(new Font("Tahoma", Font.BOLD, 15));
		
		
		add(setIntervalbtn);
		add(StartIntBrdbtn);
		add(StopIntbtn);
		add(LiveStreamsbtn);
		add(Studiobtn);
		
		setIntervalbtn.addActionListener(this);
		StartIntBrdbtn.addActionListener(this);
		StopIntbtn.addActionListener(this);
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
	public JButton getLiveStreamsbtn() {
		return LiveStreamsbtn;
	}
	public JButton getStudiobtn() {
		return Studiobtn;
	}
}
