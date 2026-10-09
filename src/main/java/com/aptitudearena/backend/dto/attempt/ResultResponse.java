package com.aptitudearena.backend.dto.attempt;

import java.math.BigDecimal;
import java.util.UUID;

public record ResultResponse(
        UUID attemptId,
        BigDecimal score,
        Integer correctAnswers,
        Integer wrongAnswers,
        Integer unanswered
) {}
