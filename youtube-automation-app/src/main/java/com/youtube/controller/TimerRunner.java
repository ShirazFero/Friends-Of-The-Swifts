package com.youtube.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.youtube.app.CompleteBroadcast;
import com.youtube.app.ListBroadcasts;
import com.youtube.utils.Constants;
/**
 * this class generates a timed function  which operates according to scheduled interval
 * - completes relevant broadcasts in scheduled time
 * - calculates next interval start and finish time 
 * - sets new timer with a timer task
 * - starts broadcasts again
 * 
 * @author Evgeny Geyfman
 *
 */

public class TimerRunner {
	
	private Date stopTime;				//Date object compatible with Timer object
	
	private Timer timer;				//Pointer to current timer
	
	public TimerRunner(Date stoptime) {
		this.stopTime = stoptime;		//set first stop time
		timer =  new Timer();			//initiate timer
		scheduleTimer();				//Schedule first interval broadcast
	}
	
	/***
	 * this method regenerates the a new task for the timer and retrieves it,
	 * 
	 * and reschedules the timer
	 * @return task
	 */
	private TimerTask getTask() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("---------------------------------------");
				System.out.println("running handling itervals");
				//in scheduled time complete live broadcasts
				String[] args = {"refresh","active"};
				CompleteBroadcast cmpBrd = null;
				List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
				System.out.println("completing live broadcasts");
				for(LiveBroadcast broadcast : returnedList) {
					String[] args1 = new String[2];
					args1[0]=broadcast.getSnippet().getTitle();
					System.out.println("completing "+args1[0]);
					cmpBrd = new CompleteBroadcast(args1);  //create new CompleteBroadcast instance,set args
					cmpBrd.start();											//start new Thread
				}
				
				Controller controller = Controller.getInstance();	//get controller instance
				
				if(!Constants.IntervalBroadcast) {   // if resume live broadcasts was chosen
					System.out.println("cancelling timer after terminating last broadcasts");
					timer.cancel();             // cancel timer after completing broadcasts on scheduled time 
					controller.updateIntervalPanel("","");	//remove start/finish date times from interval panel
					return;					   //end timer runner after completing broadcasts on stop time
				}
				
				System.out.println("calc new time and shcdule timer again");
				Date newStartTime = stopTime;						//set new start time
				
				stopTime = controller.calcStopTime(); 				// calculate next interval stop time
				
				
				controller.updateIntervalPanel(newStartTime.toString(),stopTime.toString());
				
				scheduleTimer();	//Reschedule timer for next interval
				Instant instant = Instant.ofEpochMilli(stopTime.getTime()); //calc and set next finish time arg
				args[1]=LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime().toString();
				
				//----------------start live broadcasts again-------------------------
				System.out.println("creating broadcasts");
				try {
					controller.startBroadcast(controller.getCheckedStreams());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		return task;
	}
	/**
	 * schedule timer with new stop time , and same task
	 */
	public void scheduleTimer(){
		timer.schedule(getTask(), stopTime);
	}
	
	/**
	 * stops interval broadcast by canceling the timer and handling remaining live broadcasts corresponding to
	 * users choice.
	 */
	public void stopIntervalBroadcast() {
		if(!Constants.IntervalBroadcast) {	// if stop interval broadcast was pressed
			String message= "Do you to stop current live broadcasts now?",title="Stop Broadcast option";
			int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION); //ask if to stop current Broadcasts
			if(reply==JOptionPane.YES_OPTION) {								// if yes, complete all live broadcasts
				String[] args = {"refresh","active"};
				System.out.println("completing live broadcasts after click ok");
				List<LiveBroadcast> returnedList =ListBroadcasts.run(args);
				CompleteBroadcast cmpBrd =null;
				for(LiveBroadcast broadcast : returnedList) {
					String[] args1 = new String[2];
					args1[0]=broadcast.getSnippet().getTitle();
					System.out.println("completing "+args1[0]);
					cmpBrd = new CompleteBroadcast(args1); //create new CompleteBroadcast instance,set args
					cmpBrd.start();											//start new Thread
				}
				timer.cancel();												//stop timer
				Controller controller = Controller.getInstance();
				controller.updateIntervalPanel("","");
				return;
			}
		}
	}

}
