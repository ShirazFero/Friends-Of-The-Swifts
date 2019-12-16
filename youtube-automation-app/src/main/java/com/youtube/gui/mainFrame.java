package com.youtube.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.controller.Controller;
import com.youtube.controller.Interval;
import com.youtube.utils.Constants;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Main frame of GUI
 * this class handles all button listeners of all panels of the frame
 * each button pressed method triggers the designated method that performs the requested option
 * @author Evgeny Geyfman
 *
 */
public class mainFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private Boolean[] checkedStreams; 
	
	private Boolean[] checkedBroadcasts; 
	
	public mainFrame() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ParseException, InvalidAlgorithmParameterException {

	//----------------------INIT PANELS---------------------
		
		super("Control Panel");
		
		Controller controller = Controller.getInstance();
		
		getContentPane().setLayout(null);
	
		//init new interval panel
		IntervalPanel intervalPanel =  new IntervalPanel();
		intervalPanel.getSettings().setSize(97, 29);
		intervalPanel.getSettings().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		intervalPanel.getSettings().setLocation(170, 147);
		intervalPanel.getBtnLogOut().setLocation(277, 147);
		intervalPanel.getLblNotSet().setLocation(84, 55);
		intervalPanel.setSize(394, 187);
		intervalPanel.setLocation(255, 45);
		IntervalPanel.setInstance(intervalPanel);
		
		//init new stream panel
		StreamPanel streamPanel = new StreamPanel();
		streamPanel.getBtnSetDescription().setLocation(10, 22);
		streamPanel.getJsp().setLocation(10, 68);
		streamPanel.setLocation(255, 229);
		streamPanel.setSize(394, 214);
		streamPanel.setData(controller.getStreams());
		streamPanel.resizeColumnWidth(streamPanel.getStreamsTbl());
		
		//init new broadcast panel
		BroadcastPanel boradcastPanel = new BroadcastPanel();
		boradcastPanel.getComboBox1().setLocation(183, 221);
		boradcastPanel.getBtnNextPage().setLocation(281, 221);
		boradcastPanel.getBtnPreviousPage().setLocation(10, 221);
		boradcastPanel.setInstance(boradcastPanel);
		boradcastPanel.getScrollPane().setSize(378, 159);
		boradcastPanel.getScrollPane().setLocation(10, 51);
		boradcastPanel.setLocation(255, 441);
		boradcastPanel.setData(controller.getBroadcasts());
		boradcastPanel.resizeColumnWidth(boradcastPanel.getBroadcastTbl());
		boradcastPanel.setSize(394, 252);

		getContentPane().add(intervalPanel);
		getContentPane().add(boradcastPanel);
		getContentPane().add(streamPanel);
		//init new interval input form
		IntervalInputForm inputForm = IntervalInputForm.getInstance();
		inputForm.setVisible(false);
		
		//set welcome label
		JLabel welcomeLabel= new JLabel("Welcome to Broadcast Control Panel",SwingConstants.CENTER);
		welcomeLabel.setLocation(41, 11);
		welcomeLabel.setSize(527, 28);
		welcomeLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		getContentPane().add(welcomeLabel);
		
		//init new button panel
		ButtonPanel btnPnl = new ButtonPanel();
		btnPnl.setInstance(btnPnl);
		btnPnl.setSize(258, 648);
		btnPnl.setLocation(0, 45);
		getContentPane().add(btnPnl,BorderLayout.WEST);
		
		//init description frame
		DescriptionFrame desFrame = new DescriptionFrame();
		desFrame.setVisible(false);
		
		//init user settings frame
		UserSettingsFrame userSetFrame =new UserSettingsFrame();
		userSetFrame.setVisible(false);
		
		loadUserData();
		
		//--------------Adding button listeners to panels------------
		
		intervalPanel.setBtnListener(new ButtonListener() {
			@Override
			public void ButtonPressed(String btnName) {
				// TODO Auto-generated method stub
				switch(btnName) {
				
				case "Log out":
								dispose();
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
								System.out.println("interval panel "+ btnName);
								break;
				case "Settings":
								userSetFrame.setVisible(true);
								System.out.println("interval panel "+ btnName);
								break;
				}
			}
		});
		
		boradcastPanel.setBtnlistener(new ButtonListener() {	//set button listener for broadcast panel
			public void ButtonPressed(String btnName) {
				if(btnName!=null) {
					switch(btnName) {
						case "Filter":
								String[] args = {"refresh",boradcastPanel.getSelected(),null,null};	//create args
								controller.refreshBroadcasts(args);							//request refresh
								boradcastPanel.setData(controller.getBroadcasts());			//set new data
								boradcastPanel.refresh();									//refresh table
								System.out.println("main frame Broadcast Panel: " +btnName);
								break;
								
						case "Next Page":
								String[] args1 = {"refresh",boradcastPanel.getSelected(),Constants.NextPageToken,null};	//create args
								controller.refreshBroadcasts(args1);							//request refresh
								boradcastPanel.setData(controller.getBroadcasts());			//set new data
								boradcastPanel.refresh();									//refresh table
								System.out.println("main frame Broadcast Panel: " +btnName);
								break;
						case "Previous Page":
								
								String[] args2 = {"refresh",boradcastPanel.getSelected(),null,Constants.PrevPageToken};	//create args
								controller.refreshBroadcasts(args2);							//request refresh
								boradcastPanel.setData(controller.getBroadcasts());			//set new data
								boradcastPanel.refresh();									//refresh table
								System.out.println("main frame Broadcast Panel: " +btnName);
								break;
						
						case "Update Description":	//this button doesn't do anything yet , prints selected broadcasts
								
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
								try {
									if(decription == null) {
										System.out.println("requset cancelled");
										return;
									}
									if("".equals(decription)) {
										JOptionPane.showMessageDialog(null,
												"No description entered",
								                "Not Completed",
								                JOptionPane.ERROR_MESSAGE);
										break;
									}
									int desLen = decription.getBytes("UTF-8").length;
									System.out.println("deslen: "+ desLen);
									if(desLen>5000) {
										//bad input
										JOptionPane.showMessageDialog(null,
												" Description is too long",
								                "Not Completed",
								                JOptionPane.ERROR_MESSAGE);
										break;
									}
								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								try {
									controller.updateDescription(decription);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
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
						
						case Constants.addStream:
							System.out.println("main frame Stream Panel: " +name);
							try {
								controller.addStream();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							//request add refresh 
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							break;	
						
						case Constants.removeStream:	
							System.out.println("main frame Stream Panel: " +name);
							checkedStreams= streamPanel.getChecked();		//get input of checked streams
							int emtpyCounter = 0;
							for(int i=0;i<checkedStreams.length;i++) {
								if(!checkedStreams[i])
									emtpyCounter++;
							}
							if(emtpyCounter == checkedStreams.length) {
								JOptionPane.showMessageDialog(null,
										"No streams were selected",
						                "Not Completed",
						                JOptionPane.ERROR_MESSAGE);
								break;
							}
								
							try {
								controller.removeStream(checkedStreams);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}		//request add refresh 
							streamPanel.setData(controller.getStreams());	//set new data to table
							streamPanel.refresh();							//refresh table
							break;
						case Constants.setDescription: //set description to desired Stream
							checkedStreams= streamPanel.getChecked();		//get input of checked streams
							desFrame.setVisible(true);
							System.out.println("main frame Stream Panel: " +name);
							break;
						default: System.out.println("main frame Stream Panel: " +name);
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
							streamPanel.getBtnSetDescription().setEnabled(false); 
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
							streamPanel.getBtnSetDescription().setEnabled(true); 
							intervalPanel.getSettings().setEnabled(true);
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
							try {
								controller.stopBroadcasts();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							btnPnl.getStartBrdbtn().setEnabled(true);
							btnPnl.getStopBrdbtn().setEnabled(false);
							btnPnl.getStartIntBrdbtn().setEnabled(true);	//enable interval broadcast
							streamPanel.getBtnSetDescription().setEnabled(true);
							intervalPanel.getSettings().setEnabled(true);
							Constants.RegularBroadcast=false;					//toggle flag off
						    System.out.println("main frame Button Panel: " +name);	
							break;
							
						case Constants.OYLS: // "Open YouTube Live Streams
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
					streamPanel.getBtnSetDescription().setEnabled(true);	
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
				streamPanel.getBtnSetDescription().setEnabled(false);
				intervalPanel.getSettings().setEnabled(false);
				
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
				streamPanel.getBtnSetDescription().setEnabled(false);
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
					intervalPanel.getSettings().setEnabled(false);
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
		
		desFrame.setBtnListener(new ButtonListener() {
			
			@Override
			public void ButtonPressed(String btnName) {
				switch(btnName) {
				case "OK": 
					
					//controller.setStreamDescription(checkedStreams, desFrame.getText());
					desFrame.setVisible(false);
					System.out.println("des  frame : " +btnName);
					break;
				case "Cancel":  
					desFrame.setVisible(false); 
					System.out.println("des  frame : " +btnName);
					break;
				}
			}
		});
		
		userSetFrame.setBtnListener(new ButtonListener() {
			
			@Override
			public void ButtonPressed(String btnName) {
				switch(btnName) {
				case "OK": 
						userSetFrame.setVisible(false);
						System.out.println("user Settings frame : " +btnName);
						break;
				case "Apply":  
						Constants.Format  = (String) userSetFrame.getFormatcomboBox().getSelectedItem();
						Constants.IngetionType = (String) userSetFrame.getIngestionComboBox().getSelectedItem();
						Constants.Privacy   = (String) userSetFrame.getPrivacyComboBox().getSelectedItem();
						//System.out.println(Constants.Privacy+" " + Constants.IngetionType+" " +Constants.Format);
						JOptionPane.showMessageDialog(null,"Setting Updated Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);	
						System.out.println("user Settings frame : " +btnName);
						break;
				}
			}
		});
		
		setSize(700, 700);
		setMinimumSize(new Dimension(675, 730));
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
	
	@SuppressWarnings("unchecked")
	private void loadUserData() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		// TODO Auto-generated method stub
		//System.out.println(Constants.UserDataPath+ Constants.Username + ".json");
		
		Controller controller = Controller.getInstance();
		final Path path = Paths.get(Constants.UserDataPath+ Constants.Username + ".json");
		 
		if(Files.exists(path)) {     	//check if there's a file of saved data
			System.out.println("here if");
			if(!controller.getBroadcasts().isEmpty()) {	//if there's active broadcasts
				String message= "Do you want to resume saved Broadcast?"
						 ,title="Resume Broadcast";
				int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.CANCEL_OPTION);
				if(reply==JOptionPane.YES_OPTION) {
					controller.loadData();	//load it
				}
			}
			else { 	// else load only the description
				JSONParser parser = new JSONParser();
			 
	            Object obj = parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
	            		
	            JSONObject jsonObject = (JSONObject) obj;
	            Constants.StreamDescription = (HashMap<String,String>) jsonObject.get("Map");
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
    		Constants.StreamDescription =new HashMap<String,String>();
    		JSONObject MapObject = new JSONObject(Constants.StreamDescription);
        	obj.put("Map" ,MapObject);
    		for(LiveStream stream: controller.getStreams()) {
    			Constants.StreamDescription.put(stream.getId(), Constants.Description);
    		}
    		
    		try (FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json")) {
    			file.write(obj.toJSONString());
    			System.out.println("Successfully created first JSON Object File...");
    			System.out.println("\nJSON Object: " + obj);
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}
		}
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
