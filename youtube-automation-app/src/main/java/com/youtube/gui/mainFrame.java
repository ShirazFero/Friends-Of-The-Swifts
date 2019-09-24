package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class mainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JLabel welcomeLabel;
	
	public mainFrame() {
		super("Control Panel");
		setLayout(new BorderLayout());
		
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
		});
		
		StatusPanel statusPanel = new StatusPanel();
		add(statusPanel,BorderLayout.EAST);
		statusPanel.setBtnListener(new ButtonListener() {
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
					case "Refresh":	System.out.println("main frame: " +name);  break;
					}
				}
			}
		
		});
		
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


}
