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
import javax.swing.JOptionPane;

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
	
	public static YouTube youtube;	//YouTube resource 

	    /**
	 * Create and insert a YouTube resource, authorize via OAuth 2.0 resource.
	 */
	public YouTubeAPI(String[] args) {
	
	    // This OAuth 2.0 access scope allows for full read/write access to the
	// authenticated user's account.
	List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");
	
	try {
	    // Authorize the request.//createbroadcast
	    Credential credential = Auth.authorize(scopes,Constants.Username);
	    
	    // This object is used to make YouTube Data API requests.
	    youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
	            .setApplicationName("youtube-automation-app").build();
	    
	    
	} catch (GoogleJsonResponseException e) {
	    System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
	            + e.getDetails().getMessage());
	    e.printStackTrace();
	    reportError("Problem Authenticating user");
	} catch (IOException e) {
	    System.err.println("IOException: " + e.getMessage());
	    e.printStackTrace();
	    reportError("Problem Authenticating user");
	} catch (Throwable t) {
	    System.err.println("Throwable: " + t.getMessage());
	    reportError("Problem Authenticating user");
	    }
	}
	
	/**
     * retrieve a List of broadcasts from the user's channel.
    * according to parameters:
    * @param String args[4] ={ 1st argument is source function :"init/refresh",
    * 						   2nd argument is filter :"all/upcoming/active/complete"
    * 						   3rd argument is next page token if exists, null otherwise}
    * 						   4th argument is previous page token if exists, null otherwise}
    */
	public static List<LiveBroadcast> listBroadcasts(String[] args) {
	
	    try {
	        // Create a request to list broadcasts.
	    YouTube.LiveBroadcasts.List liveBroadcastRequest =
	    		youtube.liveBroadcasts().list("id,snippet,status");
	
	    // Indicate that the API response should not filter broadcasts
	    // based on their type or status.
	    if(args[0].equals("init"))
	    	liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus(args[1]);
	    else if(args[0].equals("refresh"))
	    	liveBroadcastRequest.setBroadcastType("all").setBroadcastStatus(args[1]);
	    liveBroadcastRequest.setMaxResults((long) Constants.NumberOfResulsts); //show up to 50 broadcasts
	    
	    //set next page token if exists
	    if(args[2]!=null)
	    	liveBroadcastRequest.setPageToken(Constants.NextPageToken);
	    if(args[3]!=null)
	    	liveBroadcastRequest.setPageToken(Constants.PrevPageToken);
	    
	    // Execute the API request and return the list of broadcasts.
	    LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();
	    List<LiveBroadcast> returnedList = returnedListResponse.getItems();
	    List<LiveBroadcast> fullreturnList= new LinkedList<LiveBroadcast>(returnedList);
	    
	    Constants.NextPageToken = returnedListResponse.getNextPageToken();
	    if(Constants.NextPageToken!=null)
	    	System.out.println("next page token is: " + Constants.NextPageToken);
	    Constants.PrevPageToken = returnedListResponse.getPrevPageToken();
	    if(Constants.PrevPageToken!=null)
	    	System.out.println("prev page token is: " + Constants.PrevPageToken);
	    return fullreturnList;
	   
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

	
	/**
	 * retrieve a List of Streams from the user's channel.
	 *
	 */
	public static List<LiveStream> listStreams(String[] args) {
	
	    try {
	    	// Create a request to list liveStream resources.
	    YouTube.LiveStreams.List livestreamRequest = youtube.
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
	} catch (IOException e) {
	    System.err.println("IOException: " + e.getMessage());
	    e.printStackTrace();
	} catch (Throwable t) {
	    System.err.println("Throwable: " + t.getMessage());
	    t.printStackTrace();
	    }
		return null;
	}
	
	/**
	 * Create and insert a liveStream resource.
	 */
	public static boolean createStream(String[] args) {
	
	    try {
		
	        // Prompt the user to enter a title for the video stream.
		    String title;
		    if(args==null)
		    	title = "New Stream";
		    else
		    	title=args[0];
		    
		    System.out.println("You chose " + title + " for stream title.");
		
		    // Create a snippet with the video stream's title.
		    LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
		    streamSnippet.setTitle(title);
		
		    // Define the content distribution network settings for the
		    // video stream. The settings specify the stream's format and
		    // ingestion type. See:
		    // https://developers.google.com/youtube/v3/live/docs/liveStreams#cdn
		    CdnSettings cdnSettings = new CdnSettings();
		    cdnSettings.setFormat(Constants.Format);
		    cdnSettings.setIngestionType(Constants.IngetionType);
		   
		      	LiveStream stream = new LiveStream();
		        stream.setKind("youtube#liveStream");
		    stream.setSnippet(streamSnippet);
		    stream.setCdn(cdnSettings);
				
		    // Construct and execute the API request to insert the stream.
		    YouTube.LiveStreams.Insert liveStreamInsert =
		    		youtube.liveStreams().insert("snippet,cdn,status", stream);
		    LiveStream returnedStream = liveStreamInsert.execute();
		    if(Constants.DEBUG) {
		        // Print information from the API response.
		        System.out.println("\n================== Returned Inserted Stream ==================\n");
		        System.out.println("  - Id: " + returnedStream.getId());
		        System.out.println("  - Title: " + returnedStream.getSnippet().getTitle());
		        System.out.println("  - Status: " + returnedStream.getStatus().getStreamStatus());
		        System.out.println("  - Description: " + returnedStream.getSnippet().getDescription());
		        System.out.println("  - Published At: " + returnedStream.getSnippet().getPublishedAt());
		        System.out.println("  - ingestion Key: " + stream.getCdn().getIngestionInfo().getStreamName());
		    }
		    return true;
		} catch (GoogleJsonResponseException e) {
		    System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
		            + e.getDetails().getMessage());
		    e.printStackTrace();
		    return false;
		} catch (IOException e) {
		    System.err.println("IOException: " + e.getMessage());
		    e.printStackTrace();
		    return false;
		} catch (Throwable t) {
		    System.err.println("Throwable: " + t.getMessage());
		    t.printStackTrace();
		    return false;
	    }
	}
	
	/**
	 * retrieve and delete a liveStream resource.
	 */
	public static boolean deleteStream(String[] args) {
	    try {
	
	        // Prompt the user to enter a title for the video stream.
		    String title;
		    if(args==null)
		    	return false;
		    title=args[0];
		    System.out.println("You chose to delete " + title + " for stream title.");
				
		    LiveStream stream = getStreamByName(title);
		    System.out.println(stream.getId());
		    // Construct and execute the API request to insert the stream.
		    YouTube.LiveStreams.Delete liveStreamDelete =
		    		youtube.liveStreams().delete(stream.getId());
		    liveStreamDelete.execute();
		    System.out.println(title +" was deleted");
		    return true;
		        
	    } catch (GoogleJsonResponseException e) {
	        System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
	            + e.getDetails().getMessage());
		    e.printStackTrace();
		    return false;
		} catch (IOException e) {
		    System.err.println("IOException: " + e.getMessage());
		    e.printStackTrace();
		    return false;
		} catch (Throwable t) {
		    System.err.println("Throwable: " + t.getMessage());
		    t.printStackTrace();
		    return false;
	    }
	}
	
	/**
	 * this method sends an update description request , returns true if server handles it ,false if not
	 * @param description
	 * @param liveBroadcast
	 * @throws IOException
	 */
	public static boolean updateDescription(String description , LiveBroadcast liveBroadcast)  {
	
		try {	
			
			 // Define the LiveBroadcast object, which will be uploaded as the request body.
	        LiveBroadcast liveBroadcastupdate = new LiveBroadcast();
	        
	        // Add the id string property to the LiveBroadcast object.
	        liveBroadcastupdate.setId(liveBroadcast.getId());
	        
	        // Add the snippet object property to the LiveBroadcast object.
	        LiveBroadcastSnippet snippet = new LiveBroadcastSnippet();
	        snippet.setDescription(description);
	        snippet.setScheduledStartTime(liveBroadcast.getSnippet().getScheduledStartTime());
	        snippet.setTitle(liveBroadcast.getSnippet().getTitle());
	        
	        liveBroadcastupdate.setSnippet(snippet);
			
			YouTube.LiveBroadcasts.Update request = youtube.liveBroadcasts()
		            .update("snippet",liveBroadcastupdate);
		        LiveBroadcast response = request.execute();
		    
		        if(response.getSnippet().getDescription().equals(description))
		        	return true;
		        return false;
		} catch (GoogleJsonResponseException e) {
		        System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
		            + e.getDetails().getMessage());
			    e.printStackTrace();
			    return false;
			} catch (IOException e) {
			    System.err.println("IOException: " + e.getMessage());
			    e.printStackTrace();
			    return false;
			} catch (Throwable t) {
			    System.err.println("Throwable: " + t.getMessage());
			    t.printStackTrace();
			    return false;
		    }    
	}
	
	/**
	 * Retrieve a stream by it's name
	 * @param name
	 * @return
	 * @throws IOException
	 */
 	public static LiveStream getStreamByName(String name) throws IOException {
		// Create a request to list liveStream resources.
	YouTube.LiveStreams.List livestreamRequest = youtube.liveStreams().list("id,snippet,status");
	
	// Modify results to only return the user's streams.
	livestreamRequest.setMine(true);
	//get relevant stream
	    LiveStreamListResponse returnedListResponse = livestreamRequest.execute();
	    List<LiveStream> returnedList = returnedListResponse.getItems();
	    for (LiveStream stream : returnedList) {
	    	if(stream.getSnippet().getTitle().equals(name))
	    		return stream;
	    }
		return null;
	}
	
	/**
	 * this method prompts to the GUI about an error occurrence
	 */
	private static void reportError(String problem) {
	    	JOptionPane.showMessageDialog(null,
	    			problem,
	                "Server request ERROR",
	                JOptionPane.ERROR_MESSAGE);
	    			//send massage
	    }
}
