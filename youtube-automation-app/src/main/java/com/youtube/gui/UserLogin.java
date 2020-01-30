package com.youtube.gui;

import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.youtube.controller.Controller;
import com.youtube.controller.FileEncrypterDecrypter;
import com.youtube.controller.MailUtil;
import com.youtube.utils.Constants;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;

import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
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
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.SystemColor;

public class UserLogin extends JFrame implements ActionListener {

	private static final long serialVersionUID = -1424474661841282281L;
	private JPasswordField passwordField;
	private JLabel lblPassword;
	private JLabel lblwrongPassword;
	private JLabel lblForgotPassword;
	private JButton btnRegister;
	private JComboBox<?> comboBox;
	private JCheckBox chckbxRememberPassword;
	private static UserLogin instantce;
	public UserLogin() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		super("Log In");
		instantce = this;
		setForeground(SystemColor.inactiveCaption);
		setBackground(SystemColor.inactiveCaption);
		getContentPane().setBackground(SystemColor.textHighlightText);
		getContentPane().setLayout(null);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(100, 61, 112, 20);
		getContentPane().add(passwordField);
		
		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblUserName.setBounds(28, 36, 74, 14);
		getContentPane().add(lblUserName);
		
		lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPassword.setBounds(28, 63, 65, 14);
		getContentPane().add(lblPassword);
		
		JButton btnLogIn = new JButton("Log in");
		btnLogIn.addActionListener(this);
		btnLogIn.setBounds(219, 60, 112, 23);
		getContentPane().add(btnLogIn);
		
		btnRegister = new JButton("Register");
		btnRegister.setBounds(100, 113, 89, 23);
		getContentPane().add(btnRegister);
		btnRegister.addActionListener(this);
		
		JLabel lblNewUser = new JLabel("New User?");
		lblNewUser.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewUser.setBounds(28, 116, 74, 14);
		getContentPane().add(lblNewUser);
		
		JLabel lblWelcomeToYoutube = new JLabel("Welcome to YouTube Auto-Broadcast App ");
		lblWelcomeToYoutube.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblWelcomeToYoutube.setBounds(66, 11, 277, 14);
		getContentPane().add(lblWelcomeToYoutube);
		
		lblForgotPassword = new JLabel("Forgot my password");
		lblForgotPassword.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblForgotPassword.setForeground(Color.BLUE);
		lblForgotPassword.setBounds(237, 37, 106, 14);
		lblForgotPassword.addMouseListener(new MouseAdapter()  
		{  
		    public void mouseClicked(MouseEvent e)  
		    {  
		       
		    	//open resend password frame
		    	String user = JOptionPane.showInputDialog(null,"Password Recovery","please enter username");
		    	if(user == null || (user != null && ("".equals(user)))) {
		    		JOptionPane.showMessageDialog(null,
							" No username enterd ",
			                "Not Completed",
			                JOptionPane.ERROR_MESSAGE);
		    		return;
				}
		    	try {
					Controller controller = Controller.getInstance();
					if(controller.userExists(user)) {
						
						MailUtil.sendMail(getUserPasswordEmail(user,false),
								"Password Recovery",
								"your password is: " +getUserPasswordEmail(user,true));
						JOptionPane.showMessageDialog(null,
								"Password was sent to user's Email",
				                "Paswword Sent",
				                JOptionPane.PLAIN_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(null,
								" wrong username ",
				                "Not Completed",
				                JOptionPane.ERROR_MESSAGE);
					}
						
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException | MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	
		    }  
		}); 
		getContentPane().add(lblForgotPassword);
		
		lblwrongPassword = new JLabel("Wrong password/username please try again");
		lblwrongPassword.setForeground(Color.RED);
		lblwrongPassword.setBounds(60, 139, 271, 14);
		lblwrongPassword.setVisible(false);
		getContentPane().add(lblwrongPassword);

		if(Constants.SavedUsers!=null)
			comboBox = new JComboBox<Object>(Constants.SavedUsers);
		else
			comboBox = new JComboBox<Object>();
		comboBox.setBounds(100, 34, 112, 20);
		comboBox.setEditable(true);
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				try {
					chckbxRememberPassword.setSelected(getRememberPass());
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		getContentPane().add(comboBox);
		
		chckbxRememberPassword = new JCheckBox("remember password");
		chckbxRememberPassword.setBackground(SystemColor.textHighlightText);
		chckbxRememberPassword.setBounds(24, 83, 158, 23);
		chckbxRememberPassword.setSelected(getRememberPass());
		if(chckbxRememberPassword.isSelected()) {
			 Controller cont = Controller.getInstance();
        	 String user=(String)comboBox.getSelectedItem();
        	 if(cont.userExists(user)) {
        		 passwordField.setText(getUserPasswordEmail(user,true));
        	 }
		}
		
		chckbxRememberPassword.addItemListener(new ItemListener() {    
             public void itemStateChanged(ItemEvent e) {    
            	 try {
            		 Controller cont = Controller.getInstance();
	            	 String user=(String)comboBox.getSelectedItem();
	            	 if(cont.userExists(user)) {
		            	 if(e.getStateChange()==1) { 
	                		 passwordField.setText(getUserPasswordEmail(user,true));
	                		 if(Constants.SavedUsers!=null)
	                			 setRememberPassword(user,Boolean.parseBoolean("true"));
		            	 }
		                 else {
			                		passwordField.setText("");
									setRememberPassword(user,Boolean.parseBoolean("false"));
		                }
            	 	}    	
            	 } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
            	 }
        	 }
         });   
        
		getContentPane().add(chckbxRememberPassword);
		
		
		setMinimumSize(new Dimension(400,200));
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * @return the instantce
	 */
	public static UserLogin getInstantce() {
		return instantce;
	}

	private boolean getRememberPass() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		// TODO Auto-generated method stub
		Controller cont = Controller.getInstance();
   	 	String username= (String) comboBox.getSelectedItem();
   	 	if(cont.userExists(username)) {
			FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
			String data = fileEncDec.decrypt(Constants.AppUserPath);
			JSONParser parser = new JSONParser();
			JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> Iterator = userArray.iterator();
			for(int i=0 ;i<Constants.SavedUsers.length;i++) {
				JSONObject user = (JSONObject) Iterator.next().get("User");
				if(username.equals((String) user.get("username")))
					return Boolean.parseBoolean((String) user.get("rememberpass"));
			}
   	 	}
		return false;
	}

	public String getUserPasswordEmail(String username , boolean flag) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		
		String passOrmail = flag ? "password":   "email";
		
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		String data = fileEncDec.decrypt(Constants.AppUserPath);
		JSONParser parser = new JSONParser();
		JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> Iterator = userArray.iterator();
		for(int i=0 ;i<Constants.SavedUsers.length;i++) {
			JSONObject user = (JSONObject) Iterator.next().get("User");
			if(username.equals((String) user.get("username")))
				return (String) user.get(passOrmail);
		}
		return null;
	}
	
