package com.youtube.controller;

import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

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
		setProgress(progress+=33);
		controller.initStreams();
		setProgress(progress+=33);
		controller.initBroadcasts();
		setProgress(progress+=34);
		return null;
	}
	
	@Override
	public void done() {
	    setProgress(100);
	    Toolkit.getDefaultToolkit().beep();
	    SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new mainFrame(controller);
				
			}
		});
	}

}
