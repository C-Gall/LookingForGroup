package com.lookingforgroup.model.lobby;

import java.time.LocalDateTime;
import java.util.List;

public class Lobby {
	private int id;
	private LocalDateTime creationTime;
	private LocalDateTime updatedTime;
	private int ownerId;
	private String ownerUsername;
	private String lobbyName;
	private String system;
	private int maxPlayers;
	private boolean payToPlay;
	private String payDetails;
	private boolean homebrew;
	private boolean thirdParty;
	private boolean remote;
	private LobbyService lobbyService;
	private LobbyAddress lobbyAddress;
	private String details;
	private List<String> tags;
	private List<LobbyMember> members;
	private List<LobbyMember> requests;
	
	public Lobby() {
		
	}

	public Lobby(int id, LocalDateTime creationTime, LocalDateTime updatedTime, int ownerId, String ownerUsername,
			String lobbyName, String system, int maxPlayers, boolean payToPlay, String payDetails, boolean homebrew,
			boolean thirdParty, boolean remote, LobbyService lobbyService, LobbyAddress lobbyAddress, String details,
			List<String> tags, List<LobbyMember> members, List<LobbyMember> requests) {
		super();
		this.id = id;
		this.creationTime = creationTime;
		this.updatedTime = updatedTime;
		this.ownerId = ownerId;
		this.ownerUsername = ownerUsername;
		this.lobbyName = lobbyName;
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
		this.tags = tags;
		this.members = members;
		this.requests = requests;
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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
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

	public String tagsToString() {
		if(tags.size() == 0) {
			return "N/A";
		}
		else {
			String result = "";
			int count = 0;
			for(String tag : tags) {
				result = result + tag;
				if(count != tags.size() - 1) {
					result = result + ", ";
				}
				count++;
			}
			return result;
		}
	}
}
