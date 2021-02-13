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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.parser.ParseException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.LiveBroadcastSnippet;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamListResponse;
import com.google.api.services.youtube.model.LiveStreamSnippet;
import com.google.common.collect.Lists;
import com.youtube.utils.Constants;


/**
 * This class represents a YouTube Object that handles API requests, authorization goes via OAuth 2.0
 * all methods represent different API requests
 * 
 * @author Evgeny Geyfman
 * */
public class YouTubeAPI {
	
	private YouTube youtubeService;	//YouTube resource 

	private static YouTubeAPI instance;
	
	public static YouTubeAPI getInstance() throws SecurityException, IOException 
	{
		if(instance == null) {
			instance = new YouTubeAPI();
		}
		return instance;
	}
	
	public YouTube getService()
	{
		return youtubeService;
	}
	
	/**
	* Create and insert a YouTube resource, authorize via OAuth 2.0 resource.
	* @throws ParseException 
	*/
	public YouTubeAPI() throws SecurityException, IOException  
	{
	    // This OAuth 2.0 access scope allows for full read/write access to the
		// authenticated user's account.
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
		try {
		    // Authorize the request.
		    Credential credential = Auth.authorize(scopes,Constants.Username);
		    // This object is used to make YouTube Data API requests.
		    youtubeService = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
		            .setApplicationName("YABA").build();
		    
		} catch (GoogleJsonResponseException e) {
		    Object errorArgs[] = {e.getDetails().getCode(),e.getDetails().getMessage(),e.getDetails().getErrors()};
		    ErrorHandler.HandleError(errorArgs);
		} catch (IOException e) {
		    ErrorHandler.HandleUnknownError(e.getMessage());
		} catch (Throwable t) {
		    ErrorHandler.HandleUnknownError(t.getMessage());
	    }
	}
	
	/**
     * retrieve a List of broadcasts from the user's channel.
    * according to parameters:
    * @param String args[3] 
 	* @param 1st argument is broadcast life status :"all/ upcoming/active/complete"
    * @param 2nd argument is max number of results requested}
    * @param 3rd argument is next/previous page token if exists, null otherwise}
	* @throws IOException 
	* @throws SecurityException 
	* @throws ParseException 
    */
	public  List<LiveBroadcast> listBroadcasts(String[] args) throws SecurityException, IOException  
	{
	    try {
	        // Create a request to list broadcasts.
		    YouTube.LiveBroadcasts.List liveBroadcastRequest =
		    		youtubeService.liveBroadcasts().list("id,snippet,status");
		   
		    if(args.length != 3) {
		    	throw  new IOException("Bad arguments number on requset");
		    }
		    	
		    // Indicate that the API response should not filter broadcasts
		    // based on their type or status.
	    	liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus(args[0]);
		    liveBroadcastRequest.setMaxResults((long) Long.parseLong(args[1])); //show up to 50 broadcasts
		    //set next/prev page token if exists
		    if(args[2] != null) {
		    	liveBroadcastRequest.setPageToken(args[2]);
		    }
		    // Execute the API request and return the list of broadcasts.
		    LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
		    Constants.NextPageToken = returnedListResponse.getNextPageToken();
		    Constants.PrevPageToken = returnedListResponse.getPrevPageToken();
		    return returnedListResponse.getItems();
	   
	    } catch (GoogleJsonResponseException e) {
		    Object errorArgs[] = {e.getDetails().getCode(),e.getDetails().getMessage(),e.getDetails().getErrors()};
		    ErrorHandler.HandleError(errorArgs);
		} catch (IOException e) {
		    ErrorHandler.HandleUnknownError(e.getMessage());
		} catch (Throwable t) {
		    ErrorHandler.HandleUnknownError(t.getMessage());
	    }
		return null;
	}
	
