package com.aptitudearena.backend.dto.exam;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateExamRequest(
        @NotNull UUID id,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 180) String slug,
        @Size(max = 2000) String description,
        @NotNull @Positive Integer durationMinutes,
        @NotNull @Positive Integer totalQuestions,
        @NotNull @DecimalMin("0.00") BigDecimal totalMarks,
        @NotNull @DecimalMin("0.00") BigDecimal negativeMarking,
        @NotNull Set<UUID> subjectIds,
        @NotNull Boolean isActive
) {}
