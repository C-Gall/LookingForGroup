package com.lookingforgroup.model.accountandprofile;

import java.util.List;

public class Profile {
	private int id;
	private String username;
	private String bio;
	private String interests;
	private String gender;
	private String nationality;
	private List<OtherProfile> sentFriendRequests;
	private List<OtherProfile> receivedFriendRequests;
	private List<OtherProfile> friends;
	private List<OtherProfile> blocked;
	
	public Profile() {
		
	}
	
	public Profile(int id, String username) {
		this.id = id;
		this.username = username;
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

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public List<OtherProfile> getSentFriendRequests() {
		return sentFriendRequests;
	}

	public void setSentFriendRequests(List<OtherProfile> sentFriendRequests) {
		this.sentFriendRequests = sentFriendRequests;
	}

	public List<OtherProfile> getReceivedFriendRequests() {
		return receivedFriendRequests;
	}

	public void setReceivedFriendRequests(List<OtherProfile> receivedFriendRequests) {
		this.receivedFriendRequests = receivedFriendRequests;
	}

	public List<OtherProfile> getFriends() {
		return friends;
	}

	public void setFriends(List<OtherProfile> friends) {
		this.friends = friends;
	}

	public List<OtherProfile> getBlocked() {
		return blocked;
	}

	public void setBlocked(List<OtherProfile> blocked) {
		this.blocked = blocked;
	}
}
