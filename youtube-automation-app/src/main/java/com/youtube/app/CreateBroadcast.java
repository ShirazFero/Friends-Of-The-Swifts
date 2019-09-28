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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.youtube.app.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Use the YouTube Live Streaming API to insert a broadcast and retrieve a stream from the stream list
 * and then bind them together, then start the live broadcast. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Evgeny Geyfman
 */
public class CreateBroadcast extends Thread{

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Create and insert a liveBroadcast resource.
     */
    public static void run(String[] args) {

        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "createbroadcastJos");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-app-createbroadcast").build();
           
            // get the Stream and validate stream is active
            String title = getStreamTitle();
            LiveStream returnedStream = getStreamByName(title);
            if(returnedStream==null) {
            	System.out.println("stream doesn't exist please try again");
            	return;
            }
            if(!returnedStream.getStatus().getStreamStatus().equals("active") || 
            		returnedStream.getStatus().getHealthStatus().getStatus().equals("noData")) {
            	System.out.println("stream is not active please start the stream and run this again");
            	return ;
            }
            System.out.println("  - ingestion Key: " + returnedStream.getCdn().getIngestionInfo().getIngestionAddress());
            // Prompt the user to enter a title for the broadcast.
            title = getBroadcastTitle();
            System.out.println("You chose " + title + " for broadcast title.");

            // Create a snippet with the title and scheduled start and end
            // times for the broadcast. Currently, those times are hard-coded.
            LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
            broadcastSnippet.setTitle(title);
            broadcastSnippet.setScheduledStartTime(new DateTime(LocalDate.now()+"T"+LocalTime.now()+"Z"));
            broadcastSnippet.setScheduledEndTime(new DateTime(LocalDate.now()+"T"+"23:59:59.000Z"));
            												  
            // Set the broadcast's privacy status to "private". See:
            // https://developers.google.com/youtube/v3/live/docs/liveBroadcasts#status.privacyStatus
            LiveBroadcastStatus status = new LiveBroadcastStatus();
            status.setPrivacyStatus("public");
           
            LiveBroadcast broadcast = new LiveBroadcast();
            broadcast.setKind("youtube#liveBroadcast");
            broadcast.setSnippet(broadcastSnippet);
            broadcast.setStatus(status);

            // Construct and execute the API request to insert the broadcast.
            YouTube.LiveBroadcasts.Insert liveBroadcastInsert =
                    youtube.liveBroadcasts().insert("snippet,status", broadcast);
            LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();

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
            	
            // Construct and execute a request to bind the new broadcast
            // and stream.
            YouTube.LiveBroadcasts.Bind liveBroadcastBind =
                    youtube.liveBroadcasts().bind(returnedBroadcast.getId(), "id,contentDetails");
            liveBroadcastBind.setStreamId(returnedStream.getId());
            returnedBroadcast = liveBroadcastBind.execute();

            // Print information from the API response.
            System.out.println("\n================== Returned Bound Broadcast ==================\n");
            System.out.println("  - Broadcast Id: " + returnedBroadcast.getId());
            System.out.println(
                    "  - Bound Stream Id: " + returnedBroadcast.getContentDetails().getBoundStreamId());
          
            
          //stream status check needed here to make sure it is active
            
           if(!returnedStream.getStatus().getStreamStatus().equals("active")) {
        	   System.out.println("stream is not active please start sending data on the stream");
        	   
           }
           else {
        	   System.out.println("stream is active starting transition to live");
           }
           
          //transition to testing 
          
    	   YouTube.LiveBroadcasts.Transition requestTesting = youtube.liveBroadcasts()
                  .transition("testing", returnedBroadcast.getId(), "snippet,status");
           returnedBroadcast = requestTesting.execute();
           //Prompt request status
           System.out.println(returnedBroadcast.getStatus().getLifeCycleStatus());
           //poll while test starting
           while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("testStarting")) {
        	   returnedBroadcast = getBroadcastById(returnedBroadcast.getId());
        	   System.out.println("polling testStarting");
           }
           
           System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus());
          
            //transition to live   
            YouTube.LiveBroadcasts.Transition requestLive = youtube.liveBroadcasts()
                    .transition("live", returnedBroadcast.getId(), "snippet,status");
            returnedBroadcast = requestLive.execute();
            //poll while live starting
            while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("liveStarting")) {
            	returnedBroadcast = getBroadcastById(returnedBroadcast.getId());
            	System.out.println("polling liveStarting");
            	Thread.sleep(1000);
            }
            returnedBroadcast = getBroadcastById(returnedBroadcast.getId());
            System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus());
           
           
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            System.err.println("Throwable: " + t.getMessage());
            t.printStackTrace();
        }
    }

    /*
     * Prompt the user to enter a title for a broadcast.
     */
    private static String getBroadcastTitle() throws IOException {

        String title = "";

        System.out.print("Please enter a broadcast title: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        title = bReader.readLine();

      
        
        if (title.length() < 1) {
            // Use "New Broadcast" as the default title.
            title = "New Broadcast";
        }
        return title;
    }

    /*
     * Prompt the user to enter a title for a stream.
     */
    private static String getStreamTitle() throws IOException {

        String title = "";

        System.out.print("Please enter a stream title: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        title = bReader.readLine();

        if (title.length() < 1) {
            // Use "New Stream" as the default title.
            title = "New Stream";
        }
        return title;
    }
    
    /***this method retrieves a relevant stream from server from the stream list 
     * 
     * @param name - stream name that is requested
     * @return found stream , null otherwise
     * @throws IOException
     */
    private static LiveStream getStreamByName(String name) throws IOException {
    	// Create a request to list liveStream resources.
        YouTube.LiveStreams.List livestreamRequest = youtube.liveStreams().list("id,snippet,status");
        // Modify results to only return the user's streams.
        livestreamRequest.setMine(true);
        //get relevant stream
        LiveStream foundstream=null;	//initite pointer to the stream
        LiveStreamListResponse returnedListResponse = livestreamRequest.execute();
        List<LiveStream> returnedList = returnedListResponse.getItems();
        for (LiveStream stream : returnedList) {
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
                 youtube.liveBroadcasts().list("id,snippet,status");

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


