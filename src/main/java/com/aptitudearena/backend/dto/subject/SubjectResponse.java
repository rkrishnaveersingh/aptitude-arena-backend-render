package com.aptitudearena.backend.dto.subject;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SubjectResponse(
        UUID id,
        String name,
        String description,
        boolean isActive,
        OffsetDateTime createdAt
) {}
