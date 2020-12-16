package com.youtube.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.crypto.NoSuchPaddingException;

import org.json.simple.parser.ParseException;
import com.youtube.api.YouTubeAPI;

/**
 * This class handles data flow from the API to the GUI and backwards,
 * all it's functions control API requests , also it temporarily holds
 * the data that is being transported to the GUI.  
 * @author Evgeny Geyfman
 *
 */
public class Controller {
	
	public Controller() throws SecurityException, IOException {
		YouTubeAPI.getInstance();
		m_streamHandler = new LiveStreamsHandler();
		m_broadcastsHandler = new LiveBroadcastHandler(m_streamHandler);
	}

	public LiveStreamsHandler getStreamHandler() {
		return m_streamHandler;
	}

	public LiveBroadcastHandler getBroadcastsHandler() {
		return m_broadcastsHandler;
	}

	/**
	 * @return the timerRunner
	 */
	public TimerRunner getTimerRunner() {
		return timerRunner;
	}
	
	/**
	 * This method creates and start a timer runner object 
	 * @throws InterruptedException
	 */
	public void startTimerRunner() throws InterruptedException {
			timerRunner = new TimerRunner(m_broadcastsHandler.calcStopTime());
	}
	
	public void setTimerRunner(Date stoptime) throws InterruptedException {
			timerRunner = new TimerRunner(stoptime);
	}
	
		/**
	 * This method stops timer runner object
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void cancelTimerRunner() throws InterruptedException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException {
		timerRunner.stopIntervalBroadcast();
	}

/**
 	* Singleton instance retriever
 * @return
 * @throws IOException 
 * @throws FileNotFoundException 
 * @throws NoSuchPaddingException 
 * @throws NoSuchAlgorithmException 
 * @throws InvalidKeyException 
 * @throws ParseException 
 * @throws InvalidAlgorithmParameterException 
 */
	public static Controller getInstance() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, InvalidAlgorithmParameterException {
		if(instance==null)
			instance = new Controller();
		return instance;
	}

	private LiveStreamsHandler m_streamHandler;	
	
	private LiveBroadcastHandler m_broadcastsHandler;		//holds currently presented broadcasts

	private TimerRunner timerRunner;						//holds current timer runner

	private static Controller instance;		
}
