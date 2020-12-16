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

import org.json.simple.parser.ParseException;

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
 * @param String[] args ={ "broadcast title" is identical to livestream name,"scheduled end time" - could be null}
 */
public  void run() 
{	
	try {
    	
    	Object lock = new Object();
    	LiveBroadcast returnedBroadcast;
    	synchronized (lock) {
    		
            LiveStream returnedStream = YouTubeAPI.getStreamByName(args[0]);
            
            if(!VerifyStream(returnedStream)) {
            	return;
            }
            
            String BroadcastDescription = returnedStream.getSnippet().getDescription();
            LiveBroadcast broadcast = initLiveBroadcast(BroadcastDescription);

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
        	if(!checkStreamStatus(returnedStream)) {
        		return;
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
       if(Constants.Debug) {
    	   System.out.println(returnedBroadcast.getStatus().getLifeCycleStatus());
       }
       
       //poll while test starting (wait while starting preview)
       if(!pollTransition(returnedBroadcast,"testStarting")) {
    	   return;
       }
      
       if(Constants.Debug) {
           	System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus());
       }
       
       synchronized (lock) {
    	   Constants.isLive--;							// set 50% OF broadcast starting completed
    	   halfWayflag = true;							// mark 50 % of starting passed
    	   YouTube.LiveBroadcasts.Transition requestLive = YouTubeAPI.youtube.liveBroadcasts()
                .transition("live", returnedBroadcast.getId(), "snippet,status"); //request transition to live
    	   returnedBroadcast = requestLive.execute();
       }
       
       if(!pollTransition(returnedBroadcast,"liveStarting")) {
    	   return;
       }
       
       Thread.sleep(1000);
       
       returnedBroadcast = YouTubeAPI.getBroadcastFromPolledList(returnedBroadcast.getId());
        
       if(Constants.Debug) {
    	   System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus() + " " + args[0]);
       }
       
       synchronized (lock) {
    	   Constants.isLive--;
    	   Constants.LiveId.add(returnedBroadcast.getId());
       }
    } catch (GoogleJsonResponseException e) {
        System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                + e.getDetails().getMessage());
        e.printStackTrace();
        try {
			reportError("Error code: " + e.getDetails().getCode()+", " + e.getDetails().getMessage());
		} catch (SecurityException | IOException e1) {
			e1.printStackTrace();
		}
        return;
    } catch (IOException e) {
        System.err.println("IOException: " + e.getMessage());
        e.printStackTrace();
        try {
			reportError(e.getMessage());
		} catch (SecurityException | IOException e1) {
			e1.printStackTrace();
		}
        return;
    } catch (Throwable t) {
        System.err.println("Throwable: " + t.getMessage());
        t.printStackTrace();
        try {
			reportError(t.getMessage());
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
        return;
    }
    return;
}

private LiveBroadcast initLiveBroadcast(String  description) throws IOException, ParseException 
{
      String title = setTitle();// set title for the broadcast.
      if(Constants.Debug) {
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

private boolean VerifyStream(LiveStream fetchedStream) throws SecurityException, IOException 
{
	 if(fetchedStream==null) {
     	System.out.println("stream doesn't exist please try again");
     	reportError("stream not found");
     	return false;
     }
     if(!fetchedStream.getStatus().getStreamStatus().equals("active") || 
    		 fetchedStream.getStatus().getHealthStatus().getStatus().equals("noData")) {
     	System.out.println("stream is not active please start the stream and run this again");
     	reportError("stream not active , not recieving data ");
     	return false;
     }
     return true;
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

private boolean checkStreamStatus(LiveStream fetchedStream) throws SecurityException, IOException 
{
	 //stream status check needed here to make sure it is active
    if(!fetchedStream.getStatus().getStreamStatus().equals("active")) {
    	if(Constants.Debug) {
    		System.out.println("stream is not active please start sending data on the stream");
    	}
    	args[0] +=" Stream not active";
    	reportError("stream not active"); //handle error on GUI
    	return false;
    }
    else {
    	if(Constants.Debug) {
    		System.out.println("stream is active starting transition to live");
    	}
    	return true;
    }
}

/***
 * poll while broadcast status transitions to it's next phase (ready - > testing -> live)
 * @param returnedBroadcast
 * @param transition
 * @return
 * @throws InterruptedException
 * @throws IOException
 */
private boolean pollTransition(LiveBroadcast returnedBroadcast, String transition) throws InterruptedException, IOException 
{
	while(returnedBroadcast.getStatus().getLifeCycleStatus().equals(transition)) {
    	synchronized (Constants.PollLock) {
 		   	if(Constants.Debug) {
 		   		System.out.println("Thread "+Thread.currentThread().getId()+" waits");
 		   	}
				Constants.PollLock.wait();		//wait for status update
 	   }
 	   if(Constants.Debug) {
 		   System.out.println("Thread "+Thread.currentThread().getId()+ " continues" );
 	   }
      
	   LiveBroadcast tempBroadcast =YouTubeAPI.getBroadcastFromPolledList(returnedBroadcast.getId());
	   if(tempBroadcast!=null) {
		   returnedBroadcast = tempBroadcast;
	   }
		
	   if(Constants.Debug) {
		   System.out.println("polling " + transition +" " + args[0]);
	   }
	   
	   if(Constants.pollingCount == Constants.MaxPolls) {	// if more then 90 seconds passed and Broadcast wasn't transitioned to Testing
       		args[0] +=" on" +transition + " Transition";
       		reportError ("100 secs passed no response on " +transition);
       		return false;
	   }
    }
    return true;
}

/**
 * this method prompts to the GUI about an error occurrence
 * @throws IOException 
 * @throws SecurityException 
 */
private synchronized void reportError(String error) throws SecurityException, IOException 
    {
    	if(halfWayflag)	{
    		Constants.isLive--;
    	}		
    	else {
    		Constants.isLive-=2;
    	}
    	Constants.badResults.add(args[0] + ": "+ error);
    	ErrorHandler.HandleApiError(args[0] + ": "+ error);
    }	
	
}


