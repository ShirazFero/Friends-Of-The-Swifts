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
		setSize(378,354);
		initIcon();
		
		initStreamSettingLbl();
		initBroadcastSettingLbl(); 
		initUserSettingLbl();
		initIngestionLbl();
		initPrivacyLbl();
		initWarningLbl();
		initFormatLbl();
		initSettingsLbl();
		
		initFormatComboBox();
		initIngestionComboBox();
		initPrivacyComboBox();
		
		initApplyBtn();
		initDateCheckBox();
		initUserStateCheckBox();
	}

	public JComboBox<?> getIngestionComboBox() {
		return ingestionComboBox;
	}

	public JComboBox<?> getFormatcomboBox() {
		return FormatcomboBox;
	}

	public JComboBox<?> getPrivacyComboBox() {
		return privacyComboBox;
	}

	public void setBtnListener(ButtonListener btnLsn) {
		this.btnLsn = btnLsn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		btnLsn.ButtonPressed(e.getActionCommand());
	}
	
	private void initIcon() 
	{
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(290, 0, 78, 105);
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("/YABA2.png")));
		add(lblNewLabel);
	}
	
	private void initStreamSettingLbl() 
	{
		JLabel lblStreamSetttings = new JLabel(" Stream Setttings");
		lblStreamSetttings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblStreamSetttings.setBounds(6, 136, 118, 16);
		add(lblStreamSetttings);
	}
	
	private void initBroadcastSettingLbl() 
	{
		JLabel lblBroadcastSettings = new JLabel("Broadcast Settings");
		lblBroadcastSettings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblBroadcastSettings.setBounds(10, 236, 168, 16);
		add(lblBroadcastSettings);
	}
	
	private void initUserSettingLbl() 
	{
		JLabel lblUserSettings = new JLabel("User settings");
		lblUserSettings.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblUserSettings.setBounds(10, 46, 125, 16);
		add(lblUserSettings);
	}
	
	private void initFormatComboBox()
	{
		String formats[]  = {"1080p","1440p_hfr","1440p","1080p_hfr","720p_hfr","720p","480p","360p","240p"};
		FormatcomboBox = new JComboBox<Object>(formats);
		FormatcomboBox.setBounds(153, 158, 78, 20);
		FormatcomboBox.setSelectedItem(Constants.Format);
		add(FormatcomboBox);
	}
	
	private void initIngestionComboBox()
	{
		String ingstionTypes[]= {"rtmp","dash","hls"};
		ingestionComboBox = new JComboBox<Object>(ingstionTypes);
		ingestionComboBox.setBounds(153, 183, 78, 20);
		ingestionComboBox.setSelectedItem(Constants.IngestionType);
		add(ingestionComboBox);
	}
	
	private void initPrivacyComboBox()
	{
		String privacy[] = {"public","private","unlisted"};
		privacyComboBox = new JComboBox<Object>(privacy);
		privacyComboBox.setBounds(153, 263, 78, 20);
		privacyComboBox.setSelectedItem(Constants.Privacy);
		add(privacyComboBox);
	}
	
	private void initFormatLbl()
	{
		JLabel lblSelectFormat = new JLabel("Select Format");
		lblSelectFormat.setBounds(10, 161, 89, 14);
		add(lblSelectFormat);
	}
	
	private void initIngestionLbl()
	{
		JLabel lblSelectIngestionType = new JLabel("Select ingestion type");
		lblSelectIngestionType.setBounds(10, 186, 125, 14);
		add(lblSelectIngestionType);
	}
	
	private void initApplyBtn()
	{
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(this);
		btnApply.setBounds(279, 317, 89, 23);
		add(btnApply);
	}
	
	private void initPrivacyLbl()
	{
		JLabel lblSelectPrivacy = new JLabel("Select Privacy ");
		lblSelectPrivacy.setBounds(10, 266, 96, 14);
		add(lblSelectPrivacy);
	}
	
	private void initWarningLbl()
	{
		JLabel lblNotemakeSureYour = new JLabel("NOTE: please make sure your Encoder is set to the same settings");
		lblNotemakeSureYour.setForeground(Color.RED);
		lblNotemakeSureYour.setBounds(10, 211, 382, 14);
		add(lblNotemakeSureYour);
	}
	
	private void initDateCheckBox()
	{
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
	}
	
	private void initSettingsLbl()
	{
		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblSettings.setBounds(10, 11, 89, 32);
		add(lblSettings);
	}
	
	private void initUserStateCheckBox()
	{
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
	

}
