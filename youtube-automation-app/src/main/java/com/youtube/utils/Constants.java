package com.youtube.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;

public class Constants {
	
	//flags
	public static final boolean Debug = true;
	public static final int MaxPolls = 10;
	public static final long MaxPollRsults = 20;
	public static volatile int pollingCount = 0;   
	public static boolean pollingState = false;         //polling flag
	public static boolean LoadingState = false;			//load state
	public static boolean IntervalBroadcast = false;	// Interval Broadcast flag
	public static boolean RegularBroadcast = false;		// Regular Broadcast flag
	public static boolean SetInterval = false;			// set interval flag
	public volatile static int isLive;					// flag array
	public static String State = null;					//starting/completing
	public static String AddingStream;					
	
	//user details
	public static String UserEmail = "";
	public static String Username = "";
	public static String Description = "Please enter description";
	
	//button labels
	public static final String setDescription = "<html>Set<br>Description</html>";
	public static final String addStream = "<html>Add<br>Stream</html>";
	public static final String removeStream = "<html>Remove<br>Stream</html>";
	public static final String OYLS = "<html>Open YouTube<br>Live Streams</html>";
	
	//user settings
	public static String Format = "1080p";
	public static String IngestionType = "rtmp";
	public static String Privacy = "public";
	public static int NumberOfResulsts = 15;
	public static boolean AddDateTime = true;
	public static boolean SendEmail = true;
	public static boolean saveState= true;
	public static String myBytes="";

	//Url's
	public static final String StudioUrl = "https://studio.youtube.com/channel/UCY9ywM1mQtQ8mTtbnhh-P2w/videos/live";
	public static final String LiveStreamUrl = "https://studio.youtube.com/channel/UCY9ywM1mQtQ8mTtbnhh-P2w/livestreaming/manage";
	
	//paths
	public static final String UserDataPath = System.getProperty("user.home")+"\\Documents\\saved_status_";
	public static final String AppUserPath = System.getProperty("user.home")+"\\Documents\\AppUsers.json";
	public static final String InfoPath = System.getProperty("user.home")+"\\Documents\\info.json";
	public static final String LogPath = System.getProperty("user.home")+"\\Documents\\applog_"+LocalDate.now()+".txt";
	public static final String tmpPath = "src\\main\\resources\\tmp.json";
	public final static String appEmail ="yaba.app@gmail.com";
	
	//Global pointers
	public static Object[] ErrorArgs; 						    //error arguments pointer
	public static ArrayList<String> SavedUsers; 						    //user list pointer
	public static SecretKey SecretKey ;					        //secret key pointer
	public static ArrayList<String> badResults;				    //bad response results pointer
	public static String NextPageToken = null; 				    //next page token
	public static String PrevPageToken = null;  			    //previous page token
	public static ArrayList<LiveBroadcast> BroadcastsToUpdate;  //broadcasts pointer 
	public static List<LiveBroadcast> PolledBroadcasts;			//broadcasts pointer 
	public static ArrayList<LiveStream> StreamToRemove;		    //Streams pointer 
	public static ArrayList<String> LiveId = null;			    //current Live broadcasts Id's
	
	//thread locks
	public static Object monitorLock = new Object();
	public static Object PollLock = new Object();			//poll lock
	public static Object PollStartLock = new Object();			//poll start lock
}
 

