package com.youtube.controller;

import java.awt.HeadlessException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.api.ErrorHandler;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.ProgressFrame;
import com.youtube.utils.Constants;

/***
 * @author Jos pc
 * @info this class is responsible on handling users LiveStreams
 *
 */
public class LiveStreamsHandler {

	private List<LiveStream> streams;		
	
	private  String[] checkedStreams;			//holds checked streams from inputform
	
	private static LiveStreamsHandler instance;
	
	public static LiveStreamsHandler getInstance() {
		if(instance == null) {
			instance = new LiveStreamsHandler();
		}
		return instance;
	}
		
	public List<LiveStream> getStreams() {
		return streams;
	}
	
	public void setCheckedStreams(String[] checkedStreams) {
		this.checkedStreams = checkedStreams;
	}
	
	public String[] getCheckedStreams() {
		return checkedStreams;
	}

	/**
	 * Retrieves  new list of streams , delete old one if exists
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws ParseException 
	 */
	public boolean refreshStreams() throws SecurityException, IOException  
	{
		if(streams != null) {
			streams.clear();
		}
		streams = YouTubeAPI.listStreams(null);
		return streams != null ? true : false;
	}
	
	/**
	 * This method inserts a new live stream to the database
	 * @param checked array indicates whether a stream was chosen or not
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws HeadlessException 
	 */
	public void addStream() throws IOException, HeadlessException, ParseException 
	{
		Constants.AddingStream = JOptionPane.showInputDialog("please enter stream name");
		if(Constants.AddingStream == null) {
			if(Constants.DEBUG) {
				System.out.println("requset cancelled");
			}
			return ;
		}
		try {
			new ProgressFrame().StreamTask();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException | ParseException e) {
			e.printStackTrace();
			ErrorHandler.HandleApiError(e.toString());
		}
		
	}
	
	/**
	 * This method removes a live stream from database
	 * @param checked array indicates whether a stream was chosen or not
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public void removeStream(String[] streamid) throws IOException, ParseException 
	{
		Constants.StreamToRemove = new ArrayList<LiveStream>();
		for(String id : streamid) {
			Constants.StreamToRemove.add(getStreamById(id));
		}
		try {
			new ProgressFrame().StreamTask();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IOException | ParseException e) {
			e.printStackTrace();
			ErrorHandler.HandleApiError(e.toString());
		}
	}
	
	/**
	 * 
	 * @return stream titles
	 */
	public String[] getStreamTitles() {
		String[] titles = new String[streams.size()];
		int index = 0;
		for(LiveStream stream : streams) {
				titles[index++] = stream.getSnippet().getTitle();
		}
		return titles;
	}
	
	/**
	 * helper method retrieve stream title 
	 * @param 
	 */
	public String getStreamTitleFromId(String id) 
	{
		for(LiveStream stream : streams) {
			if(stream.getId().equals(id))
				return stream.getSnippet().getTitle();
		}
		return null;
	}
	
	/**
	 * returns relevant stream title from streams List
	 * @param title
	 * @return
	 */
	public  LiveStream getStreamByName(String title) throws IOException, ParseException 
	{
		for (LiveStream stream : streams) {
			if(stream.getSnippet().getTitle().equals(title)) {
				return stream;
			}
		}
		return null;
	}
	
	/**
	 * returns relevant stream from streams List
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public  LiveStream getStreamById(String id) throws IOException, ParseException
		{
			for (LiveStream stream : streams) {
				if(stream.getId().equals(id)) {
					return stream;
				}
			}
			return null;
		}
	
		/**
	 * This method filters streams by there status={active,ready,inactive}
	 * @param filter
	 * @return filterdList
		 * @throws IOException 
		 * @throws SecurityException 
	 * @throws ParseException 
	 */
	public List<LiveStream> filterStreams(String filter) throws SecurityException, IOException {
		refreshStreams();											// get latest list of streams from server
		List<LiveStream> filterdList = new ArrayList<LiveStream>();	//init return list
		for(LiveStream stream : streams) {
			if(stream.getStatus().getStreamStatus().equals(filter))
				filterdList.add(stream);
		}
		return filterdList;
}

}
