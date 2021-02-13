package com.youtube.gui;

import javax.swing.JPanel;
import com.youtube.utils.Constants;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.SystemColor;
import java.awt.Color;

public class IntervalPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -4869953988939880062L;

	private JLabel lblNotSet;
	
	private JLabel lblstime;
	
	private JLabel ftime;
	
	private JLabel lblHello;
	
	private ButtonListener btnListener;
	
	private static IntervalPanel instance;
	
	public IntervalPanel() {
		setBackground(SystemColor.textHighlightText);
		setLayout(null);
		initHelloLbl();
		initIntervalLbl();
		initStartTimeLbl();
		initEndTimeLbl();
		initNotSetLbl();
		initFStimeLbls();
		initYouTubeAppLbl();
		initIcon();
	}
	
	private void initHelloLbl()
	{
		lblHello = new JLabel("Hello "+Constants.Username);
		lblHello.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblHello.setBounds(0, 41, 303, 49);
		add(lblHello);
	}
	
	private void initIntervalLbl()
	{
		JLabel lblInterval = new JLabel("Interval:");
		lblInterval.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblInterval.setBounds(0, 101, 87, 28);
		add(lblInterval);
	}
	
	private void initStartTimeLbl()
	{
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblStartTime.setBounds(0, 140, 114, 21);
		add(lblStartTime);
	}
	
	private void initEndTimeLbl()
	{
		JLabel lblEndTime = new JLabel("End Time:");
		lblEndTime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblEndTime.setBounds(0, 172, 114, 28);
		add(lblEndTime);
	}
	
	private void initNotSetLbl()
	{
		lblNotSet = new JLabel("Interval is not set");
		lblNotSet.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNotSet.setBounds(100, 101, 249, 28);
		add(lblNotSet);
	}
	
	private void initFStimeLbls()
	{
		lblstime = new JLabel("stime");
		lblstime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblstime.setBounds(100, 136, 262, 28);
		add(lblstime);
		lblstime.setVisible(false);
		
		ftime = new JLabel("ftime");
		ftime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		ftime.setBounds(100, 172, 262, 28);
		add(ftime);
		ftime.setVisible(false);
	}
	
	private void initYouTubeAppLbl()
	{
		Color c =  Color.decode("#CF1717");
		setSize(388, 225);
		setLocation(257, 10);
		JLabel lblYoutubeAutobroadcastApp = new JLabel("<html>YouTube Auto Broadcast App</html>");
		lblYoutubeAutobroadcastApp.setForeground(c);
		lblYoutubeAutobroadcastApp.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblYoutubeAutobroadcastApp.setBounds(0, -9, 312, 68);
		add(lblYoutubeAutobroadcastApp);
	}
	
	private void initIcon()
	{
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(300, -9, 78, 105);
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("/YABA2.png")));
		add(lblNewLabel);
	}
	
	public JLabel getLblHello() {
		return lblHello;
	}

	public static IntervalPanel getInstance() {
		if(instance == null)
			instance = new IntervalPanel();
		return instance;
	}

	public static void setInstance(IntervalPanel instance) {
		IntervalPanel.instance = instance;
	}

	public JLabel getLblNotSet() {
		return lblNotSet;
	}

	public void setLblNotSet(JLabel lblNotSet) {
		this.lblNotSet = lblNotSet;
	}

	public JLabel getLblstime() {
		return lblstime;
	}

	public void setLblstime(JLabel lblstime) {
		this.lblstime = lblstime;
	}

	public JLabel getFtime() {
		return ftime;
	}

	public void setFtime(JLabel ftime) {
		this.ftime = ftime;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		btnListener.ButtonPressed(event.getActionCommand());
	}
	
	public void setBtnListener(ButtonListener btnListener) {
		this.btnListener = btnListener;
	}
	
	public  void updateIntervalPanel(String newStartTime, String stopTime) 
	{
		IntervalPanel intervalPanel = IntervalPanel.getInstance();
		//prompt new start time to interval panel
		intervalPanel.getLblstime().setText(newStartTime);
		//prompt new end time to interval panel
		intervalPanel.getFtime().setText(stopTime);
	}
}
