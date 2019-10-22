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

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Lists;


/**
 * create an YouTube Object for API requests and authorize it via OAuth 2.0
 *
 * 
 * @author Evgeny Geyfman
 * */

public class CreateYouTube {

	 private static YouTube youtube;

	    /**
	     * Create and insert a liveBroadcast/liveStream resource.
	     */
	    public CreateYouTube(String[] args) {

	        // This OAuth 2.0 access scope allows for full read/write access to the
	        // authenticated user's account.
	        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

	        try {
	            // Authorize the request.
	            Credential credential = Auth.authorize(scopes,"createbroadcast");

	            // This object is used to make YouTube Data API requests.
	            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
	                    .setApplicationName("youtube-automation-app").build();
	            
	            
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
	            reportError();
	        }
	    }

		public static YouTube getYoutube() {
			return youtube;
		}

		/**
	     * this method prompts to the GUI about an error occurrence
	     */
	    private static void reportError() {
	    	JOptionPane.showMessageDialog(null,
	                "Problem Authenticating user",
	                "Server request problem",
	                JOptionPane.ERROR_MESSAGE);
	    }
}
