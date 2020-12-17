package com.youtube.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.*;

import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.gui.UserLogin;

public class AppMain 
{
	public static void main(String[] args) throws IOException
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					AppBootLoader loader = new AppBootLoader();
					loader.InitData(); // set initial data
					new UserLogin(loader);
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					e.printStackTrace();
					ErrorHandler.HandleLoadError(e.toString());
				}
			}
		});
	}
}
