package com.lookingforgroup.db.lobby;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.lookingforgroup.model.lobby.Lobby;
import com.lookingforgroup.model.lobby.LobbyMember;
import com.lookingforgroup.model.lobby.WebLobby;

@Repository
public interface LobbyDao {
	// CREATE OPS
	// Lobbies
	public WebLobby addLobby(WebLobby lobby);
	// Requests
	public LobbyMember sendJoinRequest(LobbyMember lobbyMember);
	public LobbyMember acceptJoinRequest(LobbyMember lobbyMember);
	public LobbyMember declineJoinRequest(LobbyMember lobbyMember);
	
	// READ OPS
	// Lobbies
	public Lobby getLobbyById(int id);
	public List<String> getLobbyTagList();
	public List<String> getLobbySystemList();
	public List<String> getCountryList();
	public List<Lobby> getLobbies();
	public List<Lobby> getLobbies(String orderBy, int limit);
	public List<Lobby> getLobbies(String filterInput);
	public List<Lobby> getMyLobbies(String username);
	// Requests
	public LobbyMember getJoinRequest(int lobby, int sender);
	
	// UPDATE OPS
	// Lobbies
	public WebLobby updateLobby(WebLobby webLobby);
	public LobbyMember promoteMember(LobbyMember lobbyMember);
	
	
	// DELETE OPS
	// Lobbies
	public Lobby deleteLobby(Lobby lobby);
	public LobbyMember removeMember(LobbyMember lobbyMember);
}
