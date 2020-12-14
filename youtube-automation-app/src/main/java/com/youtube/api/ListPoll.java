package com.youtube.api;

import java.io.IOException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.youtube.utils.Constants;

public class ListPoll extends Thread {
	
public void run() {
	try {
		synchronized (Constants.PollStartLock) {
			if(Constants.Debug) {
				System.out.println("List poll waits");
			}
			Constants.PollStartLock.wait();
		} 
		if(Constants.Debug) {
			System.out.println("List poll continues");
		}
	    WaitUntilPollingIsFinished();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
	
private void WaitUntilPollingIsFinished() {
	   try {
		   	Constants.pollingCount = 0;
   			while(Constants.pollingState && Constants.pollingCount < Constants.MaxPolls) {
				YouTube.LiveBroadcasts.List liveBroadcastRequest 
					= YouTubeAPI.youtube.liveBroadcasts().list("id,snippet,status");
			    // Indicate that the API response should not filter broadcasts
			    // based on their type or status.
			    liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("all");
			    liveBroadcastRequest.setMaxResults(Constants.MaxPollRsults);
			    
			    // Execute the API request and return the list of broadcasts.
			    LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
			    Constants.PolledBroadcasts = returnedListResponse.getItems();
			    synchronized (Constants.PollLock) {
					Constants.PollLock.notifyAll();
			    }
			    Constants.pollingCount++;
			    Thread.sleep(10000);
   			}
		   	
	   	} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
   }
}


