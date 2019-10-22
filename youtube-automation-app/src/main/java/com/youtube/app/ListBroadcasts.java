package com.youtube.app;

/*
 * 
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
*
*/

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.youtube.utils.Constants;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

/**this class hold a static method that:
 * Retrieves a list of a channel's broadcasts, using OAuth 2.0 to authorize
 * API requests.
 *
 * @author Evgeny Geyfman
 */
public class ListBroadcasts {
    /**
     * List broadcasts for the user's channel.
     *
     * @param String args[2] ={1st arg is source function :"init/refresh",
     * 						   2nd arg is filter :"all/upcoming/active/complete"}
     */
    public static List<LiveBroadcast> run(String[] args) {

        try {
            // Create a request to list broadcasts.
            YouTube.LiveBroadcasts.List liveBroadcastRequest =
            		CreateYouTube.getYoutube().liveBroadcasts().list("id,snippet,status");

            // Indicate that the API response should not filter broadcasts
            // based on their type or status.
            if(args[0].equals("init"))
            	liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus(args[1]);
            else if(args[0].equals("refresh"))
            	liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus(args[1]);
            liveBroadcastRequest.setMaxResults((long) 50); //show top 50 streams
            
            // Execute the API request and return the list of broadcasts.
            LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
            List<LiveBroadcast> returnedList = returnedListResponse.getItems();
            List<LiveBroadcast> fullreturnList= new LinkedList<LiveBroadcast>(returnedList);
            
            boolean nextPageflag = true;	//flag that checks if there's more pages
            while(nextPageflag) {
	            if(Constants.DEBUG) {
	            // Print information from the API response.
		            System.out.println("\n================== Returned Broadcasts ==================\n");
		            for (LiveBroadcast broadcast : returnedList) {
		                System.out.println("  - Id: " + broadcast.getId());
		                System.out.println("  - Title: " + broadcast.getSnippet().getTitle());
		                System.out.println("  - Description: " + broadcast.getSnippet().getDescription());
		                System.out.println("  - Published At: " + broadcast.getSnippet().getPublishedAt());
		                System.out.println("  - status At: " + broadcast.getStatus());
		                System.out.println(
		                        "  - Scheduled Start Time: " + broadcast.getSnippet().getScheduledStartTime());
		                System.out.println(
		                        "  - Scheduled End Time: " + broadcast.getSnippet().getScheduledEndTime());
		                System.out.println("\n-------------------------------------------------------------\n");
		            }
	            }
	            //check if there are more pages of broadcasts
	            if(returnedListResponse.getNextPageToken()!=null) {
		            liveBroadcastRequest.setPageToken(returnedListResponse.getNextPageToken());
		            returnedListResponse = liveBroadcastRequest.execute();
		            returnedList = returnedListResponse.getItems();
		            fullreturnList.addAll(returnedList);
	            }
	            else
	            	nextPageflag = false;
           }
            
           return fullreturnList;
           
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
		return null;
    }
    
    /**
     * this method prompts to the GUI about an error occurrence
     */
    private static void reportError() {
    	JOptionPane.showMessageDialog(null,
                "Problem fethcing broadcasts",
                "Server request problem",
                JOptionPane.ERROR_MESSAGE);
    }
}

