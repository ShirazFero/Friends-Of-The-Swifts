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
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;


/**
 * Use the YouTube Live Streaming API to retrieve a live broadcast
 * and complete the broadcast. Use OAuth 2.0 to authorize the API requests.
 * Thread safe implementation
 * @param args[0] = broadcast Id to be completed
 * @author Evgeny Geyfman
 */
public class CompleteBroadcast extends Thread {
	
	private String[] args;
	
	public  CompleteBroadcast(String[] args) {
		this.args = args;
	}

	/**
     * find and delete a liveBroadcast resource.
     */
    public  void run() {

        try {
        	
            LiveBroadcast returnedBroadcast = getBroadcastByID(args[0]);
            
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
            YouTube.LiveBroadcasts.Transition requestTransition = YouTubeAPI.youtube.liveBroadcasts()
                    .transition("complete", returnedBroadcast.getId(), "snippet,status");
             returnedBroadcast = requestTransition.execute();
             
             returnedBroadcast = getBroadcastByID(args[0]);
             System.out.println(returnedBroadcast.getStatus().getLifeCycleStatus() + "title "+returnedBroadcast.getId()
            		 + "ID: " + args[0]);
             //poll while test starting
             while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("live")) {
          	   returnedBroadcast = getBroadcastByID(args[0]);
          	   System.out.println("polling live");
          	   Thread.sleep(1000);
             }
             
             System.out.println("We are "+returnedBroadcast.getStatus().getLifeCycleStatus());
            
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
    
    private static LiveBroadcast getBroadcastByID(String id) throws IOException {
    	
   	 YouTube.LiveBroadcasts.List liveBroadcastRequest =
   			YouTubeAPI.youtube.liveBroadcasts().list("id,snippet,status");

        // Indicate that the API response should not filter broadcasts
        // based on their type or status.
        liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("all");
        liveBroadcastRequest.setMaxResults((long)20);
        
        // Execute the API request and return the list of broadcasts.
        LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
        List<LiveBroadcast> returnedList = returnedListResponse.getItems();
        for (LiveBroadcast broadcast : returnedList) {
       	 if(broadcast.getId().equals(id))
       		 return broadcast;
        }
        return null;
   }
    
	/**
	 * this method prompts to the GUI about an error occurrence
	 */
	private  void reportError() {
		JOptionPane.showMessageDialog(null,
	            "Problem completing broadcast " + args[0] +
                ", please check manually at https://www.youtube.com/my_live_events",
	            "Server request problem",
	            JOptionPane.ERROR_MESSAGE);
	}
}
