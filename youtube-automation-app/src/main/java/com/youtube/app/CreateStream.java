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

import javax.swing.JOptionPane;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamSnippet;
import com.youtube.utils.Constants;


/**
 * Use the YouTube Live Streaming API to Create a live Stream
 * and insert it to the Stream list. Use OAuth 2.0 to authorize the API requests.
 *
 * @author Evgeny Geyfman
 */
public class CreateStream extends Thread {

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
            System.out.println("You chose " + title + " for stream title.");

            // Create a snippet with the video stream's title.
            LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
            streamSnippet.setTitle(title);

            // Define the content distribution network settings for the
            // video stream. The settings specify the stream's format and
            // ingestion type. See:
            // https://developers.google.com/youtube/v3/live/docs/liveStreams#cdn
            CdnSettings cdnSettings = new CdnSettings();
            cdnSettings.setFormat("1080p");
            cdnSettings.setIngestionType("rtmp");
       
          	LiveStream stream = new LiveStream();
            stream.setKind("youtube#liveStream");
            stream.setSnippet(streamSnippet);
            stream.setCdn(cdnSettings);
				
            // Construct and execute the API request to insert the stream.
            YouTube.LiveStreams.Insert liveStreamInsert =
            		CreateYouTube.getYoutube().liveStreams().insert("snippet,cdn,status", stream);
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
        
    /**
     * this method prompts to the GUI about an error occurrence
     */
    private static void reportError() {
    	JOptionPane.showMessageDialog(null,
                "Problem creating stream",
                "Server request problem",
                JOptionPane.ERROR_MESSAGE);
    }
}
