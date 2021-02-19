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
import com.youtube.utils.Constants;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Use the YouTube Live Streaming API to retrieve a live broadcast
 * and complete the broadcast. Use OAuth 2.0 to authorize the API requests.
 * Thread safe implementation
 * @param m_id = broadcast Id to be completed
 * @author Evgeny Geyfman
 */
public class BroadcastCompleter extends Thread {
	
	private String m_id;
	private AtomicInteger m_doneFlag;
	
	public  BroadcastCompleter(String Id,AtomicInteger doneFlag) {
		this.m_id = Id;
		m_doneFlag = doneFlag;
	}

	/**
     * find and delete a liveBroadcast resource.
     */
    public  void run() {

        try {
        	Object lock = new Object();
        	YouTubeAPI youtubeApi = YouTubeAPI.getInstance();
            LiveBroadcast returnedBroadcast = youtubeApi.getBroadcastFromPolledList(m_id);
            
            if(returnedBroadcast==null) {
            	Constants.DebugPrint("no broadcast with this title was found");
            	return;
            }
            
            //Check broadcast is live
            if(!returnedBroadcast.getStatus().getLifeCycleStatus().equals("live")) {
        	    Constants.DebugPrint("broadcast is not live");
            	return;
            }
           
            //Request transition to complete broadcast
            YouTube.LiveBroadcasts.Transition requestTransition = youtubeApi.getService().liveBroadcasts()
                    .transition("complete", returnedBroadcast.getId(), "snippet,status");
            synchronized (lock) { 
            	returnedBroadcast = requestTransition.execute();
            }
            Thread.sleep(1000);
            returnedBroadcast = youtubeApi.getBroadcastFromPolledList(m_id);
            System.out.println(returnedBroadcast.getStatus().getLifeCycleStatus() + "title "+returnedBroadcast.getId()
            		 + "ID: " + m_id);
            synchronized (Constants.PollStartLock) {
				Constants.PollStartLock.notifyAll();	//start polling the api 
           } //poll while test starting
             
             while(returnedBroadcast.getStatus().getLifeCycleStatus().equals("live")) {
	        	 synchronized (Constants.PollLock) {
	        		 Constants.DebugPrint("Thread "+Thread.currentThread().getId()+" waits");
					Constants.PollLock.wait();		//wait for status update
        	 	}
        	   Constants.DebugPrint("Thread "+Thread.currentThread().getId()+ " continues" );
 	    	   LiveBroadcast tempBroadcast = youtubeApi.getBroadcastFromPolledList(returnedBroadcast.getId());
 	    	   if(tempBroadcast!=null)
 	    		   returnedBroadcast = tempBroadcast;
 	    	   Constants.DebugPrint("polling live");
          	   if(Constants.pollingCount==10) {	// if more then 100 seconds passed and Broadcast wasn't transitioned to live
          		   throw new IOException ("100 secs passed no response on completion");
      	 		}
             }
             
             synchronized (lock) {
            	 m_doneFlag.decrementAndGet();
 			}
             Constants.DebugPrint("We are "+returnedBroadcast.getStatus().getLifeCycleStatus());
            
        } catch (GoogleJsonResponseException e) {
        	String errormsg = "Error code: " + e.getDetails().getCode()+", " + e.getDetails().getMessage();
        	ErrorHandler.HandleApiError(m_id + ": "+ errormsg);
        } catch (IOException e) {
            ErrorHandler.HandleApiError(m_id + ": "+ e.getMessage());
        } catch (Throwable t) {
            ErrorHandler.HandleApiError(m_id + ": "+ t.getMessage());
        }
    
    } 
    
	
}
