package com.youtube.controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;
import com.youtube.api.CompleteBroadcast;
import com.youtube.api.CreateBroadcast;
import com.youtube.api.CreateStream;
import com.youtube.api.DeleteStream;
import com.youtube.api.ListBroadcasts;
import com.youtube.api.ListStreams;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.ButtonPanel;
import com.youtube.gui.IntervalPanel;
import com.youtube.gui.LoadingFrame;
import com.youtube.utils.Constants;

/**
 * this class handles data flow from the API to the GUI and backwards,
 * all it's functions control API requests , also it temporarily holds
 * the data that is being transported to the GUI.  
 * @author Evgeny Geyfman
 *
 */
public class Controller {
	
	private List<LiveStream> streams;			//holds currently presented streams
	
	private List<LiveBroadcast> broadcasts;		//holds currently presented broadcasts
	
	private TimerRunner timerRunner;			//holds current timer runner
	
	private  Boolean[] checkedStreams;			//holds shecked streams from inputform

	private static Controller instance;			//singleton instance
	
	/**
	 * gets checked stream
	 * @return
	 */
	public  Boolean[] getCheckedStreams() {
		return checkedStreams;
	}

	/**
	 * sets checked streams from Interval input form
	 * @param checkedStreams
	 */
	public void setCheckedStreams(Boolean[] checkedStreams) {
		this.checkedStreams = checkedStreams;
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
		
		//convert to Date Object applicable with Timer.schedule(Task,Date)
		Date finishDatetime = Date.from( now.atZone( ZoneId.systemDefault()).toInstant());
		
		//System.out.println("interval finish time: " + now.toString());
		//System.out.println("interval finish Date object: "+finishDatetime.toString());
		interval.setCorrentInterval(finishDatetime);	//set current end time
		return finishDatetime;
	}
	
	/**
	 * stream getter
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
	 * retrieves  new list of streams , delete old one if exists
	 */
	public void refreshStreams() {
		if(streams!=null)
			streams.clear();
		streams=ListStreams.run(null);
		if(streams==null) {
			System.out.println("streams wasn't set correctly");
		}
			
	}
	
	/**
	 * retrieves  new list of broadcasts and filter it depending on args , delete old one if exists ,
	 * @param args
	 */
	public void refreshBroadcasts(String[] args) {
		if(broadcasts!=null)
			broadcasts.clear();
		broadcasts=ListBroadcasts.run(args);
		if(broadcasts==null) {
			System.out.println("broadcasts wasn't set correctly");
		}
	}
	
	/**
	 * This method inserts a new live stream to the database
	 * @param checked array indicates whether a stream was chosen or not
	 */
	public void addStream() {
		String[] args = new String[1];
		args[0]=JOptionPane.showInputDialog("please enter stream name");
		CreateStream.run(args);
		refreshStreams();
	}
	
	/**
	 * This method removes a live stream from database
	 * @param checked array indicates whether a stream was chosen or not
	 */
	public void removeStream(Boolean[] checked) {
		String[] args = new String[1];
		for(int i=0;i<streams.size();i++) {
			if(checked[i]) {
				args[0]=streams.get(i).getSnippet().getTitle();
				DeleteStream.run(args);
			}
		}
		refreshStreams();
	}
	
	/**
	 * This method starts new live broadcast to every stream that was chosen ion the GUI
	 * @param checked array indicates whether a stream was chosen or not
	 * 
	 * @throws InterruptedException
	 */
	public void startBroadcast(Boolean[] checked) throws InterruptedException {
		
		List<LiveStream> streams = filterStreams("active");		
		if(streams==null) {
			System.out.println("error retrieving streams");
			System.exit(1);
		}
		CreateBroadcast brd =null;					//Pointer to currently created broadcast
		int checkedStreamsCount = 0;				//checked stream counter
		for(int i = 0;i<checked.length;i++) {		//count checked streams
			if(checked[i])
				checkedStreamsCount++;
		}
		Constants.isLive = new Boolean[checkedStreamsCount];	//init flag array to mark when broadcast is live
		for(int i=0;i<Constants.isLive.length;i++)
			Constants.isLive[i]=false;						
		System.out.println("loading frame starting...isLive length: "+Constants.isLive.length);
		new LoadingFrame();
		
		for(int i = checked.length-1 ; i>=0 ; i--) {
			if(checked[i])	{
				String[] args = new String[2];		// args[0] = title , args[1] = end time
				args[0]= streams.get(i).getSnippet().getTitle();
				if(Constants.IntervalBroadcast) {	//calculate interval end time and set it as args 
					Instant instant = Instant.ofEpochMilli(calcStopTime().getTime());
					LocalDateTime finTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
					args[1]= finTime.toString()	;
				}
				else { 								//if it's indefinite Broadcast no end time is set
					args[1]=null;
				}
				if(args[1]!=null)
					System.out.println("starting "+ args[0]+" end time: "+args[1]);
				else
					System.out.println("starting "+ args[0]);
				brd =  new CreateBroadcast(args);				// initiate new CreateBroadcast object
				brd.setQueueNum(checked.length-1-i);   			// set broadcasts queue index for Constants.isLive array
				brd.start();									// start new thread
				Thread.sleep(1000);								// wait 1 second, better handles server requests
			}
		}
	}
	
