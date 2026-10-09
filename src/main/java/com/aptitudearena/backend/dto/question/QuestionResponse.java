package com.aptitudearena.backend.dto.question;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record QuestionResponse(
        UUID id,
        UUID subjectId,
        String questionText,
        String explanation,
        String difficulty,
        String questionType,
        BigDecimal marks,
        BigDecimal negativeMarks,
        String sourceExam,
        Integer sourceYear,
        String sourceQuestionNo,
        boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<QuestionOptionResponse> options
) {}
