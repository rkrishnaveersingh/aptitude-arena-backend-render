package com.aptitudearena.backend.service;

import com.aptitudearena.backend.dto.paper.PaperCategoryResponse;
import com.aptitudearena.backend.dto.paper.PaperResponse;
import com.aptitudearena.backend.entity.PreviousYearPaper;
import com.aptitudearena.backend.repository.PreviousYearPaperRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PreviousYearPaperService {

    private static final List<String> EXAM_CODES = List.of(
            "SSC-CGL", "RAS-Rajasthan", "JEE-Main", "NEET-UG", "CA-Foundation");
    private static final Set<String> EXAM_CODE_SET = Set.copyOf(EXAM_CODES);

    private final PreviousYearPaperRepository paperRepository;

    public PreviousYearPaperService(PreviousYearPaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    public List<PaperCategoryResponse> findAvailableCategories() {
        Map<String, List<Integer>> yearsByExam = new LinkedHashMap<>();
        for (PreviousYearPaperRepository.PaperAvailability availability :
                paperRepository.findAvailableExamYears(EXAM_CODES)) {
            yearsByExam.computeIfAbsent(availability.getExamCode(), ignored -> new ArrayList<>())
                    .add(availability.getExamYear());
        }
        return EXAM_CODES.stream()
                .filter(yearsByExam::containsKey)
                .map(examCode -> new PaperCategoryResponse(
                        examCode, List.copyOf(yearsByExam.get(examCode))))
                .toList();
    }

    public List<PaperResponse> findPapers(String examCode, Integer year) {
        if (!EXAM_CODE_SET.contains(examCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported paper exam code");
        }
        return paperRepository.findByExamCodeAndExamYearAndIsActiveTrueOrderByIdAsc(examCode, year)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaperResponse toResponse(PreviousYearPaper paper) {
        return new PaperResponse(
                paper.getId(),
                paper.getTitle(),
                paper.getExamYear(),
                paper.getExamCode(),
                paper.getPaperStage(),
                paper.getFilePath());
    }
}