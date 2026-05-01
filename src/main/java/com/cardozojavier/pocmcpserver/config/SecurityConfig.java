package com.cardozojavier.pocmcpserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
				.anyRequest().authenticated())
			.httpBasic(Customizer.withDefaults())
			.formLogin(AbstractHttpConfigurer::disable);
		return http.build();
	}

	@Bean
	UserDetailsService userDetailsService(
			@Value("${app.security.username}") String username,
			@Value("${app.security.password}") String password,
			PasswordEncoder passwordEncoder) {
		UserDetails user = User.withUsername(username)
			.password(passwordEncoder.encode(password))
			.roles("MCP")
			.build();
		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
