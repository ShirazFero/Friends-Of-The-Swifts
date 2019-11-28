package com.youtube.gui;

import javax.swing.JPanel;
import javax.swing.border.Border;

import com.youtube.utils.Constants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IntervalPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = -4869953988939880062L;

	private JLabel lblNotSet;
	
	private JLabel lblstime;
	
	private JLabel ftime;
	
	private ButtonListener btnListener;
	
	private static IntervalPanel instance;
	
	public IntervalPanel() {
		setLayout(null);
		
		JButton btnLogOut = new JButton("Log out");
		btnLogOut.setBounds(242, 33, 109, 23);
		btnLogOut.addActionListener(this);
		add(btnLogOut);
		
		JLabel lblHello = new JLabel("Hello "+Constants.Username+",");
		lblHello.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblHello.setBounds(10, 36, 150, 14);
		add(lblHello);
		
		
		JLabel lblInterval = new JLabel("Interval:");
		lblInterval.setBounds(10, 93, 77, 14);
		add(lblInterval);
		
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setBounds(10, 118, 77, 14);
		add(lblStartTime);
		
		JLabel lblEndTime = new JLabel("End Time:");
		lblEndTime.setBounds(10, 143, 60, 14);
		add(lblEndTime);
		
		lblNotSet = new JLabel("Interval is not set");
		lblNotSet.setBounds(102, 93, 249, 14);
		add(lblNotSet);
		
		lblstime = new JLabel("stime");
		lblstime.setBounds(102, 118, 237, 14);
		add(lblstime);
		lblstime.setVisible(false);
		
		ftime = new JLabel("ftime");
		ftime.setBounds(102, 143, 237, 14);
		add(ftime);
		ftime.setVisible(false);
		
		Border outerborder = BorderFactory.createTitledBorder("Interval Panel");
		Border innerborder = BorderFactory.createEmptyBorder(5,5,5,5);
		setBorder(BorderFactory.createCompoundBorder(outerborder, innerborder));
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
