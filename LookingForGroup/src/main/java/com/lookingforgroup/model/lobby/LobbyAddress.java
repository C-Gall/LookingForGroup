package com.lookingforgroup.model.lobby;

public class LobbyAddress {
	private int id;
	private String line1;
	private String line2;
	private String line3;
	private String city;
	private String countyProvince;
	private String zipPostcode;
	private String country;
	private String otherDetails;
	
	public LobbyAddress() {
		
	}
	
	public LobbyAddress(int id, String line1, String line2, String line3, String city, String countyProvince,
			String zipPostcode, String country, String otherDetails) {
		super();
		this.id = id;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.city = city;
		this.countyProvince = countyProvince;
		this.zipPostcode = zipPostcode;
		this.country = country;
		this.otherDetails = otherDetails;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountyProvince() {
		return countyProvince;
	}

	public void setCountyProvince(String countyProvince) {
		this.countyProvince = countyProvince;
	}

	public String getZipPostcode() {
		return zipPostcode;
	}

	public void setZipPostcode(String zipPostcode) {
		this.zipPostcode = zipPostcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOtherDetails() {
		return otherDetails;
	}

	public void setOtherDetails(String otherDetails) {
		this.otherDetails = otherDetails;
	}
}