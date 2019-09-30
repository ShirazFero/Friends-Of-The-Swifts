package com.youtube.controller;

import java.util.List;

import javax.swing.JOptionPane;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;
import com.youtube.app.CompleteBroadcast;
import com.youtube.app.CreateBroadcast;
import com.youtube.app.CreateStream;
import com.youtube.app.DeleteStream;
import com.youtube.app.ListBroadcasts;
import com.youtube.app.ListStreams;

public class Controller {

	private List<LiveStream> streams;
	private List<LiveBroadcast> broadcasts;
	private double interval;
	
	public double getInterval() {
		return interval;
	}

	public void setInterval() {
		String interval= JOptionPane.showInputDialog("please enter requested interval in HH:MM Format ");
		this.interval = toTimeObject(interval);
		
	}

	private double toTimeObject(String interval) {
		
		return 0;
	}

	public Controller() {
		streams=ListStreams.run(null);
		broadcasts=ListBroadcasts.run(null);
	}
	
	public List<LiveStream> getStreams() {
		return streams;
	}
	
	
	public List<LiveBroadcast> getBroadcasts(){
		return broadcasts;
	}
	
	public void refreshStreams() {
		if(streams!=null)
			streams.clear();
		streams=ListStreams.run(null);
	}
	
	public void refreshBroadcasts() {
		if(broadcasts!=null)
			broadcasts.clear();
		broadcasts=ListBroadcasts.run(null);
	}
	
	
	public void addStream() {
		String[] args = new String[1];
		args[0]=JOptionPane.showInputDialog("please enter stream name");
		CreateStream.run(args);
		refreshStreams();
	}
	
	public void removeStream(Boolean[] checked) {
		String[] args = new String[1];
		for(int i=0;i<streams.size();i++) {
			if(checked[i]) {
				args[0]=streams.get(i).getSnippet().getTitle();
				DeleteStream.run(args);
			}
		}
		refreshStreams();
	}
	
	public void startBroadcast() {
		CreateBroadcast.run(null);
		refreshBroadcasts();
	}
	
	public void stopBroadcast() {
		CompleteBroadcast.run(null);
		refreshBroadcasts();
	}
	
}
