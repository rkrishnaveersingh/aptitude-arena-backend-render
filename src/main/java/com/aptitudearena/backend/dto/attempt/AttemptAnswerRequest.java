package com.aptitudearena.backend.dto.attempt;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AttemptAnswerRequest(
        @NotNull UUID questionId,
        UUID selectedOptionId
) {}
