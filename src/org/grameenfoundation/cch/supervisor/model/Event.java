package org.grameenfoundation.cch.supervisor.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.text.format.DateFormat;

public class Event { 
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getEventId() {
		return eventId;
	}


	public void setEventId(int eventId) {
		this.eventId = eventId;
	}


	public String getNurseId() {
		return nurseId;
	}


	public void setNurseId(String nurseId) {
		this.nurseId = nurseId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getEventType() {
		return eventType;
	}


	public void setEventType(String eventType) {
		this.eventType = eventType;
	}


	public Date getStart() {
		return start;
	}


	public void setStart(Date start) {
		this.start = start;
	}


	public Date getEnd() {
		return end;
	}


	public void setEnd(Date end) {
		this.end = end;
	}

	int id;
	int eventId;
	String nurseId;
	String title;
	String eventType;
	Date start;
	Date end;

	
	public Event() {
		// TODO Auto-generated constructor stub
	}


	public Event(int id, int eventId, String nurseId, String title,
			String eventType, String start, String end) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.nurseId = nurseId;
		this.title = title;
		this.eventType = eventType;
		this.start = getStringFromDate(start);
		this.end = getStringFromDate(end);
	}
	
	
	
	Date getStringFromDate(String date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		
		try {
			return formatter.parse(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public boolean isToday() {
		long milliSeconds = this.start.getTime();
		String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date(
				System.currentTimeMillis()));
		return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
				.toString().equals(today)) ? true : false;
	}

	public boolean isTomorrow() {
		long milliSeconds = this.start.getTime();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		String tomorrow = new SimpleDateFormat("MM/dd/yyyy")
				.format(new Date(c.getTimeInMillis()));
		return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
				.toString().equals(tomorrow)) ? true : false;
	}

	public boolean isFuture() {
		long milliSeconds = this.start.getTime();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.add(Calendar.DATE, 2);
		return (milliSeconds >= c.getTimeInMillis()) ? true : false;
	}

	public boolean isPast() {
		long milliSeconds = this.start.getTime();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return (milliSeconds < c.getTimeInMillis()) ? true : false;
	}
	
	public String getDate(String format) {
		long milliSeconds = this.start.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
	
}
