package com.aptitudearena.backend.dto.exam;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record ExamResponse(
        UUID id,
        String name,
        String slug,
        String description,
        Integer durationMinutes,
        Integer totalQuestions,
        BigDecimal totalMarks,
        BigDecimal negativeMarking,
        boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Set<UUID> subjectIds
) {}
