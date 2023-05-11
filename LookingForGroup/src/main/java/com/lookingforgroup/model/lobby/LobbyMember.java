package com.lookingforgroup.model.lobby;

import java.time.LocalDateTime;

public class LobbyMember {
	private int lobbyId;
	private int profileId;
	private String profileUsername;
	private LocalDateTime creationTime;
	private String requestDetails;
	
	public LobbyMember() {
		
	}

	public LobbyMember(int lobbyId, int profileId, String profileUsername, String requestDetails) {
		super();
		this.lobbyId = lobbyId;
		this.profileId = profileId;
		this.profileUsername = profileUsername;
		this.requestDetails = requestDetails;
	}

	public int getLobbyId() {
		return lobbyId;
	}

	public void setLobbyId(int lobbyId) {
		this.lobbyId = lobbyId;
	}

	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public String getProfileUsername() {
		return profileUsername;
	}

	public void setProfileUsername(String profileUsername) {
		this.profileUsername = profileUsername;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public String getRequestDetails() {
		return requestDetails;
	}

	public void setRequestDetails(String requestDetails) {
		this.requestDetails = requestDetails;
	}
}
