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
					FileLogger.logger.info("---------------------------------------");
					FileLogger.logger.info("running handling itervals" +Thread.currentThread().getId());
					
					Controller controller;
					controller = Controller.getInstance();
					
					//get all current broadcast ID to be completed
					String[] brdID = new String[Constants.LiveId.size()]; 
					Constants.LiveId.toArray(brdID);
					for(String id : brdID) {
						FileLogger.logger.info("id list"+id);
					}
					
					//----------------start new live broadcasts -------------------------
					FileLogger.logger.info("creating broadcasts");
					controller.startBroadcast();
					
					synchronized (Constants.monitorLock) {
						FileLogger.logger.info("---------------Timer waits-------------------");
						Constants.monitorLock.wait();		//wait until all new broadcasts go live
						
					}
					FileLogger.logger.info("---------------Timer continues---------------");
					//----------------stop previous live broadcasts
					controller.stopBroadcasts(brdID);   //on scheduled time complete live broadcasts
					//System.out.println("calc new time and shcdule timer again");
					//set new start time
					Date newStartTime = stopTime;						
					// calculate next interval stop time
					stopTime = controller.calcStopTime(); 				
					
					controller.updateIntervalPanel(newStartTime.toString(),stopTime.toString());
					//Reschedule timer for next interval
					rescheduleTimer();									
					
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
				Controller controller = Controller.getInstance();
				String[] brdID = new String[Constants.LiveId.size()]; 
				Constants.LiveId.toArray(brdID);
				controller.stopBroadcasts(brdID);  					     //stop all live broadcasts
				cancelTimer();								    //stop timer
				controller.updateIntervalPanel("","");
		}
	}
	
	public void cancelTimer() {
		timer.cancel();	
		timer.purge();
		FileLogger.logger.info("Timer cancelled and purged");
	}
}
