package com.youtube.api;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.youtube.controller.FileLogger;
import com.youtube.utils.Constants;

/**
 * this Class generates and prompts to the GUI about an error occurrence
 */
public class ErrorHandler {

	public static synchronized void HandleError(Object[] args) 
	{
		try {
			String message ="ERROR code: " + args[0] + ", ERROR message : "+ args[1];
					
			JOptionPane.showMessageDialog(null,message,"API REQUEST ERROR",JOptionPane.ERROR_MESSAGE);
			Constants.DebugPrint(message);
			
			FileLogger.getInstance().Info(message);
			Constants.DebugPrint(message);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void HandleMultipleError(String titles) 
	{
		try {
		
			String message = titles 
				+"\r\n Please check https://developers.google.com/youtube/v3/live/docs/errors";
			JOptionPane.showMessageDialog(null,message,"API REQUEST ERROR",JOptionPane.ERROR_MESSAGE);
			Constants.DebugPrint(message);
			FileLogger.getInstance().Info(message);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	 
	public static synchronized void HandleError(String kind ,String  msg) 
	{
		try {
			String message = kind + " error: " + msg;
					
			JOptionPane.showMessageDialog(null,message,"APP LOADING ERROR",JOptionPane.ERROR_MESSAGE);
			Constants.DebugPrint(message);
			FileLogger.getInstance().Info(message);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
