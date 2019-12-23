package com.youtube.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;

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
		rescheduleTimer();				//Schedule first interval broadcast
	}
	
	/***
	 * this method regenerates a new task for the timer and retrieves it,
	 * 
	 * and reschedules the timer
	 * @return task
	 */
	private TimerTask getTask() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					System.out.println("---------------------------------------");
					System.out.println("running handling itervals" +Thread.currentThread().getId());
					
					Controller controller;
					controller = Controller.getInstance();
					controller.stopBroadcasts();   //on scheduled time complete live broadcasts
					                  
					if(!Constants.IntervalBroadcast) {             // if resume live broadcasts was chosen
						System.out.println("cancelling timer after terminating last broadcasts");
						timer.cancel();                          // cancel timer after completing broadcasts on scheduled time 
						controller.updateIntervalPanel("","");	//remove start/finish date times from interval panel
						return;				                   //end timer runner after completing broadcasts on stop time
					}
					//System.out.println("calc new time and shcdule timer again");
					//set new start time
					Date newStartTime = stopTime;						
					// calculate next interval stop time
					stopTime = controller.calcStopTime(); 				
					
					controller.updateIntervalPanel(newStartTime.toString(),stopTime.toString());
					//Reschedule timer for next interval
					rescheduleTimer();									
					
					//----------------start live broadcasts again-------------------------
					System.out.println("creating broadcasts");
					controller.startBroadcast(controller.getCheckedStreams());
					
				}catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException
						| NoSuchPaddingException | IOException | InvalidAlgorithmParameterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		return task;
	}
	
	/**
	 * schedule timer with new stop time , and same task
	 */
	private void rescheduleTimer(){
		timer.schedule(getTask(), stopTime);
	}
	
	/**
	 * schedule timer with new stop time , and same task
	 */
	public void scheduleTimer(Date time){
		timer.schedule(getTask(), time);
	}
	
	/**
	 * stops interval broadcast by canceling the timer and handling remaining live broadcasts corresponding to
	 * users choice.
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void stopIntervalBroadcast() throws InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException {
		if(!Constants.IntervalBroadcast) {	// if stop interval broadcast was pressed
			String message= "Do you want to stop current live broadcasts now?",title="Stop Broadcast option";
			int reply =JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION); //ask if to stop current Broadcasts
			if(reply==JOptionPane.YES_OPTION) {						  // if pressed Yes, complete all live broadcasts
				Controller controller = Controller.getInstance();
				controller.stopBroadcasts();  					     //stop all live broadcasts
				timer.cancel();									    //stop timer
				controller.updateIntervalPanel("","");
				return;
			}
		}
	}
	
	public void cancelTimer() {
		
		timer.cancel();	
		timer.purge();
	}
}
