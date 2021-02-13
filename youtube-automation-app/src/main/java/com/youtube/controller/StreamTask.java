package com.youtube.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.google.api.services.youtube.model.LiveStream;
import com.youtube.api.ErrorHandler;
import com.youtube.api.YouTubeAPI;
import com.youtube.gui.StreamPanel;
import com.youtube.utils.Constants;

/**
 * This task handles Api requests on adding & removing streams
 * @author Jos pc
 *
 */
public class StreamTask extends SwingWorker<Void, Void>{

	@Override
	protected Void doInBackground() throws Exception {
		if(Constants.AddingStream==null) {
			int percentage = Math.round(100/ Constants.StreamToRemove.size()) ,progress = 0;
			ArrayList<String> Badresults = new ArrayList<String>();	
			for(LiveStream stream : Constants.StreamToRemove) {
				String title  = stream.getSnippet().getTitle();
				if(!YouTubeAPI.getInstance().deleteStream(title)) { //if didn't succeed
					Badresults.add(title);		//add stream title to error massage
				}
				else { 
					progress+=percentage;	//add percentage
					setProgress(progress);
				}
			}
			if(!Badresults.isEmpty()) {
				String massage  = "Following Streams weren't deleted:  ";
				for(String title : Badresults) {
					massage += title +" ERROR code: " + Constants.ErrorArgs[0] + ", ERROR message : "+ Constants.ErrorArgs[1]+ ",\r\n ";
				}
				ErrorHandler.HandleMultipleError(massage);
				return null;
			}
			JOptionPane.showMessageDialog(null,"Streams removed","Completed",JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			String title = Constants.AddingStream;
			if(YouTubeAPI.getInstance().createStream(title)) {
				setProgress(100);
			}
		}
		return null;
	}
				

	@Override
	public void done() {
		try {
			Controller cont = Controller.getInstance();
			cont.getStreamHandler().refreshStreams();
			StreamPanel streamPanel = StreamPanel.getInstance();
			streamPanel.setData(cont.getStreamHandler().getStreams());	//set new data to table
			streamPanel.refresh();
			setProgress(100);
			if(Constants.AddingStream != null) {
				System.out.println("done adding streams");
				JOptionPane.showMessageDialog(null,Constants.AddingStream +
						" Stream Added Successfully","Completed",JOptionPane.INFORMATION_MESSAGE);
				Constants.AddingStream = null;
			}
			else {
				System.out.println("done removing streams");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
