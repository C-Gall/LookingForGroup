package com.lookingforgroup.web;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lookingforgroup.db.AppService;
import com.lookingforgroup.model.accountandprofile.Profile;
import com.lookingforgroup.model.lobby.Lobby;
import com.lookingforgroup.model.lobby.LobbyMember;
import com.lookingforgroup.model.lobby.WebLobby;
import com.lookingforgroup.util.validator.WebLobbyValidator;

@Controller
public class LobbyController {
	@Autowired
	private AppService appService;
	@Autowired
	private WebLobbyValidator webLobbyValidator;
	
	// ---------------------------
	// MAPPINGS
	// ---------------------------
	@RequestMapping(value="/lobby", method=RequestMethod.GET)
	public String lobbyPage(@RequestParam(name="id", required=true) Integer id, Model model, Principal principal) {
		boolean owner = false;
		boolean isMember = false;
		boolean activeRequest = false;
		boolean hasMaxPlayers = false;
		
		Lobby lobby = appService.getLobbyById(id);
		if(lobby == null) {
			return "error/500";
		}
		
		Profile yourUser = appService.getProfileById(appService.getAccountByEmail(principal.getName()).getId());
		
		if(appService.isBlocked(lobby.getOwnerId(), yourUser.getId())) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
		
		// +1 since Members list will always include the owner, but not count them as a player.
		if(lobby.getMembers().size() >= (lobby.getMaxPlayers() + 1)) {
			hasMaxPlayers = true;
		}
		
		if(lobby.getOwnerId() == yourUser.getId()) {
			owner = true;
		}
		
		for(LobbyMember member : lobby.getMembers()) {
			if(member.getProfileId() == yourUser.getId()) {
				isMember = true;
			}
		}
		
		for(LobbyMember request : lobby.getRequests()) {
			if(request.getProfileId() == yourUser.getId()) {
				activeRequest = true;
			}
		}
		
		model.addAttribute("joinRequest", new LobbyMember(lobby.getId(), yourUser.getId(), yourUser.getUsername(), ""));
		model.addAttribute("lobby", lobby);
		model.addAttribute("yourUser", yourUser);
		model.addAttribute("hasMaxPlayers", hasMaxPlayers);
		model.addAttribute("owner", owner);
		model.addAttribute("isMember", isMember);
		model.addAttribute("activeRequest", activeRequest);
		return "lobbies/Lobby";
	}
	
	@RequestMapping(value="/lobby/sendrequest", method=RequestMethod.POST)
	public String sendLobbyRequest(LobbyMember joinRequest, Model model, Principal principal) {
		System.out.println("Lobby ID = " + joinRequest.getLobbyId());
		System.out.println("Profile ID = " + joinRequest.getProfileId());
		System.out.println("Profile Username = " + joinRequest.getProfileUsername());
		System.out.println("Request Details = " + joinRequest.getRequestDetails());
		Lobby lobby = appService.getLobbyById(joinRequest.getLobbyId());
		Profile profile = appService.getProfileById(appService.getAccountByEmail(principal.getName()).getId());
		
		if(appService.isBlocked(lobby.getOwnerId(), profile.getId())) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
		else {
			appService.sendJoinRequest(joinRequest);
		}
		
		return "redirect:/lobby?id=" + joinRequest.getLobbyId();
	}
	
