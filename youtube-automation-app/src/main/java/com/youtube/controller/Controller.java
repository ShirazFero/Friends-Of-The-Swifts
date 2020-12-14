package com.youtube.controller;

import java.awt.HeadlessException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;
import com.youtube.api.CompleteBroadcast;
import com.youtube.api.CreateBroadcast;
import com.youtube.api.ListPoll;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.ButtonPanel;
import com.youtube.gui.IntervalPanel;
import com.youtube.gui.ProgressFrame;
import com.youtube.utils.Constants;

/**
 * This class handles data flow from the API to the GUI and backwards,
 * all it's functions control API requests , also it temporarily holds
 * the data that is being transported to the GUI.  
 * @author Evgeny Geyfman
 *
 */
public class Controller {
	
	private List<LiveStream> streams;			//holds currently presented streams
	
	private List<LiveBroadcast> broadcasts;		//holds currently presented broadcasts
	
	private TimerRunner timerRunner;			//holds current timer runner
	
	private  String[] checkedStreams;			//holds checked streams from inputform

	private  Boolean[] checkedBroadcasts;		//holds checked broadcasts from inputform
	
	private static Controller instance;			//singleton instance
	
	/**
	 * Gets checked stream
	 * @return
	 */
	public String[] getCheckedStreams() {
		return checkedStreams;
	}

	/**
	 * Sets checked streams from Interval input form
	 * @param checkedStreams
	 */
	public void setCheckedStreams(String[] checkedStreams) {
		this.checkedStreams = checkedStreams;
	}
	
	/**
	 * Sets checked broadcasts from broadcast panel
	 * 
	 */
	public void setCheckedBroadcasts(Boolean[] checkedBroadcasts) {
		this.checkedBroadcasts = checkedBroadcasts;
	}
	
