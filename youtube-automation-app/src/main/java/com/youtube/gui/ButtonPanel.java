package com.youtube.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.SystemColor;

/**
 * panel which holds all operating buttons of the GUI
 */
public class ButtonPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -4615359192110905493L;

	private JButton setIntervalbtn;
	private JButton StartIntBrdbtn;
	private JButton StopIntbtn;
	private JButton LiveStreamsbtn;
	private JButton Studiobtn;
	private JButton logOutBtn;
	private ButtonListener btnListener;
	
	/**
	 * @return the logOutBtn
	 */
	public JButton getLogOutBtn() {
		return logOutBtn;
	}
	private static ButtonPanel instance;
	
	/**
	 * @param instance the instance to set
	 */
	public  void setInstance(ButtonPanel instance) {
		ButtonPanel.instance = instance;
	}
	
	public ButtonPanel() {
		
		
		setLayout(null);
		startButtons();
		
		setIntervalbtn.setBackground(new Color(255, 255, 255));
		setIntervalbtn.setForeground(new Color(0, 204, 204));
		setIntervalbtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		setIntervalbtn.setIcon(new ImageIcon(getClass().getResource("/Time-Machine-icon.png")));
		setIntervalbtn.setBounds(10, 10, 238, 114);
		
		StartIntBrdbtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		StartIntBrdbtn.setForeground(new Color(50, 205, 50));
		StartIntBrdbtn.setBackground(new Color(255, 255, 255));
		StartIntBrdbtn.setIcon(new ImageIcon(getClass().getResource("/Start-small.png")));
		StartIntBrdbtn.setBounds(10, 130, 238, 114);
		
		
		StopIntbtn.setEnabled(false);
		StopIntbtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		StopIntbtn.setIcon(new ImageIcon(getClass().getResource("/stop-sign-icon.png")));
		StopIntbtn.setBackground(Color.WHITE);
		StopIntbtn.setForeground(new Color(178, 34, 34));
		StopIntbtn.setBounds(10, 130, 238, 114);
		
		LiveStreamsbtn.setBounds(10, 250, 238, 114);
		LiveStreamsbtn.setIcon(new ImageIcon(getClass().getResource("/liveicon6.png")));
		LiveStreamsbtn.setBackground(Color.WHITE);
		LiveStreamsbtn.setForeground(new Color(100, 149, 237));
		LiveStreamsbtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		
		Studiobtn.setIcon(new ImageIcon(getClass().getResource("/studio.png")));
		Studiobtn.setBounds(10, 370, 238, 114);
		Studiobtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		Studiobtn.setBackground(Color.WHITE);
		Studiobtn.setForeground(Color.BLACK);
		
		logOutBtn.setFont(new Font("Tahoma", Font.BOLD, 25));
		logOutBtn.setBackground(SystemColor.textHighlightText);
		logOutBtn.setForeground(SystemColor.activeCaption);
		logOutBtn.setIcon(new ImageIcon(getClass().getResource("/logout_icon.png")));
		logOutBtn.setBounds(10, 490, 238, 114);
		
		setBackground(new Color(255, 255, 255));
		setSize(258, 648);
		setLocation(0, 10);
		getStopIntbtn().setEnabled(false);
		
	}
	public void setBtnListener(ButtonListener listener) {
		this.btnListener=listener;
	}
	
	public void actionPerformed(ActionEvent ev) {
		btnListener.ButtonPressed(ev.getActionCommand());
	}
	
	private void startButtons() {
		
		setIntervalbtn = new JButton("<html>Set<br>Interval</html>");
		StartIntBrdbtn = new JButton("<html>Start Live<br>Broadcast</html>");
		StopIntbtn = new JButton("<html>Stop Live<br>Broadcast</html>");
		LiveStreamsbtn = new JButton("<html>Live<br>Manager</html>");
		Studiobtn = new JButton("<html>YouTube<br>Studio</html>");
		logOutBtn = new JButton("Log Out");
		
		add(setIntervalbtn);
		add(StartIntBrdbtn);
		add(StopIntbtn);
		add(LiveStreamsbtn);
		add(Studiobtn);
		add(logOutBtn);
		
		setIntervalbtn.addActionListener(this);
		StartIntBrdbtn.addActionListener(this);
		StopIntbtn.addActionListener(this);
		LiveStreamsbtn.addActionListener(this);
		Studiobtn.addActionListener(this);
		logOutBtn.addActionListener(this);
		
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
