package com.javaStudies.todosimple.configs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.javaStudies.todosimple.security.JwtAuthenticationFilter;
import com.javaStudies.todosimple.security.JwtAuthorizationFilter;
import com.javaStudies.todosimple.security.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	private static final String[] PUBLIC_MATCHERS = { "/" };
	private static final String[] PUBLIC_MATCHERS_POST = { "/user", "/login" };

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());

		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(this.userDetailsService)
				.passwordEncoder(bCryptPasswordEncoder());
		this.authenticationManager = authenticationManagerBuilder.build();

		http.authorizeHttpRequests(authz -> authz.requestMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
				.requestMatchers(PUBLIC_MATCHERS).permitAll().anyRequest().authenticated())
				.authenticationManager(authenticationManager);
		
		http.addFilter(new JwtAuthenticationFilter(this.authenticationManager, this.jwtUtil));
		http.addFilter(new JwtAuthorizationFilter(this.authenticationManager, this.jwtUtil, this.userDetailsService));
		http.csrf(AbstractHttpConfigurer::disable).sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
