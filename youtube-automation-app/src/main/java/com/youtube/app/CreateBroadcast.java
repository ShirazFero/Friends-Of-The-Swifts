package com.youtube.app;
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
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Use the YouTube Live Streaming API to insert a broadcast and retrieve a stream from the stream list
 * and then bind them together, then start the live broadcast. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Evgeny Geyfman
 */
public class CreateBroadcast extends Thread{

	private int queueNum;
	
	/**
	 * @param queueNum the queueNum to set
	 */
	public void setQueueNum(int queueNum) {
		this.queueNum = queueNum;
	}

	private String[] args;

	public CreateBroadcast(String[] args) {
		this.args = args;
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
        	System.out.println("Thread "+ Thread.currentThread().getId() + " starting "+args[0]);
        	//Retrieve  a stream by it's title from args
            LiveStream returnedStream = getStreamByName(args[0]);
            if(returnedStream==null) {
            	System.out.println("stream doesn't exist please try again");
            	return;
            }
            if(!returnedStream.getStatus().getStreamStatus().equals("active") || 
            		returnedStream.getStatus().getHealthStatus().getStatus().equals("noData")) {
            	System.out.println("stream is not active please start the stream and run this again");
            	return ;
            }
            
            // set title for the broadcast.
            String title = args[0] +" "+ LocalTime.now();
            System.out.println("You chose " + title + " for broadcast title.");

            // Create a snippet with the title and scheduled start and end
            // times for the broadcast.
            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
            broadcastSnippet.setTitle(title);
            broadcastSnippet.setScheduledStartTime(new DateTime(LocalDate.now()+"T"+LocalTime.now()+"Z"));
            if(args[1]!=null)	//set scheduled end time if exists
            	broadcastSnippet.setScheduledEndTime(new DateTime(args[1]+"Z"));
            else
            	broadcastSnippet.setScheduledEndTime(null);			//indefinite broadcast
            
            // Set the broadcast's privacy status to "public". 
            //See: https://developers.google.com/youtube/v3/live/docs/liveBroadcasts#status.privacyStatus
            LiveBroadcastStatus status = new LiveBroadcastStatus();
            status.setPrivacyStatus("public");
           
            LiveBroadcast broadcast = new LiveBroadcast();
            broadcast.setKind("youtube#liveBroadcast");
            broadcast.setSnippet(broadcastSnippet);
            broadcast.setStatus(status);

            // Construct and execute the API request to insert the broadcast.
            YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
            		CreateYouTube.getYoutube().liveBroadcasts().insert("snippet,status", broadcast);
            LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();
            if(Constants.DEBUG) {
	            // Print information from the API response.
	            System.out.println("\n================== Returned Broadcast ==================\n");
	            System.out.println("  - Id: " + returnedBroadcast.getId());
	            System.out.println("  - Title: " + returnedBroadcast.getSnippet().getTitle());
	            System.out.println("  - Status: " + returnedBroadcast.getStatus().getLifeCycleStatus());
	            System.out.println("  - Description: " + returnedBroadcast.getSnippet().getDescription());
	            System.out.println("  - Published At: " + returnedBroadcast.getSnippet().getPublishedAt());
	            System.out.println(
	                    "  - Scheduled Start Time: " + returnedBroadcast.getSnippet().getScheduledStartTime());
	            System.out.println(
	                    "  - Scheduled End Time: " + returnedBroadcast.getSnippet().getScheduledEndTime());
	            
	            // Print information from the API response.
	            System.out.println("\n================== Returned Stream ==================\n");
	            System.out.println("  - Id: " + returnedStream.getId());
	            System.out.println("  - Title: " + returnedStream.getSnippet().getTitle());
	            System.out.println("  - Status: " + returnedStream.getStatus().getStreamStatus());
	            System.out.println("  - Description: " + returnedStream.getSnippet().getDescription());
	            System.out.println("  - Published At: " + returnedStream.getSnippet().getPublishedAt());
            }
            
            // Construct and execute a request to bind the new broadcast
            // and stream.
            YouTube.LiveBroadcasts.Bind liveBroadcastBind =
            		CreateYouTube.getYoutube().liveBroadcasts().bind(returnedBroadcast.getId(), "id,contentDetails");
            liveBroadcastBind.setStreamId(returnedStream.getId());
            returnedBroadcast = liveBroadcastBind.execute();

            if(Constants.DEBUG){
	            // Print information from the API response.
	            System.out.println("\n================== Returned Bound Broadcast ==================\n");
	            System.out.println("  - Broadcast Id: " + returnedBroadcast.getId());
	            System.out.println(
	                    "  - Bound Stream Id: " + returnedBroadcast.getContentDetails().getBoundStreamId());
            }
            
          
            //stream status check needed here to make sure it is active
           if(!returnedStream.getStatus().getStreamStatus().equals("active")) {
        	   System.out.println("stream is not active please start sending data on the stream");
           }
           else {
        	   System.out.println("stream is active starting transition to live");
           }
           
          //transition to testing mode (preview mode)
    	   YouTube.LiveBroadcasts.Transition requestTesting = CreateYouTube.getYoutube().liveBroadcasts()
                  .transition("testing", returnedBroadcast.getId(), "snippet,status");
           returnedBroadcast = requestTesting.execute();
           //Prompt request status
           System.out.println(returnedBroadcast.getStatus().getLifeCycleStatus());
           
           //poll while test starting (wait while starting preview)
           while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("testStarting")) {
        	   returnedBroadcast = getBroadcastById(returnedBroadcast.getId());
        	   System.out.println("polling testStarting "+args[0]);
        	   Thread.sleep(1000);
           }
           //preview started
           System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus());
          
            //transition to live  mode
            YouTube.LiveBroadcasts.Transition requestLive = CreateYouTube.getYoutube().liveBroadcasts()
                    .transition("live", returnedBroadcast.getId(), "snippet,status");
            returnedBroadcast = requestLive.execute();
            //poll while live starting (wait while starting live)
            while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("liveStarting")) {
            	returnedBroadcast = getBroadcastById(returnedBroadcast.getId());
            	System.out.println("polling liveStarting "+args[0]);
            	Thread.sleep(1000);
            }
            Thread.sleep(1000);
            returnedBroadcast = getBroadcastById(returnedBroadcast.getId());
            //promt status to screen
            System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus()+" "+ args[0]);
            if(Constants.isLive!=null)
            	Constants.isLive[queueNum]=true;
           
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();
            reportError();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            reportError();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
            reportError();
        }
    }
    
    /**
     * this method prompts to the GUI about an error occurrence
     */
    private void reportError() {
    	JOptionPane.showMessageDialog(null,
                "Problem starting broadcast "+ args[0] +
                ", please check manually at https://www.youtube.com/my_live_events",
                "Server request problem",
                JOptionPane.ERROR_MESSAGE);
        Constants.isLive[queueNum]=true;
    }
    /***this method retrieves a relevant stream from server from the stream list 
     * 
     * @param name - stream name that is requested
     * @return found stream , null otherwise
     * @throws IOException
     */
    private static LiveStream getStreamByName(String name) throws IOException {
    	// Create a request to list liveStream resources.
        YouTube.LiveStreams.List livestreamRequest = CreateYouTube.getYoutube().liveStreams().list("id,snippet,status");
        // Modify results to only return the user's streams.
        livestreamRequest.setMine(true);
        livestreamRequest.setMaxResults((long) 10); //show top 10 streams
        //get relevant stream
        LiveStream foundstream=null;	//initite pointer to the stream
        LiveStreamListResponse returnedListResponse = livestreamRequest.execute();
        List<LiveStream> returnedList = returnedListResponse.getItems();
        for (LiveStream stream : returnedList) {
        	//System.out.println(stream.getSnippet().getTitle());
        	if(stream.getSnippet().getTitle().equals(name))
        		foundstream= stream;
        }
    	return foundstream;
    }
    
    /***this method retrieves a relevant broadcast from server from the broadcast list 
     * 
     * @param id - broadcast id that is requested
     * @return found broadcast, null other wise
     * @throws IOException
     */
    private static LiveBroadcast getBroadcastById(String id) throws IOException {
    	
    	 YouTube.LiveBroadcasts.List liveBroadcastRequest =
    			 CreateYouTube.getYoutube().liveBroadcasts().list("id,snippet,status");

         // Indicate that the API response should not filter broadcasts
         // based on their type or status.
         liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("all");
         LiveBroadcast foundbroadcast=null; 	//initate pointer to the broadcast
         // Execute the API request and return the list of broadcasts.
         LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
         List<LiveBroadcast> returnedList = returnedListResponse.getItems();
         for (LiveBroadcast broadcast : returnedList) {
        	 if(broadcast.getId().equals(id))
        		 foundbroadcast= broadcast;
         }
         return foundbroadcast;
    }
    
}


