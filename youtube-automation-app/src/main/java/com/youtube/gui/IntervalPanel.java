package com.youtube.gui;

import javax.swing.JPanel;
import com.youtube.utils.Constants;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.SystemColor;
import java.awt.Color;

public class IntervalPanel extends JPanel implements ActionListener{
	
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
		
		lblHello = new JLabel("Hello "+Constants.Username);
		lblHello.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblHello.setBounds(10, 49, 312, 23);
		add(lblHello);
		
		
		JLabel lblInterval = new JLabel("Interval:");
		lblInterval.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblInterval.setBounds(10, 83, 87, 28);
		add(lblInterval);
		
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblStartTime.setBounds(10, 122, 114, 21);
		add(lblStartTime);
		
		JLabel lblEndTime = new JLabel("End Time:");
		lblEndTime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblEndTime.setBounds(10, 154, 114, 28);
		add(lblEndTime);
		
		lblNotSet = new JLabel("Interval is not set");
		lblNotSet.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNotSet.setBounds(122, 83, 249, 28);
		add(lblNotSet);
		
		lblstime = new JLabel("stime");
		lblstime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblstime.setBounds(122, 118, 262, 28);
		add(lblstime);
		lblstime.setVisible(false);
		
		ftime = new JLabel("ftime");
		ftime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		ftime.setBounds(122, 154, 262, 28);
		add(ftime);
		ftime.setVisible(false);
		
		setSize(394, 258);
		
		JLabel lblYoutubeAutobroadcastApp = new JLabel("YouTube Auto-Broadcast App");
		lblYoutubeAutobroadcastApp.setForeground(new Color(100, 149, 237));
		lblYoutubeAutobroadcastApp.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblYoutubeAutobroadcastApp.setBounds(58, 11, 290, 29);
		add(lblYoutubeAutobroadcastApp);
		
	}
	
	
	/**
	 * @return the lblHello
	 */
	public JLabel getLblHello() {
		return lblHello;
	}



	public static IntervalPanel getInstance() {
		if(instance == null)
			instance = new IntervalPanel();
		return instance;
	}

	
	/**
	 * @param instance the instance to set
	 */
	public static void setInstance(IntervalPanel instance) {
		IntervalPanel.instance = instance;
	}


	/**
	 * @return the lblNotSet
	 */
	public JLabel getLblNotSet() {
		return lblNotSet;
	}

	/**
	 * @param lblNotSet the lblNotSet to set
	 */
	public void setLblNotSet(JLabel lblNotSet) {
		this.lblNotSet = lblNotSet;
	}

	/**
	 * @return the lblstime
	 */
	public JLabel getLblstime() {
		return lblstime;
	}

	/**
	 * @param lblstime the lblstime to set
	 */
	public void setLblstime(JLabel lblstime) {
		this.lblstime = lblstime;
	}

	/**
	 * @return the ftime
	 */
	public JLabel getFtime() {
		return ftime;
	}

	/**
	 * @param ftime the ftime to set
	 */
	public void setFtime(JLabel ftime) {
		this.ftime = ftime;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		btnListener.ButtonPressed(event.getActionCommand());
	}
	
	public void setBtnListener(ButtonListener btnListener) {
		this.btnListener = btnListener;
	}
}
