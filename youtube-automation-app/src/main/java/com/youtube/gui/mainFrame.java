package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.youtube.controller.Controller;
import com.youtube.controller.Interval;
import com.youtube.utils.Constants;
import java.awt.Font;

/**
 * Main frame of GUI
 * this class handles all button listeners of all panels of the frame
 * each button pressed method triggers the designated method that performs the requested option
 * @author Evgeny Geyfman
 *
 */
public class mainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private JLabel welcomeLabel;
	
	private TablesPanel tablePanel;
	
	private Boolean[] checkedStreams; 
	
	private Boolean[] checkedBroadcasts; 
	
	private ButtonPanel btnPnl;
	
	@SuppressWarnings("unchecked")
	public mainFrame() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ParseException, InvalidAlgorithmParameterException {

	//----------------------INIT PANELS---------------------
		
		super("Control Panel");
		
		
		Controller controller = Controller.getInstance();
		
		getContentPane().setLayout(new BorderLayout());
	
		//init new interval panel
		IntervalPanel intervalPanel =  new IntervalPanel();
		IntervalPanel.setInstance(intervalPanel);
		
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
		welcomeLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		getContentPane().add(welcomeLabel,BorderLayout.NORTH);
		
		//init new button panel
		btnPnl =  ButtonPanel.getInstance();
		getContentPane().add(btnPnl,BorderLayout.WEST);
		System.out.println(Constants.UserDataPath+ Constants.Username + ".json");
		final Path path = Paths.get(Constants.UserDataPath+ Constants.Username + ".json");
		 
		if(Files.exists(path)) {     	//check if there's a file of saved data
			System.out.println("here if");
			if(!controller.getBroadcasts().isEmpty()) {	//if there's active broadcasts
				String message= "Do you want to resume saved Broadcast?"
						 ,title="Resume Broadcast";
				int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.CANCEL_OPTION);
				if(reply==JOptionPane.YES_OPTION) 
					controller.loadData();	//load it
			}
			else { 	// else load only the description
				JSONParser parser = new JSONParser();
			 
	            Object obj = parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
	            		
	            JSONObject jsonObject = (JSONObject) obj;
	 			
	            Constants.Description = (String) jsonObject.get("Description");
	        
			}
		}
		else {						//else create one
			JSONObject obj = new JSONObject();
    		//save regular broadcast flag
			System.out.println("here else");
    		obj.put("Regular Broadcast", "OFF");
    		obj.put("Interval Broadcast", "OFF");
    		obj.put("Description", Constants.Description);
    		try (FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json")) {
    			file.write(obj.toJSONString());
    			System.out.println("Successfully created first JSON Object File...");
    			System.out.println("\nJSON Object: " + obj);
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
		}
		
		//--------------Adding button listeners to panels------------
		
		intervalPanel.setBtnListener(new ButtonListener() {
			@Override
			public void ButtonPressed(String btnName) {
				// TODO Auto-generated method stub
				switch(btnName) {
				
				case "Log out": dispose();
								SwingUtilities.invokeLater(new Runnable() {
									
									public void run() {
										try {
							            	controller.saveData(); //save status of broadcast 
											dispose();				
											Controller controller = Controller.getInstance();
											controller.initData(); // set initial data
											new UserLogin();
											//new ProgressFrame();
										} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
												| InvalidAlgorithmParameterException | IOException | ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								});
								 
								 System.out.println("input panel "+ btnName);
								 break;
				case "Set Description": String input = JOptionPane.showInputDialog("please enter Description");
										if(input!=null)
											Constants.Description = input;
										else
											System.out.println("no text was entred");
										System.out.println("input panel "+ btnName);
										break;
				}
			}
		});
		
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
						case "Set Description":	//this button doesn't do anything yet , prints selected broadcasts
								
								checkedBroadcasts=boradcastPanel.getChecked();
								controller.setCheckedBroadcasts(checkedBroadcasts);
								String decription = JOptionPane.showInputDialog("please enter Description");
								if(decription!=null) {
									try {
										controller.updateDescription(decription);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								else {
									System.out.println("no description was enterd");
								}
								System.out.println("main frame Broadcast Panel: " +btnName);
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
							intervalPanel.getBtnSetdescription().setEnabled(false); 
							Constants.IntervalBroadcast=true;
							inputForm.setVisible(true);						//open input form
							System.out.println("main frame Button Panel: " +name);
							break;
						
						case "Stop Interval Broadcast":					//stop interval broadcast
							System.out.println("---------------------------------------");
							Constants.IntervalBroadcast=false;			//toggle flag off
							try {
								controller.cancelTimerRunner();
							} catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException | ParseException | InvalidAlgorithmParameterException e1) {
								e1.printStackTrace();
							}
							btnPnl.getStartBrdbtn().setEnabled(true);	//toggle gui buttons 
							btnPnl.getStopIntbtn().setEnabled(false);
							btnPnl.getStartIntBrdbtn().setEnabled(true);
							intervalPanel.getBtnSetdescription().setEnabled(true);
							System.out.println("main frame Button Panel: " +name);	
							break;
							
						case "Start Broadcast":	
							System.out.println("---------------------------------------");
							inputForm.setData(controller.filterStreams("active")); 			//set active streams to form
							inputForm.refresh();
							intervalPanel.getBtnSetdescription().setEnabled(false);
							Constants.RegularBroadcast=true;			//toggle flag on
							inputForm.getBox().setVisible(false);
							inputForm.getLblPleaseEnterInterval().setVisible(false);
							inputForm.setVisible(true);		
							System.out.println("main frame Button Panel: " +name);	
							break;
							
						case "Stop Broadcast":
							System.out.println("---------------------------------------");
							checkedBroadcasts = boradcastPanel.getChecked();
							try {
								controller.stopBroadcasts();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							btnPnl.getStartBrdbtn().setEnabled(true);
							btnPnl.getStopBrdbtn().setEnabled(false);
							btnPnl.getStartIntBrdbtn().setEnabled(true);	//enable interval broadcast
							intervalPanel.getBtnSetdescription().setEnabled(true);
							Constants.RegularBroadcast=false;					//toggle flag off
						    System.out.println("main frame Button Panel: " +name);	
							break;
							
						case "Open YouTube Live Streams":
							System.out.println("---------------------------------------");
							try {
								java.awt.Desktop.getDesktop().browse(new URI(Constants.LiveStreamUrl));
							} catch (IOException | URISyntaxException e) {
								e.printStackTrace();
							}
							System.out.println("main frame Button Panel: " +name);	
							break;
						case "Open YouTube Studio":
							System.out.println("---------------------------------------");
							try {
								java.awt.Desktop.getDesktop().browse(new URI(Constants.StudioUrl));
							} catch (IOException | URISyntaxException e) {
								e.printStackTrace();
							}
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
				case "Start": 
				
					checkedStreams = inputForm.getChecked();		// set input of checked streams to main frame
					controller.setCheckedStreams(checkedStreams);	// set input of checked streams to controller
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
						System.out.println("cancel regular broadcast");
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
		
		setSize(600, 700);
		setMinimumSize(new Dimension(600,700));
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){		//handle App close operation ,if live save corrent status
			public void windowClosing(WindowEvent e){
            	//save status of broadcast 
            	controller.saveData();
            	System.exit(0);
            }
            
        });
		
		setLocationRelativeTo(null);
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
	
	
	
}
