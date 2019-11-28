package com.youtube.gui;

import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import org.json.simple.parser.ParseException;

import com.youtube.controller.Controller;
import com.youtube.utils.Constants;

import javax.swing.JLabel;
import java.awt.Font;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

public class UserLogin extends JFrame implements ActionListener {

	private static final long serialVersionUID = -1424474661841282281L;
	private JPasswordField passwordField;
	private JLabel lblPassword;
	private JLabel lblwrongPassword;
	private JLabel lblForgotPassword;
	private JButton btnRegister;
	private JComboBox<String> comboBox;
	public UserLogin() {
		super("Log In");
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
		
		JLabel lblWelcomeToYoutube = new JLabel("Welcome to YouTube Automation App ");
		lblWelcomeToYoutube.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblWelcomeToYoutube.setBounds(66, 11, 233, 14);
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
		    }  
		}); 
		getContentPane().add(lblForgotPassword);
		
		lblwrongPassword = new JLabel("Wrong password/username please try again");
		lblwrongPassword.setForeground(Color.RED);
		lblwrongPassword.setBounds(100, 88, 271, 14);
		lblwrongPassword.setVisible(false);
		getContentPane().add(lblwrongPassword);
//		Constants.SavedUsers
		comboBox = new JComboBox<String>(Constants.SavedUsers);
		comboBox.setBounds(100, 34, 112, 20);
		comboBox.setEditable(true);
		getContentPane().add(comboBox);
		
		
		setMinimumSize(new Dimension(400,200));
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
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
														new ProgressFrame();
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
}
