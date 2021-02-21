package com.youtube.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.controller.AppBootLoader;
import com.youtube.utils.Constants;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;

import javax.crypto.NoSuchPaddingException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import java.awt.Toolkit;

public class UserLoginFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -1424474661841282281L;
	private JComboBox<Object> m_usersComboBox;
	public UserLoginFrame(AppBootLoader boot_loader) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException
	{
		super("Log In");
		initFrame();
		initUserNameLbl();
		initLogInBtn();
		initNewUserLbl();
		initWelcomeLbl();
		initYabaIcon();
		initUserCombox();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			switch(event.getActionCommand()) {
				case "Log in": handleLogInPressed(); break;
			
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.HandleError("Unknown", e.toString());
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
	
	private void initUserNameLbl()
	{
		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblUserName.setBounds(28, 76, 74, 14);
		getContentPane().add(lblUserName);
	}
	
	private void initLogInBtn()
	{
		JButton btnLogIn = new JButton("Log in");
		btnLogIn.addActionListener(this);
		btnLogIn.setBounds(219, 73, 112, 23);
		getContentPane().add(btnLogIn);
	}
	
	private void initNewUserLbl()
	{
		JLabel lblNewUser = new JLabel("New user? Please enter desired Username and press Log in");
		lblNewUser.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNewUser.setBounds(28, 116, 378, 23);
		getContentPane().add(lblNewUser);
	}
	
	private void initWelcomeLbl()
	{
		JLabel lblWelcomeToYoutube = new JLabel("Welcome To YouTube Auto-Broadcast App ");
		lblWelcomeToYoutube.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblWelcomeToYoutube.setBounds(28, 11, 314, 35);
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
	
	private void initUserCombox()
	{
		if(Constants.SavedUsers != null) {
			m_usersComboBox = new JComboBox<Object>(Constants.SavedUsers.toArray());
		}
		else {
			m_usersComboBox = new JComboBox<Object>();
		}
		m_usersComboBox.setBounds(100, 73, 112, 23);
		m_usersComboBox.setEditable(true);
		getContentPane().add(m_usersComboBox);
	}
	
	
	private void handleLogInPressed( )throws  IOException
	{
		String username = null;
		username = (String) m_usersComboBox.getSelectedItem();
		if(username == null || username.isEmpty() || username.contains(" ")) {
			JOptionPane.showMessageDialog( null,"Please Enter a username without spaces");
			return;
		}
		Constants.Username = username;
		loadUserDataFromYouTube();
		dispose();
	}

	private void loadUserDataFromYouTube()
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() {
				try {
					new ProgressFrame().getUserDataFromServer();
				} catch (IOException e) {
					e.printStackTrace();
					ErrorHandler.HandleError("Unknown", e.toString());
				}
		}});
		dispose();
	}
}
