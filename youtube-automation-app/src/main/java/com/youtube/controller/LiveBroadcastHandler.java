package com.youtube.controller;

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

import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.youtube.api.CompleteBroadcast;
import com.youtube.api.CreateBroadcast;
import com.youtube.api.ErrorHandler;
import com.youtube.api.ListPoll;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.ProgressFrame;
import com.youtube.utils.Constants;

public class LiveBroadcastHandler {

	private LiveStreamsHandler m_streamHandler;	
	
	private List<LiveBroadcast> broadcasts;		//holds currently presented broadcasts
	
	private  Boolean[] checkedBroadcasts;		//holds checked broadcasts from inputform
	
	public LiveBroadcastHandler(LiveStreamsHandler streamhandler) {
		m_streamHandler = streamhandler;
	}
	
	public List<LiveBroadcast> getBroadcasts(){
		return broadcasts;
	}
	
	/**
	 * Sets checked broadcasts from broadcast panel
	 * 
	 */
	public void setCheckedBroadcasts(Boolean[] checkedBroadcasts) {
		this.checkedBroadcasts = checkedBroadcasts;
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
		broadcasts = YouTubeAPI.listBroadcasts(args);
		if(broadcasts == null) {
			return false;
		}
		//handle page tokens
		BroadcastPanel bpanel = BroadcastPanel.getInstance();
		if(Constants.NextPageToken!=null) {
			bpanel.getBtnNextPage().setEnabled(true);
			bpanel.getBtnNextPage().setVisible(true);
		}
		else {
			bpanel.getBtnNextPage().setEnabled(false);
			bpanel.getBtnNextPage().setVisible(false);
		}
		if(Constants.PrevPageToken!=null) {
			bpanel.getBtnPreviousPage().setEnabled(true);
			bpanel.getBtnPreviousPage().setVisible(true);
		}
		else {
			bpanel.getBtnPreviousPage().setEnabled(false);
			bpanel.getBtnPreviousPage().setVisible(false);
		}
		return true;
	}

	/**
	 * This method starts new live broadcast to every stream that was chosen on the GUI
	 * @param checked array indicates whether a stream was chosen or not
	 * Constants.isLive array delivers data to loading tasks regarding the progress of starting broadcast.
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
	public void startBroadcast() throws InterruptedException 
	{
		Constants.badResults =  new ArrayList<String>();
		CreateBroadcast brd = null; //Pointer to currently created broadcast
		
		if(Constants.LiveId!= null && !Constants.LiveId.isEmpty()) {
			Constants.LiveId.clear();
		}
			
		Constants.LiveId = new ArrayList<String>();
		
		String[] checkedStreams = m_streamHandler.getCheckedStreams();
		
		Constants.isLive = checkedStreams.length * 2;	//init flag array to mark starting progress of broadcast
		if(Constants.Debug) {
			System.out.println("here starts load frame");
		}
		
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				try {
					if(Constants.Debug) {
						System.out.println("satrting loading task frame");
					}
					new ProgressFrame().loadTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					e.printStackTrace();
					ErrorHandler.HandleLoadError(e.toString());
				}
			}
		});
		ListPoll listpoll = new ListPoll();
		Constants.pollingState = true;
		listpoll.start();
		for(String streamId : checkedStreams) {
				System.out.println("inside for :starting " + streamId);
				String[] args = new String[2];			// args[0] = title , args[1] = end time
				args[0]= m_streamHandler.getStreamTitleFromId(streamId);		//set stream title arg
				if(Constants.IntervalBroadcast) {		//calculate interval end time and set it as args 
					LocalDateTime finTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(calcStopTime().getTime())
							, ZoneId.systemDefault());
					args[1]= finTime.toString()	;
					System.out.println("starting "+ args[0]+" end time: "+args[1]);
				}
				else {  								//if it's indefinite Broadcast no end time is set
					args[1]=null;
					System.out.println("starting "+ args[0]);
				}
				brd =  new CreateBroadcast(args);			// initiate new CreateBroadcast Thread
				brd.start();								// start new thread
				Thread.sleep(1000);							// wait 1 second, better handles server requests
		}
	}
	
	public  Date calcStopTime() {
		Interval interval = Interval.getInstance();
		LocalDateTime now = LocalDateTime.now();		//set start time
		System.out.println("interval start time: " + now);
		now = now.plusHours(interval.getHours());		//calculate added interval hours
		now = now.plusMinutes(interval.getMinutes());	//calculate added interval minutes
		
		//convert to Date Object to be applicable with Timer.schedule(Task,Date)
		Date finishDatetime = Date.from( now.atZone( ZoneId.systemDefault()).toInstant());
		
		//System.out.println("interval finish time: " + now.toString());
		//System.out.println("interval finish Date object: "+finishDatetime.toString());
		interval.setCorrentInterval(finishDatetime);	//set current end time
		return finishDatetime;
	}	
	
		/**
	 * This method stops all active live broadcasts
	 * @throws InterruptedException 
	 * @throws ParseException 
	 */
	public void stopBroadcasts(String[] broadcastIDs) throws InterruptedException 
{
	CompleteBroadcast cmpBrd = null;
	String[] args = new String[1];
	ListPoll listpoll = new ListPoll();
	Constants.pollingState = true;
	listpoll.start();
	if(broadcastIDs!=null) {
		Constants.isLive = broadcastIDs.length;	//init flag array to mark starting progress of broadcast
		SwingUtilities.invokeLater(new Runnable() { //start loading frame
			public void run() {
				try {
					new ProgressFrame().completeTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					e.printStackTrace();
					ErrorHandler.HandleLoadError(e.toString());
				}
			}
		});
		for(String ID : broadcastIDs) {
			args[0] =  ID;
			cmpBrd = new CompleteBroadcast(args);
			cmpBrd.start();
			System.out.println("stopped "+ args[0]);
			Thread.sleep(1000);	// wait 1 second, better handles server requests
		}
	}
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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new ProgressFrame().updateTask();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					e.printStackTrace();
					ErrorHandler.HandleLoadError(e.toString());
				}
			}
		});
		
	}
	
}
