package com.youtube.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;

import org.json.simple.parser.ParseException;

import com.youtube.controller.AppBootLoader;
import com.youtube.controller.AppMain;
import com.youtube.utils.Constants;

import javax.swing.JPasswordField;
import javax.crypto.NoSuchPaddingException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.Toolkit;

public class RegistrationForm extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 8456223525517951378L;
	private final AppBootLoader m_loader;
	private final Pattern m_validPasswordPattern = Pattern.compile(Constants.PasswordPattern);
	private final Pattern m_validEmailPattern = Pattern.compile(Constants.EmailPattern, Pattern.CASE_INSENSITIVE);
	private JTextField m_usernameField;
	private JPasswordField m_passwordField;
	private JPasswordField m_passwordConfirmField;
	private JTextField m_emailField;
	private JTextField m_emailConfirmField;
	private JLabel m_lblBadInputMsg;
	
	public RegistrationForm(final AppBootLoader a_loader) 
	{
		m_loader = a_loader;
		initForm();
		initLabels();
		initFields();
		initRegisterBtn();
		initCancelBtn();
		initTextPane();
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			
			switch(event.getActionCommand()) {
			
			case "Submit": handleSubmitPressed();
				break;
			
			case "Cancel": 
							dispose();	
							AppMain.main(null);
							break;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void initForm()
	{
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/YABAWB.png")));
		getContentPane().setBackground(SystemColor.textHighlightText);
		getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 13));
		getContentPane().setLayout(null);
		setMinimumSize(new Dimension(550,300));
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	private void initEnterUserLbl() 
	{
		JLabel lblEnterUserName = new JLabel("*User Name");
		lblEnterUserName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblEnterUserName.setBounds(37, 67, 100, 14);
		getContentPane().add(lblEnterUserName);
	}
	
	private void initPasswordLbl()
	{
		JLabel lblPassword = new JLabel("*Password");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPassword.setBounds(37, 92, 81, 14);
		getContentPane().add(lblPassword);
	}
	
	private void initConfirmPasswordLbl()
	{
		JLabel lblConfirmPassword = new JLabel("*Confirm Password");
		lblConfirmPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblConfirmPassword.setBounds(37, 117, 118, 14);
		getContentPane().add(lblConfirmPassword);
	}
	
	private void initRegNewUserLbl()
	{
		JLabel lblNewLabel = new JLabel("Register New User");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(178, 11, 135, 19);
		getContentPane().add(lblNewLabel);
	}
	
	private void initIconLbl()
	{
		JLabel lblNewLabel1 = new JLabel("");
		lblNewLabel1.setBounds(367, -11, 78, 105);
		lblNewLabel1.setIcon(new ImageIcon(getClass().getResource("/YABA2.png")));
		getContentPane().add(lblNewLabel1);
	}
	
	private void initUsernameFld()
	{
		m_usernameField = new JTextField();
		m_usernameField.setBounds(161, 65, 174, 20);
		m_usernameField.setColumns(10);
		getContentPane().add(m_usernameField);
	}
	
	private void initPasswordFld()
	{
		m_passwordField = new JPasswordField();
		m_passwordField.setBounds(161, 90, 174, 20);
		getContentPane().add(m_passwordField);
	}
	
	private void initConfirmPasswordFld()
	{
		m_passwordConfirmField = new JPasswordField();
		m_passwordConfirmField.setBounds(161, 115, 174, 20);
		getContentPane().add(m_passwordConfirmField);
	}
	
	private void initEmailLbl()
	{
		JLabel lblNewLabel_1 = new JLabel("*Email");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_1.setBounds(37, 142, 46, 14);
		getContentPane().add(lblNewLabel_1);
	}
	
	private void initConfirmEmailLbl()
	{
		JLabel lblNewLabel_2 = new JLabel("*Confirm Email");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewLabel_2.setBounds(37, 167, 100, 14);
		getContentPane().add(lblNewLabel_2);
	}
	
	private void initEmailFld()
	{
		m_emailField = new JTextField();
		m_emailField.setBounds(161, 140, 174, 20);
		m_emailField.setColumns(10);
		getContentPane().add(m_emailField);
	}
	
	private void initConfirmEmailFld()
	{
		m_emailConfirmField = new JTextField();
		m_emailConfirmField.setBounds(161, 165, 174, 20);
		m_emailConfirmField.setColumns(10);
		getContentPane().add(m_emailConfirmField);
	}
	
	private void initRegisterBtn()
	{
		JButton btnRegister = new JButton("Submit");
		btnRegister.setBounds(125, 217, 89, 23);
		getContentPane().add(btnRegister);
		btnRegister.addActionListener(this);
	}
	
	private void initCancelBtn()
	{
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(224, 217, 89, 23);
		getContentPane().add(btnCancel);
		btnCancel.addActionListener(this);
	}
	
	private void initBadInputLbl()
	{
		m_lblBadInputMsg = new JLabel("Bad Input Massgage");
		m_lblBadInputMsg.setForeground(Color.RED);
		m_lblBadInputMsg.setBounds(47, 192, 366, 14);
		m_lblBadInputMsg.setVisible(false);
		getContentPane().add(m_lblBadInputMsg);
	}
	
	private void initTextPane() 
	{
		JTextPane txtpnpasswordMustHave = new JTextPane();
		txtpnpasswordMustHave.setEditable(false);
		txtpnpasswordMustHave.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnpasswordMustHave.setText("Password  Rules: \r\n- at least 8 characters long\r\n- one digit 1-9\r\n- one lower case letter\r\n- one Uupper case letter\r\n- one  special character @,#,$,%,^,&,+,= \r\n- no spaces allowed\r\n");
		txtpnpasswordMustHave.setBounds(345, 83, 178, 141);
		getContentPane().add(txtpnpasswordMustHave);
	}
	
	private void initLabels() 
	{
		initEnterUserLbl(); 
		initPasswordLbl();
		initConfirmPasswordLbl();
		initRegNewUserLbl();
		initEmailLbl();
		initIconLbl();
		initConfirmEmailLbl();
		initBadInputLbl();
	}
	
	private void initFields()
	{
		initUsernameFld();
		initPasswordFld();
		initConfirmPasswordFld();
		initEmailFld();
		initConfirmEmailFld();
	}

	private void showBadInputMessage(String msg)
	{
		m_lblBadInputMsg.setText(msg);
		m_lblBadInputMsg.setVisible(true);
	}
	
	private boolean detailsAreValid() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, ParseException
	{
		if(m_loader.userExistsInFile(m_usernameField.getText())) {
			showBadInputMessage("User Exists already");
			return false;
		}
		if(m_usernameField.getText() == null || m_usernameField.getText().isEmpty() ) {
			showBadInputMessage("No user name entered");
			return false;
		}
		if(m_usernameField.getText().length() > 20) {
			showBadInputMessage("User name is too long please use less then 20 letters");
			return false;
		}
		if(m_passwordField.getPassword() == null || m_passwordField.getPassword().length == 0) {
			showBadInputMessage("No password entered");
			return false;
		}
		if(m_passwordField.getPassword().length >= 20) {
			showBadInputMessage("Password is too long please use less then 20 characters");
			return false;
		}
		if(!Arrays.equals(m_passwordField.getPassword(),m_passwordConfirmField.getPassword())) {
			showBadInputMessage("Password doesn't match confirmation Password");
			return false;
		}
		if(m_emailField.getText()==null || "".equals(m_emailField.getText())) {
			showBadInputMessage("No email entered");
			return false;
		}
		if(m_emailField.getText().length()>30) {
			showBadInputMessage("Email is too long please use less then 20 letters");
			return false;
		}
		if(!m_emailField.getText().equals(m_emailConfirmField.getText())) {
			showBadInputMessage("Email doesn't match cofirmation Email");
			return false;
		}
		String inputedPassword = String.valueOf(m_passwordField.getPassword());
		if(!validatePasswordExpression(inputedPassword)) {
			showBadInputMessage("Password Not Valid");
			return false;
		}
		if(!validateEmailExpression(m_emailField.getText())) {
			showBadInputMessage("Email Not Valid");
			return false;
		}
		return true;
	}
	
	private void handleSubmitPressed() 
	{
		try {
			if(detailsAreValid()){
				String inputedPassword = String.valueOf(m_passwordField.getPassword());
				m_loader.registerUser(m_usernameField.getText(),inputedPassword,m_emailField.getText());
				JOptionPane.showMessageDialog(null,"User Registerd Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);
				dispose();	
				AppMain.main(null);
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	private boolean validatePasswordExpression(String password) {
	      Matcher matcher = m_validPasswordPattern.matcher(password);
	      return matcher.find();
	}
	
	private boolean validateEmailExpression(String email) {
	        Matcher matcher = m_validEmailPattern .matcher(email);
	        return matcher.find();
	}
}
