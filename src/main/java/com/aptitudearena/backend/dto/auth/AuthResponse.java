package com.aptitudearena.backend.dto.auth;

import com.aptitudearena.backend.dto.auth.UserResponse;

public record AuthResponse(String token, UserResponse user) {}
