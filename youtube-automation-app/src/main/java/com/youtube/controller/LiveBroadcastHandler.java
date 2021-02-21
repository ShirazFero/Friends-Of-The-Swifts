package com.youtube.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger; 

import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.youtube.api.BroadcastCompleter;
import com.youtube.api.BroadcastStarter;
import com.youtube.api.ErrorHandler;
import com.youtube.api.ApiPoller;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.ProgressFrame;
import com.youtube.utils.Constants;

public class LiveBroadcastHandler {

	private List<LiveBroadcast> broadcasts;		//holds currently presented broadcasts
	private Boolean[] checkedBroadcasts;		//holds checked broadcasts from inputform
	private ArrayList<String> m_currentlyLive;
	private AtomicInteger m_percetageCounter;
	private TimerRunner timerRunner;						//holds current timer runner
	private LiveStreamsHandler m_streamHandler;
	
	public LiveBroadcastHandler(LiveStreamsHandler streamHandler) {
		m_streamHandler = streamHandler;
	}
	
	/**
	 * Retrieves  new list of broadcasts and filter it depending on args , delete old one if exists ,
	 * @param args
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws ParseException 
	 */
	public boolean refreshBroadcasts(String[] args) throws SecurityException, IOException  {
		if(broadcasts!=null)
			broadcasts.clear();
		broadcasts = YouTubeAPI.getInstance().requestBroadcastList(args);
		if(broadcasts == null) {
			return false;
		}
		handlePageTokens();
		return true;
	}

	/**
	 * This method starts new live broadcast to every stream that was chosen on the GUI
	 * @param checked array indicates whether a stream was chosen or not
	 * Constants.isLive array delivers data to loading tasks regarding the progress of starting broadcast.
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void startBroadcast() throws InterruptedException {
		Constants.badResults =  new ArrayList<String>();
		m_currentlyLive =  new ArrayList<String>();
		String[] checkedStreams = m_streamHandler.getCheckedStreams();
		m_percetageCounter = new AtomicInteger(checkedStreams.length * 2);
		initLoadProgressFrame();
		startApiPolling();
		startGoLiveThreads(checkedStreams);
	}

	public  Date calcStopTime() {
		Interval interval = Interval.getInstance();
		LocalDateTime now = LocalDateTime.now();		//set start time
		Constants.DebugPrint("interval start time: " + now);
		now = now.plusHours(interval.getHours());		//calculate added interval hours
		now = now.plusMinutes(interval.getMinutes());	//calculate added interval minutes
		
		//convert to Date Object to be applicable with Timer.schedule(Task,Date)
		Date finishDatetime = Date.from( now.atZone( ZoneId.systemDefault()).toInstant());
		interval.setCorrentInterval(finishDatetime);	//set current end time
		return finishDatetime;
	}	
	
		/**
	 * This method stops all active live broadcasts
	 * @throws InterruptedException 
	 * @throws ParseException 
	 */
	public void stopBroadcasts(ArrayList<String> broadcastIDs) throws InterruptedException 
	{
		startApiPolling();
		if(broadcastIDs != null && !broadcastIDs.isEmpty()) {
			m_percetageCounter = new AtomicInteger(broadcastIDs.size());
			initCompleteProgressFrame();
			for(String ID : broadcastIDs) {
				BroadcastCompleter cmpBrd = new BroadcastCompleter(ID, m_percetageCounter);
				cmpBrd.start();
				Constants.DebugPrint("stopped "+ ID);
				Thread.sleep(1000);	// wait 1 second, better handles server requests
			}
		}
	}
	
	/**
		 * @return the m_currentlyLive
		 */
	public ArrayList<String> getCurrentlyLive() {
		return m_currentlyLive;
	}

	public void startTimerRunner() throws InterruptedException {
		timerRunner = new TimerRunner(calcStopTime());
	}

	public void setTimerRunner(Date stoptime) throws InterruptedException {
			timerRunner = new TimerRunner(stoptime);
	}

