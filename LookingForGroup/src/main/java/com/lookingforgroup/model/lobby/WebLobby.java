package com.lookingforgroup.model.lobby;

import java.time.LocalDateTime;
import java.util.List;

public class WebLobby {
	private int id;
	private LocalDateTime creationTime;
	private LocalDateTime updatedTime;
	private int ownerId;
	private String ownerUsername;
	private String lobbyName;
	private List<String> lobbyTags;
	private String system;
	private int maxPlayers;
	private boolean payToPlay;
	private String payDetails;
	private boolean homebrew;
	private boolean thirdParty;
	private boolean remote;
	private LobbyService lobbyService;
	private LobbyAddress lobbyAddress;
	private String details; //Description of a Lobby
	private List<LobbyMember> members;
	private List<LobbyMember> requests;
	private String serviceName;
	private String serviceUrl;
	private String line1;
	private String line2;
	private String line3;
	private String city;
	private String countyProvince;
	private String zipPostcode;
	private String country;
	private String otherDetails; //Details for an Address if used
	
	public WebLobby() {
		
	}

	public WebLobby(int id, LocalDateTime creationTime, LocalDateTime updatedTime, int ownerId, String ownerUsername,
			String lobbyName, List<String> lobbyTags, String system, int maxPlayers, boolean payToPlay,
			String payDetails, boolean homebrew, boolean thirdParty, boolean remote, LobbyService lobbyService,
			LobbyAddress lobbyAddress, String details, List<LobbyMember> members, List<LobbyMember> requests,
			String serviceName, String serviceUrl, String line1, String line2, String line3, String city,
			String countyProvince, String zipPostcode, String country, String otherDetails) {
		super();
		this.id = id;
		this.creationTime = creationTime;
		this.updatedTime = updatedTime;
		this.ownerId = ownerId;
		this.ownerUsername = ownerUsername;
		this.lobbyName = lobbyName;
		this.lobbyTags = lobbyTags;
		this.system = system;
		this.maxPlayers = maxPlayers;
		this.payToPlay = payToPlay;
		this.payDetails = payDetails;
		this.homebrew = homebrew;
		this.thirdParty = thirdParty;
		this.remote = remote;
		this.lobbyService = lobbyService;
		this.lobbyAddress = lobbyAddress;
		this.details = details;
		this.members = members;
		this.requests = requests;
		this.serviceName = serviceName;
		this.serviceUrl = serviceUrl;
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

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	public String getLobbyName() {
		return lobbyName;
	}

	public void setLobbyName(String lobbyName) {
		this.lobbyName = lobbyName;
	}

	public List<String> getLobbyTags() {
		return lobbyTags;
	}

	public void setLobbyTags(List<String> lobbyTags) {
		this.lobbyTags = lobbyTags;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public boolean isPayToPlay() {
		return payToPlay;
	}

	public void setPayToPlay(boolean payToPlay) {
		this.payToPlay = payToPlay;
	}

	public String getPayDetails() {
		return payDetails;
	}

	public void setPayDetails(String payDetails) {
		this.payDetails = payDetails;
	}

	public boolean isHomebrew() {
		return homebrew;
	}

	public void setHomebrew(boolean homebrew) {
		this.homebrew = homebrew;
	}

	public boolean isThirdParty() {
		return thirdParty;
	}

	public void setThirdParty(boolean thirdParty) {
		this.thirdParty = thirdParty;
	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	public LobbyService getLobbyService() {
		return lobbyService;
	}

	public void setLobbyService(LobbyService lobbyService) {
		this.lobbyService = lobbyService;
	}

	public LobbyAddress getLobbyAddress() {
		return lobbyAddress;
	}

	public void setLobbyAddress(LobbyAddress lobbyAddress) {
		this.lobbyAddress = lobbyAddress;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public List<LobbyMember> getMembers() {
		return members;
	}

	public void setMembers(List<LobbyMember> members) {
		this.members = members;
	}

	public List<LobbyMember> getRequests() {
		return requests;
	}

	public void setRequests(List<LobbyMember> requests) {
		this.requests = requests;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
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
	
	public void setFromLobby(Lobby lobby) {
		this.id = lobby.getId();
		this.creationTime = lobby.getCreationTime();
		this.updatedTime = lobby.getUpdatedTime();
		this.ownerId = lobby.getOwnerId();
		this.ownerUsername = lobby.getOwnerUsername();
		this.lobbyName = lobby.getLobbyName();
		this.lobbyTags = lobby.getTags();
		this.system = lobby.getSystem();
		this.maxPlayers = lobby.getMaxPlayers();
		this.payToPlay = lobby.isPayToPlay();
		this.payDetails = lobby.getPayDetails();
		this.homebrew = lobby.isHomebrew();
		this.thirdParty = lobby.isThirdParty();
		this.remote = lobby.isRemote();
		this.details = lobby.getDetails();
		
		this.members = lobby.getMembers();
		this.requests = lobby.getRequests();
		
		if(lobby.getLobbyService() != null) {
			this.lobbyService = lobby.getLobbyService();
			
			this.serviceName = lobbyService.getServiceName();
			this.serviceUrl = lobbyService.getServiceUrl();			
		}

		if(lobby.getLobbyAddress() != null) {
			this.lobbyAddress = lobby.getLobbyAddress();
			
			this.line1 = lobbyAddress.getLine1();
			this.line2 = lobbyAddress.getLine2();
			this.line3 = lobbyAddress.getLine3();
			this.city = lobbyAddress.getCity();
			this.countyProvince = lobbyAddress.getCountyProvince();
			this.zipPostcode = lobbyAddress.getZipPostcode();
			this.country = lobbyAddress.getCountry();
			this.otherDetails = lobbyAddress.getOtherDetails();			
		}
	}
}