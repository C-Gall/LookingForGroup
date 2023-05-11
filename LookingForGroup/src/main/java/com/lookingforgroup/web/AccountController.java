package com.lookingforgroup.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.lookingforgroup.db.AppService;
import com.lookingforgroup.model.accountandprofile.Account;
import com.lookingforgroup.model.accountandprofile.Profile;
import com.lookingforgroup.model.accountandprofile.WebAccount;
import com.lookingforgroup.util.validator.WebAccountValidator;

@Controller
public class AccountController {
	@Autowired
	private AppService appService;
	@Autowired
	private WebAccountValidator webAccountValidator;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JavaMailSender mailSender;
	
	// ---------------------------
	// MAPPINGS
	// ---------------------------
	@RequestMapping(value="/account", method=RequestMethod.GET)
	public String accountPage(@RequestParam(name="id", required=true) int name, Model model) {
		Account account = appService.getAccountById(name);
		System.out.println(account.toString());
		model.addAttribute("account", account);
		String username = appService.getProfileById(name).getUsername();
		model.addAttribute("username", username);
		return "accounts/Account";
	}
	
	@RequestMapping(value="/accounts", method=RequestMethod.GET)
	public String accountsPage(Model model) {
		model.addAttribute("accounts", appService.getAllAccounts());
		return "accounts/Accounts";
	}
	
	@RequestMapping(value="/registration", method=RequestMethod.GET)
	public String registration(WebAccount webAccount, Model model){
		model.addAttribute("webAccount", new WebAccount());
		model.addAttribute("nameInUse", false);
		return "accounts/AccountRegistration";
	}
	
	@RequestMapping(value="/registration", method=RequestMethod.POST)
	public String processAccount(@Valid @ModelAttribute WebAccount webAccount, BindingResult result, HttpServletRequest request, Model model) {
		//Check validation.
		webAccountValidator.validate(webAccount, result);
		if(result.hasErrors()) {
			model.addAttribute("nameInUse", false);
			return "accounts/AccountRegistration";
		}
		
		//Check if username exists already.
		Profile tempProfile = appService.getProfileByUsername(webAccount.getUsername());
		if(tempProfile != null) {
			model.addAttribute("nameInUse", true);
			return "accounts/AccountRegistration";
		}
		
		//Account and Profile creation in AccountDao
		String encodedPassword = passwordEncoder.encode(webAccount.getPassword());
		webAccount.setPassword(encodedPassword);
		Account check = appService.addAccount(webAccount, determineRoles(true, false, false, false)); //Hardcoded to User, change later.
		
		if(check != null) {
			String siteUrl = request.getRequestURL().toString();
			siteUrl = siteUrl.replace(request.getServletPath(), "");
			// Unfortunately required.
			try {
				sendEmailForVerification(check, webAccount.getUsername(), siteUrl);
			} catch (UnsupportedEncodingException | MessagingException e) {
				// Should never occur.
				e.printStackTrace();
			}
			return "redirect:/verifynotice";
		}
		else {
			return "error/500";
		}
	}
	
	@RequestMapping(value="/verify", method=RequestMethod.GET)
	public String verifyAccount(@RequestParam(name="code", required=true) String code) {
		boolean success = appService.verify(code);
		
		if(success) {
			return "redirect:/verifysuccess";
		}
		else {
			return "redirect:/verifyfailure";
		}
	}
	
	
	private void sendEmailForVerification(Account account, String username, String siteUrl) throws MessagingException, UnsupportedEncodingException {
		String toAddress = account.getEmail();
		String fromAddress = "lookingforgroup.services@gmail.com";
		String fromName = "LookingForGroup";
		String subject = "LookingForGroup - Verify Your Account!";
		String content = "Greetings!<br>"
					   + "Click the link below to verify your account, and begin using LookingForGroup!:<br>"
					   + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
					   + "We look forward to seeing you there!"
					   + "- LookingForGroup";
		
		MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message);
	    
	    helper.setFrom(fromAddress, fromName);
	    helper.setTo(toAddress);
	    helper.setSubject(subject);
	    
	    content = content.replace("[[USERNAME]]", username);
	    
	    String verificationURL = siteUrl + "/verify?code=" + account.getVerificationCode();
	    
	    content = content.replace("[[URL]]", verificationURL);
	    
	    helper.setText(content, true);
	    
	    mailSender.send(message);
	}
	
	/*
	private void autologin(WebAccount webAccount, List<String> roles) {
		Collection<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(", ", roles));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(webAccount.getEmail(), null, authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}*/
	
	private List<String> determineRoles(boolean userFlag, boolean modFlag, boolean adminFlag, boolean corpFlag) {
		List<String> roles = new ArrayList<>();
		if(userFlag) {
			roles.add("ROLE_USER");
		}
		if(modFlag) {
			roles.add("ROLE_MOD");
		}
		if(adminFlag) {
			roles.add("ROLE_ADMIN");
		}
		if(corpFlag) {
			roles.add("ROLE_CORP");
		}
		return roles;
	}
}
