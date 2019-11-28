package com.youtube.utils;
import javax.crypto.SecretKey;
public class Constants {
	
	public static final boolean DEBUG = false;
	public static boolean IntervalBroadcast = false;	// Interval Broadcast flag
	public static boolean RegularBroadcast = false;		// Regular Broadcast flag
	public static boolean SetInterval = false;			// set interval flag
	public static int numOfBroadcasts;
	public static Boolean[] isLive;	
	public static String Username = "";
	public static String Description = "Defualt description";
	
	//Url's
	public static final String StudioUrl = "https://studio.youtube.com/channel/"
			+ "UCWZGW9h-Yyjcsws5dBgGAYA/videos/live?filter=%5B%5D&"
			+ "sort=%7B%22columnType%22%3A%22date%22%2C%22sortOrder%22%3A%22DESCENDING%22%7D";
	public static final String LiveStreamUrl = "https://studio.youtube.com/channel/UCWZGW9h-Yyjcsws5dBgGAYA/livestreaming/manage";
	
	//key
	public static SecretKey SecretKey = null;
	//paths
	public static String[] SavedUsers = null;
	public static final  String UserDataPath = System.getProperty("user.home")+"\\Documents\\saved_status_";
	public static final  String AppUserPath = System.getProperty("user.home")+"\\Documents\\AppUsers.json";
	public static final String InfoPath = System.getProperty("user.home")+"\\Documents\\info.json";
}
 

