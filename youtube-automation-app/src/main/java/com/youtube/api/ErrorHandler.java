package com.youtube.api;

import javax.swing.JOptionPane;

import com.youtube.controller.FileLogger;

/**
 * this Class generates and prompts to the GUI about an error occurrence
 */
public class ErrorHandler {

	public static void HandleError(Object[] args) {
		
		String message ="ERROR code: " + args[0] + ", ERROR message : "+ args[1];
				
		JOptionPane.showMessageDialog(null,message,"API REQUEST ERROR",JOptionPane.ERROR_MESSAGE);
		FileLogger.logger.info(message);

	}
	
	public static void HandleMultipleError(String titles) {
		String message =titles 
				+"\r\n Please check https://developers.google.com/youtube/v3/live/docs/errors";
	            
		JOptionPane.showMessageDialog(null,message,"API REQUEST ERROR",JOptionPane.ERROR_MESSAGE);
		FileLogger.logger.info(message);
	}
	
	public static void HandleUnknownError(String  error) {
		
		String message ="Unknown error: " + error;
				
		JOptionPane.showMessageDialog(null,message,"UNKNOWN ERROR",JOptionPane.ERROR_MESSAGE);
		FileLogger.logger.info(message);
	}
	
}
