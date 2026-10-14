package com.aptitudearena.backend.controller;

import com.aptitudearena.backend.dto.paper.PaperCategoryResponse;
import com.aptitudearena.backend.dto.paper.PaperResponse;
import com.aptitudearena.backend.service.PreviousYearPaperService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/papers")
public class PreviousYearPaperController {

    private final PreviousYearPaperService paperService;

    public PreviousYearPaperController(PreviousYearPaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/categories")
    public List<PaperCategoryResponse> findAvailableCategories() {
        return paperService.findAvailableCategories();
    }

    @GetMapping
    public List<PaperResponse> findPapers(
            @RequestParam("exam") @NotBlank String exam,
            @RequestParam("year") @Min(1900) @Max(2100) Integer year) {
        return paperService.findPapers(exam, year);
    }
}