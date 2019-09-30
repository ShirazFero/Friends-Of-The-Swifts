package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.youtube.controller.Controller;

public class mainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JLabel welcomeLabel;
	private Controller controller;
	private TablesPanel tablePanel;
	private Boolean[] checked; 
	
	public mainFrame() {
		super("Control Panel");
		
		setLayout(new BorderLayout());
		tablePanel = new TablesPanel();
		StreamPanel streamPanel = new StreamPanel();
		
		controller = new Controller();
		
		streamPanel.setData(controller.getStreams());
		//add(streamPanel,BorderLayout.CENTER);
		
		tablePanel.setSP(streamPanel);
		
		BroadcastPanel boradcastPanel =new BroadcastPanel();
		boradcastPanel.setData(controller.getBroadcasts());
		//add(boradcastPanel,BorderLayout.SOUTH);
		
		tablePanel.setBP(boradcastPanel);
		
		add(tablePanel,BorderLayout.CENTER);
		
		streamPanel.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
					case "Refresh":	System.out.println("main frame1: " +name);
					controller.refreshStreams();
					streamPanel.setData(controller.getStreams());
					streamPanel.refresh();
					break;
					case "Add Stream":					System.out.println("main frame: " +name);
					controller.addStream();
					streamPanel.setData(controller.getStreams());
					streamPanel.refresh();
					break;
					case "Remove Streams":				System.out.println("main frame: " +name);
					controller.removeStream(checked);
					streamPanel.setData(controller.getStreams());
					streamPanel.refresh();
					break;
					}
				}
			}

			public void StreamsSelected(Boolean[] checked) {
				setChecked(checked);
			}
		
		});
		
		welcomeLabel= new JLabel("Welcome to Broadcast Control Panel",SwingConstants.CENTER);
		add(welcomeLabel,BorderLayout.NORTH);
		
		ButtonPanel btnPnl = new ButtonPanel();
		add(btnPnl,BorderLayout.WEST);
		btnPnl.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
					case "Set Interval":				controller.setInterval();
														System.out.println("main frame: " +name);	break;
					case "Start Interval Broadcast":	//start int broadcast
														btnPnl.getStartIntBrdbtn().setEnabled(false);
														btnPnl.getStopIntbtn().setEnabled(true);
														System.out.println("main frame: " +name);	break;
					case "Stop Interval Broadcast":		//stop int broadcast
														btnPnl.getStopIntbtn().setEnabled(false);
														btnPnl.getStartIntBrdbtn().setEnabled(true);
														System.out.println("main frame: " +name);	break;
					case "My Streams":					System.out.println("main frame: " +name);	break;
					case "Add Stream":					System.out.println("main frame: " +name);	break;
					case "Remove Stream":				System.out.println("main frame: " +name);
					}
				}
			}

			@Override
			public void StreamsSelected(Boolean[] checked) {// TODO Auto-generated method stub
				setChecked(checked);
			}
		});
		
		setSize(600, 600);
		setMinimumSize(new Dimension(550,300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}

	public void setChecked(Boolean[] checked) {
		this.checked=checked;
	}

}
