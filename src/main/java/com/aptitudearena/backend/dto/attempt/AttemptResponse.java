package com.aptitudearena.backend.dto.attempt;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record AttemptResponse(
        UUID id,
        UUID userId,
        UUID examId,
        OffsetDateTime startedAt,
        OffsetDateTime submittedAt,
        String status,
        BigDecimal score,
        Integer correctAnswers,
        Integer wrongAnswers,
        Integer unanswered,
        List<AttemptAnswerRequest> answers
) {}
