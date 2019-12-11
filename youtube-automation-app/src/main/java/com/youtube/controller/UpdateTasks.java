package com.youtube.controller;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.youtube.api.YouTubeAPI;
import com.youtube.utils.Constants;

public class UpdateTasks extends SwingWorker<Void, Void>{

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		int percentage = Math.round(100/ Constants.BroadcastsToUpdate.size()) ,progress = 0;
		ArrayList<String> Badresults = new ArrayList<String>();	
		for(LiveBroadcast broadcast : Constants.BroadcastsToUpdate) {
				if(!YouTubeAPI.updateDescription(Constants.Description, broadcast)) //try to update
					Badresults.add( broadcast.getSnippet().getTitle());
				progress+=percentage;	//add percentage
				setProgress(progress);
		}
		
		if(!Badresults.isEmpty()) {
			String massage  = "Descriptions of following Broadcasts weren't set correctly to ";
			for(String title : Badresults) {
				massage += title + ",\r\n ";
			}
			massage += "please check internet connection"; 
				JOptionPane.showMessageDialog(null,massage,"Server request ERROR",JOptionPane.ERROR_MESSAGE);
		}
		
		JOptionPane.showMessageDialog(null,"Description updated","Completed",JOptionPane.INFORMATION_MESSAGE);	
		return null;
	}

	
	@Override
	public void done() {
		setProgress(100);
		System.out.println("done");
		
	}
}
