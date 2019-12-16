package com.youtube.utils;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.SecretKey;

import com.google.api.services.youtube.model.LiveBroadcast;
public class Constants {
	
	//flags
	public static final boolean DEBUG = false;
	public static boolean IntervalBroadcast = false;	// Interval Broadcast flag
	public static boolean RegularBroadcast = false;		// Regular Broadcast flag
	public static boolean SetInterval = false;			// set interval flag
	public static ArrayList<LiveBroadcast> BroadcastsToUpdate;
	public static Boolean[] isLive;
	
	//user details
	public static String UserEmail = "";
	public static String Username = "";
	public static String Description = "Default description";
	
	//button labels
	public static final String setDescription = "<html>Set<br>Description</html>";
	public static final String addStream = "<html>Add<br>Stream</html>";
	public static final String removeStream = "<html>Remove<br>Stream</html>";
	public static final String OYLS = "<html>Open YouTube<br>Live Streams</html>";
	
	//descirptions hash map
	public static HashMap<String, String> StreamDescription;
	
	//user settins
	public static String Format = "1080p";
	public static String IngetionType = "rtmp";
	public static String Privacy = "public";
	public static ArrayList<String> badResults;
	public static String NextPageToken = null;
	public static String PrevPageToken = null;
	public static int NumberOfResulsts = 10;
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
 

