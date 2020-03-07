package com.youtube.api;

import java.io.IOException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.youtube.utils.Constants;

public class ListPoll extends Thread {

	public void run() {
    	try {
    		synchronized (Constants.PollStartLock) {
    			 System.out.println("poll waits");
 				Constants.PollStartLock.wait();
 			} 
    		System.out.println("poll continues");
    		while(Constants.pollingState) {
			  
			YouTube.LiveBroadcasts.List liveBroadcastRequest 
				= YouTubeAPI.youtube.liveBroadcasts().list("id,snippet,status");
		    // Indicate that the API response should not filter broadcasts
		    // based on their type or status.
		    liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("all");
		    liveBroadcastRequest.setMaxResults((long)20);
		    
		    // Execute the API request and return the list of broadcasts.
		    LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
		    
		    Constants.PolledBroadcasts = returnedListResponse.getItems();
		    synchronized (Constants.PollLock) {
				Constants.PollLock.notifyAll();
			} 
		    Thread.sleep(5000);
		  }
		    
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

   }
	
}
