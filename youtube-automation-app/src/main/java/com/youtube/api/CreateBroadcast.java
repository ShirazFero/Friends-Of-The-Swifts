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

/**
 * Use the YouTube Live Streaming API to insert a broadcast and retrieve a stream from the stream list
 * and then bind them together, then start the live broadcast. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Evgeny Geyfman
 */
public class CreateBroadcast extends Thread{

	private boolean halfWayflag ;

	private String[] args;

	public CreateBroadcast(String[] args) {
		this.args = args;
		this.halfWayflag = false;
	}
	/**
     * Create a liveBroadcast,retrieve a relevant stream by it's title 
     * bind them together a and insert resource.
     * Finally transition to preview mode,  and transition into live stream
     * 
     * @param String[] args ={ "broadcast title","scheduled end time" - could be null}
     */
    public  void run() {	
    	
        try {
        	
        	Object lock = new Object();
        	LiveBroadcast returnedBroadcast;
        	synchronized (lock) {
	
	        	//System.out.println("Thread "+ Thread.currentThread().getId() + " starting "+args[0]);
	        	//Retrieve  a stream by it's title from args
	            LiveStream returnedStream = YouTubeAPI.getStreamByName(args[0]);
	            if(returnedStream==null) {
	            	System.out.println("stream doesn't exist please try again");
	            	reportError("stream not found");
	            	return;
	            }
	            if(!returnedStream.getStatus().getStreamStatus().equals("active") || 
	            		returnedStream.getStatus().getHealthStatus().getStatus().equals("noData")) {
	            	System.out.println("stream is not active please start the stream and run this again");
	            	reportError("stream not active , not recieving data ");
	            	return ;
	            }
	            
	            
	            String title ;// set title for the broadcast.
	            if(Constants.AddDateTime)
	            	 title = args[0]+ " " + LocalTime.now()+" "+LocalDate.now();
	            else
	        		 title = args[0];
	            System.out.println("You chose " + title + " for broadcast title.");
	
	            // Create a snippet with the title and scheduled start and end
	           
	            // times for the broadcast.
	            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
	            broadcastSnippet.setTitle(title);
	            
	            //set start time as current time
	            broadcastSnippet.setScheduledStartTime(new DateTime(LocalDate.now()+"T"+LocalTime.now()+"Z"));
	            
	            //set description of stream
	            broadcastSnippet.setDescription(returnedStream.getSnippet().getDescription()); 
	          
	            if(args[1]!=null)	//set scheduled end time if exists
	            	broadcastSnippet.setScheduledEndTime(new DateTime(args[1]+"Z"));
	            else
	            	broadcastSnippet.setScheduledEndTime(null);			//indefinite broadcast
	            
	            // Set the broadcast's privacy status to "public". 
	            //See: https://developers.google.com/youtube/v3/live/docs/liveBroadcasts#status.privacyStatus
	            LiveBroadcastStatus status = new LiveBroadcastStatus();
	            status.setPrivacyStatus(Constants.Privacy);
	           
	            LiveBroadcast broadcast = new LiveBroadcast();
	            broadcast.setKind("youtube#liveBroadcast");
	            broadcast.setSnippet(broadcastSnippet);
	            broadcast.setStatus(status);
	
	            // Construct and execute the API request to insert the broadcast.
	            YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
	            		YouTubeAPI.youtube.liveBroadcasts().insert("snippet,status", broadcast);
	            
            	 returnedBroadcast = liveBroadcastInsert.execute();
	            // Construct and execute a request to bind the new broadcast
	            // and stream.
	            YouTube.LiveBroadcasts.Bind liveBroadcastBind =
	            		YouTubeAPI.youtube.liveBroadcasts().bind(returnedBroadcast.getId(), "id,contentDetails");
	            liveBroadcastBind.setStreamId(returnedStream.getId());
	        	returnedBroadcast = liveBroadcastBind.execute();
	            //stream status check needed here to make sure it is active
	            if(!returnedStream.getStatus().getStreamStatus().equals("active")) {
	            	System.out.println("stream is not active please start sending data on the stream");
	            	args[0] +=" Stream not active";
	            	reportError("stream not active"); //handle error on GUI
	            }
	            else {
	            	System.out.println("stream is active starting transition to live");
	            }
	            //transition to testing mode (preview mode)
	    	   	YouTube.LiveBroadcasts.Transition requestTesting = YouTubeAPI.youtube.liveBroadcasts()
	                  .transition("testing", returnedBroadcast.getId(), "snippet,status");
	    	   	returnedBroadcast = requestTesting.execute();
		    	   	
           }
           synchronized (Constants.PollStartLock) {
				Constants.PollStartLock.notifyAll();	//start polling the api 
           } 
           Thread.sleep(2000);
           //Prompt request status
           System.out.println(returnedBroadcast.getStatus().getLifeCycleStatus());
           
           int seconds = 0; // second counter for  server response to transition
           
           //poll while test starting (wait while starting preview)
           while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("testStarting")) {
        	   synchronized (Constants.PollLock) {
					System.out.println("Thread "+Thread.currentThread().getId()+" waits");
					Constants.PollLock.wait();		//wait for status update
				}
        	   System.out.println("Thread "+Thread.currentThread().getId()+ " continues" );
	    	   LiveBroadcast tempBroadcast =YouTubeAPI.getBroadcastFromPolledList(returnedBroadcast.getId());
	    	   if(tempBroadcast!=null)
	    		   returnedBroadcast = tempBroadcast;
	    	   System.out.println("polling testStarting "+args[0]);
	    	   if(seconds>90) {	// if more then 90 seconds passed and Broadcast wasn't transitioned to Testing
	           		args[0] +=" on Transition to Testing";
	           		reportError ("90 secs passed no response on testing transiton");
	       		}
	       		else
	       			seconds++;
           }
           
           //preview started
           System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus());
           synchronized (lock) {
        	   Constants.isLive--;						// set 50% OF broadcast starting completed
			
           halfWayflag = true;							// mark 50 % of starting passed
           //transition to live  mode
            YouTube.LiveBroadcasts.Transition requestLive = YouTubeAPI.youtube.liveBroadcasts()
                    .transition("live", returnedBroadcast.getId(), "snippet,status");
            returnedBroadcast = requestLive.execute();
           }
            //poll while live starting (wait while starting live)
           seconds = 0;
           while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("liveStarting")) {
            	
        	   synchronized (Constants.PollLock) {
        		   System.out.println("Thread "+Thread.currentThread().getId()+" waits");
        		   Constants.PollLock.wait();		//wait for status update
        	   }
        	   System.out.println("Thread "+Thread.currentThread().getId()+ " continues" );
            	
        	   LiveBroadcast tempBroadcast =YouTubeAPI.getBroadcastFromPolledList(returnedBroadcast.getId());
        	   if(tempBroadcast!=null)
        		   returnedBroadcast = tempBroadcast;
        	   System.out.println("polling liveStarting "+args[0]);
        	   if(seconds>90) {	// if more then 90 seconds passed and Broadcast wasn't transitioned to live
        			args[0] +=" on Transition to Live";
        			reportError(" 90 secs passed no response on live transiton\n");
        	   }
        	   else
        		   seconds++;
           }
           Thread.sleep(1000);
           returnedBroadcast = YouTubeAPI.getBroadcastFromPolledList(returnedBroadcast.getId());
            
           //promt status to screen
           System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus()+" "+ args[0]);
           synchronized (lock) {
        	   Constants.isLive--;
           }
           Constants.LiveId.add(returnedBroadcast.getId());
           
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();
            reportError("Error code: " + e.getDetails().getCode()+", " + e.getDetails().getMessage());
            return;
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            reportError(e.getMessage());
            return;
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
            reportError(t.getMessage());
            return;
        }
        return;
    }
    
    /**
     * this method prompts to the GUI about an error occurrence
     */
    private synchronized void reportError(String error) {

    	if(halfWayflag)			
    		Constants.isLive--;
    	else
    		Constants.isLive-=2;
    	Constants.badResults.add(args[0] + ": "+ error);
    }	

}