	/**
	 * This method stops all active live broadcasts if they were chosen
	 * @param checked
	 */
	public void stopBroadcast(Boolean[] checked) {
		String[] args = {"refresh","active"};
		List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
		for(int i=returnedList.size()-1 ; i>=0 ;i--) {
			if(checked[i]) {
				args[0]=broadcasts.get(i).getSnippet().getTitle();
				CompleteBroadcast cmpBrd = new CompleteBroadcast(args);
				cmpBrd.start();
				System.out.println("stoping "+ args[0]);
			}
		}
	}
	
	/**
	 * This method stops all active live broadcasts
	 */
	public void stopBroadcasts() {
		CompleteBroadcast cmpBrd= null;
		String[] args = {"refresh","active"};
		List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
		if(returnedList==null) {
			System.out.println("error fetching broadcasts");
			return;
		}
		for(int i=returnedList.size()-1 ; i>=0 ;i--) {
			args[0]=broadcasts.get(i).getSnippet().getTitle();
			cmpBrd = new CompleteBroadcast(args);
			cmpBrd.start();
			System.out.println("stoping "+ args[0]);
		}
	}

	/**
	 * this method creates and start a timer runner object 
	 * @throws InterruptedException
	 */
	public void startTimerRunner() throws InterruptedException {
		timerRunner = new TimerRunner(calcStopTime());
	}
	
