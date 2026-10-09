package com.aptitudearena.backend.controller;

import com.aptitudearena.backend.dto.auth.AuthResponse;
import com.aptitudearena.backend.dto.auth.LoginRequest;
import com.aptitudearena.backend.dto.auth.RegisterRequest;
import com.aptitudearena.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		try {
			return ResponseEntity.ok(authService.login(request));
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
		}
	}
}
