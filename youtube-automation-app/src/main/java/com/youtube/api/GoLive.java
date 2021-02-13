package com.youtube.api;
/*
  	Copyright (c) 2019 Evgeny Geyfman.
 	this application uses YouTube Live Streaming API, Copyright (c) 2013 Google Inc.
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License
	
 */

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.youtube.utils.Constants;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.parser.ParseException;

/**
 * Use the YouTube Live Streaming API to insert a broadcast and retrieve a stream from the stream list
 * and then bind them together, then start the live broadcast. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Evgeny Geyfman
 */
public class GoLive extends Thread {

	private boolean halfWayflag ;
	
	private String[] args;
	
	private ArrayList<String> m_currentlyLive;
	
	private AtomicInteger m_doneFlag;
	
	public GoLive(String[] args,  ArrayList<String> currLive, AtomicInteger doneFlag) {
		this.args = args;
		this.halfWayflag = false;
		m_currentlyLive = currLive;
		m_doneFlag = doneFlag;
	}

	/**
	 * Create a liveBroadcast,retrieve a relevant stream by it's title 
	 * bind them together a and insert resource.
	 * Finally transition to preview mode,  and transition into live stream
	 * 
	 * @param String[] args ={ "broadcast title" is identical to livestream name,"scheduled end time" - could be null}
	 */
	public void run() 
	{	
		try{
	    	
			LiveBroadcast response = initStartSequence();
			
    	    response = requestTransitionToMode(response,"testing");
    	    
    	    notifyPollStart(); 
    	    
    	    debugPrint(response);
    	    
    	    response = waitWhileModeTransitions(response,"testing");
    	    
    	    debugPrint(response);
	       
    	    markHalfWay();
    	    
    	    response = requestTransitionToMode(response,"live");
    	    
    	    response = waitWhileModeTransitions(response,"live");
	        
    	    debugPrint(response);
	       
    	    markFinished(response.getId());
	       
		} catch (GoogleJsonResponseException  e) {
	    	String errormsg = "Error code: " + e.getDetails().getCode()+", " + e.getDetails().getMessage();
	    	 if(Constants.DEBUG) {
	    		 System.out.println(errormsg);
	    		 e.printStackTrace();
	    	 }
			reportError(errormsg);
	    } catch (SecurityException | IOException | InterruptedException | ParseException e) {
	    	String errormsg = "Error: " + e.getMessage();
		   	if(Constants.DEBUG) {
		   		System.out.println(errormsg);
		   		e.printStackTrace();
		   	}
	        reportError(errormsg);
	    } 
	}
	
	private void debugPrint(LiveBroadcast response)
	{
		if(Constants.DEBUG) {
  	    	System.out.println(args[0]+ " is "+ response.getStatus().getLifeCycleStatus());
  	    }
	}
	
	private synchronized LiveBroadcast requestTransitionToMode(LiveBroadcast returnedBroadcast, String mode) throws IOException
	{
		//transition to testing mode (preview mode)
		assert(mode.equals("testing") || mode.equals("live"));
	   	YouTube.LiveBroadcasts.Transition requestTesting = YouTubeAPI.getInstance().getService().liveBroadcasts()
	          .transition(mode, returnedBroadcast.getId(), "snippet,status");
	   	return requestTesting.execute();
	}

	private synchronized void markHalfWay()
	{
		m_doneFlag.decrementAndGet();
		halfWayflag = true;	
	}
	
	private synchronized void markFinished(String brdId) 
	{
		 m_doneFlag.decrementAndGet();
		 m_currentlyLive.add(brdId);
	}
	
	private synchronized LiveBroadcast initStartSequence() throws SecurityException, IOException, ParseException 
	{
	
		YouTubeAPI youtubeApi = YouTubeAPI.getInstance();
		LiveStream returnedStream =  youtubeApi.getStreamByName(args[0]);
	    
		assertLiveStreamReady(returnedStream);
	    
	    String BroadcastDescription = returnedStream.getSnippet().getDescription();
	    LiveBroadcast broadcast = initLiveBroadcast(BroadcastDescription);
	
	    // Construct and execute the API request to insert the broadcast.
	    YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
	    		youtubeApi.getService().liveBroadcasts().insert("snippet,status", broadcast);
	    
	    LiveBroadcast response = liveBroadcastInsert.execute();
	    
	    // Construct and execute a request to bind the new broadcast and stream.
	    YouTube.LiveBroadcasts.Bind liveBroadcastBind =
	    		youtubeApi.getService().liveBroadcasts().bind(response.getId(), "id,contentDetails");
	    
	    liveBroadcastBind.setStreamId(returnedStream.getId());
	    return liveBroadcastBind.execute();
		
	}

	private void notifyPollStart() 
	{
		synchronized (Constants.PollStartLock) {
			Constants.PollStartLock.notifyAll();	//start polling the api 
        } 
	}
	
