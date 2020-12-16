package com.youtube.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.youtube.api.ErrorHandler;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.ButtonPanel;
import com.youtube.gui.IntervalPanel;
import com.youtube.utils.Constants;

public class UserDataHandler {
	
	public static UserDataHandler instance;
	
	public static UserDataHandler getInstance() {
		if(instance == null) {
			instance = new UserDataHandler();
		}
		return instance;
	}
	
	/**
	 * This method saves current broadcast data on window closing event
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public void saveData() throws SecurityException, IOException 
	{
		try {
			JSONObject obj = new JSONObject();
			SaveUserState(obj);
			SaveUserSettings(obj);
			FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json");
			file.write(obj.toJSONString());
			if(Constants.Debug) {
				System.out.println("Successfully Saved User data to File...");
				System.out.println("\nJSON Object: " + obj);
			}
			file.close();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException 
				| IOException | InvalidAlgorithmParameterException e1) {
			e1.printStackTrace();
			ErrorHandler.HandleLoadError(e1.toString());
		}
	}
	
	public boolean loadUserSettings() throws SecurityException, IOException 
	{
		if(userFileExists()) {     	
			loadSettingsFromJson();
			return true;
		}
		createNewUserInJson();
		return false;
	}
	
	/**
	 * This method loads current broadcast data when window is opening
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public void loadUserState() 
	{
		try {
			if(!userFileExists()) {
				return;
			}
			Constants.LoadingState = true;
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
    	
			String broadcastState = (String) jsonObject.get("Broadcast State");
	        if(broadcastState == null || broadcastState.equals("OFF") || !loadLiveIdList(jsonObject)) {
	        	return; // if all saved live broadcasts were terminated
	        }
	        setCheckedStreamsList(jsonObject);
	        if(broadcastState.equals("Regular")){
	        	resumeRegularBroadcast();
	        }
	        else if(broadcastState.equals("Interval")){
	        	 ArrayList<String> livebroadcasts = addLiveBroadcastIdsToList();
	 	        if(livebroadcasts.isEmpty()) { //check that saved broadcasts are still live 
	 	        	return;
	 	        }
	        	resumeIntervalBroadcast(jsonObject,livebroadcasts);
	        }
	        refreshGuiPanels();
        
	    } catch (Exception e ) {
	        e.printStackTrace();
	        ErrorHandler.HandleLoadError(e.toString());
	    }		
	}
	
	@SuppressWarnings("unchecked")
	private void CloseRegularBroadcast(JSONObject userState) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException 
	{
		if(Constants.Debug) {
			System.out.println("closing regular broadcast");
		}
		
		//save regular broadcast flag
		userState.put("Broadcast State", "Regular");
		
		//save checked streams
		JSONArray checkedStreamList = new JSONArray();
		
		String[] checkedStreams = Controller.getInstance().getStreamHandler().getCheckedStreams();
		for(String id: checkedStreams) {
			checkedStreamList.add(id);
		}
			
		userState.put("Stream List", checkedStreamList);
		
		//save Live broadcasts id's
		JSONArray LiveIdList = new JSONArray();
		for(String ID : Constants.LiveId) {
			LiveIdList.add(ID);
		}
		userState.put("Live ID List", LiveIdList);
	}
	
	@SuppressWarnings("unchecked")
	private void CloseIntervalBroadcast(JSONObject userState) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException 
	{
		if(Constants.Debug) {
			System.out.println("closing inteval broadcast");
		}
		//save interval broadcast flag
		userState.put("Broadcast State", "Interval");
		
		//save current interval stop and start time
		userState.put("Stop Time", Interval.getInstance().getCorrentInterval().toString());
		userState.put("Start Time", IntervalPanel.getInstance().getLblstime().getText());
		
		//save interval
		userState.put("Interval", Interval.getInstance().getInterval());
		
		//save checked streams
		JSONArray checkedStreamList = new JSONArray();
		
		String[] checkedStreams =  Controller.getInstance().getStreamHandler().getCheckedStreams();
		for(String id: checkedStreams) {
			checkedStreamList.add(id);
		}
			
		userState.put("Stream List", checkedStreamList);
		System.out.println("stream list size: "+ checkedStreamList.size());
		
		//save Live broadcasts id's
		JSONArray LiveIdList = new JSONArray();
		for(String ID: Constants.LiveId)
			LiveIdList.add(ID);
		userState.put("Live ID List", LiveIdList);
	}
	
	@SuppressWarnings("unchecked")
	private void DefaultClose(JSONObject userState)
	{
		userState.put("Broadcast State", "OFF");
	}

	@SuppressWarnings("unchecked")
	private void SaveUserState(JSONObject obj) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
	{
		JSONObject userState = new JSONObject();
		if(Constants.RegularBroadcast) {
			CloseRegularBroadcast(userState);
			
		}
		else if(Constants.IntervalBroadcast) {
			CloseIntervalBroadcast(userState); 
		}
		else {
			DefaultClose(userState);
		}
		obj.put("User State", userState);
	}
	
	@SuppressWarnings("unchecked")
	private void SaveUserSettings(JSONObject obj) 
	{
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
	}
	
	private boolean userFileExists() {
		final Path path = Paths.get(Constants.UserDataPath + Constants.Username + ".json");
		return Files.exists(path);
	}
	
	private void loadSettingsFromJson()
	{
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.HandleLoadError(e.toString());
        }
	}
	
	@SuppressWarnings("unchecked")
	private void createNewUserInJson() {
		if(Constants.Debug) {
			System.out.println("creating new user data file");
		}
		JSONObject obj = new JSONObject();
		JSONObject UserSettings = new JSONObject();
    	obj.put("User Settings", UserSettings);
    	
    	try (FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json")) {
    			file.write(obj.toJSONString());
    			if(Constants.Debug) {
	    			System.out.println("Successfully Saved JSON Object to File...");
	    			System.out.println("\nJSON Object: " + obj);
    			}
		} catch (IOException e1) {
			e1.printStackTrace();
			ErrorHandler.HandleLoadError(e1.toString());
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private boolean loadLiveIdList(JSONObject jsonObject) 
	{
		//get and set live broadcast id list
        JSONArray liveIdList = (JSONArray) jsonObject.get("Live ID List");
        // get live broadcasts id's
        if(liveIdList != null) { 
        	String[] liveBroadcastsId = new String[liveIdList.size()];
        	liveIdList.toArray(liveBroadcastsId);
        	Constants.LiveId =new ArrayList<String>();
        	Constants.LiveId.addAll(liveIdList);
        	return true;
        }
        return false;
	}
	
	@SuppressWarnings("unchecked")
	private void setCheckedStreamsList(JSONObject jsonObject) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
	{
		JSONArray streamList = (JSONArray) jsonObject.get("Stream List"); // get checked stream id's
        if(streamList != null) {		
        	String[] checkedStreams = new String[streamList.size()];
        	streamList.toArray(checkedStreams);
        	 Controller.getInstance().getStreamHandler().setCheckedStreams(checkedStreams);
        }
	}
	
	private ArrayList<String> addLiveBroadcastIdsToList() throws IOException
	{
		ArrayList<String> livebroadcasts = new ArrayList<String>();
		for(String id : Constants.LiveId) {
			String status = YouTubeAPI.getBroadcastByID(id).getStatus().getLifeCycleStatus();
			if(status.equals("live")) {
				livebroadcasts.add(id);
			}
		}
		return livebroadcasts;
	}
	
	private Date loadStopTime(JSONObject jsonObject) throws ParseException
	{
		//load and cast stored stop time
        SimpleDateFormat dateformat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        String stp = (String) jsonObject.get("Stop Time");
		Date stoptime =  dateformat.parse(stp);
		if(Constants.Debug) {
			System.out.println("stoptime on load: "+ stoptime.toString());
		}
		return stoptime;
	}
	
	private void completeBroadcasts(ArrayList<String> livebroadcasts) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, InterruptedException, IOException 
	{
		if(Constants.Debug) {
			System.out.println("intervals has ended");
		}
		String message= "Iterval time ended ,Current LiveBroadcast will be Completed";
		String title= "Stopping Broadcast";
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.CANCEL_OPTION);
		
		if(!livebroadcasts.isEmpty()) {
			System.out.println("stopping old broadcasts");
			String[] brdIds = new String[livebroadcasts.size()]; 
			livebroadcasts.toArray(brdIds);
			Controller.getInstance().getBroadcastsHandler().stopBroadcasts(brdIds);
		}
	}
	
	private void resumeBroadcasts(Date stoptime, JSONObject jsonObject) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, InterruptedException, IOException 
	{
		Interval interval = Interval.getInstance(); 	  //load saved interval
	    IntervalPanel intervalPanel = IntervalPanel.getInstance();
    	//set data to interval panel
        intervalPanel.getLblNotSet().setText(interval.getHours() +
				 " Hours and " + interval.getMinutes() +" minutes");
    	//set interval panel
    	System.out.println("setting interval panel");
    	IntervalPanel.getInstance().updateIntervalPanel((String) jsonObject.get("Start Time"),stoptime.toString());
	
		intervalPanel.getFtime().setVisible(true);						
    	intervalPanel.getLblstime().setVisible(true);
    	
    	interval.setCorrentInterval(stoptime);	//set stop tome to current interval
        
    	Controller.getInstance().setTimerRunner(stoptime);
	}
	
	private void refreshGuiPanels() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, IOException
	{
		 //refresh broadcast to active panel
		LiveBroadcastHandler broadcastHandler =  Controller.getInstance().getBroadcastsHandler();
    	String[] args = {"refresh","active",null,null};
    	broadcastHandler.refreshBroadcasts(args);
	    BroadcastPanel broadcastPanel = BroadcastPanel.getInstance();
		broadcastPanel.setData(broadcastHandler.getBroadcasts());			//set new data
	    broadcastPanel.refresh();
        
    	//toggle buttons on GUI
        System.out.println("toggleing buttons");
    	ButtonPanel btnPnl =  ButtonPanel.getInstance();
    	btnPnl.getStartIntBrdbtn().setVisible(false);
		btnPnl.getStopIntbtn().setVisible(true);
    	btnPnl.getStartIntBrdbtn().setEnabled(false); 
		btnPnl.getStopIntbtn().setEnabled(true);
		IntervalPanel.getInstance().getLblHello().setText("Hello "+ Constants.Username+", you are live!");
	}
	
	private void resumeIntervalBroadcast(JSONObject jsonObject, ArrayList<String> livebroadcasts) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, InterruptedException, IOException, ParseException 
	{
    	Date stoptime = loadStopTime(jsonObject);
		Date now = Date.from(LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant());
		Interval interval = Interval.getInstance(); 	  //load saved interval
        interval.setInterval((String) jsonObject.get("Interval"));
	
		if(stoptime.before(now)) {   //if interval time has ended complete broadcasts
			 completeBroadcasts(livebroadcasts);
			 return;
        }
		else {
			resumeBroadcasts(stoptime,jsonObject);
		}
		Constants.IntervalBroadcast = true;
	}
	
	private void resumeRegularBroadcast() 
	{
		IntervalPanel intervalPanel = IntervalPanel.getInstance();
    	//start regular flag
    	Constants.RegularBroadcast = true;
    	//set non stop label
        intervalPanel.getLblNotSet().setText("Non-Stop");
	}
	
}
