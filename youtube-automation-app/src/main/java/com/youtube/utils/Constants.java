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
	public static int isLive;						// flag array
	
	//user details
	public static String UserEmail = "";
	public static String Username = "";
	public static String Description = "Default description";
	
	//button labels
	public static final String setDescription = "<html>Set<br>Description</html>";
	public static final String addStream = "<html>Add<br>Stream</html>";
	public static final String removeStream = "<html>Remove<br>Stream</html>";
	public static final String OYLS = "<html>Open YouTube<br>Live Streams</html>";
	
	//user settings
	public static String Format = "1080p";
	public static String IngetionType = "rtmp";
	public static String Privacy = "public";
	public static int NumberOfResulsts = 10;
	
	//Url's
	public static final String StudioUrl = "https://studio.youtube.com/channel/"
			+ "UCWZGW9h-Yyjcsws5dBgGAYA/videos/live?filter=%5B%5D&"
			+ "sort=%7B%22columnType%22%3A%22date%22%2C%22sortOrder%22%3A%22DESCENDING%22%7D";
	public static final String LiveStreamUrl = "https://studio.youtube.com/channel/UCWZGW9h-Yyjcsws5dBgGAYA/livestreaming/manage";
	
	//paths
	public static final  String UserDataPath = System.getProperty("user.home")+"\\Documents\\saved_status_";
	public static final  String AppUserPath = System.getProperty("user.home")+"\\Documents\\AppUsers.json";
	public static final String InfoPath = System.getProperty("user.home")+"\\Documents\\info.json";
	
	//Global pointers
	public static Object[] ErrorArgs; 						  //error arguments pointer
	public static String[] SavedUsers; 						  //user list pointer
	public static SecretKey SecretKey ;					      //secret key pointer
	public static ArrayList<String> badResults;				  //bad response results pointer
	public static String NextPageToken = null; 				  //next page token
	public static String PrevPageToken = null;  			  //prev page token
	public static HashMap<String, String> StreamDescription;  //Descriptions hash map pointer
	public static ArrayList<LiveBroadcast> BroadcastsToUpdate;//broadcasts to update pointer 
	public static ArrayList<String> LiveId = null;
}
 

