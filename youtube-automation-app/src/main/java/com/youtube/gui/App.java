package com.youtube.gui;

import java.io.IOException;
import javax.swing.*;

public class App {
	
	public static void main(String[] args) throws IOException {
		
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				new ProgressFrame();
			}
		});
	}

}
