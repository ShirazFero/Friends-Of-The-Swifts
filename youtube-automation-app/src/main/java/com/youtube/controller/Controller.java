package com.youtube.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
import com.youtube.utils.Constants;

public class Controller {
	
	private List<LiveStream> streams;
	
	private List<LiveBroadcast> broadcasts;
	
	private static Interval interval;
	
	private TimerRunner timerRunner;
	
	public static Interval getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		if(Controller.interval==null)
			Controller.interval = new Interval();
		Controller.interval.setInterval(interval);
	}

	public static Date calcStopTime() {
		LocalDateTime now = LocalDateTime.now();		//set start time
		System.out.println("interval start time: " + now);
		now = now.plusHours(interval.getHours());		//calculate added interval hours
		now = now.plusMinutes(interval.getMinutes());	//calculate added interval minutes
		//covert to Date Object applicable with Timer.schedule(Task,Date)
		Date finishDatetime = Date.from( now.atZone( ZoneId.systemDefault()).toInstant());
		System.out.println("interval finish time: " + now.toString());
		return finishDatetime;
	}

	public void initStreams() {
		streams=ListStreams.run(null);
		interval= new Interval();
		
	}
	
	public void initBroadcasts() {
		String[] args = {"init","all"};
		broadcasts=ListBroadcasts.run(args);
		
	}
	
	public List<LiveStream> getStreams() {
		return streams;
	}
	
	public List<LiveBroadcast> getBroadcasts(){
		return broadcasts;
	}
	
	public void refreshStreams() {
		if(streams!=null)
			streams.clear();
		streams=ListStreams.run(null);
	}
	
	public void refreshBroadcasts(String filter) {
		String[] args = {"refresh",filter};
		if(broadcasts!=null)
			broadcasts.clear();
		broadcasts=ListBroadcasts.run(args);
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
	
	public void startBroadcast(Boolean[] checked) {
		String[] args = new String[2];	// args[0] = title , args[1] = end time
		List<LiveStream> streams = ListStreams.run(null);
		for(int i=streams.size()-1 ; i>=0 ; i--) {
			if(checked[i])	{
				args[0]= streams.get(i).getSnippet().getTitle();
				if(Constants.IntervalBroadcast) {
					Instant instant = Instant.ofEpochMilli(calcStopTime().getTime());
				    LocalTime finTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime();
					args[1]= finTime.toString()	;
				}
				else
					args[1]=null;
				System.out.println("starting "+ args[0]+"time "+args[1]);
				CreateBroadcast.run(args);
			}
		}
	}
	
	public void stopBroadcast(Boolean[] checked) {
		String[] args = {"refresh","active"};
		List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
		for(int i=returnedList.size()-1 ; i>=0 ;i--) {
			if(checked[i]) {
				args[0]=broadcasts.get(i).getSnippet().getTitle();
				CompleteBroadcast.run(args);
			}
		}
	}

	public void startTimerRunner() throws InterruptedException {
		timerRunner = new TimerRunner();
		timerRunner.doTasks(calcStopTime());
	}
	
	public void cancelTimerRunner() {
		timerRunner.stopInterval();
	}
	
}
