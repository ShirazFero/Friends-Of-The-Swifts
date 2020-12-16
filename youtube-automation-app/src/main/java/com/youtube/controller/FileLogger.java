package com.youtube.controller;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.youtube.utils.Constants;

public  class FileLogger {

	public static FileLogger instance;
	private static Logger logger ;
    private FileHandler fh; 
     
    public FileLogger() throws SecurityException, IOException{
    	 File myFile = new File(Constants.LogPath);
    	 if(!myFile.exists()) {
    		 myFile.createNewFile();
    		 System.out.println("created file");
    	 }
    	 logger = Logger.getLogger("Yaba Log");  
    	 fh = new FileHandler(Constants.LogPath, true);  
         logger.addHandler(fh);
         SimpleFormatter formatter = new SimpleFormatter();  
         fh.setFormatter(formatter); 
    }

    public synchronized void Info(String msg) {
    	logger.info(msg);
    }
    
	/**
	 * @return the logger
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public static FileLogger getInstance() throws SecurityException, IOException {
		if(instance == null) {
			instance = new FileLogger();
		}
		return instance;
	}
}
