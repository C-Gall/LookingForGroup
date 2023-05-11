package com.lookingforgroup.util.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.lookingforgroup.model.lobby.WebLobby;

@Component
public class WebLobbyValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return WebLobby.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		WebLobby webLobby = (WebLobby) target;
		
		// Baseline Lobby Attributes
		// Lobby Name Checks
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lobbyName", "webLobby.lobbyName.empty");
		int lobbyNameSize = webLobby.getLobbyName().length();
		if(lobbyNameSize < 6 || lobbyNameSize > 255) {
			errors.rejectValue("lobbyName", "webLobby.lobbyName.size");
		}
		
		// Max Player Checks
		if(webLobby.getMaxPlayers() < 2 || webLobby.getMaxPlayers() > 20) {
			errors.rejectValue("maxPlayers", "webLobby.maxPlayers.size");
		}
		
		// Pay Details Checks
		if(webLobby.isPayToPlay()) {
			int payDetailsSize = webLobby.getPayDetails().length();
			if(payDetailsSize > 255) {
				errors.rejectValue("payDetails", "webLobby.payDetails.size");
			}			
		}
		
		// Details Checks
		int detailsSize = webLobby.getDetails().length();
		if(detailsSize > 65565) {
			errors.rejectValue("details", "webLobby.details.size");
		}
		
		// LobbyService Attributes
		if(webLobby.isRemote() == true) {
			// Service Name Checks
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serviceName", "webLobby.serviceName.empty");
			int serviceNameSize = webLobby.getServiceName().length();
			if(serviceNameSize < 3 || serviceNameSize > 255) {
				errors.rejectValue("serviceName", "webLobby.serviceName.size");
			}
			
			// Service URL Checks
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serviceUrl", "webLobby.serviceUrl.empty");
			int serviceUrlSize = webLobby.getServiceUrl().length();
			if(serviceUrlSize < 3 || serviceUrlSize > 2048) {
				errors.rejectValue("serviceUrl", "webLobby.serviceUrl.size");
			}
			//TODO: Fix the Regex for matching URLs
			/*
			else if(!webLobby.getServiceUrl().matches
					("^((https?|ftp|smtp):\\/\\/)?(www.)?[a-z0-9]+\\.[a-z]+(\\/[a-zA-Z0-9#]+\\/?)*$")) {
				errors.rejectValue("serviceUrl", "webLobby.serviceUrl.mismatch");
			}*/
		}
		// LobbyAddress Checks
		else {
			// Line1 Checks
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "line1", "webLobby.line1.empty");
			int line1Size = webLobby.getLine1().length();
			if(line1Size < 1 || line1Size > 255) {
				errors.rejectValue("line1", "webLobby.line1.size");
			}
			
			// Line2 Checks
			int line2Size = webLobby.getLine2().length();
			if(line2Size > 255) {
				errors.rejectValue("line2", "webLobby.line2.size");
			}
			
			// Line3 Checks
			int line3Size = webLobby.getLine3().length();
			if(line3Size > 255) {
				errors.rejectValue("line3", "webLobby.line3.size");
			}
			
			// City Checks
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "webLobby.city.empty");
			int citySize = webLobby.getCity().length();
			if(citySize < 1 || citySize > 255) {
				errors.rejectValue("city", "webLobby.city.size");
			}
			
			// County/Province Checks
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countyProvince", "webLobby.countyProvince.empty");
			int countyProvinceSize = webLobby.getCountyProvince().length();
			if(countyProvinceSize < 1 || countyProvinceSize > 255) {
				errors.rejectValue("countyProvince", "webLobby.countyProvince.size");
			}
			
			// Zip/Postcode Checks
			int zipPostcodeSize = webLobby.getZipPostcode().length();
			if(zipPostcodeSize > 255) {
				errors.rejectValue("zipPostcode", "webLobby.zipPostcode.size");
			}
			
			// Country Checks
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "webLobby.country.empty");
			
			// Other/Details Checks
			int otherDetailsSize = webLobby.getOtherDetails().length();
			if(otherDetailsSize > 255) {
				errors.rejectValue("otherDetails", "webLobby.otherDetails.size");
			}
		}
	}

}