	/**
	 * @return the comboBox
	 */
	public JComboBox<?> getComboBox() {
		return comboBox;
	}

	@SuppressWarnings("unchecked")
	public void setRememberPassword(String username ,Boolean rememberPass) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException { 
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		String data = fileEncDec.decrypt(Constants.AppUserPath);
		JSONParser parser = new JSONParser();
		JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
		Iterator<JSONObject> Iterator = userArray.iterator();
		for(int i=0 ;i<Constants.SavedUsers.length;i++) {
			JSONObject user = (JSONObject) Iterator.next().get("User");
			if(username.equals((String) user.get("username")))
				user.put("rememberpass",rememberPass.toString());
		}
		JSONObject jsonObject =new JSONObject();
		jsonObject.put("User List", userArray);
		FileWriter file = new FileWriter(System.getProperty("user.home")+"\\Documents\\AppUsers.json");
		file.write(jsonObject.toJSONString());
		System.out.println("Successfully updated remember password New User And Saved JSON Object to File...");
		//System.out.println("\nJSON Object: " + jsonObject);
		file.close();
		fileEncDec.encrypt(jsonObject.toString(), Constants.AppUserPath);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		try {
			switch(event.getActionCommand()) {
			case "Log in": String username = (String) comboBox.getSelectedItem();
						   String password = String.valueOf(passwordField.getPassword());
						   Controller controller = Controller.getInstance();
							 // validate user&password
								if(controller.validateUser(username,password)){											
										Constants.Username = username;
										System.out.println("selected "+ Constants.Username);
										SwingUtilities.invokeLater(new Runnable() {
												public void run() {
													try {
														new ProgressFrame().initTask();
													} catch (InvalidKeyException | NoSuchAlgorithmException
															| NoSuchPaddingException | IOException 
															| ParseException | InvalidAlgorithmParameterException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}
											});
										dispose();
								}
								else {
										System.out.println("wrong username/password entered please try again");
										lblwrongPassword.setVisible(true);
								}
							
							break;
			
			case "Register": System.out.println("register");
							// open registration page
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									new RegistrationForm();
								}
							});
							break;
			}
			
		} catch (IOException | ParseException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException  | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshUsers() {
		if(Constants.SavedUsers!=null)
			comboBox = new JComboBox<Object>(Constants.SavedUsers);
	}
}
