package com.aptitudearena.backend.dto.paper;

import java.util.List;

public record PaperCategoryResponse(String examCode, List<Integer> years) {
}