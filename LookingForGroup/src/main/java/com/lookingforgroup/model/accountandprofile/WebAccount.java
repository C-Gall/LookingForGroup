package com.lookingforgroup.model.accountandprofile;

import org.springframework.web.multipart.MultipartFile;

public class WebAccount {
	private String email;
	private String password;
	private String confPassword;
	private String username;
	private String verificationCode;
	
	//NOT YET IMPLEMENTED
	private MultipartFile bannerImage;
	private MultipartFile profileImage;
	
	public WebAccount() {
		
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

	public String getConfPassword() {
		return confPassword;
	}

	public void setConfPassword(String confPassword) {
		this.confPassword = confPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public MultipartFile getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(MultipartFile bannerImage) {
		this.bannerImage = bannerImage;
	}

	public MultipartFile getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(MultipartFile profileImage) {
		this.profileImage = profileImage;
	}
}
