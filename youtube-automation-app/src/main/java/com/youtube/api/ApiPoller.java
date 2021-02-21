package com.youtube.api;

import java.io.IOException;
import com.youtube.utils.Constants;

public class ApiPoller extends Thread {
	
	public void run() {
		try {
			waitUntilPollStart();
		    pollApiWhileTransitioning();
		} catch (InterruptedException | IOException e) {
			ErrorHandler.HandleError("API",e.getMessage());
		}
	}

	private void waitUntilPollStart() throws InterruptedException
	{
		synchronized (Constants.PollStartLock) {
			Constants.DebugPrint("ApiPoller waits before start");
			Constants.pollingCount = 0;
			Constants.PollStartLock.wait();
		} 
		Constants.DebugPrint("ApiPoller starts polling");
	}

	private void pollApiWhileTransitioning() throws IOException, InterruptedException {
	   	while(Constants.pollingState && Constants.pollingCount < Constants.MaxPolls) {
		    String[] args = {"all",Constants.MaxPollRsults,null};
		    Constants.PolledBroadcasts = YouTubeAPI.getInstance().requestBroadcastList(args);
		    synchronized (Constants.PollLock) {
				Constants.PollLock.notifyAll();
		    }
		    ++Constants.pollingCount;
		    Thread.sleep(Constants.POLL_SLEEP_MILISEC);
		}
   }
}


