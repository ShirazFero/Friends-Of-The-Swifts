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
import com.youtube.controller.LoadingTasks;
import com.youtube.controller.StreamTask;
import com.youtube.controller.UpdateTasks;
import com.youtube.utils.Constants;

import javax.swing.JLabel;
import java.awt.SystemColor;


public class ProgressFrame extends JFrame implements PropertyChangeListener {
	
	
	private static final long serialVersionUID = -4984062073217802768L;

	private JProgressBar progressBar;
	
	private BackroundTasks task;
	
	private LoadingTasks loadTask;
	
	private UpdateTasks updateTask;
	
	private StreamTask streamTask;
	
	private JLabel lblFetch;
	

	public ProgressFrame() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, ParseException, InvalidAlgorithmParameterException{
		super("Loading");
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.textHighlightText);
		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(93, 51, 146, 17);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		panel.add(progressBar);
		
		lblFetch = new JLabel("");
		lblFetch.setBounds(93, 26, 165, 14);
		panel.add(lblFetch);
		
		setBounds(100, 100, 371,157);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void initTask() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		Controller controller = Controller.getInstance();
		lblFetch.setText("Fetching Data from Server...");
		task = new BackroundTasks(controller);
		task.addPropertyChangeListener(this);
		task.execute();
	}
	
	public void loadTask() {
		
		lblFetch.setText("Starting Live Broadcasts...");
		loadTask = new LoadingTasks();
		loadTask.addPropertyChangeListener(this);
		loadTask.execute();
	}
	
	public void completeTask() {
		lblFetch.setText("Completing Live Broadcasts...");
		loadTask = new LoadingTasks();
		loadTask.addPropertyChangeListener(this);
		loadTask.execute();
	}
	
	public void updateTask() {
		lblFetch.setText("Updating descriptions...");
		updateTask = new UpdateTasks();
		updateTask.addPropertyChangeListener(this);
		updateTask.execute();
	}
	
	public void StreamTask() {
		if(Constants.AddingStream==null)
			lblFetch.setText("Removing streams...");
		else
			lblFetch.setText("Adding stream...");
		streamTask = new StreamTask();
		streamTask.addPropertyChangeListener(this);
		streamTask.execute();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getPropertyName().equals("progress")) {
		    int progress = (Integer) evt.getNewValue();
		    progressBar.setValue(progress);
		}
		if(task!=null && task.isDone()) {
			setVisible(false);
			dispose();
		}
		if(loadTask!=null && loadTask.isDone()) {
			setVisible(false);
			dispose();
		}
		if(updateTask!=null && updateTask.isDone()) {
			//setVisible(false);
			dispose();
		}
		if(streamTask!=null && streamTask.isDone()) {
			//setVisible(false);
			dispose();
		}
		
	}

}
