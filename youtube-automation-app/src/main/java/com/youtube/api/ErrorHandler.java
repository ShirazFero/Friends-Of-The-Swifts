package com.youtube.api;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.youtube.controller.FileLogger;
import com.youtube.utils.Constants;

/**
 * this Class generates and prompts to the GUI about an error occurrence
 */
public class ErrorHandler {

	public static void HandleError(Object[] args) 
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
	
	public static void HandleMultipleError(String titles) 
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
	
	public static void HandleUnknownError(String  error) 
	{
		try {
			String message ="Unknown error: " + error;
					
			JOptionPane.showMessageDialog(null,message,"UNKNOWN ERROR",JOptionPane.ERROR_MESSAGE);
			Constants.DebugPrint(message);
			FileLogger.getInstance().Info(message);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void HandleLoadError(String  error) 
	{
		try {
			String message ="Unknown error: " + error;
					
			JOptionPane.showMessageDialog(null,message,"APP LOADING ERROR",JOptionPane.ERROR_MESSAGE);
			Constants.DebugPrint(message);
			FileLogger.getInstance().Info(message);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void HandleApiError(String  error) 
	{
		try {
			String message ="Unknown error: " + error;
					
			JOptionPane.showMessageDialog(null,message,"APP LOADING ERROR",JOptionPane.ERROR_MESSAGE);
			Constants.DebugPrint(message);
			FileLogger.getInstance().Info(message);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
