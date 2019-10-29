package com.youtube.utils;

public class Constants {
	
	public static final boolean DEBUG = false;
	public static boolean IntervalBroadcast = false;	// Interval Broadcast flag
	public static boolean RegularBroadcast = false;		// Regular Broadcast flag
	public static boolean SetInterval = false;			// set interval flag
	public static int numOfBroadcasts;
	public static volatile Boolean[] isLive;	
	public static final String StudioUrl = "https://studio.youtube.com/channel/"
			+ "UCWZGW9h-Yyjcsws5dBgGAYA/videos/live?filter=%5B%5D&"
			+ "sort=%7B%22columnType%22%3A%22date%22%2C%22sortOrder%22%3A%22DESCENDING%22%7D";
	public static final String LiveStreamUrl = "https://www.youtube.com/my_live_events";
}
