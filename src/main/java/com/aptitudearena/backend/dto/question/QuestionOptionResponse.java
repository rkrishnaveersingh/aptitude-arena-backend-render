package com.aptitudearena.backend.dto.question;

import java.util.UUID;

public record QuestionOptionResponse(
        UUID id,
        String optionLabel,
        String optionText,
        boolean isCorrect
) {}
