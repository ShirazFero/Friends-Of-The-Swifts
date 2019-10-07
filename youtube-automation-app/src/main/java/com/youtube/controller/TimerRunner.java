package com.youtube.controller;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;
import com.youtube.app.ListBroadcasts;
import com.youtube.app.ListStreams;
import com.youtube.utils.Constants;

public class TimerRunner extends Thread {
	
	private Date stopTime;
	
	private Timer timer;
	
	public void doTasks(Date stoptime) {
		
		this.stopTime = stoptime;		//set first stop time
		timer =  new Timer();			//initiate timer
		scheduleTimer();				//Schedule next interval
		
		
	}
	
	private TimerTask getTask() {
		TimerTask task =new TimerTask() {
			@Override
			public void run() {
				System.out.println("---------------------------------------");
				System.out.println("running handling itervals");
				//in scheduled time complete live broadcasts
				String[] args = {"refresh","active"};
				List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
				System.out.println("completing live broadcasts");
				for(LiveBroadcast broadcast : returnedList) {
					args[0]=broadcast.getSnippet().getTitle();
					//System.out.println("completing "+args[0]);
					//CompleteBroadcast.run(args);
				}
			if(!Constants.IntervalBroadcast) {   // if resume live broadcasts was chosen
					System.out.println("cancelling timer after terminating last broadcasts");
					timer.cancel();             // cancel timer after completing broadcasts on scheduled time  
					return;					   //end timer runner after completing broadcasts on stop time
				}
				
				System.out.println("calc new time and shcdule timer again on "+ stopTime);
				stopTime = Controller.calcStopTime(); // calculate next interval stop time
				
				scheduleTimer();	//schedule timer for next interval
				args[1]=stopTime.toString();
														//start live broadcasts again
				System.out.println("creating broadcasts");
				List<LiveStream> streams = ListStreams.run(null);	//need to get the correct streams that were  
				for(LiveStream stream:streams) {					// chosen on the first time
					args[0]= stream.getSnippet().getTitle();
					//System.out.println("creating broadcast: "+args[0]);
					//CreateBroadcast.run(args);
				}
			}
		};
		return task;
	}
	
	public void scheduleTimer(){
		timer.schedule(getTask(), stopTime);
	}
	
	public void stopInterval() {
		
		if(!Constants.IntervalBroadcast) {	// if stop interval broadcast was pressed
			String message= "Do you to stop current live broadcasts now?",title="Stop Broadcast option";
			int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
			if(reply==JOptionPane.YES_OPTION) {
				String[] args = {"refresh","active"};
				System.out.println("completing live broadcasts after click ok");
				List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
				for(LiveBroadcast broadcast : returnedList) {
					args[0]=broadcast.getSnippet().getTitle();
					System.out.println("completing "+args[0]);
					//CompleteBroadcast.run(args);
				}
				timer.cancel();
				return;
			}
		}
	}

}
