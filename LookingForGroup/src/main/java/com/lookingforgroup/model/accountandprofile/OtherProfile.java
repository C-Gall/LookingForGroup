package com.lookingforgroup.model.accountandprofile;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

// Note: Used to handle Friend Request, Friend, and Block lists.
public class OtherProfile {
	private int senderId;
	private String senderUsername;
	private int recipientId;
	private String recipientUsername;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate sentDate;
	
	public OtherProfile() {
		
	}
	
	public OtherProfile(int senderId, String senderUsername, int recipientId, String recipientUsername) {
		this.senderId = senderId;
		this.senderUsername = senderUsername;
		this.recipientId = recipientId;
		this.recipientUsername = recipientUsername;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public int getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(int recipientId) {
		this.recipientId = recipientId;
	}

	public String getRecipientUsername() {
		return recipientUsername;
	}

	public void setRecipientUsername(String recipientUsername) {
		this.recipientUsername = recipientUsername;
	}

	public LocalDate getSentDate() {
		return sentDate;
	}

	public void setSentDate(LocalDate sentDate) {
		this.sentDate = sentDate;
	}
}
