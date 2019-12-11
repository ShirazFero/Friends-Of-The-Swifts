package com.youtube.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;

public class UserSettingsFrame extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = -5171032239961861958L;

	private ButtonListener btnLsn;
	
	private JComboBox<?> ingestionComboBox;
	
	private JComboBox<?> FormatcomboBox;
	
	private JComboBox<?> privacyComboBox;
	

	public UserSettingsFrame()  {
		super("Settings");
		getContentPane().setLayout(null);
		
		JLabel lblStreamSetttings = new JLabel(" Stream Setttings");
		lblStreamSetttings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblStreamSetttings.setBounds(10, 85, 118, 14);
		getContentPane().add(lblStreamSetttings);
		
		JLabel lblBroadcastSettings = new JLabel("Broadcast Settings");
		lblBroadcastSettings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblBroadcastSettings.setBounds(10, 153, 168, 16);
		getContentPane().add(lblBroadcastSettings);
		
		JLabel lblUserSettings = new JLabel("User settings");
		lblUserSettings.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblUserSettings.setBounds(10, 11, 125, 20);
		getContentPane().add(lblUserSettings);
		
		String formats[]  = {"1080p","1440p_hfr","1440p","1080p_hfr","720p_hfr","720p","480p","360p","240p"};
		
		String ingstionTypes[]= {"rtmp","dash","hls"};
		
		String privacy[] = {"public","private","unlisted"};
		
		FormatcomboBox = new JComboBox<Object>(formats);
		FormatcomboBox.setBounds(100, 110, 78, 20);
		getContentPane().add(FormatcomboBox);
		
		ingestionComboBox = new JComboBox<Object>(ingstionTypes);
		ingestionComboBox.setBounds(336, 110, 75, 20);
		getContentPane().add(ingestionComboBox);
		
		privacyComboBox = new JComboBox<Object>(privacy);
		privacyComboBox.setBounds(100, 180, 78, 20);
		getContentPane().add(privacyComboBox);
		
		JLabel lblSelectFormat = new JLabel("Select Format");
		lblSelectFormat.setBounds(12, 109, 89, 14);
		getContentPane().add(lblSelectFormat);
		
		JLabel lblSelectIngestionType = new JLabel("Select ingestion type");
		lblSelectIngestionType.setBounds(208, 109, 125, 14);
		getContentPane().add(lblSelectIngestionType);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(332, 230, 89, 23);
		btnOk.addActionListener(this);
		getContentPane().add(btnOk);
		
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(this);
		btnApply.setBounds(240, 230, 89, 23);
		getContentPane().add(btnApply);
		
		JLabel lblSelectPrivacy = new JLabel("Select Privacy ");
		lblSelectPrivacy.setBounds(10, 180, 96, 14);
		getContentPane().add(lblSelectPrivacy);
		
		JLabel lblNotemakeSureYour = new JLabel("NOTE: please make sure your Encoder is set to the same settings");
		lblNotemakeSureYour.setForeground(Color.RED);
		lblNotemakeSureYour.setBounds(10, 137, 382, 14);
		getContentPane().add(lblNotemakeSureYour);
		
		JButton btnNewButton = new JButton("Change Password");
		btnNewButton.setBounds(10, 51, 156, 23);
		getContentPane().add(btnNewButton);
		
		JCheckBox chckbxAddDateAnd = new JCheckBox("Add Date and Time to Broadcast title");
		chckbxAddDateAnd.setSelected(true);
		chckbxAddDateAnd.setBounds(10, 202, 286, 23);
		getContentPane().add(chckbxAddDateAnd);
		
		setSize(447,303);
		setLocationRelativeTo(null);
	}

	/**
	 * @return the ingestionComboBox
	 */
	public JComboBox<?> getIngestionComboBox() {
		return ingestionComboBox;
	}


	/**
	 * @return the formatcomboBox
	 */
	public JComboBox<?> getFormatcomboBox() {
		return FormatcomboBox;
	}


	/**
	 * @return the privacyComboBox
	 */
	public JComboBox<?> getPrivacyComboBox() {
		return privacyComboBox;
	}



	/**
	 * @param btnLsn the btnLsn to set
	 */
	public void setBtnListener(ButtonListener btnLsn) {
		this.btnLsn = btnLsn;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		btnLsn.ButtonPressed(e.getActionCommand());
	}
}
