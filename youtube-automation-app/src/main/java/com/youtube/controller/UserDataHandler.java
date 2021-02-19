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
import com.youtube.gui.IntervalPanel;
import com.youtube.utils.Constants;

public class UserDataHandler {
	
	public void saveData() throws SecurityException, IOException 
	{
		try {
			JSONObject obj = new JSONObject();
			saveUserState(obj);
			saveUserSettings(obj);
			FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json");
			file.write(obj.toJSONString());
			Constants.DebugPrint("Successfully Saved User data to File...");
			Constants.DebugPrint("\nJSON Object: " + obj);
			file.close();
		} catch ( IOException  e1) {
			e1.printStackTrace();
			ErrorHandler.HandleLoadError(e1.toString());
		}
	}
	
	public void loadUserSettings() throws SecurityException, IOException 
	{
		if(userFileExists()) {     	
			loadSettingsFromJson();
		}
		else {
			createNewUserSettingsJson();
		}
	}
	
	public boolean loadUserState() 
	{
		try {
			if(!userFileExists()) {
				return false;
			}
			Constants.LoadingState = true;
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
    	
			String broadcastState = (String) jsonObject.get("Broadcast State");
	        if(broadcastState == null || broadcastState.equals("OFF") || !loadLiveIdList(jsonObject)) {
	        	return false; // if all saved live broadcasts were terminated
	        }
	        setCheckedStreamsList(jsonObject);
	        if(broadcastState.equals("Regular")){
	        	resumeRegularBroadcast();
	        }
	        else if(broadcastState.equals("Interval")){
	        	 ArrayList<String> livebroadcasts = addLiveBroadcastIdsToList();
	 	        if(livebroadcasts.isEmpty()) { //check that saved broadcasts are still live 
	 	        	return false;
	 	        }
	        	resumeIntervalBroadcast(jsonObject,livebroadcasts);
	        }
	        return true;
        
	    } catch (Exception e ) {
	        e.printStackTrace();
	        ErrorHandler.HandleLoadError(e.toString());
	    }
		return false;		
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getLiveIdList() throws IOException {
		JSONArray LiveIdList = new JSONArray();
		ArrayList<String> currLive = Controller.getInstance().getBroadcastsHandler().getCurrentlyLive();
		for(String ID : currLive) {
			LiveIdList.add(ID);
		}
		return LiveIdList;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getCheckedStreamList() throws IOException 
	{
		JSONArray checkedStreamList = new JSONArray();
		String[] checkedStreams = Controller.getInstance().getStreamHandler().getCheckedStreams();
		for(String id: checkedStreams) {
			checkedStreamList.add(id);
		}
		return checkedStreamList;
	}
	
	@SuppressWarnings("unchecked")
	private void CloseRegularBroadcast(JSONObject userState) throws IOException 
	{
		Constants.DebugPrint("closing regular broadcast");
		userState.put("Broadcast State", "Regular");
		JSONArray checkedStreamList = getCheckedStreamList();
		userState.put("Stream List", checkedStreamList);
		JSONArray LiveIdList = getLiveIdList();
		userState.put("Live ID List", LiveIdList);
	}
	
	@SuppressWarnings("unchecked")
	private void CloseIntervalBroadcast(JSONObject userState) throws IOException 
	{
		Constants.DebugPrint("closing inteval broadcast");
		userState.put("Broadcast State", "Interval");
		userState.put("Stop Time", Interval.getInstance().getCorrentInterval().toString());
		userState.put("Start Time", IntervalPanel.getInstance().getLblstime().getText());
		userState.put("Interval", Interval.getInstance().getInterval());
		JSONArray checkedStreamList = getCheckedStreamList();	
		userState.put("Stream List", checkedStreamList);
		JSONArray LiveIdList = getLiveIdList();
		userState.put("Live ID List", LiveIdList);
	}
	
	@SuppressWarnings("unchecked")
	private void DefaultClose(JSONObject userState)
	{
		userState.put("Broadcast State", "OFF");
	}

	@SuppressWarnings("unchecked")
	private void saveUserState(JSONObject obj) throws IOException
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
	private void saveUserSettings(JSONObject obj) 
	{
		JSONObject UserSettings = new JSONObject();
		UserSettings.put("Add Time and Date", Constants.AddDateTime);
		UserSettings.put("Privacy", Constants.Privacy);
		UserSettings.put("Ingestion Type", Constants.IngestionType);
		UserSettings.put("Format", Constants.Format);
		UserSettings.put("Save State", Constants.saveState);
		obj.put("User Settings", UserSettings);
	}
	
	private boolean userFileExists() {
		final Path path = Paths.get(Constants.UserDataPath + Constants.Username + ".json");
		return Files.exists(path);
	}
	
	private void loadSettingsFromJson()
	{
        try {
        	if(userFileExists()) {
        		JSONParser parser = new JSONParser();
        		JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(Constants.UserDataPath + Constants.Username + ".json"));
        		jsonObject =  (JSONObject) jsonObject.get("User Settings");
            	Constants.AddDateTime = (boolean) jsonObject.get("Add Time and Date");
            	Constants.Privacy = (String) jsonObject.get("Privacy");
	            Constants.IngestionType = (String) jsonObject.get("Ingestion Type");
            	Constants.Format = (String) jsonObject.get("Format");
            	Constants.saveState = (boolean) jsonObject.get("Save State");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorHandler.HandleLoadError(e.toString());
        }
	}
	
	@SuppressWarnings("unchecked")
	private void createNewUserSettingsJson() 
	{
		Constants.DebugPrint("creating new user data file");
		JSONObject obj = new JSONObject();
		JSONObject UserSettings = new JSONObject();
		saveUserSettings(UserSettings);
    	obj.put("User Settings", UserSettings);
    	
    	try (FileWriter file = new FileWriter(Constants.UserDataPath + Constants.Username + ".json")) {
    			file.write(obj.toJSONString());
				Constants.DebugPrint("Successfully Saved JSON Object to File...");
				Constants.DebugPrint("\nJSON Object: " + obj);
		} catch (IOException e1) {
			e1.printStackTrace();
			ErrorHandler.HandleLoadError(e1.toString());
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private boolean loadLiveIdList(JSONObject jsonObject) throws IOException 
	{
        JSONArray liveIdList = (JSONArray) jsonObject.get("Live ID List");
        if(liveIdList != null) { 
        	String[] liveBroadcastsId = new String[liveIdList.size()];
        	liveIdList.toArray(liveBroadcastsId);
        	ArrayList<String> currLive = Controller.getInstance().getBroadcastsHandler().getCurrentlyLive();
        	currLive = new ArrayList<String>();
        	currLive.addAll(liveIdList);
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
	
	private ArrayList<String> addLiveBroadcastIdsToList() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException
	{
		ArrayList<String> livebroadcasts = new ArrayList<String>();
		ArrayList<String> currLive = Controller.getInstance().getBroadcastsHandler().getCurrentlyLive();
		for(String id : currLive) {
			String status = YouTubeAPI.getInstance().getBroadcastByID(id).getStatus().getLifeCycleStatus();
			if(status.equals("live")) {
				livebroadcasts.add(id);
			}
		}
		return livebroadcasts;
	}
	
	private Date loadStopTime(JSONObject jsonObject) throws ParseException
	{
        SimpleDateFormat dateformat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
        String stp = (String) jsonObject.get("Stop Time");
		Date stoptime =  dateformat.parse(stp);
		Constants.DebugPrint("stoptime on load: "+ stoptime.toString());
		return stoptime;
	}
	
	private void completeBroadcasts(ArrayList<String> livebroadcasts) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, InterruptedException, IOException 
	{
		Constants.DebugPrint("intervals has ended");
		String message= "Iterval time ended ,Current LiveBroadcast will be Completed";
		String title= "Stopping Broadcast";
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.CANCEL_OPTION);
		
		if(!livebroadcasts.isEmpty()) {
			Constants.DebugPrint("stopping old broadcasts");
			Controller.getInstance().getBroadcastsHandler().stopBroadcasts(livebroadcasts);
		}
	}
	
	private void resumeBroadcasts(Date stoptime, JSONObject jsonObject) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, InterruptedException, IOException 
	{
		Interval interval = Interval.getInstance(); 	  //load saved interval
	    IntervalPanel intervalPanel = IntervalPanel.getInstance();
        intervalPanel.getLblNotSet().setText(interval.getHours() +
				 " Hours and " + interval.getMinutes() +" minutes");
    	
        Constants.DebugPrint("setting interval panel");
    	IntervalPanel.getInstance().updateIntervalPanel((String) jsonObject.get("Start Time"),stoptime.toString());
		intervalPanel.getFtime().setVisible(true);						
    	intervalPanel.getLblstime().setVisible(true);
    	interval.setCorrentInterval(stoptime);	//set stop tome to current interval
    	Controller.getInstance().getBroadcastsHandler().setTimerRunner(stoptime);
	}
	
	private void resumeIntervalBroadcast(JSONObject jsonObject, ArrayList<String> livebroadcasts) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, InvalidAlgorithmParameterException, InterruptedException, IOException, ParseException 
	{
    	Date stoptime = loadStopTime(jsonObject);
		Date now = Date.from(LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant());
		Interval interval = Interval.getInstance(); 	 
        interval.setInterval((String) jsonObject.get("Interval"));
		if(stoptime.before(now)) {   
			 completeBroadcasts(livebroadcasts);
			 return;
        }
		resumeBroadcasts(stoptime,jsonObject);
		Constants.IntervalBroadcast = true;
	}
	
	private void resumeRegularBroadcast() 
	{
		IntervalPanel intervalPanel = IntervalPanel.getInstance();
    	Constants.RegularBroadcast = true;
        intervalPanel.getLblNotSet().setText("Non-Stop");
	}
	
}