	private LiveBroadcast initLiveBroadcast(String  description) throws IOException, ParseException 
	{
	      String title = setTitle();// set title for the broadcast.
	      if(Constants.DEBUG) {
	    	  System.out.println("You chose " + title + " for broadcast title.");
	      }
	      // Create a snippet with the title and scheduled start and end
	      // times for the broadcast.
	      LiveBroadcastSnippet broadcastSnippet = setBroadcastSnippet(title, description);
	      
	      // Set the broadcast's privacy status to "public". 
	      //See: https://developers.google.com/youtube/v3/live/docs/liveBroadcasts#status.privacyStatus
	      LiveBroadcastStatus status = setBroadcastStatus();
	      
	      return createBroadcast(broadcastSnippet,status);
	}

	private void assertLiveStreamReady(LiveStream fetchedStream) throws SecurityException, IOException 
	{
		if(fetchedStream == null) {
			throw new IOException("stream doesn't exist please try again");
	    }
	    if(!fetchedStream.getStatus().getStreamStatus().equals("active")) {
	    	throw new IOException("stream is not active please start the stream and run this again");
	    }
	    if(fetchedStream.getStatus().getHealthStatus().getStatus().equals("noData")) {
	    	throw new IOException("stream is not not recieving data please check encoding software and try again");
	    }
	}

	private String setTitle() 
	{
		 if(Constants.AddDateTime) {
			 return args[0] + " " + LocalTime.now() + " " + LocalDate.now();
		 }
		 else {
	    	return  args[0];
		 }
	}

	/***
	 *   Create a snippet with the title and scheduled start and end
	 *   times for the broadcast.
	 * @param fetchedStream
	 * @param title
	 * @return
	 */
	private LiveBroadcastSnippet setBroadcastSnippet(String title, String description) 
	{
	    LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
	    broadcastSnippet.setTitle(title);
	    broadcastSnippet.setScheduledStartTime(new DateTime(LocalDate.now()+"T"+LocalTime.now()+"Z"));
	    broadcastSnippet.setDescription(description); 
	    if(args[1] != null)	
	    	broadcastSnippet.setScheduledEndTime(new DateTime(args[1]+"Z")); //set scheduled end time if exists
	    else
	    	broadcastSnippet.setScheduledEndTime(null);			            //indefinite broadcast
	    return broadcastSnippet;
	}

	private LiveBroadcastStatus setBroadcastStatus() 
	{
		 LiveBroadcastStatus status = new LiveBroadcastStatus();
	     status.setPrivacyStatus(Constants.Privacy);
	     return status;
	}

	private LiveBroadcast createBroadcast(LiveBroadcastSnippet broadcastSnippet, LiveBroadcastStatus status) 
	{
		LiveBroadcast broadcast = new LiveBroadcast();
	    broadcast.setKind("youtube#liveBroadcast");
	    broadcast.setSnippet(broadcastSnippet);
	    broadcast.setStatus(status);
	    return broadcast;
	}

	/***
	 * poll while broadcast status transitions to it's next phase (ready - > testing -> live)
	 * @param returnedBroadcast
	 * @param transition
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private LiveBroadcast waitWhileModeTransitions(LiveBroadcast returnedBroadcast, String mode) throws InterruptedException, IOException 
	{
		YouTubeAPI youtubeApi = YouTubeAPI.getInstance();
		while(!returnedBroadcast.getStatus().getLifeCycleStatus().equals(mode)) {
			synchronized (Constants.PollLock) {
	 		   	if(Constants.DEBUG) {
	 		   		System.out.println("Thread "+Thread.currentThread().getId()+" waits");
	 		   	}
				Constants.PollLock.wait();		//wait for status update
	 	   	}
			if(Constants.DEBUG) {
				System.out.println("Thread "+Thread.currentThread().getId()+ " continues" );
	 	   	}
	      
			LiveBroadcast tempBroadcast = youtubeApi.getBroadcastFromPolledList(returnedBroadcast.getId());
			if(tempBroadcast != null) {
				returnedBroadcast = tempBroadcast;
			}
			
			if(Constants.DEBUG) {
				System.out.println("polling " + mode + "Staring " + args[0]);
			}
		   
			if(Constants.pollingCount == Constants.MaxPolls) {	// if more then 90 seconds passed and Broadcast wasn't transitioned to Testing
	       			throw new IOException ("100 secs passed no response on " + mode + " staring ");
			}
	    }
		return returnedBroadcast;
	}

	/**
	 * this method prompts to the GUI about an error occurrence
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	private synchronized void reportError(String error) 
    {
		if(Constants.DEBUG) {
    		System.out.println(error);
    	}
		m_doneFlag.decrementAndGet();
		if(!halfWayflag) {
			m_doneFlag.decrementAndGet();
		}
    	Constants.badResults.add(args[0] + ": "+ error);
    	ErrorHandler.HandleApiError(args[0] + ": "+ error);
    }	
	
}
