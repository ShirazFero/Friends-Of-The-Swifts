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
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import java.io.IOException;
import java.util.List;


/**
 * Use the YouTube Live Streaming API to retrieve a live broadcast
 * and complete the broadcast. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Evgeny Geyfman
 */
public class CompleteBroadcast extends Thread {
    /**
     * find and delete a liveBroadcast resource.
     */
    public static void run(String[] args) {

        try {
        	
            LiveBroadcast returnedBroadcast = getBroadcastByName(args[0]);
            
            if(returnedBroadcast==null) {
            	System.out.println("no broadcast with this title was found");
            	return;
            }
            
            //Check broadcast is live
            if(!returnedBroadcast.getStatus().getLifeCycleStatus().equals("live")) {
            	System.out.println("broadcast is not live");
            	return;
            }
           
            //Request transition to complete broadcast
            YouTube.LiveBroadcasts.Transition requestTesting = CreateYouTube.getYoutube().liveBroadcasts()
                    .transition("complete", returnedBroadcast.getId(), "snippet,status");
             returnedBroadcast = requestTesting.execute();
             
             returnedBroadcast = getBroadcastByName(args[0]);
             System.out.println(returnedBroadcast.getStatus().getLifeCycleStatus());
             //poll while test starting
             while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("live")) {
          	   returnedBroadcast = getBroadcastByName(args[0]);
          	   System.out.println("polling live");
             }
             
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
    
    private static LiveBroadcast getBroadcastByName(String name) throws IOException {
    	
   	 YouTube.LiveBroadcasts.List liveBroadcastRequest =
   			CreateYouTube.getYoutube().liveBroadcasts().list("id,snippet,status");

        // Indicate that the API response should not filter broadcasts
        // based on their type or status.
        liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("all");
        
        // Execute the API request and return the list of broadcasts.
        LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
        List<LiveBroadcast> returnedList = returnedListResponse.getItems();
        for (LiveBroadcast broadcast : returnedList) {
       	 if(broadcast.getSnippet().getTitle().equals(name))
       		 return broadcast;
        }
        return null;
   }
}