	/**
	 * retrieve a List of Streams from the user's channel.
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws ParseException 
	 */
	public  List<LiveStream> listStreams(String[] args) throws SecurityException, IOException  
	{
	    try{
	    	// Create a request to list liveStream resources.
	    	YouTube.LiveStreams.List livestreamRequest = youtubeService.
		    		liveStreams().list("id,snippet,status,cdn");
		    // Modify results to only return the user's streams.
		    livestreamRequest.setMine(true);
		    livestreamRequest.setMaxResults(Constants.MAX_REQUEST_RESULTS); //show top 10 streams
		    return getAllStreams(livestreamRequest);
		   
	    } catch (GoogleJsonResponseException e) {
		    Object errorArgs[] = {e.getDetails().getCode(),e.getDetails().getMessage(),e.getDetails().getErrors()};
		    ErrorHandler.HandleError(errorArgs);
		} catch (IOException e) {
		    ErrorHandler.HandleUnknownError(e.getMessage());
		} catch (Throwable t) {
		    ErrorHandler.HandleUnknownError(t.getMessage());
	    }
		return null;
	}
	
	/**
	 * Create and insert a liveStream resource.
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public boolean createStream(String chosenTitle) throws IOException
	{
	    try {
		
	        // Prompt the user to enter a title for the video stream.
		    String title = (chosenTitle == null || chosenTitle.isEmpty()) ?  "New Stream" : chosenTitle ;
		    if(Constants.DEBUG) {
		    	System.out.println("You chose " + title + " for stream title.");
		    }
		
		    LiveStreamSnippet streamSnippet = initLiveStreamSnippet(title,null);
		    CdnSettings cdnSettings = initCdnSettings();
		    LiveStream stream = initLiveStream(streamSnippet,cdnSettings);
		    
		    // Construct and execute the API request to insert the stream.
		    YouTube.LiveStreams.Insert liveStreamInsert =
		    		youtubeService.liveStreams().insert("snippet,cdn,status", stream);
		    liveStreamInsert.execute();
		    return true;
		    
	    } catch (GoogleJsonResponseException e) {
		    Object errorArgs[] = {e.getDetails().getCode(),e.getDetails().getMessage(),e.getDetails().getErrors()};
		    ErrorHandler.HandleError(errorArgs);
		    return false;
		} catch (IOException e) {
		    ErrorHandler.HandleUnknownError(e.getMessage());
		    return false;
		} catch (Throwable t) {
		    ErrorHandler.HandleUnknownError(t.getMessage());
		    return false;
	    }
	}
	
	/**
	 * retrieve and delete a liveStream resource.
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public boolean deleteStream(String title) throws SecurityException, IOException 
	{
	    try {
	        // Prompt the user to enter a title for the video stream.
		    if(title == null) {
		    	return false;
		    }
		    
		    if(Constants.DEBUG) {
		    	System.out.println("You chose to delete " + title + " for stream title.");
		    }
		    
		    LiveStream stream = getStreamByName(title);
		    if(stream == null) {
		    	if(Constants.DEBUG) {
				    	System.out.println("no such stream exists: " + title);
			    }
		    	return false;
		    }
		    if(Constants.DEBUG) {
		    	System.out.println("deleted stream id: " + stream.getId());
		    }
		    // Construct and execute the API request to insert the stream.
		    YouTube.LiveStreams.Delete liveStreamDelete =
		    		youtubeService.liveStreams().delete(stream.getId());
		    liveStreamDelete.execute();
		    if(Constants.DEBUG) {
		    	System.out.println(title +" was deleted");
		    }
		    return true;
		        
		} catch (GoogleJsonResponseException e) {
		    Object errorArgs[] = {e.getDetails().getCode(),e.getDetails().getMessage(),e.getDetails().getErrors()};
		    ErrorHandler.HandleError(errorArgs);
		    return false;
		} catch (IOException e) {
		    ErrorHandler.HandleUnknownError(e.getMessage());
		    return false;
		} catch (Throwable t) {
		    ErrorHandler.HandleUnknownError(t.getMessage());
		    return false;
	    }
	}
	
	/**
	 * this method sends an update description request , returns true if server handles it ,false if not
	 * @param description
	 * @param liveBroadcast
	 * @throws SecurityException 
	 * @throws IOException
	 */
	public  boolean updateDescription(String description , LiveBroadcast liveBroadcast) throws SecurityException, IOException 
	{
		try {	
	        LiveBroadcastSnippet snippet =  initBroadcastSnippet(description,liveBroadcast);
			// Define the LiveBroadcast object, which will be uploaded as the request body.
	        LiveBroadcast liveBroadcastupdate = new LiveBroadcast();
	        // Add the id string property to the LiveBroadcast object.
	        liveBroadcastupdate.setSnippet(snippet);
	        liveBroadcastupdate.setId(liveBroadcast.getId());
			
			YouTube.LiveBroadcasts.Update request = youtubeService.liveBroadcasts()
		            .update("snippet",liveBroadcastupdate);
	        LiveBroadcast response = request.execute();
	        return response.getSnippet().getDescription().equals(description) ? true : false;
	        
		} catch (GoogleJsonResponseException e) {
		    Object errorArgs[] = {e.getDetails().getCode(),e.getDetails().getMessage(),e.getDetails().getErrors()};
		    ErrorHandler.HandleError(errorArgs);
		    return false;
		} catch (IOException e) {
		    ErrorHandler.HandleUnknownError(e.getMessage());
		    return false;
		} catch (Throwable t) {
		    ErrorHandler.HandleUnknownError(t.getMessage());
		    return false;
	    }
	}
	
