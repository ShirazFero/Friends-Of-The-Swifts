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

import com.youtube.api.ErrorHandler;
import com.youtube.gui.MainFrame;
import com.youtube.utils.Constants;

public class BackroundTasks extends SwingWorker<Void, Void> {

	@Override
	protected Void doInBackground() throws Exception {
		Controller controller = Controller.getInstance();
		int progress = 0;
		setProgress(progress+=33);
		if(!controller.getStreamHandler().refreshStreams()) {			//get initial streams
			String failmsg = "failed fetching streams on boot load";
			if(Constants.DEBUG) {
				System.out.println(failmsg);
			}
			ErrorHandler.HandleLoadError(failmsg);
			System.exit(1);
		}
		setProgress(progress+=33);
		String[] args = {"active",Constants.NumberOfResulsts,null};
		if(!controller.getBroadcastsHandler().refreshBroadcasts(args)){	//get initial broadcasts
			String failmsg = "failed fetching broadcasts on boot load";
			System.out.println(failmsg);
			ErrorHandler.HandleLoadError(failmsg);
			System.exit(1);
		}
		setProgress(progress+=34);
		return null;
	}
	
	@Override
	public void done() {
	    Toolkit.getDefaultToolkit().beep();
	    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
						new MainFrame();
				} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | ParseException | InvalidAlgorithmParameterException e) {
					e.printStackTrace();
					ErrorHandler.HandleLoadError(e.toString());
				}
			}
		});
	}

}
