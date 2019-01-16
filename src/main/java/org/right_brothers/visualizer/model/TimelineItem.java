package org.right_brothers.visualizer.model;

import org.maas.utils.Time;

public class TimelineItem {
	private Time time;
	private String messageType;
	private String message;
	
	public TimelineItem(Time time, String messageType, String message) {
		this.setTime(time);
		this.setMessageType(messageType);
		this.setMessage(message);
	}
	
	public Time getTime() {
		return time;
	}
	public void setTime(Time time) {
		this.time = time;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
