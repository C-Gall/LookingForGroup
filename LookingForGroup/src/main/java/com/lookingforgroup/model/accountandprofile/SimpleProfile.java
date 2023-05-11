package com.lookingforgroup.model.accountandprofile;

import java.time.LocalDateTime;

// Used solely for listing the information on the main page!
public class SimpleProfile {
	private int id;
	private String username;
	private LocalDateTime creationTime;
	private LocalDateTime updatedTime;
	
	public SimpleProfile() {
		
	}
	
	public SimpleProfile(int id, String username, LocalDateTime creationTime, LocalDateTime updatedTime) {
		this.id = id;
		this.username = username;
		this.creationTime = creationTime;
		this.updatedTime = updatedTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}
	
	
}
