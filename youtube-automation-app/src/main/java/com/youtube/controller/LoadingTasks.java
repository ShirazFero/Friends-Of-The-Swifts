package com.youtube.controller;

import javax.swing.SwingWorker;

import com.youtube.gui.BroadcastPanel;
import com.youtube.utils.Constants;

public class LoadingTasks extends SwingWorker<Void, Void>  {

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		Controller controller =Controller.getInstance();
		int percentage = Math.round(100/ controller.getCheckedStreams().length) ,progress = 0;
		//Object lock = new Object();	// obtain a lock
		
		//initiate an array of flags one for each broadcast,
		//to mark that the a live broadcast started and not be checked again
		Boolean[] marked = new Boolean[Constants.isLive.length];	
		for(int i=0;i<marked.length;i++) {
			marked[i]=false;						//set initial values to false
		}
		while(true) {
			Thread.sleep(1000);
			//synchronized (lock) {					//sync Thread
				for(int i=0;i<Constants.isLive.length;i++) {
					if(Constants.isLive[i]) {		//check if broadcast has started
						if(!marked[i]) {			//check that broadcast hasn't been marked yet 
							System.out.println("setting progress");
							progress+=percentage;	//add percentage
							setProgress(progress);	//set progress
							marked[i]=true;			//mark as live
						}
					}
				}
			//}
			if(allMarked(marked)) {	//if all broadcasts have been marked 
				done();				//complete 
				return null;
			}
		}
	}
	
	private boolean allMarked(Boolean[] flags) {
		for(int i=0;i<flags.length;i++) {
			if(flags[i]==false)
				return false;
		}
		return true;
	}

	@Override
	public void done() {
			//prompt active broadcasts to broadcast panel
			setProgress(100);
			System.out.println("done");
			Controller controller =Controller.getInstance();
		    String[] args = {"refresh","active"};
		    controller.refreshBroadcasts(args);
			BroadcastPanel broadcastPanel =BroadcastPanel.getInstance();
			broadcastPanel.setData(controller.getBroadcasts());
			broadcastPanel.refresh();
		  
	}
	
}
