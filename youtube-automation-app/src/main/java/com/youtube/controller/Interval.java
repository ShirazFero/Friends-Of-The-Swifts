package com.youtube.controller;

import java.util.Date;
import java.util.StringTokenizer;
/**
 * this class hold all Interval related data , singleton instance
 * @author Evgeny Geyfman
 *
 */
public class Interval {

	private String interval;	//general interval String of HH:MM format
	 
	private long hours;			
	
	private long minutes;
	
	private Date correntInterval;	//Current interval end time for interval panel
	
	private static Interval instance;
	
	public static Interval getInstance() {
		if(instance == null)
			instance = new Interval();
		return instance;
	}
	
	public Date getCorrentInterval() {
		return correntInterval;
	}

	public void setCorrentInterval(Date correntInterval) {
		this.correntInterval = correntInterval;
	}

	public String getInterval() {
		return interval;
	}
	
	public void setInterval(String interval) {
		this.interval = interval;
		parseInterval();
								
	}
	
	private void parseInterval() {
		final String delim =":";	//delimiter string
		String minutes,hours;
		StringTokenizer tok = new StringTokenizer(interval);
		hours=tok.nextToken(delim);
		minutes=tok.nextToken(delim);
		setMinutes(Integer.parseInt(minutes));
		setHours(Integer.parseInt(hours));
	}
	
	public long getMinutes() {
		return minutes;
	}
	
	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}
	
	public long getHours() {
		return hours;
	}
	
	public void setHours(long hours) {
		this.hours = hours;
	}
}