	/**
	 * Check which broadcast was chosen to update description and request an update
	 * @param decription
	 * @throws IOException
	 */
	public void updateDescription(String decription) throws IOException {
		
		Constants.BroadcastsToUpdate = new ArrayList<LiveBroadcast>();
		for(int i = 0;i<checkedBroadcasts.length ;i++) {
			if(checkedBroadcasts[i]) {
				Constants.BroadcastsToUpdate.add(broadcasts.get(i));
			}
		}
		if(Constants.BroadcastsToUpdate.size()>100) {
			JOptionPane.showMessageDialog(null,"Please select less then 100 broadcasts","Server request ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
			
		Constants.Description = decription;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ProgressFrame().updateTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					e.printStackTrace();
					FileLogger.logger.info(e.toString());
				}
			}
		});
		
	}
	
	/**
	 * This method calculates interval stop time, it adds interval time to to current time instance 
	 * and converts it to Date object which is compatible with Timer Object
	 * @return finishDatetime
	 */
	public  Date calcStopTime() {
		Interval interval = Interval.getInstance();
		LocalDateTime now = LocalDateTime.now();		//set start time
		System.out.println("interval start time: " + now);
		now = now.plusHours(interval.getHours());		//calculate added interval hours
		now = now.plusMinutes(interval.getMinutes());	//calculate added interval minutes
		
		//convert to Date Object to be applicable with Timer.schedule(Task,Date)
		Date finishDatetime = Date.from( now.atZone( ZoneId.systemDefault()).toInstant());
		
		//System.out.println("interval finish time: " + now.toString());
		//System.out.println("interval finish Date object: "+finishDatetime.toString());
		interval.setCorrentInterval(finishDatetime);	//set current end time
		return finishDatetime;
	}
	
	/**
	 * Stream getter
	 * @return
	 */
	public List<LiveStream> getStreams() {
		return streams;
	}
	
	/**
	 * Broadcasts getter
	 * @return
	 */
	public List<LiveBroadcast> getBroadcasts(){
		return broadcasts;
	}
	
	/**
	 * Retrieves  new list of streams , delete old one if exists
	 * @throws ParseException 
	 */
	public boolean refreshStreams()  {
		if(streams!=null)
			streams.clear();
		streams = YouTubeAPI.listStreams(null);
		if(streams==null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieves  new list of broadcasts and filter it depending on args , delete old one if exists ,
	 * @param args
	 * @throws ParseException 
	 */
	public boolean refreshBroadcasts(String[] args)  {
		if(broadcasts!=null)
			broadcasts.clear();
		broadcasts = YouTubeAPI.listBroadcasts(args);
		if(broadcasts==null) {
			return false;
		}
		
		//handle page tokens
		BroadcastPanel bpanel = BroadcastPanel.getInstance();
		if(Constants.NextPageToken!=null) {
			bpanel.getBtnNextPage().setEnabled(true);
			bpanel.getBtnNextPage().setVisible(true);
		}
		else {
			bpanel.getBtnNextPage().setEnabled(false);
			bpanel.getBtnNextPage().setVisible(false);
		}
		if(Constants.PrevPageToken!=null) {
			bpanel.getBtnPreviousPage().setEnabled(true);
			bpanel.getBtnPreviousPage().setVisible(true);
		}
		else {
			bpanel.getBtnPreviousPage().setEnabled(false);
			bpanel.getBtnPreviousPage().setVisible(false);
		}
		return true;
		
	}
	
	/**
	 * This method inserts a new live stream to the database
	 * @param checked array indicates whether a stream was chosen or not
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws HeadlessException 
	 */
	public void addStream() throws IOException, HeadlessException, ParseException {
 
		Constants.AddingStream = JOptionPane.showInputDialog("please enter stream name");
		if(Constants.AddingStream == null) {
			System.out.println("requset cancelled");
			return ;
		}
		try {
			new ProgressFrame().StreamTask();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLogger.logger.info(e.toString());
		}
		
	}
	
	/**
	 * This method removes a live stream from database
	 * @param checked array indicates whether a stream was chosen or not
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public void removeStream(String[] streamid) throws IOException, ParseException {
		Constants.StreamToRemove = new ArrayList<LiveStream>();
		for(String id : streamid) {
				Constants.StreamToRemove.add(getStreamById(id));
		}
		try {
			new ProgressFrame().StreamTask();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLogger.logger.info(e.toString());
		}
	}
	
	
	/**
	 * This method starts new live broadcast to every stream that was chosen on the GUI
	 * @param checked array indicates whether a stream was chosen or not
	 * Constants.isLive array delivers data to loading tasks regarding the progress of starting broadcast.
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void startBroadcast() throws InterruptedException 
	{
		Constants.badResults =  new ArrayList<String>();
		CreateBroadcast brd = null; //Pointer to currently created broadcast
		
		if(Constants.LiveId!= null && !Constants.LiveId.isEmpty())
			Constants.LiveId.clear();
		Constants.LiveId = new ArrayList<String>();
		
		
		Constants.isLive = checkedStreams.length*2;	//init flag array to mark starting progress of broadcast
		System.out.println("here starts load frame");
		SwingUtilities.invokeLater(new Runnable() { //start loading frame
			public void run() {
				try {
					System.out.println("satrting loading task frame");
					new ProgressFrame().loadTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLogger.logger.info(e.toString());
				}
			}
		});
		ListPoll listpoll = new ListPoll();
		Constants.pollingState = true;
		listpoll.start();
		for(String streamId : checkedStreams) {
				System.out.println("inside for :starting " + streamId);
				String[] args = new String[2];			// args[0] = title , args[1] = end time
				args[0]= getStreamTitle(streamId);		//set stream title arg
				if(Constants.IntervalBroadcast) {		//calculate interval end time and set it as args 
					LocalDateTime finTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(calcStopTime().getTime())
							, ZoneId.systemDefault());
					args[1]= finTime.toString()	;
					System.out.println("starting "+ args[0]+" end time: "+args[1]);
				}
				else {  								//if it's indefinite Broadcast no end time is set
					args[1]=null;
					System.out.println("starting "+ args[0]);
				}
				brd =  new CreateBroadcast(args);			// initiate new CreateBroadcast Thread
				brd.start();								// start new thread
				Thread.sleep(1000);							// wait 1 second, better handles server requests
		}
	}
	
	/**
	 * helper method retrieve stream title 
	 * @param 
	 */
	public String getStreamTitle(String id) {
		for(LiveStream stream : streams) {
			if(stream.getId().equals(id))
				return stream.getSnippet().getTitle();
		}
		return null;
	}
	
	
	/**
	 * This method stops all active live broadcasts
	 * @throws InterruptedException 
	 * @throws ParseException 
	 */
	public void stopBroadcasts(String[] broadcastIDs) throws InterruptedException {
		CompleteBroadcast cmpBrd = null;
		String[] args = new String[1];
		ListPoll listpoll = new ListPoll();
		Constants.pollingState = true;
		listpoll.start();
		if(broadcastIDs!=null) {
			Constants.isLive = broadcastIDs.length;	//init flag array to mark starting progress of broadcast
			SwingUtilities.invokeLater(new Runnable() { //start loading frame
				public void run() {
					try {
						new ProgressFrame().completeTask();
					} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
							| InvalidAlgorithmParameterException | IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FileLogger.logger.info(e.toString());
					}
				}
			});
			for(String ID : broadcastIDs) {
				args[0] =  ID;
				cmpBrd = new CompleteBroadcast(args);
				cmpBrd.start();
				System.out.println("stopped "+ args[0]);
				Thread.sleep(1000);	// wait 1 second, better handles server requests
			}
		}
	}

	/**
	 * This method creates and start a timer runner object 
	 * @throws InterruptedException
	 */
	public void startTimerRunner() throws InterruptedException {
		timerRunner = new TimerRunner(calcStopTime());
	}
	
	/**
	 * This method stops timer runner object
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void cancelTimerRunner() throws InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException {
		timerRunner.stopIntervalBroadcast();
	}

	/**
	 * This method updates Inerval Panel with ne parameters:
	 * @param newStartTime
	 * @param stopTime
	 */
	public  void updateIntervalPanel(String newStartTime, String stopTime) {
		IntervalPanel intervalPanel = IntervalPanel.getInstance();
		//prompt new start time to interval panel
		intervalPanel.getLblstime().setText(newStartTime);
		//prompt new end time to interval panel
		intervalPanel.getFtime().setText(stopTime);
	}

	/**
	 * This method filters streams by there status={active,ready,inactive}
	 * @param filter
	 * @return filterdList
	 * @throws ParseException 
	 */
	public List<LiveStream> filterStreams(String filter) {
		refreshStreams();											// get latest list of streams from server
		List<LiveStream> filterdList = new ArrayList<LiveStream>();	//init return list
		for(LiveStream stream : streams) {
			if(stream.getStatus().getStreamStatus().equals(filter))
				filterdList.add(stream);
		}
		return filterdList;
	}

	/**
	 * This method saves current broadcast data on window closing event
	 */
	@SuppressWarnings("unchecked")
	public void saveData() {
		JSONObject obj = new JSONObject();
    	
		JSONObject userState = new JSONObject();
		
    	if(Constants.RegularBroadcast) {
    		System.out.println("closing regular broadcast");
    		
    		//save regular broadcast flag
    		userState.put("Regular Broadcast", "ON");
    		userState.put("Interval Broadcast", "OFF");
    		
    		//save checked streams
    		JSONArray checkedStreamList = new JSONArray();
    		for(String id: checkedStreams)
    			checkedStreamList.add(id);
    		userState.put("Stream List", checkedStreamList);
    		
    		//save Live broadcasts id's
    		JSONArray LiveIdList = new JSONArray();
    		for(String ID: Constants.LiveId)
    			LiveIdList.add(ID);
    		userState.put("Live ID List", LiveIdList);
    		
    	}
    	else if(Constants.IntervalBroadcast) {
    		System.out.println("closing inteval broadcast");
    	
    		//save interval broadcast flag
    		userState.put("Regular Broadcast", "OFF");
    		userState.put("Interval Broadcast", "ON");
    		
    		//save current interval stop and start time
    		userState.put("Stop Time", Interval.getInstance().getCorrentInterval().toString());
    		userState.put("Start Time", IntervalPanel.getInstance().getLblstime().getText());
    		
    		//save interval
    		userState.put("Interval", Interval.getInstance().getInterval());
    		
    		//save checked streams
    		JSONArray checkedStreamList = new JSONArray();
    		for(String id: checkedStreams)
    			checkedStreamList.add(id);
    		userState.put("Stream List", checkedStreamList);
    		System.out.println("stream list size: "+ checkedStreamList.size());
    		
    		//save Live broadcasts id's
    		JSONArray LiveIdList = new JSONArray();
    		for(String ID: Constants.LiveId)
    			LiveIdList.add(ID);
    		userState.put("Live ID List", LiveIdList);
    	}
    	else {
    		userState.put("Regular Broadcast", "OFF");
    		userState.put("Interval Broadcast", "OFF");
    	}
    	
    	obj.put("User State", userState);
    	
    	JSONObject UserSettings = new JSONObject();
    	//save user settings
    	UserSettings.put("Add Time and Date", Constants.AddDateTime);
    	UserSettings.put("Send Email", Constants.SendEmail);
    	UserSettings.put("Privacy", Constants.Privacy);
    	UserSettings.put("Ingestion Type", Constants.IngestionType);
    	UserSettings.put("Format", Constants.Format);
    	UserSettings.put("Save State", Constants.saveState);
    	//save stream descriptions map
    	
    	obj.put("User Settings", UserSettings);
    	
    	try (FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json")) {
			file.write(obj.toJSONString());
			System.out.println("Successfully Saved JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
		} catch (IOException e1) {
			e1.printStackTrace();
			FileLogger.logger.info(e1.toString());
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean loadUserSettings() {
		
		final Path path = Paths.get(Constants.UserDataPath+ Constants.Username + ".json");
		 
		if(Files.exists(path)) {     	//check if there's a file of saved data
			JSONParser parser = new JSONParser();
			 
	        try {
	 			
	            //parse users saved json file to json object	
	            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
	            jsonObject =  (JSONObject) jsonObject.get("User Settings");
	            //get and set stream description map 
	            if(jsonObject!=null) {
		            Boolean addtimeDate = (boolean) jsonObject.get("Add Time and Date");
		            Boolean sendEmail = (boolean) jsonObject.get("Send Email");
		            String privacy = (String) jsonObject.get("Privacy");
		            String ingetionType = (String) jsonObject.get("Ingestion Type");
		            String format = (String) jsonObject.get("Format");
		            Boolean saveState = (boolean) jsonObject.get("Save State");
		           	
		            //set user settings to it's global variables
		            if(addtimeDate!=null)
		            	Constants.AddDateTime = addtimeDate;
		            if(sendEmail!=null)
		            	Constants.SendEmail = sendEmail;
		            if(privacy!=null)
		            	Constants.Privacy = privacy;
		            if(ingetionType!=null)
		            Constants.IngestionType = ingetionType;
		            if(format!=null)	
		            	Constants.Format = format;
		            if(saveState!=null)
		            	Constants.saveState=saveState;
		            return true;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            FileLogger.logger.info(e.toString());
	        }
		}
		else {   //else create new user settings file
	    		//save regular broadcast flag
			System.out.println("creating new user data file");
    		JSONObject obj =new JSONObject();
    		JSONObject UserSettings =new JSONObject();
        	
        	obj.put("User Settings", UserSettings);
        	
        	try (FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json")) {
	    			file.write(obj.toJSONString());
	    			System.out.println("Successfully Saved JSON Object to File...");
	    			System.out.println("\nJSON Object: " + obj);
    		} catch (IOException e1) {
    			e1.printStackTrace();
    			FileLogger.logger.info(e1.toString());
    		}
		}
        return false;
	}
	
	/**
	 * This method loads current broadcast data when window is opening
	 */
	@SuppressWarnings({ "unchecked" })
	public void loadUserState() {
		
		Constants.LoadingState=true;
		JSONParser parser = new JSONParser();
		 
        try {
 			
            //parse users saved json file to json object	
        	JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
        	if(jsonObject!=null)
        		jsonObject = (JSONObject) jsonObject.get("User State");
           
            if(jsonObject==null)
            	return;
            
            String RegularBroadcast = (String) jsonObject.get("Regular Broadcast");
            String IntervalBroadcast = (String) jsonObject.get("Interval Broadcast");
        	
            if(IntervalBroadcast!=null && RegularBroadcast !=null &&
        			IntervalBroadcast.equals("OFF") && RegularBroadcast.equals("OFF"))
        		return; // if all saved live broadcasts were terminated
            
            //get and set live broadcast id list
            JSONArray liveIdList = (JSONArray) jsonObject.get("Live ID List");
           
            // get live broadcasts id's
            if(liveIdList!=null) { 
	        	String[] liveBroadcastsId = new String[liveIdList.size()];
	        	liveIdList.toArray(liveBroadcastsId);
	        	Constants.LiveId =new ArrayList<String>();
	        	Constants.LiveId.addAll(liveIdList);
            }
            else 
            	return;
            
            //check that saved broadcasts are still live 
			ArrayList<String> livebroadcasts = new ArrayList<String>();
			for(String id : Constants.LiveId) {
				String status = YouTubeAPI.getBroadcastByID(id).getStatus().getLifeCycleStatus();
				if(status.equals("live")) {
					livebroadcasts.add(id);
				}
			}
            if(livebroadcasts.isEmpty())
            	return;
			
            JSONArray streamList = (JSONArray) jsonObject.get("Stream List"); // get checked stream id's
            if(streamList!=null) {										     //set checked streams
	            checkedStreams = new String[streamList.size()];
	        	streamList.toArray(checkedStreams);
            }
        	
        	IntervalPanel intervalPanel = IntervalPanel.getInstance();
           
			if(RegularBroadcast.equals("ON")){
            	//start regular flag
            	Constants.RegularBroadcast = true;
            	//set non stop label
                intervalPanel.getLblNotSet().setText("Non-Stop");
            }
            else if(IntervalBroadcast !=null && IntervalBroadcast.equals("ON")){

	        	//set interval panel
	        	Constants.IntervalBroadcast = true;
	            
	        	//load and cast stored stop time
	            SimpleDateFormat dateformat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
	            String stp = (String) jsonObject.get("Stop Time");
				Date stoptime =  dateformat.parse(stp);
				System.out.println("stoptime on load: "+stoptime.toString());
				
				Date now = Date.from(LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant());
				
				Interval interval =Interval.getInstance(); 	  //load saved interval
                interval.setInterval((String) jsonObject.get("Interval"));
				
				if(stoptime.before(now)) {  //if interval time has ended complete broadcasts
					System.out.println("intervals has ended");
					  
					String message= "Iterval time ended ,Current LiveBroadcast will be Completed";
					String title="Stopping Broadcast";
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.CANCEL_OPTION);
					
					if(!livebroadcasts.isEmpty()) {
						System.out.println("stopping old broadcasts");
						String[] brdIds = new String[livebroadcasts.size()]; 
						livebroadcasts.toArray(brdIds);
						stopBroadcasts(brdIds);
					}
					Constants.IntervalBroadcast = false;
					return;
					
	            }
				else{ /*else resume broadcasts and
                		set timer runner again	*/
					
	            	//set data to interval panel
	                intervalPanel.getLblNotSet().setText(interval.getHours() +
							 " Hours and " + interval.getMinutes() +" minutes");
	            	//set interval panel
	            	System.out.println("setting interval panel");
	            	updateIntervalPanel((String) jsonObject.get("Start Time"),stoptime.toString());
				
					intervalPanel.getFtime().setVisible(true);						
	            	intervalPanel.getLblstime().setVisible(true);
	            	interval.setCorrentInterval(stoptime);	//set stop tome to current interval
		            
	            	timerRunner = new TimerRunner(stoptime);   //start timer run
				}
			}
			
            //refresh broadcast to active panel
        	String[] args = {"refresh","active",null,null};
		    refreshBroadcasts(args);
		    BroadcastPanel broadcastPanel = BroadcastPanel.getInstance();
    		broadcastPanel.setData(broadcasts);			//set new data
		    broadcastPanel.refresh();
            
        	//toggle buttons on GUI
            System.out.println("toggleing buttons");
        	ButtonPanel btnPnl =  ButtonPanel.getInstance();
        	btnPnl.getStartIntBrdbtn().setVisible(false);
			btnPnl.getStopIntbtn().setVisible(true);
        	btnPnl.getStartIntBrdbtn().setEnabled(false); 
			btnPnl.getStopIntbtn().setEnabled(true);
			intervalPanel.getLblHello().setText("Hello "+Constants.Username+", you are live!");
            
        } catch (Exception e) {
            e.printStackTrace();
            FileLogger.logger.info( e.toString());
        }		
	}
	
	/**
	 * Singleton instance retriever
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws ParseException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public static Controller getInstance() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException {
		if(instance==null)
			instance = new Controller();
		return instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(Controller instance) {
		Controller.instance = instance;
	}

	/** 
	 * This method registers new user locally
	 * @param username
	 * @param password
	 * @param email
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void registerUser(String username, String password, String email) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, ParseException {
		
		JSONParser parser = new JSONParser();
		
		String data = fileDecrypt();  //decrypt file
		
		Object readObject = parser.parse(data);
        JSONObject jsonObject = (JSONObject) readObject;
        JSONObject userObject = new JSONObject();
		JSONObject userDetailsObject = new JSONObject();
		
		userDetailsObject.put("username",username);
		
		//encrypt password
		
		userDetailsObject.put("password",password);
		userDetailsObject.put("email",email);
		userDetailsObject.put("rememberpass","false");
		
		userObject.put("User",userDetailsObject);
		
		//get user list
		JSONArray userArray = (JSONArray) jsonObject.get("User List");
		
		//add new user object
		userArray.add(userObject);
		
		Iterator<JSONObject> Iterator = userArray.iterator();
		Constants.SavedUsers = new String[userArray.size()]; 
		int i=0;
		while (Iterator.hasNext()) {
			Constants.SavedUsers[i++] = (String) ((JSONObject) Iterator.next().get("User")).get("username");
		}
		jsonObject.put("User List", userArray);
		
		//save new list to file
		FileWriter file = new FileWriter(System.getProperty("user.home")+"\\Documents\\AppUsers.json");
		file.write(jsonObject.toJSONString());
		System.out.println("Successfully Registerd New User And Saved JSON Object to File...");
		//System.out.println("\nJSON Object: " + jsonObject);
		file.close();
		
		fileEncrypt(jsonObject.toString());
		
		JOptionPane.showMessageDialog(null,"User Registerd Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);	

	}

	/**
 	 * This method checks if user exists
	 * @param username
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public boolean userExists(String username) throws FileNotFoundException, IOException, ParseException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {

		if(username!=null && !"".equals(username)) {
			String data = fileDecrypt(); //decrypt file
			JSONParser parser = new JSONParser();
			Object readObject = parser.parse(data);
			JSONObject jsonObject = (JSONObject) readObject;
			JSONArray userArray = (JSONArray) jsonObject.get("User List");
			for(int i=0;i<userArray.size();i++) {
				JSONObject user = (JSONObject) userArray.get(i);
				user = (JSONObject) user.get("User");
				String userInList = (String) user.get("username");
				if(username.equals(userInList))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * This method validates the user to be logged in
	 * @param username
	 * @param password
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public boolean validateUser(String username, String password) throws FileNotFoundException, IOException, ParseException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		// TODO Auto-generated method stub
		final Path path = Paths.get(Constants.AppUserPath);
		
		JSONParser parser = new JSONParser();
		
		if(Files.exists(path)) {     	//check if there's a file of saved data
			//decrypt file
			String data = fileDecrypt();
			JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
			String decryptedPassword = null ,email =null; 	// to hold the encrypted password exported from the file
			for(int i=0;i<userArray.size();i++) {
				JSONObject	user = (JSONObject) ((JSONObject) userArray.get(i)).get("User");
				String userInList = (String) user.get("username");
				if(username!=null && username.equals(userInList)) {
					decryptedPassword = (String) user.get("password");
				    email = (String) user.get("email");
				}
			}
			if(password.equals(decryptedPassword)) {
				Constants.UserEmail = email;
				return true;
			}
				
            return false;
		}
		return true;
	}
	
	/**
	 * This method loads initial user details if they exist otherwise it generates files to hold the details
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws InvalidAlgorithmParameterException
	 */
	@SuppressWarnings({ "unchecked" })
	public void initData() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, IOException, ParseException, InvalidAlgorithmParameterException {
		
		final Path path = Paths.get(System.getProperty("user.home")+"\\Documents\\info.json");
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		if(!Files.exists(path)) {
			
			Constants.SecretKey = KeyGenerator.getInstance("AES").generateKey();
			
			// get base64 encoded version of the key
			String encodedKey = Base64.getEncoder().encodeToString(Constants.SecretKey.getEncoded());
			
			obj.put("info", encodedKey);
			try (FileWriter file = new FileWriter(Constants.InfoPath)) {
    			file.write(obj.toJSONString());
    			System.out.println("Successfully created first user list JSON Object File...");
    			//System.out.println("\nJSON Object: " + obj);
    			} catch (IOException e1) {
    				e1.printStackTrace();
    				FileLogger.logger.info(e1.toString());
    			}
		}
		else {
			  JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.InfoPath));
			 if(jsonObject!=null) {
				  String key =(String)jsonObject.get("info");
				  // decode the base64 encoded string
				  byte[] decodedKey = null;
				  if(key!=null) {
					  decodedKey = Base64.getDecoder().decode(key);
				  // rebuild key using SecretKeySpec
				  Constants.SecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
				  }
			  }
		}
		
		final Path path1 = Paths.get(Constants.AppUserPath);

		if(!Files.exists(path1)) {
			obj = new JSONObject();
			JSONArray userArray = new JSONArray();
			obj.put("User List", userArray);
			try (FileWriter file = new FileWriter(Constants.AppUserPath)) {
    			file.write(obj.toJSONString());
    			System.out.println("Successfully created first user list JSON Object File...");
    			//System.out.println("\nJSON Object: " + obj);
    			} catch (IOException e1) {
    				e1.printStackTrace();
    				FileLogger.logger.info(e1.toString());
    			}
			
			fileEncrypt(obj.toString());
		}
		else{
			
			String data = fileDecrypt();
			//System.out.println(data);
			JSONArray userArray = (JSONArray) ((JSONObject) parser.parse(data)).get("User List");
			Iterator<JSONObject> Iterator = userArray.iterator();
			Constants.SavedUsers = new String[userArray.size()]; 
			int i=0;
			while (Iterator.hasNext()) {
				Constants.SavedUsers[i++] = (String) ((JSONObject) Iterator.next().get("User")).get("username");
			}
		}
		Reader reader = new InputStreamReader(Controller.class.getResourceAsStream("/tmp.json"));
		JSONObject jsonObject = (JSONObject) parser.parse(reader);
		jsonObject = (JSONObject) jsonObject.get("installed");
		Constants.myBytes = (String) jsonObject.get("client_id");
		Constants.myBytes = (String) Constants.myBytes.subSequence(0, 12);
	}
	
	
	
	/**
	 * Encrypt user file
	 * @param data
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	private void fileEncrypt(String data) throws InvalidKeyException, FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		fileEncDec.encrypt(data,Constants.AppUserPath);
	}
	
	/**
	 * Decrypt user file
	 * @return
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	private String fileDecrypt() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileEncrypterDecrypter fileEncDec = new FileEncrypterDecrypter(Constants.SecretKey,"AES/CBC/PKCS5Padding");
		return fileEncDec.decrypt(Constants.AppUserPath);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getStreamTitles() {
		// TODO Auto-generated method stub
		String[] titles = new String[streams.size()];
		int index = 0;
		for(LiveStream stream :streams) {
				titles[index++] = stream.getSnippet().getTitle();
		}
		return titles;
	}
	
	public TimerRunner getTimerRunner() {
		return timerRunner;
	}
	
	/**
	 * returns relevant stream title from streams List
	 * @param title
	 * @return
	 */
	public  LiveStream getStreamByName(String title) throws IOException, ParseException {
    	LiveStream foundstream=null;			//initite pointer to the stream
        for (LiveStream stream : streams) {
        	if(stream.getSnippet().getTitle().equals(title))
        		foundstream= stream;
        }
    	return foundstream;
  }

/**
 * returns relevant stream from streams List
 * @param id
 * @return
 * @throws IOException
 * @throws ParseException
 */
	 public  LiveStream getStreamById(String id) throws IOException, ParseException {

	    	LiveStream foundstream=null;			//initite pointer to the stream
	        for (LiveStream stream : streams) {
	        	if(stream.getId().equals(id))
	        		foundstream= stream;
	        }
	    	return foundstream;
	  }

}
