package com.youtube.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.StringTokenizer;

public class Interval {

	private String interval;
	private long hours;
	private long minutes;
	/**
	 * @return the hours
	 */
	public String getInterval() {
		return interval;
	}
	/**
	 * @param hours the hours to set
	 */
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
