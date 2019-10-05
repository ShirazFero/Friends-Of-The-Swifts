package com.youtube.app;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamListResponse;


/**
 * Use the YouTube Live Streaming API to Delete a live Stream
 * and remove it from the Stream list. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Evgeny Geyfman
 */
public class DeleteStream extends Thread {


    /**
     * Create and insert a liveBroadcast resource.
     */
    public static void run(String[] args) {


        try {

            // Prompt the user to enter a title for the video stream.
            String title;
            if(args==null)
            	title = getStreamTitle();
            title=args[0];
            System.out.println("You chose to delete " + title + " for stream title.");
				
            LiveStream stream = getStreamByName(title);
            System.out.println(stream.getId());
            // Construct and execute the API request to insert the stream.
            YouTube.LiveStreams.Delete liveStreamDelete =
            		CreateYouTube.getYoutube().liveStreams().delete(stream.getId());
           liveStreamDelete.execute();
           System.out.println(title +" was deleted");
      
            
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
        }
            private static String getStreamTitle() throws IOException {

                String title = "";

                System.out.print("Please enter a stream title: ");
                BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
                title = bReader.readLine();

                if (title.length() < 1) {
                    // Use "New Stream" as the default title.
                    title = "New Stream";
                }
                return title;
            }
            
            private static LiveStream getStreamByName(String name) throws IOException {
            	// Create a request to list liveStream resources.
                YouTube.LiveStreams.List livestreamRequest = CreateYouTube.getYoutube().liveStreams().list("id,snippet,status");

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
}