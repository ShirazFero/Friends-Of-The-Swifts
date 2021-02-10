package com.youtube.gui;

import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.controller.AppBootLoader;
import com.youtube.controller.MailUtil;
import com.youtube.utils.Constants;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;

import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.SystemColor;
import java.awt.Toolkit;

public class UserLoginFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -1424474661841282281L;
	final private AppBootLoader m_loader;
	private JPasswordField passwordField;
	private JLabel lblPassword;
	private JLabel lblwrongPassword;
	private JLabel lblForgotPassword;
	private JButton btnRegister;
	private JComboBox<Object> usersComboBox;
	private JCheckBox chckbxRememberPassword;

	public UserLoginFrame(final AppBootLoader a_loader) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException
	{
		super("Log In");
		m_loader = a_loader;
		
		initFrame();
		initPasswordField();
		initUserNameLbl();
		initPasswordLbl();
		initLogInBtn();
		initRegisterBtn();
		initNewUserLbl();
		initWelcomeLbl();
		initYabaIcon();
		initForgotPassLbl();
		initUserCombox();
		initRememberPassCheckBox();
		
		setPasswordRecoveryMouseListener(); 
		setUserComboxItemListener();
		setRememberPassCheckBoxItemListener();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			switch(event.getActionCommand()) {
			case "Log in": handleLogInPressed(); break;
			
			case "Register":handleRgisterPressed();
							dispose();	
							break;
			}
			
		} catch (IOException | ParseException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException  | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			ErrorHandler.HandleLoadError(e.toString());
		}
	}
	
	private void initFrame()
	{
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/YABAWB.png")));
		setForeground(SystemColor.inactiveCaption);
		setBackground(SystemColor.inactiveCaption);
		getContentPane().setBackground(new Color(255, 255, 255));
		getContentPane().setLayout(null);
		setMinimumSize(new Dimension(450, 200));
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void initPasswordField()
	{
		passwordField = new JPasswordField();
		passwordField.setBounds(100, 61, 112, 20);
		getContentPane().add(passwordField);
	}
	
	private void initUserNameLbl()
	{
		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblUserName.setBounds(28, 36, 74, 14);
		getContentPane().add(lblUserName);
	}
	
	private void initPasswordLbl()
	{
		lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPassword.setBounds(28, 63, 65, 14);
		getContentPane().add(lblPassword);
	}
	
	private void initLogInBtn()
	{
		JButton btnLogIn = new JButton("Log in");
		btnLogIn.addActionListener(this);
		btnLogIn.setBounds(219, 60, 112, 23);
		getContentPane().add(btnLogIn);
	}
	
	private void initRegisterBtn()
	{
		btnRegister = new JButton("Register");
		btnRegister.setBounds(100, 113, 89, 23);
		getContentPane().add(btnRegister);
		btnRegister.addActionListener(this);
	}
	
	private void initNewUserLbl()
	{
		JLabel lblNewUser = new JLabel("New User?");
		lblNewUser.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewUser.setBounds(28, 116, 74, 14);
		getContentPane().add(lblNewUser);
	}
	
	private void initWelcomeLbl()
	{
		JLabel lblWelcomeToYoutube = new JLabel("Welcome To YouTube Auto-Broadcast App ");
		lblWelcomeToYoutube.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblWelcomeToYoutube.setBounds(76, 11, 314, 14);
		lblWelcomeToYoutube.setForeground(Color.decode("#CF1717"));
		getContentPane().add(lblWelcomeToYoutube);
	}
	
	private void initYabaIcon()
	{
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(341, 11, 78, 105);
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("/YABA2.png")));
		getContentPane().add(lblNewLabel);
	}
	
	private void initForgotPassLbl()
	{
		lblForgotPassword = new JLabel("Forgot my password");
		lblForgotPassword.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblForgotPassword.setForeground(Color.BLUE);
		lblForgotPassword.setBounds(237, 37, 106, 14);
		getContentPane().add(lblForgotPassword);
	}
	
	private void setPasswordRecoveryMouseListener() 
	{
		lblForgotPassword.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		    	try {
			    	String user = JOptionPane.showInputDialog(null,"Password Recovery","please enter username");
			    	if(user == null || user.isEmpty()) {
			    		JOptionPane.showMessageDialog(null,"No username enterd ","Not Completed", JOptionPane.ERROR_MESSAGE);
			    		return;
					}
					if(m_loader.userExistsInFile(user)) {
						MailUtil.sendMail(getUserDetails(user,"email"),"Password Recovery", "your password is: " + getUserDetails(user,"password"));
						JOptionPane.showMessageDialog(null, "Password was sent to user's Email","Paswword Sent",JOptionPane.PLAIN_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(null," Wrong Username ", "Not Completed", JOptionPane.ERROR_MESSAGE);
					}
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException | MessagingException e1) {
					e1.printStackTrace();
					ErrorHandler.HandleUnknownError(e1.toString());
				}
		    }  
		}); 
	}
	
	private void initUserCombox()
	{
		if(Constants.SavedUsers != null) {
			usersComboBox = new JComboBox<Object>(Constants.SavedUsers.toArray());
		}
		else {
			usersComboBox = new JComboBox<Object>();
		}
		usersComboBox.setBounds(100, 34, 112, 20);
		usersComboBox.setEditable(true);
		getContentPane().add(usersComboBox);
	}
	
	private void setUserComboxItemListener()
	{
		usersComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					chckbxRememberPassword.setSelected(getRememberPass());
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e1) {
					e1.printStackTrace();
					ErrorHandler.HandleUnknownError(e1.toString());
				}
			}
		});
	}
	
 	private void initRememberPassCheckBox() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException
	{
		chckbxRememberPassword = new JCheckBox("remember password");
		chckbxRememberPassword.setBackground(SystemColor.textHighlightText);
		chckbxRememberPassword.setBounds(24, 83, 158, 23);
		chckbxRememberPassword.setSelected(getRememberPass());
		if(chckbxRememberPassword.isSelected()) {
        	 String user=(String)usersComboBox.getSelectedItem();
        	 if(m_loader.userExistsInFile(user)) {
        		 passwordField.setText(getUserDetails(user,"password"));
        	 }
		}
		getContentPane().add(chckbxRememberPassword);
	}
	
	private void setRememberPassCheckBoxItemListener()
	{
		chckbxRememberPassword.addItemListener(new ItemListener() {    
        public void itemStateChanged(ItemEvent e) {    
       	 	try {
            	 String user = (String) usersComboBox.getSelectedItem();
            	 if(m_loader.userExistsInFile(user)) {
	            	 if(e.getStateChange()==1) {
                		 passwordField.setText(getUserDetails(user,"password"));
            			 setRememberPassword(user,Boolean.parseBoolean("true"));
	            	 }
	                 else {
	            		passwordField.setText("");
						setRememberPassword(user,Boolean.parseBoolean("false"));
	                 }
 				 }    	
       	 	} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e2) {
					ErrorHandler.HandleUnknownError(e2.toString());
           	 	}
       	 	}
        });  
	}
	
	@SuppressWarnings("unchecked")
	private boolean getRememberPass() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException 
	{
   	 	String username = (String) usersComboBox.getSelectedItem();
   	 	if(m_loader.userExistsInFile(username)) {
			JSONArray userArray = m_loader.getUsers();
			Iterator<JSONObject> Iterator = userArray.iterator();
			for(int i=0 ; i  < Constants.SavedUsers.size(); ++i) {
				JSONObject user = (JSONObject) Iterator.next().get("User");
				if(username.equals((String) user.get("username"))) {
					return Boolean.parseBoolean((String) user.get("rememberpass"));
				}
			}
   	 	}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private String getUserDetails(String username , String detail) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException 
	{
		JSONArray userArray = m_loader.getUsers();
		Iterator<JSONObject> Iterator = userArray.iterator();
		for(int i=0 ;i<Constants.SavedUsers.size() ; ++i) {
			JSONObject user = (JSONObject) Iterator.next().get("User");
			if(username.equals((String) user.get("username"))) {
				return (String) user.get(detail);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void setRememberPassword(String username ,Boolean rememberPass) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException 
	{ 
		JSONArray userArray = m_loader.getUsers();
		Iterator<JSONObject> Iterator = userArray.iterator();
		for(int i=0 ;i<Constants.SavedUsers.size() ; ++i) {
			JSONObject user = (JSONObject) Iterator.next().get("User");
			if(username.equals((String) user.get("username"))) {
				user.put("rememberpass",rememberPass.toString());
			}
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("User List", userArray);
		m_loader.fileEncrypt(jsonObject.toString());
	}
	
	private void handleLogInPressed() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, ParseException
	{
//		Constants.Username = (String) usersComboBox.getSelectedItem();
//		loadUserDataFromYouTube();
		String username =  (String) usersComboBox.getSelectedItem();
		String password = String.valueOf(passwordField.getPassword());
		if(m_loader.isValidUser(username,password)){											
				if(Constants.DEBUG) {
					System.out.println("selected "+ Constants.Username);
				}
				loadUserDataFromYouTube();
				dispose();
		}
		else {	
			if(Constants.DEBUG) {
				System.out.println("wrong username/password entered please try again");
			}
			lblwrongPassword.setVisible(true);
		}
	}

	private void loadUserDataFromYouTube()
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() {
				try {
					new ProgressFrame().getUserDataFromServer();
				} catch (InvalidKeyException | NoSuchAlgorithmException
						| NoSuchPaddingException | IOException 
						| ParseException | InvalidAlgorithmParameterException e) {
					
					e.printStackTrace();
					ErrorHandler.HandleLoadError(e.toString());
				}
		}});
		dispose();
	}
	
	private void handleRgisterPressed()
	{
		if(Constants.DEBUG) {
			System.out.println("register");
		}
		SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			new RegistrationForm(m_loader);
		}
		});
		dispose();
	}
	
}
