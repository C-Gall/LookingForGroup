package com.lookingforgroup.web;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lookingforgroup.db.AppService;

@Controller
public class MainController {
	@Autowired
	private AppService appService;
	
	// ---------------------------
	// MAPPINGS
	// ---------------------------
	@RequestMapping(value="/main", method=RequestMethod.GET)
	public String mainPage(@RequestParam(name="greeting", required=false) String greeting, Model model, Principal principal, Authentication auth) {
		boolean newUser = false;
		if(greeting != null) {
			newUser = true;
		}
		model.addAttribute("newUser", newUser);
		model.addAttribute("username", appService.getUsernameByEmail(auth.getName()));
		model.addAttribute("newestUsers", appService.getAllSimpleProfiles(10));
		model.addAttribute("newestLobbies", appService.getLobbies("creation_time", 10));
		model.addAttribute("recentlyUpdatedLobbies", appService.getLobbies("updated_time", 10));
		return "mainpage/Main";
	}
}
