package com.youtube.api;

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
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamListResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * this class hold a static method that Retrieve a list
 * of a channel's streams, using OAuth 2.0 to authorize
 * API requests.
 *
 * @author Evgeny Geyfman
 */
public class ListStreams {
    /**
     * List streams for the user's channel.
     * @return 
     */
    public static List<LiveStream> run(String[] args) {

        try {
        	// Create a request to list liveStream resources.
            YouTube.LiveStreams.List livestreamRequest = CreateYouTube.getYoutube().
            		liveStreams().list("id,snippet,status,cdn");

            // Modify results to only return the user's streams.
            livestreamRequest.setMine(true);
            livestreamRequest.setMaxResults((long) 10); //show top 10 streams
            
            // Execute the API request and return the list of streams.
            LiveStreamListResponse returnedListResponse = livestreamRequest.execute();
            List<LiveStream> returnedList = returnedListResponse.getItems();
            List<LiveStream> fullreturnList= new LinkedList<LiveStream>(returnedList);
            
            boolean nextPageflag = true;	//flag that checks if there's more pages
            while(nextPageflag) {
	            if(Constants.DEBUG) {
		            // Print information from the API response.
		            System.out.println("\n================== Returned Streams ==================\n");
		            for (LiveStream stream : returnedList) {
		                System.out.println("  - Id: " + stream.getId());
		                System.out.println("  - Title: " + stream.getSnippet().getTitle());
		                System.out.println("  - Description: " + stream.getSnippet().getDescription());
		                System.out.println("  - Published At: " + stream.getSnippet().getPublishedAt());
		                System.out.println("  - status: " + stream.getStatus());
		                System.out.println("  - ingestion Key: " + stream.getCdn().getIngestionInfo().getStreamName());
		                System.out.println("\n-------------------------------------------------------------\n");
		            }
	            }
	            //check if there are more pages of streams
	            if(returnedListResponse.getNextPageToken()!=null) {
	            	livestreamRequest.setPageToken(returnedListResponse.getNextPageToken());	//set next page token
		            returnedListResponse = livestreamRequest.execute();							//Request next page	
		            returnedList = returnedListResponse.getItems();								//Receive	next page
		            fullreturnList.addAll(returnedList);										//add to return list
		            System.out.println(returnedListResponse.getPageInfo());
	            }
	            else
	            	nextPageflag = false;
           }
            
           return fullreturnList;	//return full list
           
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
                "Problem fethcing Streams",
                "Server request problem",
                JOptionPane.ERROR_MESSAGE);
    }
}
