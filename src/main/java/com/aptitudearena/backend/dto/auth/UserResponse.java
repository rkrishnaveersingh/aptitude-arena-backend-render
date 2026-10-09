package com.aptitudearena.backend.dto.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String role,
        boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