	/**
	 * This method stops timer runner object
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void cancelTimerRunner() throws InterruptedException, IOException {
		timerRunner.stopIntervalBroadcast();
	}
	
	public TimerRunner getTimerRunner() {
		return timerRunner;
	}
	
	/**
	 * Check which broadcast was chosen to update description and request an update
	 * @param decription
	 * @throws IOException
	 */
	public void updateDescription(String decription) throws IOException {
		
		Constants.BroadcastsToUpdate = new ArrayList<LiveBroadcast>();
		for(int i = 0;i<checkedBroadcasts.length ;i++) {
			if(checkedBroadcasts[i]) {
				Constants.BroadcastsToUpdate.add(broadcasts.get(i));
			}
		}
		if(Constants.BroadcastsToUpdate.size()>100) {
			JOptionPane.showMessageDialog(null,"Please select less then 100 broadcasts","Server request ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
			
		Constants.Description = decription;
		initUpdateProgressFrame();
	}
	
	private void initUpdateProgressFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ProgressFrame().updateTask();
				}  catch (IOException e) {
					e.printStackTrace();
					ErrorHandler.HandleError("Boot",  e.toString());
				}
			}
		});
	}
	
	public List<LiveBroadcast> getBroadcasts(){
		return broadcasts;
	}
	
	public void setCheckedBroadcasts(Boolean[] checkedBroadcasts) {
		this.checkedBroadcasts = checkedBroadcasts;
	}
	
	private void setVisability(JButton btn, boolean isVisable)
	{
		btn.setEnabled(isVisable);
		btn.setVisible(isVisable);
	}
	
	private void handlePageTokens() {
		BroadcastPanel bpanel = BroadcastPanel.getInstance();
		if(Constants.NextPageToken!=null) {
			setVisability(bpanel.getBtnNextPage(),true);
		}
		else {
			setVisability(bpanel.getBtnNextPage(),false);
		}
		if(Constants.PrevPageToken!=null) {
			setVisability(bpanel.getBtnPreviousPage(),true);
		}
		else {
			setVisability(bpanel.getBtnPreviousPage(),false);
		}
	}
	
	private void initLoadProgressFrame() {
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				try {
					Constants.DebugPrint("satrting loading task frame");
					new ProgressFrame().loadTask(m_percetageCounter);
				} catch (IOException e) {
					e.printStackTrace();
					ErrorHandler.HandleError("Boot",  e.toString());
				}
			}
		});
	}
	
	private void startApiPolling() {
		ApiPoller listpoll = new ApiPoller();
		Constants.pollingState = true;
		listpoll.start();
	}
	
	private String[] initBroadcastArgs(String streamId) {
		String[] args = new String[2];			
		args[0]= m_streamHandler.getStreamTitleFromId(streamId);		
		if(Constants.IntervalBroadcast) {		
			LocalDateTime finTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(calcStopTime().getTime())
					, ZoneId.systemDefault());
			args[1]= finTime.toString()	;
		}
		else {  								
			args[1] = null;
		}
		return args;
	}
	
	private void startGoLiveThreads(String[] checkedStreams) throws InterruptedException
	{
		for(String streamId : checkedStreams) {
			Constants.DebugPrint("inside for :starting " + streamId);
			String[] args = initBroadcastArgs(streamId); // args[0] = title , args[1] = end time
			BroadcastStarter brd =  new BroadcastStarter(args, m_currentlyLive, m_percetageCounter);	// initiate new CreateBroadcast Thread
			brd.start();										// start new thread
			Thread.sleep(1000);									// wait 1 second, better handles server requests
		}
	}
	
	private void initCompleteProgressFrame()
	{
		SwingUtilities.invokeLater(new Runnable() { //start loading frame
			public void run() {
				try {
					new ProgressFrame().completeTask(m_percetageCounter);
				} catch (IOException e) {
					e.printStackTrace();
					ErrorHandler.HandleError("Boot",  e.toString());
				}
			}
		});
	}
	
}