	/**
	 * Retrieve a stream by it's name
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public  LiveStream getStreamByName(String name) throws IOException, ParseException 
	{
    	LiveStream foundstream = null;			//initite pointer to the stream
    	List<LiveStream> returnedList = listStreams(null); //get stream list
        for (LiveStream stream : returnedList) {
        	//System.out.println(stream.getSnippet().getTitle());
        	if(stream.getSnippet().getTitle().equals(name)) {
        		foundstream = stream;
        	}
        }
    	return foundstream;
	}
	
	/**
	 * this method sends an update Stream description request , returns true if server handles it ,false if not
	 * @param description
	 * @param liveBroadcast
	 * @throws SecurityException 
	 * @throws IOException
	 */
	public  boolean updateStreamDescription(String description , LiveStream stream) throws SecurityException, IOException  
	{
		try {	
			// Define the LiveStream object, which will be uploaded as the request body.
	        LiveStream LiveStreamupdate = new LiveStream();
	        // Add the id string property to the LiveStream object.
	        LiveStreamupdate.setId(stream.getId());
	        // Add the snippet object property to the LiveStream object.
	        LiveStreamSnippet streamSnippet = initLiveStreamSnippet(stream.getSnippet().getTitle(), description);
	        LiveStreamupdate.setSnippet(streamSnippet);
			
			YouTube.LiveStreams.Update request = youtubeService.liveStreams()
		            .update("snippet",LiveStreamupdate);
			LiveStream response = request.execute();
		    
	        return response.getSnippet().getDescription().equals(description) ? true : false;
		} catch (GoogleJsonResponseException e) {
		    Object errorArgs[] = {e.getDetails().getCode(),e.getDetails().getMessage(),e.getDetails().getErrors()};
		    ErrorHandler.HandleError(errorArgs);
		    return false;
		} catch (IOException e) {
		    ErrorHandler.HandleUnknownError(e.getMessage());
		    return false;
		} catch (Throwable t) {
		    ErrorHandler.HandleUnknownError(t.getMessage());
		    return false;
	    }   
	}
	
 	/**
 	 * retrieves a relevant broadcast from server from the broadcast list 
     * 
     * @param id - broadcast id that is requested
     * @return found broadcast, null other wise
     * @throws IOException
     */
	public  LiveBroadcast getBroadcastByID(String id) throws IOException 
	{
		YouTube.LiveBroadcasts.List liveBroadcastRequest =
			youtubeService.liveBroadcasts().list("id,snippet,status");
		
		// Indicate that the API response should not filter broadcasts
		// based on their type or status.
		liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus("all");
		liveBroadcastRequest.setMaxResults(Constants.MAX_REQUEST_RESULTS);
		return lookInAllPages(id,liveBroadcastRequest);
	}
	
 	public LiveBroadcast getBroadcastFromPolledList(String id) throws IOException 
 	{
 		for (LiveBroadcast broadcast : Constants.PolledBroadcasts) {
		    if(broadcast.getId().equals(id)) {
		    	return broadcast;
		    }
 		}
 		return null;
 	}
	
