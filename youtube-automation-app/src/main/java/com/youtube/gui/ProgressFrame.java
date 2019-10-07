package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.youtube.controller.BackroundTasks;
import com.youtube.controller.Controller;
import javax.swing.JLabel;


public class ProgressFrame extends JFrame implements ActionListener,
PropertyChangeListener {
	
	private JProgressBar progressBar;
	
	private BackroundTasks task;
	
	private static final long serialVersionUID = 1L;

	public ProgressFrame(Controller controller){
		super("Loading");
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
			removeAll();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
