package com.youtube.controller;

import java.text.DateFormat;
import java.time.LocalTime;
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
import com.youtube.app.CompleteBroadcast;
import com.youtube.app.CreateBroadcast;

public class TimerRunner extends Thread {
	
	private Date stopTime;
	
	private Timer timer;
	
	public void doTasks(Date stoptime) {
		
		this.stopTime = stoptime;		//set first stop time
		System.out.println(stopTime);
		timer =  new Timer();			//init timer
		scheduleTimer();						//schdule next interval
		
		
	}
	
	public void scheduleTimer(){
		TimerTask task =new TimerTask() {
			@Override
			public void run() {
				System.out.println("running handaling itervals");
				//in scheduled time complete live broadcasts
				String[] args = {"refresh","active"};
				List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
				System.out.println("completing live broadcasts");
				for(LiveBroadcast broadcast : returnedList) {
					args[0]=broadcast.getSnippet().getTitle();
					System.out.println("completing "+args[0]);
					//CompleteBroadcast.run(args);
				}
			if(!Constants.IntervalBroadcast) {   // if resume live broadcasts was chosen
					System.out.println("cancelling timer after terminating last broadcasts");
					timer.cancel();             // cancel timer after completing broadcasts on scheduled time  
					return;					   //end timer runner after completing broadcasts on stop time
				}
				
				stopTime = Controller.calcStopTime(); // calculate next interval stop time
				System.out.println("calc new time and shcdule timer again on "+ stopTime);
				scheduleTimer();	//schedule timer for next interval
				args[1]=stopTime.toString();
														//start live broadcasts again
				List<LiveStream> streams = ListStreams.run(null);
				for(LiveStream stream:streams) {
					args[0]= stream.getSnippet().getTitle();
					System.out.println("creating broadcast: "+args[0]);
					//CreateBroadcast.run(args);
				}
			}
		};
		timer.schedule(task, stopTime);
	}
	
	public void stopInterval() {
		
		if(!Constants.IntervalBroadcast) {	// if stop interval broadcast was pressed
			String message= "Do you to stop current live broadcasts now?",title="Stop Broadcast option";
			int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
			if(reply==JOptionPane.YES_OPTION) {
				String[] args = {"refresh","active"};
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
