package com.youtube.controller;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.youtube.api.ErrorHandler;
import com.youtube.api.YouTubeAPI;
import com.youtube.utils.Constants;

public class UpdateTasks extends SwingWorker<Void, Void>{

	@Override
	protected Void doInBackground() throws Exception {
		int percentage = Math.round(100/ Constants.BroadcastsToUpdate.size()) ,progress = 0;
		ArrayList<String> Badresults = new ArrayList<String>();	
		for(LiveBroadcast broadcast : Constants.BroadcastsToUpdate) {
				if(!YouTubeAPI.getInstance().updateDescription(Constants.Description, broadcast)) //try to update
					Badresults.add( broadcast.getSnippet().getTitle());
				progress+=percentage;	//add percentage
				setProgress(progress);
		}
		
		if(!Badresults.isEmpty()) {
			String massage  = "Descriptions of following Broadcasts weren't set correctly to:  \r\n";
			for(String title : Badresults) {
				massage += title +" ERROR code: " + Constants.ErrorArgs[0] + ", ERROR message : "+ Constants.ErrorArgs[1]+ ",\r\n ";
			}
			ErrorHandler.HandleMultipleError(massage);
			return null;
		}
		
		JOptionPane.showMessageDialog(null,"Description updated","Completed",JOptionPane.INFORMATION_MESSAGE);	
		return null;
	}

	@Override
	public void done() {
		setProgress(100);
		System.out.println("done updating descriptions");
		
	}
}
