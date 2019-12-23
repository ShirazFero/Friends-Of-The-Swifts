package com.youtube.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.swing.SwingWorker;

import com.youtube.gui.BroadcastPanel;
import com.youtube.utils.Constants;

public class LoadingTasks extends SwingWorker<Void, Void>  {

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		//Controller controller = Controller.getInstance();
		int percentage = Math.round(100/ Constants.isLive) ,progress = 0;
		Object lock = new Object();	// obtain a lock
		int lastIsLiveValue = Constants.isLive, addToProgress=0;
		while(Constants.isLive>0) {
			Thread.sleep(1000);
			synchronized (lock) {	//sync Thread
				if(lastIsLiveValue>Constants.isLive) {
					addToProgress = (lastIsLiveValue-Constants.isLive)*percentage;
					progress+=addToProgress;	//add percentage
					setProgress(progress);	//set progress
					lastIsLiveValue = Constants.isLive;
				}
			}
		}
		notify();
		return null;
	}
							
	
	@Override
	public void done() {
			//prompt active broadcasts to broadcast panel
		try {
				// prepare failure message
				if(!Constants.badResults.isEmpty()) {
					String allTitles =" ";
					for(String title:Constants.badResults) {
						allTitles+=", " + title;
					}
					try {  //send failure message
						MailUtil.sendMail(Constants.UserEmail,
								"Server request problem",
								"Problem starting broadcast\\s "+ allTitles +",\n"+
				                "please check manually at " +Constants.LiveStreamUrl);
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				setProgress(100);
				System.out.println("done loading tasks");
				Controller controller= Controller.getInstance();;
			    String[] args = {"refresh","active",null,null};
			    controller.refreshBroadcasts(args);
				BroadcastPanel broadcastPanel =BroadcastPanel.getInstance();
				broadcastPanel.setData(controller.getBroadcasts());
				broadcastPanel.refresh();
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
					 | InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
}
