package com.lookingforgroup.web;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lookingforgroup.db.AppService;
import com.lookingforgroup.model.accountandprofile.OtherProfile;
import com.lookingforgroup.model.accountandprofile.Profile;
import com.lookingforgroup.util.validator.ProfileValidator;

@Controller
public class ProfileController {
	@Autowired
	private AppService appService;
	@Autowired
	private ProfileValidator profileValidator;
	
	// ---------------------------
	// MAPPINGS
	// ---------------------------
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String profilePage(@RequestParam(name="id", required=false) Integer name, Model model, Principal principal) {
		boolean owner = true;
		boolean friendOrRequest = true;
		
		if(name == null) {
			Profile profile = appService.getProfileById(appService.getAccountByEmail(principal.getName()).getId());
			profile.setFriends(appService.getFriends(profile.getId()));
			profile.setReceivedFriendRequests(appService.getReceivedFriendRequests(profile.getId()));
			profile.setSentFriendRequests(appService.getSentFriendRequests(profile.getId()));
			model.addAttribute("profile", profile);
			model.addAttribute("owner", owner);
			model.addAttribute("isFriends", friendOrRequest);
			return "profiles/Profile";
		}
		else {
			Profile profile = appService.getProfileById(name);
			profile.setFriends(appService.getFriends(profile.getId()));
			if(!appService.getAccountById(profile.getId()).getEmail().equals(principal.getName())) {
				owner = false;
				int yourid = appService.getAccountByEmail(principal.getName()).getId();
				if(appService.isBlocked(yourid, profile.getId())) {
					model.addAttribute("message", "It looks like you do not have permission to view this page!");
					return "error/403";
				}
				if(appService.getFriendRequest(yourid, profile.getId()) == null
				   && !appService.isFriend(profile.getId(), name)) {
					friendOrRequest = false;
				}
			}
			if(owner) {
				profile.setReceivedFriendRequests(appService.getReceivedFriendRequests(profile.getId()));
				profile.setSentFriendRequests(appService.getSentFriendRequests(profile.getId()));
			}
			model.addAttribute("profile", profile);
			model.addAttribute("owner", owner);
			model.addAttribute("isFriends", friendOrRequest);
			return "profiles/Profile";
		}
	}
	
	@RequestMapping(value="/profiles", method=RequestMethod.GET)
	public String profilesPage(Model model) {
		model.addAttribute("profiles", appService.getAllProfiles());
		return "profiles/Profiles";
	}
	
	@RequestMapping(value="/profile/social", method=RequestMethod.GET)
	public String profileSocial(@RequestParam(name="form", required=true) String form, Model model, Principal principal) {
		int yourId = appService.getAccountByEmail(principal.getName()).getId();
		Profile profile = new Profile();
		if(form.equals("friended")) {
			profile.setFriends(appService.getFriends(yourId));
		}
		else if(form.equals("sent")) {
			profile.setSentFriendRequests(appService.getSentFriendRequests(yourId));
		}
		else if(form.equals("received")) {
			profile.setReceivedFriendRequests(appService.getReceivedFriendRequests(yourId));
		}
		else if(form.equals("blocked")) {
			profile.setBlocked(appService.getBlocked(yourId));
		}
		else {
			return "error/403";
		}
		
		model.addAttribute("profile", profile);
		return "profiles/ProfileSocial";
	}
	
	@RequestMapping(value="/profile/sendrequest", method=RequestMethod.POST)
	public String sendFriendRequest(@RequestParam(name="id", required=true) int id, Model model, Principal principal) {
		Profile yourProfile = appService.getProfileById(appService.getAccountByEmail(principal.getName()).getId());
		Profile otherProfile = appService.getProfileById(id);
		OtherProfile request = new OtherProfile(yourProfile.getId(), yourProfile.getUsername(), otherProfile.getId(), otherProfile.getUsername());
	
		if(appService.getFriendRequest(request.getSenderId(), request.getRecipientId()) == null 
		   && !appService.isBlocked(yourProfile.getId(), id)) {
			appService.sendFriendRequest(request);
			return "redirect:/profile?id=" + request.getRecipientId();
		}
		else {
			model.addAttribute("message", "It looks like you do not have permission to view this page!");
			return "error/403";
		}
	}
	
