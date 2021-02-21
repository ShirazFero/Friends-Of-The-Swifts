package com.youtube.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.*;

import org.json.simple.parser.ParseException;

import com.youtube.api.ErrorHandler;
import com.youtube.gui.UserLoginFrame;

public class AppMain 
{
	public static void main(String[] args) throws IOException
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					AppBootLoader loader = new AppBootLoader();
					loader.InitData(); // set initial data
					new UserLoginFrame(loader);
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| InvalidAlgorithmParameterException | IOException | ParseException e) {
					ErrorHandler.HandleError("Boot", e.toString());
				}
			}
		});
	}
}
