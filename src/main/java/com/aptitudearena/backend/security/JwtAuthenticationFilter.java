package com.aptitudearena.backend.security;

import com.aptitudearena.backend.entity.AppUser;
import com.aptitudearena.backend.service.AppUserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtService jwtService;
	private final AppUserService appUserService;

	public JwtAuthenticationFilter(JwtService jwtService, AppUserService appUserService) {
		this.jwtService = jwtService;
		this.appUserService = appUserService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
		if (!token.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				String username = jwtService.extractUsername(token);
				if (username != null && !username.isBlank()) {
					appUserService.findByEmail(username)
							.filter(user -> jwtService.isTokenValid(token, user))
							.ifPresent(user -> setAuthentication(user, request));
				}
			} catch (JwtException | IllegalArgumentException ignored) {
				// Invalid tokens are treated as unauthenticated requests.
			}
		}

		filterChain.doFilter(request, response);
	}

	private void setAuthentication(AppUser user, HttpServletRequest request) {
		var authentication = new UsernamePasswordAuthenticationToken(
				user,
				null,
				List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