	private  List<LiveStream> getAllStreams(YouTube.LiveStreams.List livestreamRequest) throws IOException
	{
		// Execute the API request and return the list of streams.
		LiveStreamListResponse returnedListResponse = livestreamRequest.execute();
	    List<LiveStream> returnedList = returnedListResponse.getItems();
	    List<LiveStream> fullreturnList = new LinkedList<LiveStream>(returnedList);
	    boolean nextPageflag = true;	//flag that checks if there's more pages
	    while(nextPageflag) {
	        //check if there are more pages of streams
	        if(returnedListResponse.getNextPageToken() != null) {
	        	livestreamRequest.setPageToken(returnedListResponse.getNextPageToken());	//set next page token
	            returnedListResponse = livestreamRequest.execute();							//Request next page	
	            returnedList = returnedListResponse.getItems();								//Receive	next page
	            fullreturnList.addAll(returnedList);	
	            //add to return list
	            if(Constants.DEBUG) {
	            	System.out.println(returnedListResponse.getPageInfo());
			    }
	        }
	        else {
	        	nextPageflag = false;
	        }
	    }
	    return fullreturnList;	//return full list
	}
	
	private LiveStreamSnippet initLiveStreamSnippet(String title, String description)
	{
		// Create a snippet with the video stream's title.
	    LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
	    streamSnippet.setTitle(title);
	    if(description != null) {
	    	streamSnippet.setDescription(description);
	    }
	    return streamSnippet;
	}
	
	private CdnSettings initCdnSettings()
	{
		// Define the content distribution network settings for the
	    // video stream. The settings specify the stream's format and
	    // ingestion type. See:
	    // https://developers.google.com/youtube/v3/live/docs/liveStreams#cdn
	    CdnSettings cdnSettings = new CdnSettings();
	    cdnSettings.setFormat(Constants.Format);
	    cdnSettings.setIngestionType(Constants.IngestionType);
	    cdnSettings.setResolution("variable");
	    cdnSettings.setFrameRate("variable");
	    return cdnSettings;
	}
	
	private LiveStream initLiveStream(LiveStreamSnippet streamSnippet, CdnSettings cdnSettings)
	{
		// Create a snippet with the video stream's title.
      	LiveStream stream = new LiveStream();
        stream.setKind("youtube#liveStream");
	    stream.setSnippet(streamSnippet);
	    stream.setCdn(cdnSettings);
	    return stream;
	}
	
	private LiveBroadcastSnippet initBroadcastSnippet(String description, LiveBroadcast liveBroadcast)
	{
		// Add the snippet object property to the LiveBroadcast object.
        LiveBroadcastSnippet snippet = new LiveBroadcastSnippet();
        snippet.setDescription(description);
        snippet.setScheduledStartTime(liveBroadcast.getSnippet().getScheduledStartTime());
        snippet.setTitle(liveBroadcast.getSnippet().getTitle());
        return snippet;
	}
	
	private LiveBroadcast lookInAllPages(String id, YouTube.LiveBroadcasts.List liveBroadcastRequest) throws IOException
	{
		// Execute the API request and return the list of broadcasts.
		LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
		String nextPageToken = returnedListResponse.getNextPageToken();
		do {
			List<LiveBroadcast> returnedList = returnedListResponse.getItems();
			LiveBroadcast foundBrd = findBroadcastInList(id,returnedList);
			if(foundBrd != null) {
				return foundBrd;
			}
			if(nextPageToken != null) {
		    	liveBroadcastRequest.setPageToken(nextPageToken);
		    	returnedListResponse = liveBroadcastRequest.execute();
		    	nextPageToken = returnedListResponse.getNextPageToken();
			}
		} while(nextPageToken != null);
		return null;
	}
	
	private LiveBroadcast findBroadcastInList(String id, List<LiveBroadcast> BroadcastList)
	{
		 for (LiveBroadcast broadcast : BroadcastList) {
	       	  if(broadcast.getId().equals(id)) {
	       		  return broadcast;
	       	  }
		 }
		 return null;
	}
	
}
