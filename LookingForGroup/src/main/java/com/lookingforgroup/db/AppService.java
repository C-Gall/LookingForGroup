package com.lookingforgroup.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lookingforgroup.db.account.AccountDao;
import com.lookingforgroup.db.lobby.LobbyDao;
import com.lookingforgroup.db.profile.ProfileDao;
import com.lookingforgroup.model.accountandprofile.Account;
import com.lookingforgroup.model.accountandprofile.OtherProfile;
import com.lookingforgroup.model.accountandprofile.Profile;
import com.lookingforgroup.model.accountandprofile.SimpleProfile;
import com.lookingforgroup.model.accountandprofile.WebAccount;
import com.lookingforgroup.model.lobby.Lobby;
import com.lookingforgroup.model.lobby.LobbyMember;
import com.lookingforgroup.model.lobby.WebLobby;

@Service
public class AppService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private ProfileDao profileDao;
	@Autowired
	private LobbyDao lobbyDao;
	
	// ------------------------
	// ACCOUNT OPERATIONS
	// ------------------------
	
	// CREATE OPS
	public Account addAccount(WebAccount webAccount, List<String> roles) {
		return accountDao.addAccount(webAccount, roles);
	}
	
	// READ OPS
	public Account getAccountById(int id) {
		return accountDao.getAccountById(id);
	}
	
	public Account getAccountByEmail(String email) {
		return accountDao.getAccountByEmail(email);
	}
	
	public List<Account> getAllAccounts() {
		return accountDao.getAllAccounts();
	}
	
	public boolean emailExists(String email) {
		return accountDao.emailExists(email);
	}
	
	// UPDATE OPS
	public boolean verify(String verificationCode) {
		return accountDao.verify(verificationCode);
	}
	
	// ------------------------
	// PROFILE OPERATIONS
	// ------------------------
	
	// CREATE OPS
	public OtherProfile sendFriendRequest(OtherProfile otherProfile) {
		return profileDao.sendFriendRequest(otherProfile);
	}
	
	public OtherProfile acceptFriendRequest(OtherProfile otherProfile) {
		return profileDao.acceptFriendRequest(otherProfile);
	}
	
	public OtherProfile blockUser(OtherProfile otherProfile) {
		return profileDao.blockUser(otherProfile);
	}
	
	// READ OPS
	public Profile getProfileById(int id) {
		return profileDao.getProfileById(id);
	}
	
	public Profile getProfileByUsername(String username) {
		return profileDao.getProfileByUsername(username);
	}
	
	public List<Profile> getAllProfiles() {
		return profileDao.getAllProfiles();
	}
	
	public List<Profile> getAllProfiles(int limit) {
		return profileDao.getAllProfiles(limit);
	}
	
	public List<SimpleProfile> getAllSimpleProfiles(int limit) {
		return profileDao.getAllSimpleProfiles(limit);
	}
	
	public boolean isFriend(int first, int second) {
		return profileDao.isFriend(first, second);
	}
	
	public List<OtherProfile> getFriends(int id) {
		return profileDao.getFriends(id);
	}
	
	public boolean isBlocked(int first, int second) {
		return profileDao.isBlocked(first, second);
	}	
	
	public List<OtherProfile> getBlocked(int id) {
		return profileDao.getBlocked(id);
	}
	
	public String getUsernameByEmail(String email) {
		return profileDao.getUsernameByEmail(email);
	}
	
	public List<OtherProfile> getSentFriendRequests(int id) {
		return profileDao.getSentFriendRequests(id);
	}
	
	public List<OtherProfile> getReceivedFriendRequests(int id) {
		return profileDao.getReceivedFriendRequests(id);
	}
	
	public OtherProfile getFriendRequest(int sender, int recipient) {
		return profileDao.getFriendRequest(sender, recipient);
	}
	
	// UPDATE OPS
	public Profile updateProfile(int id, Profile profile) {
		return profileDao.updateProfile(id, profile);
	}
	
	// DELETE OPS
	public void cancelOrDeleteFriendRequest(int sender, int recipient) {
		profileDao.cancelOrDeleteFriendRequest(sender, recipient);
	}
	
	public void removeFriend(int profileOne, int profileTwo) {
		profileDao.removeFriend(profileOne, profileTwo);
	}
	
	public void unblockUser(int blocker, int blocked) {
		profileDao.unblockUser(blocker, blocked);
	}
	
	// ------------------------
	// LOBBY OPERATIONS
	// ------------------------
	
	// CREATE OPS
	public WebLobby addLobby(WebLobby lobby) {
		return lobbyDao.addLobby(lobby);
	}
	
	public LobbyMember sendJoinRequest(LobbyMember lobbyMember) {
		return lobbyDao.sendJoinRequest(lobbyMember);
	}
	
	// READ OPS
	public Lobby getLobbyById(int id) {
		return lobbyDao.getLobbyById(id);
	}
	
	public List<String> getLobbyTagList() {
		return lobbyDao.getLobbyTagList();
	}
	
	public List<String> getLobbySystemList() {
		return lobbyDao.getLobbySystemList();
	}
	
	public List<String> getCountryList() {
		return lobbyDao.getCountryList();
	}
	
	public List<Lobby> getLobbies() {
		return lobbyDao.getLobbies();
	}
	
	public List<Lobby> getLobbies(String orderBy, int limit) {
		return lobbyDao.getLobbies(orderBy, limit);
	}
	
	public List<Lobby> getLobbies(String filterInput) {
		return lobbyDao.getLobbies(filterInput);
	}
	
	public List<Lobby> getMyLobbies(String username) {
		return lobbyDao.getMyLobbies(username);
	}
	
	public LobbyMember getJoinRequest(int lobby, int sender) {
		return lobbyDao.getJoinRequest(lobby, sender);
	}
	
	public LobbyMember acceptJoinRequest(LobbyMember lobbyMember) {
		return lobbyDao.acceptJoinRequest(lobbyMember);
	}
	
	public LobbyMember declineJoinRequest(LobbyMember lobbyMember) {
		return lobbyDao.declineJoinRequest(lobbyMember);
	}
	
	// UPDATE OPS
	public WebLobby updateLobby(WebLobby webLobby) {
		return lobbyDao.updateLobby(webLobby);
	}
	
	public LobbyMember promoteMember(LobbyMember lobbyMember) {
		return lobbyDao.promoteMember(lobbyMember);
	}
	
	// DELETE OPS
	public LobbyMember removeMember(LobbyMember lobbyMember) {
		return lobbyDao.removeMember(lobbyMember);
	}
	
	public Lobby deleteLobby(Lobby lobby) {
		return lobbyDao.deleteLobby(lobby);
	}
}
