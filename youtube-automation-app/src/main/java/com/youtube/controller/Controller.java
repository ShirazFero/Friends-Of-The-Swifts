package com.youtube.controller;

import java.io.IOException;
import com.youtube.api.YouTubeAPI;

/**
 * This class handles data flow from the API to the GUI and backwards,
 * all it's functions control API requests , also it temporarily holds
 * the data that is being transported to the GUI.  
 * @author Evgeny Geyfman
 *
 */
public class Controller {
	
	private YouTubeAPI m_youtubeService;

	private LiveStreamsHandler m_streamHandler;	
	
	private LiveBroadcastHandler m_broadcastsHandler;		//holds currently presented broadcasts

	private UserDataHandler m_userDataHandler;
	
	private static Controller instance;	
	
	private Controller() throws IOException {
		m_youtubeService = YouTubeAPI.getInstance();
		m_streamHandler = new LiveStreamsHandler();
		m_broadcastsHandler = new LiveBroadcastHandler(m_streamHandler);
		m_userDataHandler = new UserDataHandler();
	}
	
	public YouTubeAPI getYouTubeService() {
		return m_youtubeService;
	}

	public LiveStreamsHandler getStreamHandler() {
		return m_streamHandler;
	}

	public LiveBroadcastHandler getBroadcastsHandler() {
		return m_broadcastsHandler;
	}

	public UserDataHandler getUserDataHandler() {
		return m_userDataHandler;
	}
	
	public static Controller getInstance() throws IOException {
		if(instance==null)
			instance = new Controller();
		return instance;
	}
}
