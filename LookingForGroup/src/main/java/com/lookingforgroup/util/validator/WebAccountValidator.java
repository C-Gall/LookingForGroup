package com.lookingforgroup.util.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.lookingforgroup.model.accountandprofile.WebAccount;

@Component
public class WebAccountValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return WebAccount.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		WebAccount webAccount = (WebAccount) target;
		
		// Email checks
		ValidationUtils.rejectIfEmpty(errors, "email", "webAccount.email.empty");
		int emailSize = webAccount.getEmail().length();
		if(emailSize < 3 || emailSize > 256) {
			errors.rejectValue("email", "webAccount.email.size");
		}
		else if(!webAccount.getEmail().matches
				("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
			errors.rejectValue("email", "webAccount.email.format");
		}
		
		// Password checks
		String password = webAccount.getPassword();
		String confPassword = webAccount.getConfPassword();
		if(webAccount.getPassword().isEmpty() && webAccount.getConfPassword().isEmpty()) {
			ValidationUtils.rejectIfEmpty(errors, "password", "webAccount.password.empty");
			ValidationUtils.rejectIfEmpty(errors, "confPassword", "webAccount.password.empty");
		}
		else if (!password.equals(confPassword)){
			errors.rejectValue("password", "webAccount.password.mismatch");
			errors.rejectValue("confPassword", "webAccount.password.mismatch");
		}
		else {
			int passwordSize = webAccount.getPassword().length();
			
			if(passwordSize < 6 || passwordSize > 64) {
				errors.rejectValue("password", "webAccount.password.size");
			}
			else if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$")) {
				errors.rejectValue("password", "webAccount.password.format");
			}
		}
		
		// Username checks
		ValidationUtils.rejectIfEmpty(errors, "username", "webAccount.username.empty");
		int usernameSize = webAccount.getUsername().length();
		if(usernameSize < 3 || usernameSize > 24) {
			errors.rejectValue("username", "webAccount.username.size");
		}
		else if(!webAccount.getUsername().matches("^[a-zA-Z0-9]+$")) {
			errors.rejectValue("username", "webAccount.username.invalidChars");
		}
		else if(!webAccount.getUsername().matches(".*[a-zA-Z].*")) {
			errors.rejectValue("username", "webAccount.username.missingLetter");
		}		
	}
}