	@RequestMapping(value="/profile/cancelrequest", method=RequestMethod.POST)
	public String cancelFriendRequest(@RequestParam(name="recipient", required=true) int recipient,
									  Principal principal) {
		int yourid = appService.getAccountByEmail(principal.getName()).getId();
		
		OtherProfile request = appService.getFriendRequest(yourid, recipient);
		
		if(request != null) {	
			appService.cancelOrDeleteFriendRequest(yourid, recipient);
		}

		return "redirect:/profile/social?form=sent";	
	}
	
	@RequestMapping(value="/profile/acceptrequest", method=RequestMethod.POST)
	public String acceptFriendRequest(@RequestParam(name="sender", required=true) int sender,
									  Principal principal) {
		int yourid = appService.getAccountByEmail(principal.getName()).getId();
		
		OtherProfile request = appService.getFriendRequest(sender, yourid);
		
		if(request != null) {
			appService.acceptFriendRequest(request);
		}
		
		return "redirect:/profile/social?form=received";	
	}
	
	@RequestMapping(value="profile/declinerequest", method=RequestMethod.POST)
	public String declineFriendRequest(@RequestParam(name="sender", required=true) int sender,
									   Principal principal) {
		int yourid = appService.getAccountByEmail(principal.getName()).getId();
		
		OtherProfile request = appService.getFriendRequest(sender, yourid);
		
		if(request != null) {
			appService.cancelOrDeleteFriendRequest(sender, yourid);
		}
		
		return "redirect:/profile/social?form=received";	
	}
	
	@RequestMapping(value="profile/removefriend", method=RequestMethod.POST)
	public String removeFriend(@RequestParam(name="friend", required=true) int friend, Principal principal) {
		int yourid = appService.getAccountByEmail(principal.getName()).getId();
		
		if(appService.isFriend(yourid, friend)) {
			appService.removeFriend(yourid, friend);
		}
		
		return "redirect:/profile/social?form=friended";
	}
	
	@RequestMapping(value="profile/block", method=RequestMethod.POST)
	public String blockUser(@RequestParam(name="id", required=true) int id, Principal principal){
		Profile yourProfile = appService.getProfileById(appService.getAccountByEmail(principal.getName()).getId());
		Profile otherProfile = appService.getProfileById(id);
		
		if(!appService.isBlocked(yourProfile.getId(), otherProfile.getId()) || !appService.isBlocked(otherProfile.getId(), yourProfile.getId())) {
			appService.removeFriend(yourProfile.getId(), otherProfile.getId());
			OtherProfile block = new OtherProfile(yourProfile.getId(), yourProfile.getUsername(), otherProfile.getId(), otherProfile.getUsername());
			appService.blockUser(block);
		}
		
		return "redirect:/profiles";
	}
	
	@RequestMapping(value="profile/unblock", method=RequestMethod.POST)
	public String unblockUser(@RequestParam(name="id", required=true) int id, Principal principal) {
		int yourId = appService.getAccountByEmail(principal.getName()).getId();
		if(appService.isBlocked(yourId, id)) {
			appService.unblockUser(yourId, id);
		}
		
		return "redirect:/profile/social?form=blocked";
	}
	
	@RequestMapping(value="/profile/update", method=RequestMethod.GET)
	public String updateProfileForm(Model model, Profile profile, Principal principal, Authentication auth) {
		profile = appService.getProfileById(appService.getAccountByEmail(auth.getName()).getId());
		System.out.println("Attempting to update profile with Id: " + profile.getId());
		model.addAttribute("profile", profile);
		model.addAttribute("nameInUse", false);
		return "profiles/UpdateProfile";
	}
	
	@RequestMapping(value="/profile/update", method=RequestMethod.POST)
	public String updateProfileSubmit(Model model, @Valid Profile profile, BindingResult result, Authentication auth) {
		profile.setId(appService.getAccountByEmail(auth.getName()).getId());
		
		//Check that inputs are valid
		profileValidator.validate(profile, result);
		if(result.hasErrors()) {
			model.addAttribute("profile", profile);
			return "profiles/UpdateProfile";
		}
		
		//Handle checking if Username is already in use.
		Profile profileCheck = appService.getProfileByUsername(profile.getUsername());
		
		if(profileCheck != null) {
			System.out.println("profileCheckId: " + profileCheck.getId());
			System.out.println("profileId: " + profile.getId());
			if(profileCheck.getId() != profile.getId()) {
				model.addAttribute("profile", profile);
				model.addAttribute("nameInUse", true);
				return "profiles/UpdateProfile";
			}
		}
		
		//Update within the DB
		appService.updateProfile(profile.getId(), profile);
		return "redirect:/profile";
	}
}
