package com.youtube.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.controller.Controller;
import com.youtube.controller.Interval;
import com.youtube.controller.LiveBroadcastHandler;
import com.youtube.controller.LiveStreamsHandler;
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
	
	public MainFrame() throws IOException, ParseException
	{
		//----------------------INIT PANELS---------------------
		super("YABA");
		Controller controller = Controller.getInstance();
		ButtonPanel btnPnl = new ButtonPanel();
		loadUserData(btnPnl);
		initMainPane();
		IntervalPanel intervalPanel = initIntervalPanel();
		BroadcastPanel boradcastPanel = initBroadcastPanel();
		StreamPanel streamPanel = initStreamPanel();
		UserSettingsFrame userSetPanel = initUserSettingsFrame(); 
		JTabbedPane tabbedPane = initTabbedPane(streamPanel, boradcastPanel, userSetPanel);
		IntervalInputForm inputForm = initItervalInputForm();
		
		DescriptionFrame desFrame = initDescriptionFrame(controller.getStreamHandler());
		
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
	
	private void refreshGuiPanels(ButtonPanel btnPnl) throws IOException
	{
		 //refresh broadcast to active panel
		LiveBroadcastHandler broadcastHandler =  Controller.getInstance().getBroadcastsHandler();
    	String[] args = {"active", Constants.NumberOfResulsts, null};
    	broadcastHandler.refreshBroadcasts(args);
	    BroadcastPanel broadcastPanel = BroadcastPanel.getInstance();
		broadcastPanel.setData(broadcastHandler.getBroadcasts());			//set new data
	    broadcastPanel.refresh();
        
    	//toggle buttons on GUI
        System.out.println("toggleing buttons");
    	btnPnl.getStartIntBrdbtn().setVisible(false);
		btnPnl.getStopIntbtn().setVisible(true);
    	btnPnl.getStartIntBrdbtn().setEnabled(false); 
		btnPnl.getStopIntbtn().setEnabled(true);
		IntervalPanel.getInstance().getLblHello().setText("Hello "+ Constants.Username + ", you are live!");
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
				windowClosingEvent();
	        }
	    });     
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void windowClosingEvent()
	{
		//save status of broadcast
		String message= "Are you sure you want to Exit?",title="Log out";
		int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
		if(reply==JOptionPane.YES_OPTION) {
			try {
				Controller.getInstance().getUserDataHandler().saveData();   //save status of broadcast 
			} catch (SecurityException | IOException e1) {
				e1.printStackTrace();
			}
        	System.exit(0);
			
		}
	}
	
	private IntervalPanel initIntervalPanel()
	{
		IntervalPanel intervalPanel =  new IntervalPanel();
		IntervalPanel.setInstance(intervalPanel);
		return intervalPanel;
	}
	
	private BroadcastPanel initBroadcastPanel() throws IOException 
	{
		Controller controller = Controller.getInstance();
		BroadcastPanel boradcastPanel = new BroadcastPanel();
		boradcastPanel.setInstance(boradcastPanel);
		boradcastPanel.setData(controller.getBroadcastsHandler().getBroadcasts());
		boradcastPanel.resizeColumnWidth(boradcastPanel.getBroadcastTbl());
		return boradcastPanel;
	}
	
	private StreamPanel initStreamPanel() throws IOException 
	{
		Controller controller = Controller.getInstance();
		StreamPanel streamPanel =	StreamPanel.getInstance();
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
	
	private UserSettingsFrame initUserSettingsFrame() 
	{
		//init user settings frame
		UserSettingsFrame userSetPanel = new UserSettingsFrame();
		userSetPanel.getPrivacyComboBox().setLocation(153, 261);
		return userSetPanel;
	}
	
	private DescriptionFrame initDescriptionFrame(LiveStreamsHandler streamsHandler) throws IOException , ParseException
	{
		DescriptionFrame desFrame = new DescriptionFrame(streamsHandler);
		desFrame.setVisible(false);
		return desFrame;
	}
	
	private void setStreamPanelListener(StreamPanel streamPanel ,DescriptionFrame desFrame) 
	{
		streamPanel.setBtnListener(new ButtonListener() {		//set button listener for stream panel
			public void ButtonPressed(String name) {
				try{
					Constants.DebugPrint("main frame Stream Panel: " +name);
					switch(name){
						case "Refresh": handleRefreshPressed(); break;
						
						case Constants.addStream: handleAddStreamPressed(); break;	
						
						case Constants.removeStream: handleRemoveStreamPressed(); break;
							
						case Constants.setDescription: desFrame.setVisible(true); break;
					}
				} catch (IOException | HeadlessException | ParseException  e1) {
					e1.printStackTrace();
				}	
			}
			
			private void handleRefreshPressed() throws  IOException
			{
				Controller controller = Controller.getInstance();
				controller.getStreamHandler().refreshStreams();//request stream refresh 
				streamPanel.setData(controller.getStreamHandler().getStreams());	//set new data to table
				streamPanel.refresh();	
			}
			
			private void handleAddStreamPressed( )throws  IOException, ParseException
			{
				Controller controller = Controller.getInstance();
				controller.getStreamHandler().addStream();
				streamPanel.setData(controller.getStreamHandler().getStreams());	//set new data to table
				streamPanel.refresh();							//refresh table
				desFrame.refresh();
			}
			
			private void handleRemoveStreamPressed() throws IOException, ParseException
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
					Constants.DebugPrint("main frame Broadcast Panel: " + btnName );
					switch(btnName) {
						case "Filter": handleFilterPressed();	break;
						
						case "Next Page": handleNextPrevPressed(true); break;
						
						case "Previous Page":  handleNextPrevPressed(false); break;
						
						case "Update Description":	handleUpdateDescriptionPressed(); break;
							
					}
				} catch (IOException | ParseException e1) { //UnsupportedEncodingException
					e1.printStackTrace();
				}
			}
			
			private void handleFilterPressed() throws IOException, ParseException
			{
				Controller controller = Controller.getInstance(); 
				String[] args = {boradcastPanel.getSelected(), Constants.NumberOfResulsts, null};	 //create args
				controller.getBroadcastsHandler().refreshBroadcasts(args); 								//request refresh
				boradcastPanel.setData(controller.getBroadcastsHandler().getBroadcasts());			   //set new data
				boradcastPanel.refresh();	
			}
			
			private void handleNextPrevPressed(boolean isNext)throws IOException, ParseException
			{
				String pageToken = isNext ?  Constants.NextPageToken : Constants.PrevPageToken;
				Controller controller = Controller.getInstance();
				String[] args = {boradcastPanel.getSelected(), Constants.NumberOfResulsts, pageToken};	//create args
				controller.getBroadcastsHandler().refreshBroadcasts(args);
				boradcastPanel.setData(controller.getBroadcastsHandler().getBroadcasts());			//set new data
				boradcastPanel.refresh();	
			}
			
			private void handleUpdateDescriptionPressed() throws IOException, ParseException
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
			public void ButtonPressed(String name){
				try {
					Constants.DebugPrint("main frame Button Panel: " +name);
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
							
						case "Exit": handleExit(); break;
					}
				} catch (URISyntaxException | IOException | ParseException | InterruptedException | SecurityException e1) {
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
			
			private void handleStartLiveBroadcast() throws IOException, ParseException
			{
				Controller controller = Controller.getInstance();
				inputForm.setData(controller.getStreamHandler().filterStreams("active"));  //set active streams to form
				inputForm.refresh();
				inputForm.setVisible(true);		
			}
			
			private void handleStopLiveBroadcast() throws IOException, ParseException, InterruptedException
			{
				Controller controller = Controller.getInstance();
				String message= "Stop Live broadcasts?",
						title="Stop Live broadcasts";
				int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_OPTION);
				if(reply!=JOptionPane.YES_OPTION) {
					return;
				}
				ArrayList<String> currentlyLive = controller.getBroadcastsHandler().getCurrentlyLive();
				if(Constants.IntervalBroadcast) {
					Constants.IntervalBroadcast = false;		
					controller.getBroadcastsHandler().cancelTimerRunner();
				}
				else if(currentlyLive!= null && !currentlyLive.isEmpty()){
					Constants.RegularBroadcast = false;
					controller.getBroadcastsHandler().stopBroadcasts(currentlyLive);
				}
				
				handleButtons();
				Constants.State = "Completing";
			}
			
			private void handleButtons()
			{
				btnPnl.getStartIntBrdbtn().setVisible(true);
				btnPnl.getStopIntbtn().setVisible(false);
				btnPnl.getStopIntbtn().setEnabled(false);
				btnPnl.getStartIntBrdbtn().setEnabled(true);
			}
			
			private void handleExit() throws IOException , URISyntaxException , ParseException {
				windowClosingEvent();
			}
		});
	}
	
	private void setInputFormListener(IntervalInputForm inputForm, IntervalPanel intervalPanel, ButtonPanel btnPnl)
	{
		inputForm.setBtnListener(new ButtonListener() {			//set button listener for input form
			public void ButtonPressed(String btnName) {
				try {
					Constants.DebugPrint("input form frame: " +btnName);
					switch(btnName) {
						case "Set": handleSetPressed(); break;
							
						case "Refresh": handleRefreshPressed(); break;
							
						case "Start": handleStartPressed(); break;
							
						case "Cancel": handleCancelPressed(); break;
					}
				} catch (IOException e1) {
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
			
			private void handleRefreshPressed() throws IOException 
			{
				Controller controller = Controller.getInstance();
				controller.getStreamHandler().refreshStreams();
				inputForm.setData(controller.getStreamHandler().filterStreams("active"));//set active streams to form
				inputForm.refresh();
			}
			
			private void handleStartPressed() throws IOException 
			{
				Controller controller = Controller.getInstance();
				if(checkSelectedStreams()) {//check that at least one stream is active and less then 10 were chosen
					inputForm.setSelected((String) inputForm.getBox().getSelectedItem());
					inputForm.getBox().setSelectedItem(inputForm.getSelected());
					controller.getStreamHandler().setCheckedStreams(inputForm.getChecked());	// set input of checked streams to controller
						// start broadcasting according to pressed button
					if(inputForm.getSelected().equals("Non-Stop")) {
						Constants.RegularBroadcast = true;
					}
					else {
						Constants.IntervalBroadcast = true;
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
					handleIntervalPanelOnStart();
					LiveBroadcastHandler broadcastHandler = Controller.getInstance().getBroadcastsHandler();
					Constants.State = "Starting";
					broadcastHandler.startBroadcast();	//start initial live Broadcasts
					handleButtonPanel() ;
					inputForm.setVisible(false);		//close input form and Prompt chosen interval to interval panel						
						
				} catch (InterruptedException | SecurityException | IOException e) {
					e.printStackTrace();
				}
			}
			
			private void handleIntervalPanelOnStart() throws InterruptedException, IOException 
			{
				if(Constants.IntervalBroadcast) {
					Interval interval = Interval.getInstance();
					interval.setInterval(inputForm.getSelected());				//set selected interval length
					Controller.getInstance().getBroadcastsHandler().startTimerRunner();			//start timer runner instance
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
			}
			
			private void handleButtonPanel() 
			{
				// toggle GUI buttons 
				btnPnl.getStartIntBrdbtn().setVisible(false);
				btnPnl.getStopIntbtn().setVisible(true);
				btnPnl.getStartIntBrdbtn().setEnabled(false); 
				btnPnl.getStopIntbtn().setEnabled(true);
			}
			
			private boolean checkSelectedStreams() {
				//if no stream was selected toggle option pane and ask to refresh streams
				if(inputForm != null && inputForm.getChecked().length < 1) {
					String message= "No active streams were chosen for Broadcast, Please choose at least one Stream",
							title="No Streams Chosen";
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.OK_OPTION);
					return false;
				}
				if(inputForm != null && inputForm.getChecked().length > Constants.MAX_LIVEBROADCASTS) {
					String message= "Please select less then "+ Constants.MAX_LIVEBROADCASTS +" broadcasts",
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
				Constants.DebugPrint("des  frame : " + btnName);
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
				Constants.DebugPrint("user Settings frame : " +btnName);
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
	
	private void loadUserData(ButtonPanel btnPnl) throws IOException {
		Controller.getInstance().getUserDataHandler().loadUserSettings();
		if(Constants.saveState) {
			if(Controller.getInstance().getUserDataHandler().loadUserState()) {
				Constants.DebugPrint("loaded user state");
				refreshGuiPanels(btnPnl);
			}
		}
		Constants.DebugPrint("user state load not enabled");
	}
		
}
