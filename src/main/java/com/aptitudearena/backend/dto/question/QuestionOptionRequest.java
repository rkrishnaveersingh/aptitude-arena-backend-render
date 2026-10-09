package com.aptitudearena.backend.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QuestionOptionRequest(
        @NotBlank @Size(max = 10) String optionLabel,
        @NotBlank String optionText,
        boolean isCorrect
) {}
