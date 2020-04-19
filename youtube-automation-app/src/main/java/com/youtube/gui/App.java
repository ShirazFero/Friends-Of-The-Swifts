package com.youtube.gui;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.*;

import org.json.simple.parser.ParseException;

import com.youtube.controller.Controller;
import com.youtube.controller.FileLogger;

public class App {
	
	public static void main(String[] args) throws IOException {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new FileLogger();
					Controller controller = new Controller();
					controller.setInstance(controller);
					controller.initData(); // set initial data
					new UserLogin();
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLogger.logger.info(e.toString());
				}
			}
		});
	}

}
