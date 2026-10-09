package com.aptitudearena.backend.dto.attempt;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartAttemptRequest(@NotNull UUID examId) {}
