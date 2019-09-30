package com.youtube.app;

/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.youtube.app.Auth;
import com.youtube.utils.Constants;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

/**
 * Retrieve a list of a channel's broadcasts, using OAuth 2.0 to authorize
 * API requests.
 *
 * @author Ibrahim Ulukaya
 */
public class ListBroadcasts extends Thread {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * List broadcasts for the user's channel.
     *
     * @param args command line args (not used).
     */
    public static List<LiveBroadcast> run(String[] args) {

        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "listbroadcasts");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-automation-app").build();

            // Create a request to list broadcasts.
            YouTube.LiveBroadcasts.List liveBroadcastRequest =
                    youtube.liveBroadcasts().list("id,snippet,status");

            // Indicate that the API response should not filter broadcasts
            // based on their type or status.
            liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("upcoming");
            
            liveBroadcastRequest.setMaxResults((long) 50); //show top 10 streams
            
            // Execute the API request and return the list of broadcasts.
            LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
            List<LiveBroadcast> returnedList = returnedListResponse.getItems();
            
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
           return returnedList;
           
            
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
		return null;
        
        
    }
    
    private static LiveBroadcast getBroadcastById(String id) throws IOException {
    	
   	 YouTube.LiveBroadcasts.List liveBroadcastRequest =
                youtube.liveBroadcasts().list("id,snippet,status");

        // Indicate that the API response should not filter broadcasts
        // based on their type or status.
        liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("all");
        
        // Execute the API request and return the list of broadcasts.
        LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
        List<LiveBroadcast> returnedList = returnedListResponse.getItems();
        for (LiveBroadcast broadcast : returnedList) {
       	 if(broadcast.getId().equals(id))
       		 return broadcast;
        }
        return null;
   }
}

