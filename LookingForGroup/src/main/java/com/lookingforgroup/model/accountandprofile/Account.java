package com.lookingforgroup.model.accountandprofile;

import java.time.*;

public class Account {
	private int id;
	private OffsetDateTime creationTime;
	private OffsetDateTime updatedTime;
	private String email;
	private String password;
	private String verificationCode;
	private boolean active;

	public Account() {
		
	}
	
	public Account(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OffsetDateTime getCreationtime() {
		return creationTime;
	}

	public void setCreationtime(OffsetDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public OffsetDateTime getUpdatedtime() {
		return updatedTime;
	}

	public void setUpdatedtime(OffsetDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "ID = " + this.id +
				"\nCreationTime = " + this.creationTime +
				"\nUpdatedTime = " + this.updatedTime +
				"\nEmail = " + this.email +
				"\nVerification Code = " + this.verificationCode;
	}
}