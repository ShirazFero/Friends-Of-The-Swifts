package com.youtube.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.swing.SwingWorker;

import com.youtube.api.ErrorHandler;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.IntervalPanel;
import com.youtube.utils.Constants;

public class LoadingTasks extends SwingWorker<Void, Void>  {

	@Override
	protected Void doInBackground() throws Exception {
		int percentage = Math.round(100/ Constants.isLive) ,progress = 0;
		int lastIsLiveValue = Constants.isLive, addToProgress=0;
		while(Constants.isLive>0) {
			Thread.sleep(1000);
			
			if(lastIsLiveValue>Constants.isLive) {
				addToProgress = (lastIsLiveValue-Constants.isLive)*percentage; //calc the percentage to add
				progress+=addToProgress;	//add percentage
				setProgress(progress);	//set progress
				lastIsLiveValue = Constants.isLive;
			}
		}
		return null;
	}
							
	
	@Override
	public void done() {
		//prompt active broadcasts to broadcast panel
		try {
			Constants.pollingState = false;
			// prepare failure message
			if(Constants.badResults!=null && !Constants.badResults.isEmpty()) {
				String allTitles =" ";
				for(String title:Constants.badResults) {
						allTitles+= title +",\n";
					}
				if(Constants.SendEmail) {
					  //send failure message
						MailUtil.sendMail(Constants.UserEmail,"Server request problem",
								"Problem starting Broadcasts:\n"+ allTitles +",\n"+
				                "please check manually at " +Constants.LiveStreamUrl);
				}
				ErrorHandler.HandleMultipleError(allTitles);
			}
			synchronized (Constants.monitorLock) {
				Constants.monitorLock.notify();
			}
			IntervalPanel intervalPanel = IntervalPanel.getInstance();
			if(Constants.State!=null && Constants.State.equals("Starting") ) {
				intervalPanel.getLblHello().setText("Hello "+Constants.Username+", you are live!");
			}
			if(Constants.State!=null && Constants.State.equals("Completing") ) {
				intervalPanel.getLblHello().setText("Hello "+Constants.Username);
			}
			System.out.println("done loading tasks");
			Controller controller = Controller.getInstance();
		    String[] args = {"refresh","active",null,null};
		    controller.refreshBroadcasts(args);
			BroadcastPanel broadcastPanel = BroadcastPanel.getInstance();
			broadcastPanel.setData(controller.getBroadcasts());
			broadcastPanel.refresh();
		} catch (MessagingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
				 | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLogger.logger.info(e.toString());
		}
	}
	
}
