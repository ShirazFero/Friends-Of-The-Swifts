package com.youtube.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.json.simple.parser.ParseException;

import com.youtube.controller.Controller;
import com.youtube.controller.FileLogger;
import com.youtube.controller.Interval;
import com.youtube.utils.Constants;

import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.JTabbedPane;

/**
 * Main frame of GUI
 * this class handles all button listeners of all panels of the frame
 * each button pressed method triggers the designated method that performs the requested option
 * @author Evgeny Geyfman
 */
public class mainFrame extends JFrame{

	private static final long serialVersionUID = 6609103195472306740L;
	
	private Boolean[] checkedBroadcasts; 
	
	public mainFrame() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ParseException, InvalidAlgorithmParameterException {

	//----------------------INIT PANELS---------------------
		
		super("YABA");
	
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/YABAWB.png")));
		getContentPane().setBackground(new Color(255, 255, 255));
		setBackground(SystemColor.textHighlightText);
		
		Controller controller = Controller.getInstance();
		
		getContentPane().setLayout(null);
	
		//init new interval panel
		IntervalPanel intervalPanel =  new IntervalPanel();
		IntervalPanel.setInstance(intervalPanel);
		
	
		//init new broadcast panel
		BroadcastPanel boradcastPanel = new BroadcastPanel();
		boradcastPanel.setInstance(boradcastPanel);
		boradcastPanel.setData(controller.getBroadcasts());
		boradcastPanel.resizeColumnWidth(boradcastPanel.getBroadcastTbl());
		
		
		//init new stream panel
		StreamPanel streamPanel = new StreamPanel();
		streamPanel.setData(controller.getStreams());
		streamPanel.resizeColumnWidth(streamPanel.getStreamsTbl());
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(SystemColor.textHighlightText);
		tabbedPane.setBorder(null);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 20));
		tabbedPane.add("Live Broadcasts",boradcastPanel);
		tabbedPane.add("Live Streams",streamPanel);
		tabbedPane.setBounds(255, 216, 400, 399);
		getContentPane().add(tabbedPane);
		
		getContentPane().add(intervalPanel);

		//init new interval input form
		IntervalInputForm inputForm = IntervalInputForm.getInstance();
		inputForm.setVisible(false);
		
		//init new button panel
		ButtonPanel btnPnl = new ButtonPanel();
		btnPnl.setInstance(btnPnl);
		
		getContentPane().add(btnPnl);
		
		//init description frame
		DescriptionFrame desFrame = new DescriptionFrame();
		desFrame.setVisible(false);
		
		if(controller.loadUserSettings())
			System.out.println("user settings loaded Successfully"); //Successfully
		else
			System.out.println("new user data file created Successfully");
		
		//init user settings frame
		UserSettingsFrame userSetPanel = new UserSettingsFrame();
		userSetPanel.getPrivacyComboBox().setLocation(153, 261);
		tabbedPane.add("Settings",userSetPanel);
		
		
		//--------------Adding button listeners to panels------------

		streamPanel.setBtnListener(new ButtonListener() {		//set button listener for stream panel
			public void ButtonPressed(String name) {
				try{
					System.out.println("main frame Stream Panel: " +name);
					switch(name){
						case "Refresh":	
							controller.refreshStreams();//request stream refresh 
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							break;
						
						case Constants.addStream:
							controller.addStream();
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							desFrame.refresh();
							
							break;	
						
						case Constants.removeStream:	
							String[] streams = streamPanel.getChecked();		//get input of checked streams
							if(streams.length<1) {
								JOptionPane.showMessageDialog(null,"No streams were selected","Not Completed",
						                JOptionPane.ERROR_MESSAGE);
								break;
							}
							String message= "Are you sure you want to remove those Streams?",title="Log out";
							int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_OPTION);
							if(reply!=JOptionPane.OK_OPTION)
								break;
							controller.removeStream(streams);//request add refresh	
							controller.refreshStreams();
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							desFrame.refresh();
							break;
							
						case Constants.setDescription: //set description to desired Stream
							desFrame.setVisible(true);
							break;
					}
				} catch (IOException | HeadlessException | ParseException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException  e1) {
					e1.printStackTrace();
				}	
		}
	});
	
	
	
	boradcastPanel.setBtnlistener(new ButtonListener() {	//set button listener for broadcast panel
		public void ButtonPressed(String btnName) {
			try {
				System.out.println("main frame Broadcast Panel: " +btnName);
				switch(btnName) {
					case "Filter":
						String[] args = {"refresh",boradcastPanel.getSelected(),null,null};	//create args
						controller.refreshBroadcasts(args); 						//request refresh
						boradcastPanel.setData(controller.getBroadcasts());			//set new data
						boradcastPanel.refresh();									//refresh table
						break;
					case "Next Page":
						String[] args1 = {"refresh",boradcastPanel.getSelected(),Constants.NextPageToken,null};	//create args
						controller.refreshBroadcasts(args1);//request refresh
						boradcastPanel.setData(controller.getBroadcasts());			//set new data
						boradcastPanel.refresh();									//refresh table
						break;
					
					case "Previous Page":
						String[] args2 = {"refresh",boradcastPanel.getSelected(),null,Constants.PrevPageToken};	//create args
						controller.refreshBroadcasts(args2);
						boradcastPanel.setData(controller.getBroadcasts());			//set new data
						boradcastPanel.refresh();									//refresh table
						break;
					
					case "Update Description":	//updates description on selected broadcasts
						checkedBroadcasts=boradcastPanel.getChecked();
						controller.setCheckedBroadcasts(checkedBroadcasts);
						int emtpyCounter = 0;
						for(int i=0;i<checkedBroadcasts.length;i++) {
							if(!checkedBroadcasts[i])
								emtpyCounter++;
						}
						if(emtpyCounter == checkedBroadcasts.length) {
							JOptionPane.showMessageDialog(null,
									"No Broadcasts Were Selected",
					                "Not Completed",
					                JOptionPane.ERROR_MESSAGE);
							break;
						}
						String decription = JOptionPane.showInputDialog("please enter Description");
						if(decription == null) {
							System.out.println("requset cancelled");
							return;
						}
						controller.updateDescription(decription);
						break;
				}
			} catch (  IOException  e1) { //UnsupportedEncodingException
				e1.printStackTrace();
			}
		}
	});	
		
		
	btnPnl.setBtnListener(new ButtonListener() {	//set button listener for button panel
		public void ButtonPressed(String name) {
			try {
				System.out.println("main frame Button Panel: " +name);
				switch(name) {
					case "<html>Set<br>Interval</html>": //set interval 
						Constants.SetInterval=true;
						inputForm.getJsp().setVisible(false);
						inputForm.getBtnRefresh().setVisible(false);
						inputForm.getBtnOk().setText("Set");
						inputForm.setVisible(true);	
						inputForm.getBtnOk().setBounds(48, 46, 89, 23);
						inputForm.getBtnCancel().setBounds(156, 46, 89, 23);
						inputForm.setSize(462,150);
						break;
						
					case "<html>Start Live<br>Broadcast</html>":	 //start interval broadcast
						inputForm.setData(controller.filterStreams("active"));  //set active streams to form
						inputForm.refresh();
						inputForm.setVisible(true);								//open input form
						break;

					case "<html>Stop Live<br>Broadcast</html>":					//stop interval broadcast
						String message= "Stop Live broadcasts?",
								title="Stop Live broadcasts";
						int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_OPTION);
						if(reply!=JOptionPane.YES_OPTION)
							break;
						if(Constants.IntervalBroadcast) {
							Constants.IntervalBroadcast=false;			//toggle flag off
							controller.cancelTimerRunner();
						}
						else if(Constants.LiveId!=null && !Constants.LiveId.isEmpty()){
							Constants.RegularBroadcast=false;
							String[] brdID = new String[Constants.LiveId.size()]; 
							Constants.LiveId.toArray(brdID);
							controller.stopBroadcasts(brdID);
						}
						btnPnl.getStartIntBrdbtn().setVisible(true);
						btnPnl.getStopIntbtn().setVisible(false);
						btnPnl.getStopIntbtn().setEnabled(false);
						btnPnl.getStartIntBrdbtn().setEnabled(true);
						Constants.State = "Completing";
						break;
					
					case "<html>Live<br>Manager</html>": // "Open YouTube Live Streams
						System.out.println("---------------------------------------");
						java.awt.Desktop.getDesktop().browse(new URI(Constants.LiveStreamUrl));
						break;
						
					case "<html>YouTube<br>Studio</html>":
						System.out.println("---------------------------------------");
						java.awt.Desktop.getDesktop().browse(new URI(Constants.StudioUrl));
						break;
					case "Log Out":
						message= "Are you sure you want to log out?";
						title="Log out";
						reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_OPTION);
						if(reply!=JOptionPane.OK_OPTION)
							break;
						dispose();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								try {
					            	controller.saveData();   //save status of broadcast 
					            	if(controller.getTimerRunner()!=null)
					            		controller.getTimerRunner().cancelTimer();
					            	dispose();				
					            	Controller controller = new Controller();
									controller.setInstance(controller);
									controller.initData();
									new UserLogin();
								} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
										| InvalidAlgorithmParameterException | IOException | ParseException e) {
									e.printStackTrace();
								} 
									
							}
								
						});
						break;
				}
			} catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException |
					URISyntaxException | InvalidAlgorithmParameterException e1) {
				e1.printStackTrace();
			}
		}
	});
	
		
		
	inputForm.setBtnListener(new ButtonListener() {			//set button listener for input form
		public void ButtonPressed(String btnName) {
			System.out.println("input form frame: " +btnName);
			switch(btnName) {
				case "Set": 
					String previousSelection = inputForm.getSelected();
					inputForm.setSelected((String) inputForm.getBox().getSelectedItem());
					if(Constants.RegularBroadcast || //can not set interval during regular broadcast or switch to regular 
						(Constants.IntervalBroadcast && inputForm.getSelected().equals("Non-Stop"))) { //from interval 
						 JOptionPane.showMessageDialog(null,"Option not possible during interval broadcast",
									"Request problem",JOptionPane.ERROR_MESSAGE);
						 inputForm.setSelected(previousSelection);
						 inputForm.getBox().setSelectedItem(previousSelection);
					}
					else {
						Interval interval = Interval.getInstance();
						if(inputForm.getSelected().equals("Non-Stop")){
							 intervalPanel.getLblNotSet().setText(inputForm.getSelected());
						}
						else{  //Prompt chosen interval to interval panel
							interval.setInterval(inputForm.getSelected());	//set chosen interval
							intervalPanel.getLblNotSet().setText(interval.getHours() +
									" Hours and " + interval.getMinutes() +" minutes");
						}
						if(Constants.IntervalBroadcast) {
						JOptionPane.showMessageDialog(null,"interval was set and will start after the end of currnet interval",
					                "Interval Set", JOptionPane.INFORMATION_MESSAGE);
						}
						inputForm.getBox().setSelectedItem(inputForm.getSelected());
					}
					inputForm.getBtnOk().setText("Start");
					Constants.SetInterval=false;
					inputForm.getBtnRefresh().setVisible(true);
					inputForm.setVisible(false);
					inputForm.getJsp().setVisible(true);
					inputForm.getBtnOk().setBounds(20, 212, 89, 23);
					inputForm.getBtnCancel().setBounds(123, 212, 89, 23);
					inputForm.setSize(462, 307);
					break;
					
				case "Refresh":
					controller.refreshStreams();
					inputForm.setData(controller.filterStreams("active"));//set active streams to form
					inputForm.refresh();
					break;
				case "Start": 
					if(checkSelectedStreams()) {//check that at least one stream is active and less then 10 were chosen
						inputForm.setSelected((String) inputForm.getBox().getSelectedItem());
						inputForm.getBox().setSelectedItem(inputForm.getSelected());
						controller.setCheckedStreams(inputForm.getChecked());	// set input of checked streams to controller
							// start broadcasting according to pressed button
						if(inputForm.getSelected().equals("Non-Stop")) {
							Constants.RegularBroadcast=true;
							//System.out.println("IntervalBroadcast");
						}
						else {
							Constants.IntervalBroadcast =true;
							//System.out.println("RegularBroadcast");
						}
						startBroadcast();
					}
					break;
					
				case "Cancel":
					inputForm.getBtnRefresh().setVisible(true);
					inputForm.getJsp().setVisible(true);
					inputForm.setVisible(false);
					Constants.SetInterval=false;
					inputForm.getLblThereAreNo().setVisible(false);
					inputForm.getBtnOk().setText("Start");
					inputForm.getBtnOk().setEnabled(true);
					inputForm.getBtnOk().setBounds(20, 212, 89, 23);
					inputForm.getBtnCancel().setBounds(123, 212, 89, 23);
					inputForm.setSize(462, 307);
					break;
				}
			
			}
			
			/**
			 * helper method starts interval/regular broadcast by controller,
			 * sets selected streams to controller, starts first live broadcasts
			 * starts Timer runner instance if needed
			 */
			private void startBroadcast() {
				try {	
					
					if(Constants.IntervalBroadcast) {
						Interval interval = Interval.getInstance();
						interval.setInterval(inputForm.getSelected());		//set selected interval length
						FileLogger.logger.info("interval was set: " + interval.getInterval());
						
						FileLogger.logger.info("starting first live broadcasts");
						 
						controller.startTimerRunner();			//start timer runner instance
						FileLogger.logger.info("timer runner started");
						
						// Prompt interval start stop times to interval panel
						String startTime = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()).toString();
						String stoptime = interval.getCorrentInterval().toString();
						controller.updateIntervalPanel(startTime, stoptime);
						intervalPanel.getLblstime().setVisible(true);
						intervalPanel.getFtime().setVisible(true);
						intervalPanel.getLblNotSet().setText(interval.getHours() + " Hours and "	
						+ interval.getMinutes() +" minutes");
					}
					else {
						intervalPanel.getLblNotSet().setText(inputForm.getSelected());
					}
					controller.startBroadcast();	//start initial live Broadcasts
					// toggle GUI buttons 
					btnPnl.getStartIntBrdbtn().setVisible(false);
					btnPnl.getStopIntbtn().setVisible(true);
					btnPnl.getStartIntBrdbtn().setEnabled(false); 
					btnPnl.getStopIntbtn().setEnabled(true);
					Constants.State = "Starting";
					
					//close input form and Prompt chosen interval to interval panel
					inputForm.setVisible(false);								
						
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			
			/**
			 * helper method checks if at least one stream was selected  
			 * @return
			 */
			private boolean checkSelectedStreams() {
				//if no stream was selected toggle option pane and ask to refresh streams
				if(inputForm!=null && inputForm.getChecked().length<1) {
					String message= "No active streams were chosen for Broadcast, Please choose at least one Stream",
							title="No Streams Chosen";
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.OK_OPTION);
					return false;
				}
				if(inputForm!=null && inputForm.getChecked().length>10) {
					String message= "Please select less then 10 broadcasts",
							title="Too much Streams Chosen";
					JOptionPane.showMessageDialog(null,message,title,JOptionPane.ERROR_MESSAGE);
					return false;
				}
				return true;
			}
		});
		
	desFrame.setBtnListener(new ButtonListener() {
		
		@Override
		public void ButtonPressed(String btnName) {
			System.out.println("des  frame : " +btnName);
			switch(btnName) {
			case "OK": 
				desFrame.setVisible(false);
				break;
			case "Cancel":  
				desFrame.setVisible(false); 
				break;
			}
		}
	});
		
	userSetPanel.setBtnListener(new ButtonListener() {
		
		@Override
		public void ButtonPressed(String btnName) {
			System.out.println("user Settings frame : " +btnName);
			switch(btnName) {
			case "OK": 
					userSetPanel.setVisible(false);
					break;
			case "Apply":  
					Constants.Format  = (String) userSetPanel.getFormatcomboBox().getSelectedItem();
					userSetPanel.getFormatcomboBox().setSelectedItem(Constants.Format);
					Constants.IngestionType = (String) userSetPanel.getIngestionComboBox().getSelectedItem();
					userSetPanel.getIngestionComboBox().setSelectedItem(Constants.IngestionType);
					Constants.Privacy   = (String) userSetPanel.getPrivacyComboBox().getSelectedItem();
					userSetPanel.getPrivacyComboBox().setSelectedItem(Constants.Privacy);
					JOptionPane.showMessageDialog(null,"Setting Updated Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);	
					break;
			}
		}
	});
	
	setSize(675, 665);
	setMinimumSize(new Dimension(675, 660));
	setMaximumSize(new Dimension(675, 660));
	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	this.addWindowListener(new WindowAdapter(){		//handle App close operation ,if live save corrent status
		public void windowClosing(WindowEvent e){
        	//save status of broadcast
			String message= "Are you sure you want to Exit?",title="Log out";
			int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
			if(reply==JOptionPane.YES_OPTION) {
				controller.saveData();
	        	System.exit(0);
				
			}
			return;
        }
    });     
	setLocationRelativeTo(null);
	setVisible(true);
	if(Constants.saveState) {
		controller.loadUserState();
		System.out.println("loaded user state");
	}
	else
		System.out.println("user state load not enabled");
}		
		
	
	/**
	 * set checked Broadcast from broadcast table to main frame
	 * @param checkedBroadcasts
	 */
	public void setCheckedBroadcasts(Boolean[] checkedBroadcasts) {
		this.checkedBroadcasts = checkedBroadcasts;
	}
}
