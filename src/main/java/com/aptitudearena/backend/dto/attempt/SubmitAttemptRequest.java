package com.aptitudearena.backend.dto.attempt;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record SubmitAttemptRequest(
        @NotNull UUID attemptId,
        @NotNull List<AttemptAnswerRequest> answers
) {}
