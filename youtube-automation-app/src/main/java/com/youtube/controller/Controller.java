package com.youtube.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;
import com.youtube.app.CompleteBroadcast;
import com.youtube.app.CreateBroadcast;
import com.youtube.app.CreateStream;
import com.youtube.app.DeleteStream;
import com.youtube.app.ListBroadcasts;
import com.youtube.app.ListStreams;
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
	
	
	public  Boolean[] getCheckedStreams() {
		return checkedStreams;
	}

	public void setCheckedStreams(Boolean[] checkedStreams) {
		this.checkedStreams = checkedStreams;
	}

	public  Date calcStopTime() {
		Interval interval = Interval.getInstance();
		LocalDateTime now = LocalDateTime.now();		//set start time
		System.out.println("interval start time: " + now);
		now = now.plusHours(interval.getHours());		//calculate added interval hours
		now = now.plusMinutes(interval.getMinutes());	//calculate added interval minutes
		
		//convert to Date Object applicable with Timer.schedule(Task,Date)
		Date finishDatetime = Date.from( now.atZone( ZoneId.systemDefault()).toInstant());
		
		System.out.println("interval finish time: " + now.toString());
		System.out.println("interval finish Date object: "+finishDatetime.toString());
		interval.setCorrentInterval(finishDatetime);	//set current end time
		return finishDatetime;
	}
	
	public List<LiveStream> getStreams() {
		return streams;
	}
	
	public List<LiveBroadcast> getBroadcasts(){
		return broadcasts;
	}
	
	//retrieves  new list of streams , delete old one if exists
	public void refreshStreams() {
		if(streams!=null)
			streams.clear();
		streams=ListStreams.run(null);
		if(streams==null) {
			System.out.println("streams wasn't set correctly");
		}
			
	}
	
	//retrieves  new list of broadcasts and filter it depending on args , delete old one if exists ,
	public void refreshBroadcasts(String[] args) {
		if(broadcasts!=null)
			broadcasts.clear();
		broadcasts=ListBroadcasts.run(args);
		if(broadcasts==null) {
			System.out.println("broadcasts wasn't set correctly");
		}
	}
	
	public void addStream() {
		String[] args = new String[1];
		args[0]=JOptionPane.showInputDialog("please enter stream name");
		CreateStream.run(args);
		refreshStreams();
	}
	
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
	
	public void startBroadcast(Boolean[] checked) throws InterruptedException {
		
		List<LiveStream> streams = filterStreams("active");		
		CreateBroadcast brd =null;
		Constants.isLive = new Boolean[checked.length];
		for(int i=0;i<checked.length;i++)
			Constants.isLive[i]=false;
		System.out.println("loading frame starting...isLive length"+Constants.isLive.length);
		new LoadingFrame();
		
		for(int i = streams.size()-1 ; i>=0 ; i--) {
			if(checked[i])	{
				String[] args = new String[2];	// args[0] = title , args[1] = end time
				args[0]= streams.get(i).getSnippet().getTitle();
				if(Constants.IntervalBroadcast) {	//calculate interval end time and set it as args 
					Instant instant = Instant.ofEpochMilli(calcStopTime().getTime());
					LocalDateTime finTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
					args[1]= finTime.toString()	;
				}
				else { //if it's indefinite Broadcast no end time is set
					args[1]=null;
				}
				if(args[1]!=null)
					System.out.println("starting "+ args[0]+" end time: "+args[1]);
				else
					System.out.println("starting "+ args[0]);
				brd =  new CreateBroadcast(args);				// initiate new CreateBroadcast object
				brd.setQueueNum(streams.size()-1-i);   			// set broadcasts queue num
				brd.start();									// start new thread
				Thread.sleep(1000);								// wait 1 second, better handles server requests
			}
		}
		
	
	}
	
	public void stopBroadcast(Boolean[] checked) {
		String[] args = {"refresh","active"};
		List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
		for(int i=returnedList.size()-1 ; i>=0 ;i--) {
			if(checked[i]) {
				args[0]=broadcasts.get(i).getSnippet().getTitle();
				CompleteBroadcast cmpBrd = new CompleteBroadcast(args);
				cmpBrd.start();
			}
		}
	}

	public void startTimerRunner() throws InterruptedException {
		timerRunner = new TimerRunner(calcStopTime());
	}
	
	public void cancelTimerRunner() {
		timerRunner.stopIntervalBroadcast();
	}

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

	public static Controller getInstance() {
		if(instance==null)
			instance = new Controller();
		return instance;
	}
	
}
