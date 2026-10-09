package com.aptitudearena.backend.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateQuestionRequest(
        @NotNull UUID subjectId,
        @NotBlank String questionText,
        String explanation,
        @NotBlank @Size(max = 20) String difficulty,
        @NotBlank @Size(max = 30) String questionType,
        @NotNull BigDecimal marks,
        @NotNull BigDecimal negativeMarks,
        String sourceExam,
        Integer sourceYear,
        String sourceQuestionNo,
        @NotNull List<QuestionOptionRequest> options
) {}
