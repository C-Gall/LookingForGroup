package com.lookingforgroup.util.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.lookingforgroup.model.accountandprofile.Profile;

@Component
public class ProfileValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return Profile.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Profile profile = (Profile) target;
		
		// Username checks
		ValidationUtils.rejectIfEmpty(errors, "username", "webAccount.username.empty");
		int usernameSize = profile.getUsername().length();
		if(usernameSize < 3 || usernameSize > 24) {
			errors.rejectValue("username", "webAccount.username.size");
		}
		else if(!profile.getUsername().matches("^[a-zA-Z0-9]+$")) {
			errors.rejectValue("username", "webAccount.username.invalidChars");
		}
		else if(!profile.getUsername().matches(".*[a-zA-Z].*")) {
			errors.rejectValue("username", "webAccount.username.missingLetter");
		}
		
		// Bio checks
		if(profile.getBio().length() > 65535) {
			errors.rejectValue("bio", "profile.bio.size");
		}
		
		// Interests checks
		if(profile.getInterests().length() > 65535) {
			errors.rejectValue("interests", "profile.interests.size");
		}
		
		// Gender checks
		if(profile.getGender().length() > 24) {
			errors.rejectValue("gender", "profile.gender.size");
		}
		
		// Nationality checks
		if(profile.getNationality().length() > 40) {
			errors.rejectValue("nationality", "profile.nationality.size");
		}
	}
}