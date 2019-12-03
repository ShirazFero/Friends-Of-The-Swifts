package com.youtube.controller;

import java.awt.Toolkit;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.json.simple.parser.ParseException;

import com.youtube.api.YouTubeAPI;
import com.youtube.gui.mainFrame;

public class BackroundTasks extends SwingWorker<Void, Void> {

	private Controller controller;
	
	public BackroundTasks(Controller controller){
		this.controller=controller;
	}
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		int progress = 0;
		new YouTubeAPI(null);			//generate youtube instance authorize it
		setProgress(progress+=33);
		controller.refreshStreams();		//get initial streams
		setProgress(progress+=33);
		String[] args = {"init","active"};
		controller.refreshBroadcasts(args);	//get initial broadcasts
		setProgress(progress+=34);
		return null;
	}
	
	@Override
	public void done() {
	    setProgress(100);
	    Toolkit.getDefaultToolkit().beep();
	    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
						new mainFrame();
				} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | ParseException | InvalidAlgorithmParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
