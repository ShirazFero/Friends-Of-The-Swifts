package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import com.youtube.controller.Controller;
import com.youtube.controller.Interval;
import com.youtube.utils.Constants;

/**
 * Main frame of GUI
 * this class handles all buttin lisnteners of all panels of the frame
 * @author Evgeny Geyfman
 *
 */
public class mainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private JLabel welcomeLabel;
	
	private TablesPanel tablePanel;
	
	private Boolean[] checkedStreams; 
	
	private Boolean[] checkedBroadcasts; 
	
	private static mainFrame instance;
	
	public mainFrame(Controller controller) {
	
	//----------------------INIT PANELS---------------------
		
		super("Control Panel");
		getContentPane().setLayout(new BorderLayout());
	
		//init new interval panel
		IntervalPanel intervalPanel =  IntervalPanel.getInstance();
		
		//init new stream panel
		StreamPanel streamPanel = new StreamPanel();
		streamPanel.setData(controller.getStreams());
	
		//init new broadcast panel
		BroadcastPanel boradcastPanel =  BroadcastPanel.getInstance();
		boradcastPanel.setData(controller.getBroadcasts());
		
		//align them into table panel
		tablePanel = new TablesPanel(intervalPanel,boradcastPanel,streamPanel);
		getContentPane().add(tablePanel,BorderLayout.CENTER);
		
		//init new interval input form
		IntervalInputForm inputForm = IntervalInputForm.getInstance();
		inputForm.setVisible(false);
		
		//set welcome label
		welcomeLabel= new JLabel("Welcome to Broadcast Control Panel",SwingConstants.CENTER);
		getContentPane().add(welcomeLabel,BorderLayout.NORTH);
		
		//init new button panel
		ButtonPanel btnPnl = new ButtonPanel();
		getContentPane().add(btnPnl,BorderLayout.WEST);
		
		
		//--------------Adding button listeners to panels------------
		
		boradcastPanel.setBtnlistener(new ButtonListener() {	//set button listener for broadcast panel
			public void ButtonPressed(String btnName) {
				if(btnName!=null) {
					switch(btnName) {
						case "Filter":
								String[] args = {"refresh",boradcastPanel.getSelected()};	//create args
								controller.refreshBroadcasts(args);							//request refresh
								boradcastPanel.setData(controller.getBroadcasts());			//set new data
								boradcastPanel.refresh();									//refresh table
								System.out.println("main frame Broadcast Panel: " +btnName);
								break;
						case "Select":	//this button doesn't do anything yet , prints selected broadcasts
								System.out.println("main frame Broadcast Panel: " +btnName);
								checkedBroadcasts=boradcastPanel.getChecked();
								for(int i=0;i<checkedBroadcasts.length;i++) 
									if(checkedBroadcasts[i]) {System.out.println("no "+ i +" "+checkedBroadcasts[i]);}
								break;
					}
				}
			}
		});
		
		streamPanel.setBtnListener(new ButtonListener() {		//set button listener for stream panel
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
						case "Refresh":	
							System.out.println("main frame Stream Panel: " +name);
							controller.refreshStreams();					//request stream refresh 
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							break;
						
						case "Add Stream":
							System.out.println("main frame Stream Panel: " +name);
							controller.addStream();							//request add refresh 
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							break;	
						
						case "Remove Streams":	
							System.out.println("main frame Stream Panel: " +name);
							checkedStreams= streamPanel.getChecked();		//get input of checked streams
							controller.removeStream(checkedStreams);		//request add refresh 
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							break;
					}
				}
			}
		});
		
		btnPnl.setBtnListener(new ButtonListener() {			//set button listener for button panel
			public void ButtonPressed(String name) {
				if(name!=null) {
					switch(name) {
						case "Set Interval":
							System.out.println("---------------------------------------");
							Constants.SetInterval=true;
							inputForm.getJsp().setVisible(false);
							inputForm.getBtnRefresh().setVisible(false);
							inputForm.setVisible(true);	
							System.out.println("main frame Button Panel: " +name);
							break;
							
						case "Start Interval Broadcast":	 //start interval broadcast
							System.out.println("---------------------------------------");
							inputForm.setData(controller.filterStreams("active")); 	//set active streams to form
							inputForm.refresh();
							Constants.IntervalBroadcast=true;
							inputForm.setVisible(true);						//open input form
							System.out.println("main frame Button Panel: " +name);
							break;
						
						case "Stop Interval Broadcast":					//stop interval broadcast
							System.out.println("---------------------------------------");
							Constants.IntervalBroadcast=false;			//toggle flag off
							controller.cancelTimerRunner();
							btnPnl.getStartBrdbtn().setEnabled(true);	//toggle gui buttons 
							btnPnl.getStopIntbtn().setEnabled(false);
							btnPnl.getStartIntBrdbtn().setEnabled(true);
							System.out.println("main frame Button Panel: " +name);	
							break;
							
						case "Start Broadcast":	
							System.out.println("---------------------------------------");
							inputForm.setData(controller.filterStreams("active")); 			//set active streams to form
							inputForm.refresh();
							Constants.RegularBroadcast=true;			//toggle flag on
							inputForm.getBox().setVisible(false);
							inputForm.getLblPleaseEnterInterval().setVisible(false);
							inputForm.setVisible(true);		
							System.out.println("main frame Button Panel: " +name);	
							break;
							
						case "Stop Broadcast":
							System.out.println("---------------------------------------");
							checkedBroadcasts = boradcastPanel.getChecked();
							controller.stopBroadcast(checkedBroadcasts);
							btnPnl.getStartBrdbtn().setEnabled(true);
							btnPnl.getStopBrdbtn().setEnabled(false);
							btnPnl.getStartIntBrdbtn().setEnabled(true);	//enable interval broadcast
							Constants.RegularBroadcast=false;					//toggle flag off
						    System.out.println("main frame Button Panel: " +name);	
							break;
							
						case "Test":
							System.out.println("---------------------------------------");
							Instant instant = Instant.ofEpochMilli(controller.calcStopTime().getTime());
							LocalDateTime finTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
							System.out.println(finTime.toString());
							System.out.println("main frame Button Panel: " +name);	
							break;
					}
				}
			}
		});
		
		inputForm.setBtnListener(new ButtonListener() {			//set button listener for input form
			public void ButtonPressed(String btnName) {
				switch(btnName) {
				case "Refresh":
					controller.refreshStreams();
					inputForm.setData(controller.filterStreams("active")); 			//set active streams to form
					inputForm.refresh();
					System.out.println("input form frame: " +btnName);
					break;
				case "OK": 
				
					checkedStreams = inputForm.getChecked();	// get input of checked streams
					
					// start broadcasting according to pressed button
					if(Constants.SetInterval) {// set iterval was pressed
						if(Constants.IntervalBroadcast) {
							JOptionPane.showMessageDialog(null,
									"interval was set and will start after the end of currnet interval",
					                "Server request problem",
					                JOptionPane.INFORMATION_MESSAGE);
						}
						 inputForm.getBtnRefresh().setVisible(true);
						 inputForm.setVisible(false);
						 inputForm.getJsp().setVisible(true);
						 Interval interval = Interval.getInstance();
						 interval.setInterval(inputForm.getSelected());	//set chosen interval
						 intervalPanel.getLblNotSet().setText(interval.getHours() +
								 " Hours and " + interval.getMinutes() +" minutes");
						 							//Prompt chosen interval to interval panel
						 Constants.SetInterval=false;
					}
					else if(Constants.IntervalBroadcast) {
						//System.out.println("IntervalBroadcast");
						if(checkActiveStreams()) //check that at least one stream is active
							startIntervalBroadcast();
					}
					else if(Constants.RegularBroadcast) {
						//System.out.println("RegularBroadcast");
						if(checkActiveStreams()) //check that at least one stream is active
							startRegularBroadcast();
					}
					break;
					
				case "Cancel":
					if(Constants.IntervalBroadcast ) {
						Constants.IntervalBroadcast = false;
						String message= "interval wasn't set, interval Broadcast isn't enabled",
								title="Cancel setting interval";
						int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.CANCEL_OPTION);
						if(reply==JOptionPane.YES_OPTION) {
							Constants.IntervalBroadcast =false ;
							inputForm.setVisible(false);
						}
					}
					else if(Constants.SetInterval) {
						inputForm.getBtnRefresh().setVisible(true);
						inputForm.getJsp().setVisible(true);
						inputForm.setVisible(false);
						Constants.SetInterval=false;
					}
					else {
						Constants.RegularBroadcast = false;
						inputForm.getBox().setVisible(true);
						inputForm.getLblPleaseEnterInterval().setVisible(true);
						inputForm.setVisible(false);
					}
						
					System.out.println("input form frame: " +btnName);
					break;
				}
			}
			
			/**
			 * helper method starts interval broadcast by controller,
			 * sets selected streams to controller, starts first live broadcasts
			 * starts Timer runner instance
			 */
			private void startIntervalBroadcast() {
				Interval interval = Interval.getInstance();
				interval.setInterval(inputForm.getSelected());		//set selected interval length
				System.out.println("interval was set: " + interval.getInterval());
				controller.setCheckedStreams(checkedStreams);
				try {
					controller.startBroadcast(checkedStreams);	//start initial live Broadcasts
				
					System.out.println("starting first live broadcasts");
				
					controller.startTimerRunner();			//start timer runner instance
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("timer runner started");
				
				// toggle GUI buttons 
				btnPnl.getStartIntBrdbtn().setEnabled(false); 
				btnPnl.getStopIntbtn().setEnabled(true);
				btnPnl.getStartBrdbtn().setEnabled(false);
				btnPnl.getStopBrdbtn().setEnabled(false);
				
				// Prompt interval start stop times to interval panel
				intervalPanel.getLblstime().setText(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()).toString());
				intervalPanel.getLblstime().setVisible(true);
				intervalPanel.getFtime().setText(interval.getCorrentInterval().toString());
				intervalPanel.getFtime().setVisible(true);
				
				//close input form and Prompt chosen interval to interval panel
				inputForm.setVisible(false);								
				intervalPanel.getLblNotSet().setText(interval.getHours() + " Hours and "	
				+ interval.getMinutes() +" minutes");	
				
			}
			
			/**
			 * helper method starts regular broadcast by controller,
			 * sets selected streams to controller, starts first live broadcasts
			 */
			private void startRegularBroadcast() {
				checkedStreams = inputForm.getChecked();	//get input of checked streams
				try {
						controller.startBroadcast(checkedStreams);	//start the broadcasts
						
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}
					//System.out.println("toggle buttons");
					//toggle GUI buttons 
					btnPnl.getStartBrdbtn().setEnabled(false);	
					btnPnl.getStopBrdbtn().setEnabled(true);
					btnPnl.getStopIntbtn().setEnabled(false);
					btnPnl.getStartIntBrdbtn().setEnabled(false);
					inputForm.setVisible(false);	// close form
					inputForm.getBox().setVisible(true);
					inputForm.getLblPleaseEnterInterval().setVisible(true);
				
			}
			
			/**
			 * helper method checks if selected streams are active
			 * @return
			 */
			private boolean checkActiveStreams() {
				Boolean notEmptyFlag = false;				// flag that marks if stream was chosen
				for(Boolean streamActive : checkedStreams) {
					if(streamActive)
						notEmptyFlag=true;					//check if at least one stream was selected
				}
				
				//if no stream was selected toggle option pane and ask to refresh streams
				if(!notEmptyFlag) {
					String message= "No active streams were chosen for Broadcast, would you like to refresh and try again?",title="No active streams";
					int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.CANCEL_OPTION);
					if(reply==JOptionPane.YES_OPTION) {
						inputForm.setData(controller.filterStreams("active")); 	//set active streams to form
						inputForm.refresh();
						return notEmptyFlag;
					}
					else {
						
						inputForm.setVisible(false);
						
					}
				}
				return notEmptyFlag;
			}
			
		});
		
		setSize(800, 800);
		setMinimumSize(new Dimension(550,300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}
	
	/**
	 * sets checked Streams from stream table to main frame
	 * @param checked
	 */
	public void setCheckedStreams(Boolean[] checkedStreams) {
		this.checkedStreams=checkedStreams;
	}
	
	/**
	 * set checked Broadcast from broadcast table to main frame
	 * @param checkedBroadcasts
	 */
	public void setCheckedBroadcasts(Boolean[] checkedBroadcasts) {
		this.checkedBroadcasts = checkedBroadcasts;
	}
	
	/**
	 * singleton instance method ,retrieves a single instance of main frame if exists
	 * else create one.
	 * @param controller
	 * @return instance
	 */
	public static mainFrame getInstance(Controller controller) {
		if (instance == null)
			instance = new mainFrame(controller);
		return instance;
	}
	
	
}
