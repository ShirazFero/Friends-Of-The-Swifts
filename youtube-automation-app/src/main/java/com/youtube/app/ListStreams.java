package com.youtube.app;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.youtube.app.Auth;
import com.youtube.utils.Constants;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamListResponse;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

/**
 * Retrieve a list of a channel's streams, using OAuth 2.0 to authorize
 * API requests.
 *
 * @author Ibrahim Ulukaya
 */
public class ListStreams extends Thread{

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * List streams for the user's channel.
     */
    public static List<LiveStream> run(String[] args) {

        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "liststreams");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube-automation-app")
                    .build();

            // Create a request to list liveStream resources.
            YouTube.LiveStreams.List livestreamRequest = youtube.liveStreams().list("id,snippet,status,cdn");

            // Modify results to only return the user's streams.
            livestreamRequest.setMine(true);
            livestreamRequest.setMaxResults((long) 50); //show top 10 streams
            // Execute the API request and return the list of streams.
            LiveStreamListResponse returnedListResponse = livestreamRequest.execute();
            List<LiveStream> returnedList = returnedListResponse.getItems();
            System.out.println(returnedListResponse.getPageInfo());
            
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
           
           return returnedList;
            
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
}

