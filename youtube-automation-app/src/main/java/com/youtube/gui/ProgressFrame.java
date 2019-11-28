package com.youtube.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.json.simple.parser.ParseException;

import com.youtube.controller.BackroundTasks;
import com.youtube.controller.Controller;
import javax.swing.JLabel;


public class ProgressFrame extends JFrame implements PropertyChangeListener {
	
	private JProgressBar progressBar;
	
	private BackroundTasks task;
	
	private static final long serialVersionUID = 1L;

	public ProgressFrame() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, ParseException, InvalidAlgorithmParameterException{
		super("Loading");
		Controller controller = Controller.getInstance();
		JPanel panel = new JPanel();
		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(93, 51, 146, 17);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		panel.add(progressBar);
		
		JLabel lblFetchingDataFrom = new JLabel("Fetching Data from Server...");
		lblFetchingDataFrom.setBounds(93, 26, 165, 14);
		panel.add(lblFetchingDataFrom);
		task = new BackroundTasks(controller);
		task.addPropertyChangeListener(this);
		task.execute();
		setBounds(100, 100, 371,157);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getPropertyName().equals("progress")) {
		    int progress = (Integer) evt.getNewValue();
		    progressBar.setValue(progress);
		}
		if(task.isDone()) {
			setVisible(false);
			dispose();
		}
		
	}

}
