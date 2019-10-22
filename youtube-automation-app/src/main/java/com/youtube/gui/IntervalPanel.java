package com.youtube.gui;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class IntervalPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JLabel lblNotSet;
	
	private JLabel lblstime;
	
	private JLabel ftime;
	
	private static IntervalPanel instance;
	
	public IntervalPanel() {
		setLayout(null);
		
		JLabel lblInterval = new JLabel("Interval:");
		lblInterval.setBounds(10, 33, 77, 14);
		add(lblInterval);
		
		JLabel lblStartTime = new JLabel("Start Time:");
		lblStartTime.setBounds(10, 58, 77, 14);
		add(lblStartTime);
		
		JLabel lblEndTime = new JLabel("End Time:");
		lblEndTime.setBounds(10, 83, 60, 14);
		add(lblEndTime);
		
		lblNotSet = new JLabel("not set");
		lblNotSet.setBounds(102, 33, 249, 14);
		add(lblNotSet);
		
		 lblstime = new JLabel("stime");
		lblstime.setBounds(104, 58, 237, 14);
		add(lblstime);
		lblstime.setVisible(false);
		
		 ftime = new JLabel("ftime");
		ftime.setBounds(102, 83, 237, 14);
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

	
}
