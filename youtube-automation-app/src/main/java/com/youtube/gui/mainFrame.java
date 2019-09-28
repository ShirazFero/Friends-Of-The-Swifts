package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.controller.Controller;

public class mainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JLabel welcomeLabel;
	private Controller controller;
	
	public mainFrame() {
		super("Control Panel");
		
		setLayout(new BorderLayout());
		
		StatusPanel statusPanel = new StatusPanel();
		
		System.out.println(statusPanel.getStm());
		controller = new Controller();
		
		statusPanel.setData(controller.getStreams());
		add(statusPanel,BorderLayout.EAST);
		
		statusPanel.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
					case "Refresh":	System.out.println("main frame: " +name);
					controller.refreshStreams();
					statusPanel.setData(controller.getStreams());
					statusPanel.refresh();
							;  break;
					case "Get Selected": break;
					}
				}
			}

			public void StreamsSelected(Boolean[] checked) {
				// TODO Auto-generated method stub
				List<LiveStream> readyList = new LinkedList<LiveStream>();
				List<LiveStream> livestreams = controller.getStreams();
				int i=0;
				for(LiveStream stream: livestreams) {
					if(checked[i]) {
						if(stream.getStatus().getStreamStatus().equals("active")) {
						
							readyList.add(stream);
						}
					
						else{
						JOptionPane.showMessageDialog(null,
								stream.getSnippet().getTitle()+"is not active, please activate and try again");
						System.out.println(stream.getSnippet().getTitle()+"is not active, please activate and try again");
						return;
						}
					}
					i++;
				}
				for(LiveStream stream: readyList) {
					System.out.println(stream.getSnippet().getTitle());
				}
			}
		
		});
		
		welcomeLabel= new JLabel("Welcome to Broadcast Control Panel");
		welcomeLabel.setLocation(new Point(5,0));
		add(welcomeLabel,BorderLayout.NORTH);
		
		ButtonPanel btnPnl = new ButtonPanel();
		add(btnPnl,BorderLayout.WEST);
		btnPnl.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
					case "Check Stream":				System.out.println("main frame: " +name);	break;
					case "Start Interval Broadcast":	System.out.println("main frame: " +name);	break;
					case "Stop Interval Broadcast":		System.out.println("main frame: " +name);	break;
					case "My Streams":					System.out.println("main frame: " +name);	break;
					case "Add Stream":					System.out.println("main frame: " +name);	break;
					case "Remove Stream":				System.out.println("main frame: " +name);	break;
					}
				}
			}

			@Override
			public void StreamsSelected(Boolean[] checked) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}


}
