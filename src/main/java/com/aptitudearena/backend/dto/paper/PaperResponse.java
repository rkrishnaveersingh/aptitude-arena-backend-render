package com.aptitudearena.backend.dto.paper;

public record PaperResponse(
        Long id,
        String title,
        Integer year,
        String examCode,
        String paperStage,
        String filePath) {
}