	/**
	 * This method stops timer runner object
	 */
	public void cancelTimerRunner() {
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
	 */
	public List<LiveStream> filterStreams(String filter){
		refreshStreams();											// get latest list of streams from server
		List<LiveStream> filterdList = new ArrayList<LiveStream>();	//init return list
		for(LiveStream stream : streams) {
			if(stream.getStatus().getStreamStatus().equals(filter))
				filterdList.add(stream);
		}
		return filterdList;
	}

	/**
	 * this method saves current broadcast data on window closing event
	 */
	@SuppressWarnings("unchecked")
	public void saveData() {
		JSONObject obj = new JSONObject();
    	
    	if(Constants.RegularBroadcast) {
    		System.out.println("closing regular broadcast");
    		
    		//save regular broadcast flag
    		obj.put("Regular Broadcast", "ON");
    		obj.put("Interval Broadcast", "OFF");
    		
    		//save checked streams
    		JSONArray checkedStreamList = new JSONArray();
    		for(Boolean checked: checkedStreams)
    			checkedStreamList.add(checked);
    		obj.put("Stream List", checkedStreamList);
    		
    	}
    	else if(Constants.IntervalBroadcast) {
    		System.out.println("closing inteval broadcast");
    	
    		//save interval broadcast flag
    		obj.put("RegularBroadcast", "OFF");
    		obj.put("IntervalBroadcast", "ON");
    		
    		//save current interval stop and start time
    		obj.put("Stop Time", Interval.getInstance().getCorrentInterval().toString());
    		obj.put("Start Time", IntervalPanel.getInstance().getLblstime().getText());
    		
    		//save interval
    		obj.put("Interval", Interval.getInstance().getInterval());
    		
    		//save checked streams
    		JSONArray checkedStreamList = new JSONArray();
    		for(Boolean checked: checkedStreams)
    			checkedStreamList.add(checked);
    		obj.put("Stream List", checkedStreamList);
    		System.out.println("stream list size: "+ checkedStreamList.size());
    		
    	}
    	else {
    		obj.put("RegularBroadcast", "OFF");
    		obj.put("IntervalBroadcast", "OFF");
    	}
    	try (FileWriter file = new FileWriter("src/main/resources/saved_status.json")) {
			file.write(obj.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	/**
	 * this method loads current broadcast data when window is opening
	 */
	@SuppressWarnings("unchecked")
	public void loadData() {
		
		JSONParser parser = new JSONParser();
		 
        try {
 				
            Object obj = parser.parse(new FileReader(
                    "src/main/resources/saved_status.json"));
 
            JSONObject jsonObject = (JSONObject) obj;
 			
            String RegularBroadcast = (String) jsonObject.get("RegularBroadcast");
            if(RegularBroadcast.equals("ON")){
            	
            	//start regular flag
            	Constants.IntervalBroadcast = true;
            	//refresh broadcast to active panel
            	
            	//set checked streams
            	JSONArray streamList = (JSONArray) jsonObject.get("Stream List");
            	checkedStreams = new Boolean[streamList.size()];
            	streamList.toArray(checkedStreams);
            	
            	//toggle buttons
                ButtonPanel btnPnl =  ButtonPanel.getInstance();
                btnPnl.getStartBrdbtn().setEnabled(false);	
				btnPnl.getStopBrdbtn().setEnabled(true);
				btnPnl.getStopIntbtn().setEnabled(false);
				btnPnl.getStartIntBrdbtn().setEnabled(false);
            }
            String IntervalBroadcast = (String) jsonObject.get("IntervalBroadcast");
            if(IntervalBroadcast.equals("ON")){
            	
            	//set interval panel
            	Constants.IntervalBroadcast = true;
            	
            	//refresh broadcast to active panel
            	String[] args = {"refresh","active"};
    		    refreshBroadcasts(args);
    		    BroadcastPanel broadcastPanel =BroadcastPanel.getInstance();
	    		broadcastPanel.setData(broadcasts);			//set new data
    		    broadcastPanel.refresh();
    		    //set checked streams
    		    JSONArray streamList = (JSONArray) jsonObject.get("Stream List");
            	checkedStreams = new Boolean[streamList.size()];
            	streamList.toArray(checkedStreams);
            	for(int i=0 ; i<checkedStreams.length ; i++) {
            		System.out.println("checked streams at index: " + i  +" is: " +checkedStreams[i]);
            	}
            	System.out.println("checked streams ");
            	
                Interval interval =Interval.getInstance(); 				//load saved interval
                interval.setInterval((String) jsonObject.get("Interval"));
                IntervalPanel intervalPanel = IntervalPanel.getInstance();
                intervalPanel.getLblNotSet().setText(interval.getHours() +
						 " Hours and " + interval.getMinutes() +" minutes");

                SimpleDateFormat dateformat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
                String stp = (String) jsonObject.get("Stop Time");
				Date stoptime =  dateformat.parse(stp);
				System.out.println("stoptime on load: "+stoptime.toString());
				
				Date now = Date.from(LocalDateTime.now().atZone( ZoneId.systemDefault()).toInstant());
				
                if(stoptime.after(now)) {
                	 //if stop time hasn't passed yet
            		//set interval panel
                	System.out.println("setting interval panel");
                	updateIntervalPanel((String) jsonObject.get("Start Time"),stoptime.toString());
                	intervalPanel.getFtime().setVisible(true);						
                	intervalPanel.getLblstime().setVisible(true);
                	interval.setCorrentInterval(stoptime);										//set it to current interval
                	//start timer run
                	timerRunner = new TimerRunner(stoptime);
                	
                }
                else { 
                	//else stop current broadcasts ,start them again ,
                	//and start timer runner with new stop time
                	stopBroadcasts();
                	startBroadcast(checkedStreams);
                	timerRunner = new TimerRunner(calcStopTime());
                	intervalPanel = IntervalPanel.getInstance();
                	updateIntervalPanel(now.toString(),interval.getCorrentInterval().toString());
                	intervalPanel.getLblstime().setVisible(true);
    				intervalPanel.getFtime().setVisible(true);
                }
                
            	//toggle buttons on GUI
            	ButtonPanel btnPnl =  ButtonPanel.getInstance();
            	btnPnl.getStartIntBrdbtn().setEnabled(false); 
				btnPnl.getStopIntbtn().setEnabled(true);
				btnPnl.getStartBrdbtn().setEnabled(false);
				btnPnl.getStopBrdbtn().setEnabled(false);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}
	
	/**
	 * singleton instance retriever
	 * @return
	 */
	public static Controller getInstance() {
		if(instance==null)
			instance = new Controller();
		return instance;
	}
	
}
