package com.youtube.api;

import java.io.IOException;
import com.youtube.utils.Constants;

public class ListPoll extends Thread {
	
	public void run() {
		try {
			waitUntilPollStart();
		    pollApiWhileTransitioning();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void waitUntilPollStart() throws InterruptedException
	{
		synchronized (Constants.PollStartLock) {
			if(Constants.DEBUG) {
				System.out.println("List poll waits");
			}
			Constants.pollingCount = 0;
			Constants.PollStartLock.wait();
		} 
		if(Constants.DEBUG) {
			System.out.println("List poll continues");
		}
	}

	private void pollApiWhileTransitioning() 
	{
	   try {
		   	while(Constants.pollingState && Constants.pollingCount < Constants.MaxPolls) {
			   
			    String[] args = {"all",Constants.MaxPollRsults,null};
			    Constants.PolledBroadcasts = YouTubeAPI.listBroadcasts(args);
			    synchronized (Constants.PollLock) {
					Constants.PollLock.notifyAll();
			    }
			    Constants.pollingCount++;
			    Thread.sleep(Constants.POLL_SLEEP_MILISEC);
   			}
		   	
	   	} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
   }
}


