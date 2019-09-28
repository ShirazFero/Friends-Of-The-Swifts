package com.youtube.controller;

import java.util.List;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;
import com.youtube.app.CompleteBroadcast;
import com.youtube.app.CreateBroadcast;
import com.youtube.app.CreateStream;
import com.youtube.app.ListBroadcasts;
import com.youtube.app.ListStreams;

public class Controller {

	private List<LiveStream> streams;
	private List<LiveBroadcast> broadcasts;
	
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
		CreateStream.run(null);
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
