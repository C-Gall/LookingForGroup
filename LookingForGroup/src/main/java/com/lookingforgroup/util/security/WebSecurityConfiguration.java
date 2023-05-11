package com.lookingforgroup.util.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		String userByEmailQuery = "SELECT email, password, active FROM lfg_account WHERE email=?";
		String userByUsernameQuery = "SELECT email, password, active from lfg_account JOIN lfg_profile ON lfg_account.id = lfg_profile.id WHERE username=?";
		
		auth.jdbcAuthentication().dataSource(dataSource)
			.usersByUsernameQuery(userByEmailQuery)
			.authoritiesByUsernameQuery("SELECT email, role FROM lfg_roles WHERE email = ?");
		
		auth.jdbcAuthentication().dataSource(dataSource)
			.usersByUsernameQuery(userByUsernameQuery)
			.authoritiesByUsernameQuery("SELECT email, role FROM lfg_roles WHERE email = ?");
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.exceptionHandling()
				.accessDeniedPage("/403")
				.and()
			.authorizeHttpRequests()
				.antMatchers("/", "/home", "*.css").permitAll()
				.antMatchers("/login", "/registration", "/403").permitAll()
				.antMatchers("/main").hasAnyRole("USER", "MOD", "ADMIN")
				.antMatchers("/account", "/accounts").hasAnyRole("ADMIN")
				.antMatchers("/profile", "/profiles", "/profile/update", "/profile/social").hasAnyRole("USER", "MOD", "ADMIN")
				.antMatchers("/lobby", "/lobby/create").hasAnyRole("USER", "MOD", "ADMIN")
				.and()
			.formLogin()
				.loginPage("/login")
				.failureUrl("/login?error")
				.defaultSuccessUrl("/main")
				.permitAll();
		return http.build();
	}
	
	// Note: Current iteration utilizes Argon2id. Argon is supposedly the new de-facto standard despite
	//       Argon2d being designed solely for password-dependent systems. In addition, Argon2PasswordEncoder
	//       defaults to using Argon2id with no ability to introduce a param that would determine the type
	//       via the builder in its declaration.
	@Bean
	PasswordEncoder passwordEncoder() {
		//BCrypt would be saltLength 32 and hashLength 64, assumed equivalent of 1 thread, 1024 memory, 1 iteration.
		//Argon2PasswordEncoder​(int saltLength, int hashLength, int parallelism, int memory, int iterations)
		return new Argon2PasswordEncoder(32, 64, 8, 1024 * 4, 8);
	}
}
