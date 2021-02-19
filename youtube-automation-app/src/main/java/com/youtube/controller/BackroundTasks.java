package com.youtube.controller;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
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
	protected Void doInBackground() {
		try {
			Controller controller = Controller.getInstance();
			int progress = 0;
			setProgress(progress+=33);
			if(!controller.getStreamHandler().refreshStreams()) {			//get initial streams
				String failmsg = "failed fetching streams on boot load";
				ErrorHandler.HandleLoadError(failmsg);
				System.exit(1);
			}
			setProgress(progress+=33);
			String[] args = {"active",Constants.NumberOfResulsts,null};
			if(!controller.getBroadcastsHandler().refreshBroadcasts(args)){	//get initial broadcasts
				String failmsg = "failed fetching broadcasts on boot load";
				ErrorHandler.HandleLoadError(failmsg);
				System.exit(1);
			}
			setProgress(progress+=34);
			return null;
		}catch(IOException e ) {
			e.printStackTrace();
			ErrorHandler.HandleLoadError(e.toString());
			System.exit(1);
		}
		return null;
	}
	
	@Override
	public void done() {
	    Toolkit.getDefaultToolkit().beep();
	    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
						addNewUser();
						new MainFrame();
				} catch (IOException | ParseException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
					e.printStackTrace();
					ErrorHandler.HandleLoadError(e.toString());
				}
			}
		});
	}
	
	private void addNewUser() throws InvalidKeyException, FileNotFoundException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, ParseException
	{
		AppBootLoader loader = new AppBootLoader();
		if(!loader.userExists(Constants.Username)) {
			loader.addUserToFile(Constants.Username);
		}
	}
}
