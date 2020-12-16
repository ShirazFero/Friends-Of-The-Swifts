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
		
		lblHello = new JLabel("Hello "+Constants.Username);
		lblHello.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblHello.setBounds(0, 41, 303, 49);
		add(lblHello);
		
		
		JLabel lblInterval = new JLabel("Interval:");
		lblInterval.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblInterval.setBounds(0, 101, 87, 28);
		add(lblInterval);
		
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblStartTime.setBounds(0, 140, 114, 21);
		add(lblStartTime);
		
		JLabel lblEndTime = new JLabel("End Time:");
		lblEndTime.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblEndTime.setBounds(0, 172, 114, 28);
		add(lblEndTime);
		
		lblNotSet = new JLabel("Interval is not set");
		lblNotSet.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNotSet.setBounds(100, 101, 249, 28);
		add(lblNotSet);
		
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
		Color c =  Color.decode("#CF1717");
		setSize(388, 225);
		setLocation(257, 10);
		JLabel lblYoutubeAutobroadcastApp = new JLabel("<html>YouTube Auto Broadcast App</html>");
		lblYoutubeAutobroadcastApp.setForeground(c);
		lblYoutubeAutobroadcastApp.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblYoutubeAutobroadcastApp.setBounds(0, -9, 312, 68);
		add(lblYoutubeAutobroadcastApp);
		
//		JLabel lblBrdapp = new JLabel("<html>Broadcast App</html>");
//		lblBrdapp.setEnabled(false);
//		lblBrdapp.setFont(new Font("Tahoma", Font.BOLD, 20));
//		
//		lblBrdapp.setForeground(c);
//		lblBrdapp.setBounds(115, 18, 187, 68);
//		add(lblBrdapp);
		
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
	
	public  void updateIntervalPanel(String newStartTime, String stopTime) 
	{
		IntervalPanel intervalPanel = IntervalPanel.getInstance();
		//prompt new start time to interval panel
		intervalPanel.getLblstime().setText(newStartTime);
		//prompt new end time to interval panel
		intervalPanel.getFtime().setText(stopTime);
	}
}
