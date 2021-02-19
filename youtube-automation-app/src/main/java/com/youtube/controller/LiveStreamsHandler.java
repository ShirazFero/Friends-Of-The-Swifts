package com.youtube.controller;

import java.awt.HeadlessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.simple.parser.ParseException;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.api.ErrorHandler;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.ProgressFrame;
import com.youtube.utils.Constants;

public class LiveStreamsHandler {

	private List<LiveStream> streams;		
	
	private  String[] checkedStreams;			//holds checked streams from inputform
	
	public List<LiveStream> getStreams() {
		return streams;
	}
	
	
	public void setCheckedStreams(String[] checkedStreams) {
		this.checkedStreams = checkedStreams;
	}
	
	public String[] getCheckedStreams() {
		return checkedStreams;
	}

	public boolean refreshStreams() throws SecurityException, IOException  
	{
		if(streams != null) {
			streams.clear();
		}
		streams = YouTubeAPI.getInstance().listStreams(null);
		return streams != null ? true : false;
	}
	
	public void addStream() throws IOException, HeadlessException, ParseException 
	{
		Constants.AddingStream = JOptionPane.showInputDialog("please enter stream name");
		if(Constants.AddingStream == null) {
			Constants.DebugPrint("requset cancelled");
			return ;
		}
		try {
			new ProgressFrame().StreamTask();
		} catch (IOException e) {
			e.printStackTrace();
			ErrorHandler.HandleApiError(e.toString());
		}
		
	}
	
	public void removeStream(String[] streamid) throws IOException, ParseException 
	{
		Constants.StreamToRemove = new ArrayList<LiveStream>();
		for(String id : streamid) {
			Constants.StreamToRemove.add(getStreamById(id));
		}
		try {
			new ProgressFrame().StreamTask();
		} catch (IOException e) {
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
	
	public String getStreamTitleFromId(String id) 
	{
		for(LiveStream stream : streams) {
			if(stream.getId().equals(id))
				return stream.getSnippet().getTitle();
		}
		return null;
	}
	
	public  LiveStream getStreamByName(String title) throws IOException, ParseException 
	{
		for (LiveStream stream : streams) {
			if(stream.getSnippet().getTitle().equals(title)) {
				return stream;
			}
		}
		return null;
	}
	
	public  LiveStream getStreamById(String id) throws IOException, ParseException
		{
			for (LiveStream stream : streams) {
				if(stream.getId().equals(id)) {
					return stream;
				}
			}
			return null;
		}
	
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