	@RequestMapping(value="/lobby/acceptrequest", method=RequestMethod.POST)
	public String acceptLobbyRequest(@RequestParam(name="lobby", required=true) int lobbyId,
			  						 @RequestParam(name="sender", required=true) int sender,
			  						 Principal principal, Model model) {
		Lobby lobby = appService.getLobbyById(lobbyId);
		if(appService.getAccountByEmail(principal.getName()).getId() == lobby.getOwnerId()) {
			LobbyMember lobbyMember = appService.getJoinRequest(lobbyId, sender);
			
			if(lobbyMember != null && (lobby.getMembers().size() < (lobby.getMaxPlayers() + 1))) {
				appService.acceptJoinRequest(lobbyMember);
			}
			return "redirect:/lobby/update?id=" + lobbyId;
		}
		else {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
	}
	
	@RequestMapping(value="/lobby/declinerequest", method=RequestMethod.POST)
	public String declineLobbyRequest(@RequestParam(name="lobby", required=true) int lobbyId,
									  @RequestParam(name="sender", required=true) int sender,
									  Principal principal, Model model) {
		Lobby lobby = appService.getLobbyById(lobbyId);
		if(appService.getAccountByEmail(principal.getName()).getId() == lobby.getOwnerId()) {
			LobbyMember lobbyMember = appService.getJoinRequest(lobbyId, sender);
			
			if(lobbyMember != null) {
				appService.declineJoinRequest(lobbyMember);
			}
			return "redirect:/lobbyupdate/?id=" + lobbyId;
		}
		else {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
	}
	
	@RequestMapping(value="/lobby/cancel", method=RequestMethod.POST)
	public String cancelLobbyRequest(@RequestParam(name="lobbyid", required=true) int lobbyId, Principal principal, Model model) {
		Lobby lobby = appService.getLobbyById(lobbyId);
		
		if(lobby != null) {
			int yourId = appService.getAccountByEmail(principal.getName()).getId();
			LobbyMember lobbyMember = appService.getJoinRequest(lobbyId, yourId);
			
			if(lobbyMember != null) {
				appService.declineJoinRequest(lobbyMember);
				return "redirect:/lobby?id=" + lobbyId;
			}
		}
		
		model.addAttribute("message", "It looks like you do not have permission to view this page!");
		return "error/403";
	}
	
	@RequestMapping(value="/lobby/lobbies", method=RequestMethod.GET)
	public String lobbiesPage(Model model) {
		List<Lobby> lobbies = appService.getLobbies();
		model.addAttribute("lobbies", lobbies);
		model.addAttribute("filter", "");
		return "lobbies/Lobbies";
	}
	
	@RequestMapping(value="/lobby/mylobbies", method=RequestMethod.GET)
	public String myLobbiesPage(Model model, Principal principal) {
		String username = appService.getUsernameByEmail(principal.getName());
		List<Lobby> lobbies = appService.getMyLobbies(username);
		model.addAttribute("lobbies", lobbies);
		model.addAttribute("filter", "");
		return "lobbies/Lobbies";
	}
	
	@RequestMapping(value="/lobby/lobbies", method=RequestMethod.POST)
	public String lobbiesPageSearch(Model model, @RequestParam String filter) {
		List<Lobby> lobbies;
		if(filter.isEmpty()) {
			lobbies = appService.getLobbies();
			model.addAttribute("filter", "");
		}
		else {
			lobbies = appService.getLobbies(filter);
			model.addAttribute("filter", filter);
		}
		
		model.addAttribute("lobbies", lobbies);
		return "lobbies/Lobbies";
	}
	
	@RequestMapping(value="/lobby/create", method=RequestMethod.GET)
	public String lobbyForm(Model model, WebLobby webLobby, Principal principal) {
		webLobby.setMaxPlayers(5);
		model.addAttribute("webLobby", webLobby);
		model.addAttribute("tags", appService.getLobbyTagList());
		model.addAttribute("systems", appService.getLobbySystemList());
		model.addAttribute("countries", appService.getCountryList());
		return "lobbies/LobbyCreation";
	}
	
	@RequestMapping(value="/lobby/create", method=RequestMethod.POST)
	public String createLobby(@Valid @ModelAttribute WebLobby webLobby, BindingResult result, Model model, Principal principal) {
		webLobbyValidator.validate(webLobby, result);
		if(result.hasErrors()) {
			model.addAttribute("webLobby", webLobby);
			model.addAttribute("tags", appService.getLobbyTagList());
			model.addAttribute("systems", appService.getLobbySystemList());
			model.addAttribute("countries", appService.getCountryList());
			return "lobbies/LobbyCreation";
		}
		else if(principal.getName().equals("") || principal == null) {
			return "error/401";
		}
		
		Profile profile = appService.getProfileById(appService.getAccountByEmail(principal.getName()).getId());
		webLobby.setOwnerId(profile.getId());
		webLobby.setOwnerUsername(profile.getUsername());
		
		webLobby = appService.addLobby(webLobby);
		
		return "redirect:/lobby?id=" + webLobby.getId();
	}
	
	@RequestMapping(value="/lobby/update", method=RequestMethod.GET)
	public String updateLobbyPage(@RequestParam(name="id", required=true) Integer id, Model model, Principal principal) {		
		WebLobby webLobby = new WebLobby();
		webLobby.setFromLobby(appService.getLobbyById(id));
		
		if(webLobby.getOwnerId() == appService.getAccountByEmail(principal.getName()).getId()) {
			boolean hasMaxPlayers = false;

			if(webLobby.getMembers().size() >= (webLobby.getMaxPlayers() + 1)) {
				hasMaxPlayers = true;
			}
			
			model.addAttribute("webLobby", webLobby);
			model.addAttribute("tags", appService.getLobbyTagList());
			model.addAttribute("systems", appService.getLobbySystemList());
			model.addAttribute("countries", appService.getCountryList());
			model.addAttribute("hasMaxPlayers", hasMaxPlayers);
			
			return "lobbies/UpdateLobby";			
		}
		else {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
	}
	
	@RequestMapping(value="/lobby/update", method=RequestMethod.POST)
	public String updateLobby(@Valid @ModelAttribute WebLobby webLobby, BindingResult result, Model model, Principal principal) {
		if(webLobby.getOwnerId() == appService.getAccountByEmail(principal.getName()).getId()) {
			webLobbyValidator.validate(webLobby, result);
			if(result.hasErrors()) {
				boolean hasMaxPlayers = false;

				if(webLobby.getMembers().size() >= (webLobby.getMaxPlayers() + 1)) {
					hasMaxPlayers = true;
				}
				
				model.addAttribute("webLobby", webLobby);
				model.addAttribute("tags", appService.getLobbyTagList());
				model.addAttribute("systems", appService.getLobbySystemList());
				model.addAttribute("countries", appService.getCountryList());
				model.addAttribute("hasMaxPlayers", hasMaxPlayers);
				
				return "lobbies/UpdateLobby";					
			}
			else {
				appService.updateLobby(webLobby);
				
				return "redirect:/lobby/update?id=" + webLobby.getId();
			}
		}
		else {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
	}
	
	@RequestMapping(value="/lobby/promotemember", method=RequestMethod.POST)
	public String promoteMember(@RequestParam(name="member", required=true) Integer memberid,
			   					@RequestParam(name="lobby", required=true) Integer lobbyid, Model model, Principal principal) {
		Lobby retrievedLobby = appService.getLobbyById(lobbyid);
		
		if(retrievedLobby.getOwnerId() != appService.getAccountByEmail(principal.getName()).getId()) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";			
		}
		
		LobbyMember lobbyMember = null;
		for(LobbyMember member : retrievedLobby.getMembers()) {
			if(member.getProfileId() == memberid) {
				lobbyMember = member;
			}
		}
		
		if(lobbyMember == null) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}		
		
		appService.promoteMember(lobbyMember);
		
		return "redirect:/lobby?id=" + lobbyid;
	}
	
	@RequestMapping(value="/lobby/delete", method=RequestMethod.POST)
	public String deleteLobby(@RequestParam(name="lobbyid", required=true) Integer lobbyid, Model model, Principal principal) {
		Lobby retrievedLobby = appService.getLobbyById(lobbyid);
		
		if(retrievedLobby == null || retrievedLobby.getOwnerId() != appService.getAccountByEmail(principal.getName()).getId()) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
		
		appService.deleteLobby(retrievedLobby);
		
		return "redirect:/lobby/lobbies";
	}
	
	@RequestMapping(value="/lobby/removemember", method=RequestMethod.POST)
	public String removeMember(@RequestParam(name="member", required=true) Integer memberid,
							   @RequestParam(name="lobby", required=true) Integer lobbyid, Model model, Principal principal){
		Lobby retrievedLobby = appService.getLobbyById(lobbyid);
		
		// If the lobby does not exist.
		if(retrievedLobby == null) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}		
		
		// If the member being removed is the owner OR if the user attempting the removal is not the owner.
		if(retrievedLobby.getOwnerId() == memberid || retrievedLobby.getOwnerId() != appService.getAccountByEmail(principal.getName()).getId()) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
		
		LobbyMember lobbyMember = null;
		for(LobbyMember member : retrievedLobby.getMembers()) {
			if(member.getProfileId() == memberid) {
				lobbyMember = member;
			}
		}
		
		// If the member does not exist.
		if(lobbyMember == null) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
		
		appService.removeMember(lobbyMember);
		
		return "redirect:/lobby/update?id=" + lobbyid;
	}
	
	@RequestMapping(value="lobby/leave", method=RequestMethod.POST)
	public String leaveLobby(@RequestParam(name="lobbyid", required=true) Integer lobbyid, Model model, Principal principal){
		Lobby retrievedLobby = appService.getLobbyById(lobbyid);
		
		// If the lobby does not exist.
		if(retrievedLobby == null) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
		
		int yourId = appService.getAccountByEmail(principal.getName()).getId();
		LobbyMember lobbyMember = null;
		for(LobbyMember member : retrievedLobby.getMembers()) {
			if(member.getProfileId() == yourId) {
				lobbyMember = member;
			}
		}
		
		// If the member does not exist OR the member being removed is the owner.
		if(lobbyMember == null || lobbyMember.getProfileId() == retrievedLobby.getOwnerId()) {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
		
		appService.removeMember(lobbyMember);
		
		return "redirect:/lobby?id=" + lobbyid;
	}
}