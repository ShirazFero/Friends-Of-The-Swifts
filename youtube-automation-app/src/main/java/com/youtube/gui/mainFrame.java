package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import com.youtube.controller.Controller;
import com.youtube.utils.Constants;

public class mainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private JLabel welcomeLabel;
	
	private TablesPanel tablePanel;
	
	private Boolean[] checkedStreams; 
	
	private Boolean[] checkedBroadcasts; 
	
	public mainFrame(Controller controller) {
	
		//--------INIT PANELS-------------------------------------------------------
		
		super("Control Panel");
		getContentPane().setLayout(new BorderLayout());
		tablePanel = new TablesPanel();
		StreamPanel streamPanel = new StreamPanel();
		streamPanel.setData(controller.getStreams());
		tablePanel.setSP(streamPanel);
		BroadcastPanel boradcastPanel = new BroadcastPanel();
		boradcastPanel.setData(controller.getBroadcasts());
		tablePanel.setBP(boradcastPanel);
		getContentPane().add(tablePanel,BorderLayout.CENTER);
		IntervalInputForm inputForm = new IntervalInputForm();
		inputForm.setVisible(false);
		welcomeLabel= new JLabel("Welcome to Broadcast Control Panel",SwingConstants.CENTER);
		getContentPane().add(welcomeLabel,BorderLayout.NORTH);
		ButtonPanel btnPnl = new ButtonPanel();
		getContentPane().add(btnPnl,BorderLayout.WEST);
		
		//--------------Adding button listeners to panels-------------------------------
		
		boradcastPanel.setBtnlistener(new ButtonListener() {
			public void ButtonPressed(String btnName) {
				// TODO Auto-generated method stub
				if(btnName!=null) {
					switch(btnName) {
						case "Filter":
								controller.refreshBroadcasts(boradcastPanel.getSelected());
								boradcastPanel.setData(controller.getBroadcasts());
								boradcastPanel.refresh();
								System.out.println("main frame1: " +btnName);
								break;
						case "Select":
								checkedBroadcasts=boradcastPanel.getChecked();
								for(int i=0;i<checkedBroadcasts.length;i++) 
									System.out.println(checkedBroadcasts[i]);
								break;
					}
				}
			}
		});
		
		streamPanel.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
						case "Refresh":	
							System.out.println("main frame1: " +name);
							controller.refreshStreams();
							streamPanel.setData(controller.getStreams());
							streamPanel.refresh();
							break;
						
						case "Add Stream":
							System.out.println("main frame: " +name);
							controller.addStream();
							streamPanel.setData(controller.getStreams());
							streamPanel.refresh();
							break;
						
						case "Remove Streams":	
							System.out.println("main frame: " +name);
							checkedStreams= streamPanel.getChecked();
							controller.removeStream(checkedStreams);
							streamPanel.setData(controller.getStreams());
							streamPanel.refresh();
							break;
					}
				}
			}
		});
		
		btnPnl.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
						case "Set Interval":
							System.out.println("---------------------------------------");
							inputForm.setVisible(true);
							btnPnl.getStartIntBrdbtn().setEnabled(true);
							System.out.println("main frame: " +name);
							break;
							
						case "Start Interval Broadcast":				//start interval broadcast
							System.out.println("---------------------------------------");
							Constants.IntervalBroadcast = true;
							//controller.startBroadcast(checkedStreams);	//start the broadcasts
							System.out.println("starting first live broadcasts");
							try {
								controller.startTimerRunner();			//start timer runner instance
							}
							catch (InterruptedException e) {
								e.printStackTrace();
							}
							System.out.println("starting timer runner");
							btnPnl.getStartIntBrdbtn().setEnabled(false);
							btnPnl.getStopIntbtn().setEnabled(true);
							btnPnl.getStartBrdbtn().setEnabled(false);
							btnPnl.getStopBrdbtn().setEnabled(false);
							System.out.println("main frame: " +name);
							break;
						
						case "Stop Interval Broadcast":					//stop interval broadcast
							System.out.println("---------------------------------------");
							Constants.IntervalBroadcast=false;
							controller.cancelTimerRunner();
							btnPnl.getStartBrdbtn().setEnabled(true);
							btnPnl.getStopIntbtn().setEnabled(false);
							btnPnl.getStartIntBrdbtn().setEnabled(true);
							System.out.println("main frame: " +name);	
							break;
							
						case "Start Broadcast":	
							System.out.println("---------------------------------------");
							checkedStreams = streamPanel.getChecked();
							controller.startBroadcast(checkedStreams);	//start the broadcasts
							btnPnl.getStartBrdbtn().setEnabled(false);
							btnPnl.getStopBrdbtn().setEnabled(true);
							btnPnl.getStopIntbtn().setEnabled(false);
							btnPnl.getStartIntBrdbtn().setEnabled(false);
							System.out.println("main frame: " +name);	
							break;
							
						case "Stop Broadcast":
							System.out.println("---------------------------------------");
							checkedBroadcasts = boradcastPanel.getChecked();
							controller.stopBroadcast(checkedBroadcasts);
							btnPnl.getStartBrdbtn().setEnabled(true);
							btnPnl.getStopBrdbtn().setEnabled(false);
							if(Controller.getInterval().getInterval()!=null) {	//if interval was'nt set yet don't 
								btnPnl.getStartIntBrdbtn().setEnabled(true);	//enable interval broadcast
							}
						    System.out.println("main frame: " +name);	
							break;
							
						case "Test":
							System.out.println("---------------------------------------");
							Controller.calcStopTime();
							break;
					}
				}
			}
		});
		
		inputForm.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String btnName) {
				// TODO Auto-generated method stub
				System.out.println("main frame1: " +btnName);
				switch(btnName) {
				case "OK":  
					System.out.println("OK");
					controller.setInterval(inputForm.getSelected());
					System.out.println("interval was set: " + Controller.getInterval().getHours(
							)+":"+ Controller.getInterval().getMinutes());
					inputForm.setVisible(false);
					break;
				case "Cancel":
					inputForm.setVisible(false);
					break;
				}
			}
		});
		
		setSize(561, 420);
		setMinimumSize(new Dimension(550,300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}

	public void setCheckedStreams(Boolean[] checked) {
		this.checkedStreams=checked;
	}

	public void setCheckedBroadcasts(Boolean[] checkedBroadcasts) {
		this.checkedBroadcasts = checkedBroadcasts;
	}

}
