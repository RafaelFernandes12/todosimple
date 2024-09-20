package com.javaStudies.todosimple.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	private JwtUtil jwtUtil;
	private UserDetailsService userDetailsService;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
			UserDetailsService userDetailsService) {
		super(authenticationManager);
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		String authorizationHeader = request.getHeader("Authorization");
		if(Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer")) {
			String token = authorizationHeader.substring(7);
			UsernamePasswordAuthenticationToken auth = getAuthentication(token);
			if(Objects.nonNull(auth)) SecurityContextHolder.getContext().setAuthentication(auth);
			
		}
		filterChain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String token) {
		if (this.jwtUtil.isValidToken(token)) {
			String username = this.jwtUtil.getUsername(token);
			UserDetails user = this.userDetailsService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(user, null,
					user.getAuthorities());
			return authenticatedUser;
		}
		return null;
	}
}
