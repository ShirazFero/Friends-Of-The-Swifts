package com.youtube.utils;

import java.util.List;

import com.google.common.collect.Lists;

public class Constants {
	
	public static final boolean DEBUG = false;
	public static boolean IntervalBroadcast = false;
	public  List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");
}
