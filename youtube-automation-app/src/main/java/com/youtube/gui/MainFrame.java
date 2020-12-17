package com.youtube.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
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
import javax.swing.WindowConstants;

import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.controller.AppMain;
import com.youtube.controller.Controller;
import com.youtube.controller.Interval;
import com.youtube.controller.UserDataHandler;
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
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 6609103195472306740L;
	
	public MainFrame() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, ParseException, InvalidAlgorithmParameterException 
	{
		//----------------------INIT PANELS---------------------
		super("YABA");
		
		loadUserData();
		initMainPane();
		IntervalPanel intervalPanel = initIntervalPanel();
		BroadcastPanel boradcastPanel = initBroadcastPanel();
		StreamPanel streamPanel = initStreamPanel();
		UserSettingsFrame userSetPanel = initUserSettingsFrame(); 
		JTabbedPane tabbedPane = initTabbedPane(streamPanel, boradcastPanel, userSetPanel);
		IntervalInputForm inputForm = initItervalInputForm();
		ButtonPanel btnPnl = initButtonPanel();
		DescriptionFrame desFrame = initDescriptionFrame();
		
		getContentPane().add(tabbedPane);
		getContentPane().add(intervalPanel);
		getContentPane().add(btnPnl);
		
		//--------------Adding listeners to panels------------
		setStreamPanelListener(streamPanel,desFrame);
		setBroadcastPanelListener(boradcastPanel);
		setButtonPanelListener(btnPnl,inputForm);
		setInputFormListener(inputForm,intervalPanel,btnPnl);
		setDescriptionFrameListener(desFrame);
		setUserSettingPanelListener(userSetPanel);
	}		
		
	private void initMainPane()
	{
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/YABAWB.png")));
		getContentPane().setBackground(new Color(255, 255, 255));
		setBackground(SystemColor.textHighlightText);
		getContentPane().setLayout(null);
		setSize(675, 665);
		setMinimumSize(new Dimension(675, 660));
		setMaximumSize(new Dimension(675, 660));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){		//handle App close operation ,if live save corrent status
			public void windowClosing(WindowEvent e){
	        	//save status of broadcast
				String message= "Are you sure you want to Exit?",title="Log out";
				int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
				if(reply==JOptionPane.YES_OPTION) {
					try {
						UserDataHandler.getInstance().saveData();   //save status of broadcast 
					} catch (SecurityException | IOException e1) {
						e1.printStackTrace();
					}
		        	System.exit(0);
					
				}
				return;
	        }
	    });     
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private IntervalPanel initIntervalPanel()
	{
		IntervalPanel intervalPanel =  new IntervalPanel();
		IntervalPanel.setInstance(intervalPanel);
		return intervalPanel;
	}
	
	private BroadcastPanel initBroadcastPanel() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
	{
		Controller controller = Controller.getInstance();
		BroadcastPanel boradcastPanel = new BroadcastPanel();
		boradcastPanel.setInstance(boradcastPanel);
		boradcastPanel.setData(controller.getBroadcastsHandler().getBroadcasts());
		boradcastPanel.resizeColumnWidth(boradcastPanel.getBroadcastTbl());
		return boradcastPanel;
	}
	
	private StreamPanel initStreamPanel() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException 
	{
		Controller controller = Controller.getInstance();
		StreamPanel streamPanel = new StreamPanel();
		streamPanel.setData(controller.getStreamHandler().getStreams());
		streamPanel.resizeColumnWidth(streamPanel.getStreamsTbl());
		return streamPanel;
	}
	
	private JTabbedPane initTabbedPane(StreamPanel streamPanel, BroadcastPanel boradcastPanel,UserSettingsFrame userSetPanel) 
	{
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(SystemColor.textHighlightText);
		tabbedPane.setBorder(null);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 20));
		tabbedPane.add("Live Broadcasts",boradcastPanel);
		tabbedPane.add("Live Streams",streamPanel);
		tabbedPane.add("Settings",userSetPanel);
		tabbedPane.setBounds(255, 216, 400, 399);
		return tabbedPane;
	}
	
	private IntervalInputForm initItervalInputForm()
	{
		//init new interval input form
		IntervalInputForm inputForm = IntervalInputForm.getInstance();
		inputForm.setVisible(false);
		return inputForm;
	}
	
	private ButtonPanel initButtonPanel()
	{
		//init new button panel
		ButtonPanel btnPnl = new ButtonPanel();
		btnPnl.setInstance(btnPnl);
		return btnPnl;
	}
	
	private UserSettingsFrame initUserSettingsFrame() 
	{
		//init user settings frame
		UserSettingsFrame userSetPanel = new UserSettingsFrame();
		userSetPanel.getPrivacyComboBox().setLocation(153, 261);
		return userSetPanel;
	}
	
	private DescriptionFrame initDescriptionFrame() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException
	{
		DescriptionFrame desFrame = new DescriptionFrame();
		desFrame.setVisible(false);
		return desFrame;
	}
	
	private void setStreamPanelListener(StreamPanel streamPanel ,DescriptionFrame desFrame) 
	{
		streamPanel.setBtnListener(new ButtonListener() {		//set button listener for stream panel
			public void ButtonPressed(String name) {
				try{
					if(Constants.Debug) {
						System.out.println("main frame Stream Panel: " +name);
					}
					switch(name){
						case "Refresh": handleRefreshPressed(); break;
						
						case Constants.addStream: handleAddStreamPressed(); break;	
						
						case Constants.removeStream: handleRemoveStreamPressed(); break;
							
						case Constants.setDescription: desFrame.setVisible(true); break;
					}
				} catch (IOException | HeadlessException | ParseException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException  e1) {
					e1.printStackTrace();
				}	
			}
			
			private void handleRefreshPressed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
			{
				Controller controller = Controller.getInstance();
				controller.getStreamHandler().refreshStreams();//request stream refresh 
				streamPanel.setData(controller.getStreamHandler().getStreams());	//set new data to table
				streamPanel.refresh();	
			}
			
			private void handleAddStreamPressed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, HeadlessException, ParseException
			{
				Controller controller = Controller.getInstance();
				controller.getStreamHandler().addStream();
				streamPanel.setData(controller.getStreamHandler().getStreams());	//set new data to table
				streamPanel.refresh();							//refresh table
				desFrame.refresh();
			}
			
			private void handleRemoveStreamPressed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException
			{
				Controller controller = Controller.getInstance();
				String[] streams = streamPanel.getChecked();		//get input of checked streams
				if(streams.length<1) {
					JOptionPane.showMessageDialog(null,"No streams were selected","Not Completed",
			                JOptionPane.ERROR_MESSAGE);
					return;
				}
				String message= "Are you sure you want to remove those Streams?",title="Log out";
				int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_OPTION);
				if(reply!=JOptionPane.OK_OPTION) {
					return;
				}
				controller.getStreamHandler().removeStream(streams);//request add refresh	
				controller.getStreamHandler().refreshStreams();
				streamPanel.setData(controller.getStreamHandler().getStreams());	//set new data to table
				streamPanel.refresh();							//refresh table
				desFrame.refresh();
			}
			
		});
	}
	
	private void setBroadcastPanelListener(BroadcastPanel boradcastPanel)
	{
		boradcastPanel.setBtnlistener(new ButtonListener() {	//set button listener for broadcast panel
			public void ButtonPressed(String btnName) {
				try {
					if(Constants.Debug) {
						System.out.println("main frame Broadcast Panel: " +btnName);
					}
					switch(btnName) {
						case "Filter": handleFilterPressed();	break;
						
						case "Next Page": handleNextPrevPressed(true); break;
						
						case "Previous Page":  handleNextPrevPressed(false); break;
						
						case "Update Description":	handleUpdateDescriptionPressed(); break;
							
					}
				} catch (  IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException  e1) { //UnsupportedEncodingException
					e1.printStackTrace();
				}
			}
			
			private void handleFilterPressed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
			{
				Controller controller = Controller.getInstance();
				String[] args = {"refresh",boradcastPanel.getSelected(),null,null};	//create args
				controller.getBroadcastsHandler().refreshBroadcasts(args); 						//request refresh
				boradcastPanel.setData(controller.getBroadcastsHandler().getBroadcasts());			//set new data
				boradcastPanel.refresh();	
			}
			
			private void handleNextPrevPressed(boolean isNext) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
			{
				String pageToken = isNext ?  Constants.NextPageToken : Constants.PrevPageToken;
				Controller controller = Controller.getInstance();
				String[] args = {"refresh",boradcastPanel.getSelected(),null,pageToken};	//create args
				controller.getBroadcastsHandler().refreshBroadcasts(args);
				boradcastPanel.setData(controller.getBroadcastsHandler().getBroadcasts());			//set new data
				boradcastPanel.refresh();	
			}
			
			private void handleUpdateDescriptionPressed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
			{
				Controller controller = Controller.getInstance();
				Boolean[] checkedBroadcasts = boradcastPanel.getChecked();
				controller.getBroadcastsHandler().setCheckedBroadcasts(checkedBroadcasts);
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
					return;
				}
				String decription = JOptionPane.showInputDialog("please enter Description");
				if(decription == null) {
					System.out.println("requset cancelled");
					return;
				}
				controller.getBroadcastsHandler().updateDescription(decription);
			}
			
		});	
	}
	
	private void setButtonPanelListener(ButtonPanel btnPnl, IntervalInputForm inputForm)
	{
		btnPnl.setBtnListener(new ButtonListener() {	//set button listener for button panel
			public void ButtonPressed(String name) {
				try {
					if(Constants.Debug) {
						System.out.println("main frame Button Panel: " +name);
					}
					switch(name) {
						case "<html>Set<br>Interval</html>": 
							  handleSetIntervalPresssed(); 
							  break;
							
						case "<html>Start Live<br>Broadcast</html>":	 
							 handleStartLiveBroadcast();				
							break;

						case "<html>Stop Live<br>Broadcast</html>":					//stop interval broadcast
							handleStopLiveBroadcast();
							break;
						
						case "<html>Live<br>Manager</html>": // "Open YouTube Live Streams
							java.awt.Desktop.getDesktop().browse(new URI(Constants.LiveStreamUrl));
							break;
							
						case "<html>YouTube<br>Studio</html>":
							java.awt.Desktop.getDesktop().browse(new URI(Constants.StudioUrl));
							break;
							
						case "Log Out": handleLogOut(); break;
					}
				} catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException |
						URISyntaxException | InvalidAlgorithmParameterException e1) {
					e1.printStackTrace();
				}
			}
			
			private void  handleSetIntervalPresssed()
			{
				Constants.SetInterval=true;
				inputForm.getJsp().setVisible(false);
				inputForm.getBtnRefresh().setVisible(false);
				inputForm.getBtnOk().setText("Set");
				inputForm.setVisible(true);	
				inputForm.getBtnOk().setBounds(48, 46, 89, 23);
				inputForm.getBtnCancel().setBounds(156, 46, 89, 23);
				inputForm.setSize(462,150);
			}
			
			private void handleStartLiveBroadcast() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
			{
				Controller controller = Controller.getInstance();
				inputForm.setData(controller.getStreamHandler().filterStreams("active"));  //set active streams to form
				inputForm.refresh();
				inputForm.setVisible(true);		
			}
			
			private void handleStopLiveBroadcast() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, InterruptedException 
			{
				Controller controller = Controller.getInstance();
				String message= "Stop Live broadcasts?",
						title="Stop Live broadcasts";
				int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_OPTION);
				if(reply!=JOptionPane.YES_OPTION) {
					return;
				}
				if(Constants.IntervalBroadcast) {
					Constants.IntervalBroadcast=false;			//toggle flag off
					controller.cancelTimerRunner();
				}
				else if(Constants.LiveId!=null && !Constants.LiveId.isEmpty()){
					Constants.RegularBroadcast=false;
					String[] brdID = new String[Constants.LiveId.size()]; 
					Constants.LiveId.toArray(brdID);
					controller.getBroadcastsHandler().stopBroadcasts(brdID);
				}
				btnPnl.getStartIntBrdbtn().setVisible(true);
				btnPnl.getStopIntbtn().setVisible(false);
				btnPnl.getStopIntbtn().setEnabled(false);
				btnPnl.getStartIntBrdbtn().setEnabled(true);
				Constants.State = "Completing";
			}
			
			private void handleLogOut() throws IOException {
				String message = "Are you sure you want to log out?";
				String title="Log out";
				int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_OPTION);
				if(reply != JOptionPane.OK_OPTION) {
					return;
				}
				dispose();
				AppMain.main(null);
			}
		});
	}
	
	private void setInputFormListener(IntervalInputForm inputForm, IntervalPanel intervalPanel, ButtonPanel btnPnl)
	{
		inputForm.setBtnListener(new ButtonListener() {			//set button listener for input form
			public void ButtonPressed(String btnName) {
				try {
					if(Constants.Debug) {
						System.out.println("input form frame: " +btnName);
					}
					switch(btnName) {
						case "Set": handleSetPressed(); break;
							
						case "Refresh": handleRefreshPressed(); break;
							
						case "Start": handleStartPressed(); break;
							
						case "Cancel": handleCancelPressed(); break;
					}
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException e1) {
					e1.printStackTrace();
					ErrorHandler.HandleUnknownError(e1.toString());
				}
			}
			
			private boolean intervalCantBeSet() 
			{
				return Constants.RegularBroadcast || 
				(Constants.IntervalBroadcast && inputForm.getSelected().equals("Non-Stop"));
			}
			
			private void setNewInterval() 
			{
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
			
			private void refreshIntervalForm()
			{
				inputForm.getBtnOk().setText("Start");
				Constants.SetInterval = false;
				inputForm.getBtnRefresh().setVisible(true);
				inputForm.setVisible(false);
				inputForm.getJsp().setVisible(true);
				inputForm.getBtnOk().setBounds(20, 212, 89, 23);
				inputForm.getBtnCancel().setBounds(123, 212, 89, 23);
				inputForm.setSize(462, 307);
			}
			
			private void handleSetPressed() 
			{
				String previousSelection = inputForm.getSelected();
				inputForm.setSelected((String) inputForm.getBox().getSelectedItem());
				if(intervalCantBeSet()) { //from interval 
					 JOptionPane.showMessageDialog(null,"Option not possible during interval broadcast",
								"Request problem",JOptionPane.ERROR_MESSAGE);
					 inputForm.setSelected(previousSelection);
					 inputForm.getBox().setSelectedItem(previousSelection);
				}
				else {
					setNewInterval(); 
				}
				refreshIntervalForm();
			}
			
			private void handleRefreshPressed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException 
			{
				Controller controller = Controller.getInstance();
				controller.getStreamHandler().refreshStreams();
				inputForm.setData(controller.getStreamHandler().filterStreams("active"));//set active streams to form
				inputForm.refresh();
			}
			
			private void handleStartPressed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException 
			{
				Controller controller = Controller.getInstance();
				if(checkSelectedStreams()) {//check that at least one stream is active and less then 10 were chosen
					inputForm.setSelected((String) inputForm.getBox().getSelectedItem());
					inputForm.getBox().setSelectedItem(inputForm.getSelected());
					controller.getStreamHandler().setCheckedStreams(inputForm.getChecked());	// set input of checked streams to controller
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
			}
			
			private void handleCancelPressed() {
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
			}	
				/**
				 * helper method starts interval/regular broadcast by controller,
				 * sets selected streams to controller, starts first live broadcasts
				 * starts Timer runner instance if needed
				 */
			private void startBroadcast() {
				try {	
					Controller controller = Controller.getInstance();
					if(Constants.IntervalBroadcast) {
						Interval interval = Interval.getInstance();
						interval.setInterval(inputForm.getSelected());		//set selected interval length
						controller.startTimerRunner();			//start timer runner instance
						// Prompt interval start stop times to interval panel
						String startTime = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()).toString();
						String stoptime = interval.getCorrentInterval().toString();
						intervalPanel.updateIntervalPanel(startTime, stoptime);
						intervalPanel.getLblstime().setVisible(true);
						intervalPanel.getFtime().setVisible(true);
						intervalPanel.getLblNotSet().setText(interval.getHours() + " Hours and "	
						+ interval.getMinutes() +" minutes");
					}
					else {
						intervalPanel.getLblNotSet().setText(inputForm.getSelected());
					}
					controller.getBroadcastsHandler().startBroadcast();	//start initial live Broadcasts
					// toggle GUI buttons 
					btnPnl.getStartIntBrdbtn().setVisible(false);
					btnPnl.getStopIntbtn().setVisible(true);
					btnPnl.getStartIntBrdbtn().setEnabled(false); 
					btnPnl.getStopIntbtn().setEnabled(true);
					Constants.State = "Starting";
					
					//close input form and Prompt chosen interval to interval panel
					inputForm.setVisible(false);								
						
				} catch (InterruptedException | SecurityException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IOException e) {
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
	}
	
	private void setDescriptionFrameListener(DescriptionFrame desFrame)
	{
		desFrame.setBtnListener(new ButtonListener() {
			@Override
			public void ButtonPressed(String btnName) {
				if(Constants.Debug) {
					System.out.println("des  frame : " +btnName);
				}
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
	}
	
	private void setUserSettingPanelListener(UserSettingsFrame userSetPanel)
	{
		userSetPanel.setBtnListener(new ButtonListener() {
			@Override
			public void ButtonPressed(String btnName) {
				if(Constants.Debug) {
					System.out.println("user Settings frame : " +btnName);
				}
				switch(btnName) {
					case "OK": userSetPanel.setVisible(false); break;
						
					case "Apply":  handleApplyPressed(); break;
				}
			}
			
			private void handleApplyPressed() 
			{
				Constants.Format  = (String) userSetPanel.getFormatcomboBox().getSelectedItem();
				userSetPanel.getFormatcomboBox().setSelectedItem(Constants.Format);
				Constants.IngestionType = (String) userSetPanel.getIngestionComboBox().getSelectedItem();
				userSetPanel.getIngestionComboBox().setSelectedItem(Constants.IngestionType);
				Constants.Privacy   = (String) userSetPanel.getPrivacyComboBox().getSelectedItem();
				userSetPanel.getPrivacyComboBox().setSelectedItem(Constants.Privacy);
				JOptionPane.showMessageDialog(null,"Setting Updated Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	private void loadUserData() throws SecurityException, IOException {
		UserDataHandler handler = UserDataHandler.getInstance();
		handler.loadUserSettings();
		if(Constants.saveState) {
			handler.loadUserState();
			if(Constants.Debug) {
				System.out.println("loaded user state");
			}
		}
		else if(Constants.Debug){
			System.out.println("user state load not enabled");
		}
	}
		
}
