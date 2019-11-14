package com.youtube.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.youtube.controller.LoadingTasks;

public class LoadingFrame extends JFrame implements PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;

	private JProgressBar progressBar;
	
	private LoadingTasks loadTask;
	
	public LoadingFrame() {
		
		super("Loading");
		System.out.println("loadnig frame c'tor");
		JPanel panel = new JPanel();
		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(93, 51, 146, 17);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		panel.add(progressBar);
		
		JLabel lblFetchingDataFrom = new JLabel("Starting Live Broadcasts...");
		lblFetchingDataFrom.setBounds(93, 26, 165, 14);
		panel.add(lblFetchingDataFrom);
		loadTask = new LoadingTasks();
		loadTask.addPropertyChangeListener(this);
		loadTask.execute();
		setBounds(100, 100, 371,157);
		setVisible(true);
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if (evt.getPropertyName().equals("progress")) {
		    int progress = (Integer) evt.getNewValue();
		    progressBar.setValue(progress);
		}
		if(loadTask.isDone()) {
			//setVisible(false);
			dispose();
		}
	}

}
