package com.aptitudearena.backend.service;

import com.aptitudearena.backend.dto.auth.AuthResponse;
import com.aptitudearena.backend.dto.auth.LoginRequest;
import com.aptitudearena.backend.dto.auth.RegisterRequest;
import com.aptitudearena.backend.dto.auth.UserResponse;
import com.aptitudearena.backend.entity.AppUser;
import com.aptitudearena.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Locale;

@Service
public class AuthService {

	private final AppUserService appUserService;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(AppUserService appUserService,
					   PasswordEncoder passwordEncoder,
					   JwtService jwtService) {
		this.appUserService = appUserService;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());
		if (appUserService.existsByEmail(email)) {
			throw new IllegalArgumentException("An account with this email already exists");
		}

		OffsetDateTime now = OffsetDateTime.now();
		AppUser user = new AppUser();
		user.setName(request.name().trim());
		user.setEmail(email);
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setRole("USER");
		user.setActive(true);
		user.setCreatedAt(now);
		user.setUpdatedAt(now);

		return toAuthResponse(appUserService.save(user));
	}

	public AuthResponse login(LoginRequest request) {
		String email = normalizeEmail(request.email());
		AppUser user = appUserService.findByEmail(email)
				.filter(AppUser::isActive)
				.filter(existingUser -> passwordEncoder.matches(
						request.password(), existingUser.getPasswordHash()))
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		return toAuthResponse(user);
	}

	private AuthResponse toAuthResponse(AppUser user) {
		UserResponse userResponse = new UserResponse(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getRole(),
				user.isActive(),
				user.getCreatedAt(),
				user.getUpdatedAt());
		String token = jwtService.generateToken(user);
		if (token == null || token.isBlank()) {
			throw new IllegalStateException("JWT generation returned an empty token");
		}
		return new AuthResponse(token, userResponse);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
