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

import com.youtube.api.ErrorHandler;
import com.youtube.gui.IntervalPanel;
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
	
/**
 * schedule timer with new stop time , and same task
 */
public void scheduleTimer(Date time){
	timer.schedule(getTask(), time);
}
	
/**
 * Stops interval broadcast by canceling the timer and handling remaining live broadcasts corresponding to
 * users choice.
 * @throws InterruptedException , ParseException , IOException ,FileNotFoundException
 * @throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, 
 * @throws InvalidAlgorithmParameterException
 */
public void stopIntervalBroadcast() throws InterruptedException, InvalidKeyException,
		NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException
{
	if(!Constants.IntervalBroadcast) {	// if stop interval broadcast was pressed
		Controller controller = Controller.getInstance();
		String[] brdID = new String[Constants.LiveId.size()]; 
		Constants.LiveId.toArray(brdID);
		controller.getBroadcastsHandler().stopBroadcasts(brdID);  					     //stop all live broadcasts
		cancelTimer();								    //stop timer
		IntervalPanel.getInstance().updateIntervalPanel("","");
	}
}
	
public void cancelTimer() {
	timer.cancel();	
	timer.purge();
	System.out.println("Timer cancelled and purged");
}

/**
 * schedule timer with new stop time , and same task
 */
private void rescheduleTimer(){
	timer.schedule(getTask(), stopTime);
}

/***
 * this method regenerates a new task for the timer and retrieves it,
 * 
 * and reschedules the timer
 * @return task
 */
private TimerTask getTask() 
{
	TimerTask task = new TimerTask() 
	{
		@Override
		public void run() {
			try {
				if(Constants.Debug) {
					System.out.println("---------------------------------------");
					System.out.println("running handling itervals" + Thread.currentThread().getId());
				}
				
				Controller controller;
				controller = Controller.getInstance();
				
				//get all current broadcast ID to be completed
				String[] brdID = new String[Constants.LiveId.size()]; 
				Constants.LiveId.toArray(brdID);
				if(Constants.Debug) {
					System.out.println("id list:");
					for(String id : brdID) {
						System.out.println(id);
					}
					System.out.println("----------------starting new live broadcasts ------------------");
				}
				controller.getBroadcastsHandler().startBroadcast();
				
				synchronized (Constants.monitorLock) {
					if(Constants.Debug) {
						System.out.println("-------------Timer waits new broadcasts to start-----------");
					}
					Constants.monitorLock.wait();		//wait until all new broadcasts go live
					
				}
				if(Constants.Debug) {
					System.out.println("---------------Timer continues---------------");
				}
				controller.getBroadcastsHandler().stopBroadcasts(brdID);   
				Date newStartTime = stopTime;						
				stopTime = controller.getBroadcastsHandler().calcStopTime(); 				
				
				IntervalPanel.getInstance().updateIntervalPanel(newStartTime.toString(),stopTime.toString());
				rescheduleTimer();									
				
			}catch (InterruptedException | InvalidKeyException | NoSuchAlgorithmException
					| NoSuchPaddingException | IOException | InvalidAlgorithmParameterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				ErrorHandler.HandleLoadError(e1.toString());
			}
		}
	};
	return task;
}
	


}
