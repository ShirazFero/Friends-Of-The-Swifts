package com.youtube.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.SwingWorker;

import com.youtube.api.ErrorHandler;
import com.youtube.gui.BroadcastPanel;
import com.youtube.gui.IntervalPanel;
import com.youtube.utils.Constants;

public class LoadingTasks extends SwingWorker<Void, Void>  {

	private AtomicInteger m_percentageCounter;
	
	public LoadingTasks(AtomicInteger percerntageCounter)
	{
		m_percentageCounter = percerntageCounter;
	}
	
	@Override
	public void done() {
		//prompt active broadcasts to broadcast panel
		try {
			Constants.pollingState = false;
			handleBadResults();
			notifyTimerRunner();
			updateIntervalPanelText();
			if(Constants.DEBUG) {
				System.out.println("done loading tasks");
			}
			updateBroadcastPanel();
			
		} catch (MessagingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException
				 | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			ErrorHandler.HandleLoadError(e.toString());
		}
	}
	
	@Override
	protected Void doInBackground() throws Exception 
	{
		if(m_percentageCounter.get() <= 0 || m_percentageCounter.get() > 100){
			throw new Exception("bad percentage number chosen");
		}
		int percentage = Math.round(100 / m_percentageCounter.get()) ;
		int lastIsLiveValue = m_percentageCounter.get();
		while(m_percentageCounter.get() > 0) {
			Thread.sleep(1000);
			int delta = lastIsLiveValue - m_percentageCounter.get();
			if(delta > 0) {
				updateProgress( delta, percentage);
				lastIsLiveValue = m_percentageCounter.get();
			}
		}
		return null;
	}
		
	private void updateProgress(int delta, int percentage)
	{
		int currPrgress = getProgress() + (delta * percentage);													
		setProgress(currPrgress);	
	}
	
	private void handleBadResults() throws AddressException, MessagingException
	{
		if(Constants.badResults!=null && !Constants.badResults.isEmpty()) {
			String allTitles =" ";
			for(String title : Constants.badResults) {
					allTitles += title +",\n";
			}
			if(Constants.SendEmail) {
				  //send failure message
					MailUtil.sendMail(Constants.UserEmail,"Server request problem",
							"Problem starting Broadcasts:\n"+ allTitles +",\n"+
			                "please check manually at " +Constants.LiveStreamUrl);
			}
			ErrorHandler.HandleMultipleError(allTitles);
		}
	}
	
	private void notifyTimerRunner()
	{
		synchronized (Constants.timeredRunnerLock) {
			Constants.timeredRunnerLock.notify();
		}
	}
	
	private void updateIntervalPanelText() throws NullPointerException
	{
		IntervalPanel intervalPanel = IntervalPanel.getInstance();
		String lbltext = "Hello " + Constants.Username;
		if(Constants.State.equals("Starting") ) {
			lbltext += ", you are live!" ;
		}
		intervalPanel.getLblHello().setText(lbltext);
	}
	
	private void updateBroadcastPanel() throws SecurityException, IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException
	{
		Controller controller = Controller.getInstance();
	    String[] args = {"active", Constants.NumberOfResulsts, null};
	    controller.getBroadcastsHandler().refreshBroadcasts(args);
		BroadcastPanel broadcastPanel = BroadcastPanel.getInstance();
		broadcastPanel.setData(controller.getBroadcastsHandler().getBroadcasts());
		broadcastPanel.refresh();
	}
	
}
