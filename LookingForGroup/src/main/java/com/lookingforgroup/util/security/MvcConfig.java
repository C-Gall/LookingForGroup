package com.lookingforgroup.util.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/home").setViewName("mainpage/Home");
		registry.addViewController("/").setViewName("mainpage/Home");
		registry.addViewController("/login").setViewName("mainpage/Login");
		registry.addViewController("/verifynotice").setViewName("mainpage/VerificationNotice");
		registry.addViewController("/verifysuccess").setViewName("mainpage/VerificationSuccess");
		registry.addViewController("/verifyfailure").setViewName("mainpage/VerificationFailure");
	}
}
