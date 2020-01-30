package com.youtube.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;

import org.json.simple.parser.ParseException;

import com.youtube.controller.Controller;

import javax.swing.JPasswordField;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextPane;
import java.awt.SystemColor;

public class RegistrationForm extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 8456223525517951378L;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField passwordConfirmField;
	private JTextField emailField;
	private JTextField emailConfirmField;
	private JLabel lblBadInputMsg;
	
	public RegistrationForm() {
		getContentPane().setBackground(SystemColor.textHighlightText);
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 13));
		getContentPane().setLayout(null);
		
		JLabel lblEnterUserName = new JLabel("*User Name");
		lblEnterUserName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblEnterUserName.setBounds(37, 67, 100, 14);
		getContentPane().add(lblEnterUserName);
		
		JLabel lblPassword = new JLabel("*Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPassword.setBounds(37, 92, 81, 14);
		getContentPane().add(lblPassword);
		
		JLabel lblConfirmPassword = new JLabel("*Confirm Password");
		lblConfirmPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblConfirmPassword.setBounds(37, 117, 118, 14);
		getContentPane().add(lblConfirmPassword);
		
		JLabel lblNewLabel = new JLabel("Register New User");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(178, 11, 135, 19);
		getContentPane().add(lblNewLabel);
		
		usernameField = new JTextField();
		usernameField.setBounds(161, 65, 174, 20);
		getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(161, 90, 174, 20);
		getContentPane().add(passwordField);
		
		passwordConfirmField = new JPasswordField();
		passwordConfirmField.setBounds(161, 115, 174, 20);
		getContentPane().add(passwordConfirmField);
		
		JLabel lblNewLabel_1 = new JLabel("*Email");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_1.setBounds(37, 142, 46, 14);
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("*Confirm Email");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_2.setBounds(37, 167, 100, 14);
		getContentPane().add(lblNewLabel_2);
		
		emailField = new JTextField();
		emailField.setBounds(161, 140, 174, 20);
		getContentPane().add(emailField);
		emailField.setColumns(10);
		
		emailConfirmField = new JTextField();
		emailConfirmField.setBounds(161, 165, 174, 20);
		getContentPane().add(emailConfirmField);
		emailConfirmField.setColumns(10);
		
		JButton btnRegister = new JButton("Submit");
		btnRegister.setBounds(125, 217, 89, 23);
		getContentPane().add(btnRegister);
		btnRegister.addActionListener(this);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(224, 217, 89, 23);
		getContentPane().add(btnCancel);
		btnCancel.addActionListener(this);
		
		lblBadInputMsg = new JLabel("Bad Input Massgage");
		lblBadInputMsg.setForeground(Color.RED);
		lblBadInputMsg.setBounds(47, 192, 366, 14);
		getContentPane().add(lblBadInputMsg);
		
		JTextPane txtpnpasswordMustHave = new JTextPane();
		txtpnpasswordMustHave.setEditable(false);
		txtpnpasswordMustHave.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnpasswordMustHave.setText("Password  Rules: \r\n- at least 8 characters long\r\n- one digit 1-9\r\n- one lower case letter\r\n- one Uupper case letter\r\n- one  special character @,#,$,%,^,&,+,= \r\n- no spaces allowed\r\n");
		txtpnpasswordMustHave.setBounds(356, 65, 178, 141);
		getContentPane().add(txtpnpasswordMustHave);
		lblBadInputMsg.setVisible(false);

		setMinimumSize(new Dimension(550,300));
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		Controller controller;
		try {
			controller = Controller.getInstance();
			
			switch(event.getActionCommand()) {
			
			case "Submit": 
				//controller.checkRegisterForm(usernameField.getText(),);
				if(controller.userExists(usernameField.getText())) {
					lblBadInputMsg.setText("User Exists already");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(usernameField.getText()==null || "".equals(usernameField.getText())) {
					lblBadInputMsg.setText("No user name entered");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(usernameField.getText().length()>20) {
					lblBadInputMsg.setText("User name is too long please use less then 20 letters");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(passwordField.getPassword()==null || passwordField.getPassword().length==0) {
					lblBadInputMsg.setText("No password entered");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(passwordField.getPassword().length>20) {
					lblBadInputMsg.setText("Password is too long please use less then 20 letters");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(!Arrays.equals(passwordField.getPassword(),passwordConfirmField.getPassword())) {
					lblBadInputMsg.setText("Password doesn't match cofirmation Password");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(emailField.getText()==null || "".equals(emailField.getText())) {
					lblBadInputMsg.setText("NO user email entered");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(emailField.getText().length()>30) {
					lblBadInputMsg.setText("Email is too long please use less then 20 letters");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(!emailField.getText().equals(emailConfirmField.getText())) {
					lblBadInputMsg.setText("Email doesn't match cofirmation Email");
					lblBadInputMsg.setVisible(true);
					break;
				}
				String inputedPassword = String.valueOf(passwordField.getPassword());
				if(!validatePassword(inputedPassword)) {
					lblBadInputMsg.setText("Password Not Valid");
					lblBadInputMsg.setVisible(true);
					break;
				}
				if(!validateEmail(emailField.getText())) {
					lblBadInputMsg.setText("Email Not Valid");
					lblBadInputMsg.setVisible(true);
					break;
				}
				controller.registerUser(usernameField.getText(),inputedPassword,emailField.getText());
				dispose();	
				break;
			
			case "Cancel": System.out.println("Cancel");
				dispose();	
				break;
			 
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
				| ParseException | InvalidAlgorithmParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static final Pattern VALID_PASSWORD_REGEX = 
			Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}");
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public boolean validatePassword(String password) {
	      Matcher matcher = VALID_PASSWORD_REGEX.matcher(password);
	      return matcher.find();
	}
	
	public static boolean validateEmail(String email) {
	        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
	        return matcher.find();
	}
}
