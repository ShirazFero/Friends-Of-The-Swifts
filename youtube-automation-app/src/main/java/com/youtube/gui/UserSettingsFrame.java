package com.youtube.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.youtube.utils.Constants;

import javax.swing.JComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JCheckBox;
import java.awt.SystemColor;

public class UserSettingsFrame extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -5171032239961861958L;

	private ButtonListener btnLsn;
	
	private JComboBox<?> ingestionComboBox;
	
	private JComboBox<?> FormatcomboBox;
	
	private JComboBox<?> privacyComboBox;
	
	private JCheckBox chckbxAddDateAnd;

	public UserSettingsFrame()  {
		setBackground(SystemColor.textHighlightText);
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(290, 0, 78, 105);
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("/YABA2.png")));
		add(lblNewLabel);
		
		JLabel lblStreamSetttings = new JLabel(" Stream Setttings");
		lblStreamSetttings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblStreamSetttings.setBounds(6, 136, 118, 16);
		add(lblStreamSetttings);
		
		JLabel lblBroadcastSettings = new JLabel("Broadcast Settings");
		lblBroadcastSettings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblBroadcastSettings.setBounds(10, 236, 168, 16);
		add(lblBroadcastSettings);
		
		JLabel lblUserSettings = new JLabel("User settings");
		lblUserSettings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblUserSettings.setBounds(10, 46, 125, 16);
		add(lblUserSettings);
		
		String formats[]  = {"1080p","1440p_hfr","1440p","1080p_hfr","720p_hfr","720p","480p","360p","240p"};
		
		String ingstionTypes[]= {"rtmp","dash","hls"};
		
		String privacy[] = {"public","private","unlisted"};
		
		FormatcomboBox = new JComboBox<Object>(formats);
		FormatcomboBox.setBounds(153, 158, 78, 20);
		FormatcomboBox.setSelectedItem(Constants.Format);
		add(FormatcomboBox);
		
		ingestionComboBox = new JComboBox<Object>(ingstionTypes);
		ingestionComboBox.setBounds(153, 183, 78, 20);
		ingestionComboBox.setSelectedItem(Constants.IngestionType);
		add(ingestionComboBox);
		
		privacyComboBox = new JComboBox<Object>(privacy);
		privacyComboBox.setBounds(153, 263, 78, 20);
		privacyComboBox.setSelectedItem(Constants.Privacy);
		add(privacyComboBox);
		
		JLabel lblSelectFormat = new JLabel("Select Format");
		lblSelectFormat.setBounds(10, 161, 89, 14);
		add(lblSelectFormat);
		
		JLabel lblSelectIngestionType = new JLabel("Select ingestion type");
		lblSelectIngestionType.setBounds(10, 186, 125, 14);
		add(lblSelectIngestionType);
		
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(this);
		btnApply.setBounds(279, 317, 89, 23);
		add(btnApply);
		
		JLabel lblSelectPrivacy = new JLabel("Select Privacy ");
		lblSelectPrivacy.setBounds(10, 266, 96, 14);
		add(lblSelectPrivacy);
		
		JLabel lblNotemakeSureYour = new JLabel("NOTE: please make sure your Encoder is set to the same settings");
		lblNotemakeSureYour.setForeground(Color.RED);
		lblNotemakeSureYour.setBounds(10, 211, 382, 14);
		add(lblNotemakeSureYour);
		
		chckbxAddDateAnd = new JCheckBox("Add Date and Time to Broadcast Title");
		chckbxAddDateAnd.setBackground(SystemColor.textHighlightText);
		chckbxAddDateAnd.setSelected(true);
		chckbxAddDateAnd.setBounds(6, 287, 286, 23);
		chckbxAddDateAnd.setSelected(Constants.AddDateTime);
		chckbxAddDateAnd.addItemListener( new ItemListener() {    
            public void itemStateChanged(ItemEvent e) { 
            	switch(e.getStateChange()) {
            	case 1:Constants.AddDateTime=true;	break;
            	case 2:Constants.AddDateTime=false; break;
            	}
            	System.out.println(Constants.AddDateTime);
            }
            
		});
		add(chckbxAddDateAnd);
		setSize(378,354);
		
		JCheckBox chckbxSendEmail = new JCheckBox("Send Email notifications");
		chckbxSendEmail.setBackground(SystemColor.textHighlightText);
		chckbxSendEmail.setBounds(6, 106, 195, 23);
		chckbxSendEmail.setSelected(Constants.SendEmail);
		chckbxSendEmail.addItemListener( new ItemListener() {    
            public void itemStateChanged(ItemEvent e) { 
            	switch(e.getStateChange()) {
            	case 1:Constants.SendEmail=true;	break;
            	case 2:Constants.SendEmail=false; break;
            	}
            }
            
		});
		add(chckbxSendEmail);
		
		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblSettings.setBounds(10, 11, 89, 32);
		add(lblSettings);
		
		JCheckBox chckbxSaveloadUserState = new JCheckBox("Save/Load User state");
		chckbxSaveloadUserState.setBackground(SystemColor.textHighlightText);
		chckbxSaveloadUserState.setBounds(6, 82, 195, 23);
		chckbxSaveloadUserState.setSelected(Constants.saveState);
		chckbxSaveloadUserState.addItemListener( new ItemListener() {    
            public void itemStateChanged(ItemEvent e) { 
            	switch(e.getStateChange()) {
            	case 1:Constants.saveState = true;break;
            	case 2:Constants.saveState = false; break;
            	}
            }
            
		});
		add(chckbxSaveloadUserState);
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
