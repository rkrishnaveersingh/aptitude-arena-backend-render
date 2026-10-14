package com.aptitudearena.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.aptitudearena.backend.repository.PreviousYearPaperRepository;
import com.aptitudearena.backend.repository.PreviousYearPaperRepository.PaperAvailability;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class PreviousYearPaperServiceTest {

    private final PreviousYearPaperRepository paperRepository = mock(PreviousYearPaperRepository.class);
    private final PreviousYearPaperService paperService = new PreviousYearPaperService(paperRepository);

    @Test
    void categoriesUseAvailableYearsAndExcludeUnconfiguredExams() {
        PaperAvailability first = availability("SSC-CGL", 2024);
        PaperAvailability second = availability("SSC-CGL", 2023);
        PaperAvailability third = availability("NEET-UG", 2024);
        when(paperRepository.findAvailableExamYears(argThat(
                (java.util.Collection<String> codes) -> codes.size() == 5 && !codes.contains("GATE"))))
                .thenReturn(List.of(first, second, third));

        var categories = paperService.findAvailableCategories();

        assertEquals(2, categories.size());
        assertEquals("SSC-CGL", categories.get(0).examCode());
        assertEquals(List.of(2024, 2023), categories.get(0).years());
        assertEquals("NEET-UG", categories.get(1).examCode());
        assertEquals(List.of(2024), categories.get(1).years());
    }

    @Test
    void rejectsUnsupportedExamCodesBeforeQueryingPapers() {
        assertThrows(ResponseStatusException.class, () -> paperService.findPapers("GATE", 2024));
        verifyNoInteractions(paperRepository);
    }

    private PaperAvailability availability(String examCode, Integer year) {
        PaperAvailability availability = mock(PaperAvailability.class);
        when(availability.getExamCode()).thenReturn(examCode);
        when(availability.getExamYear()).thenReturn(year);
        return availability;
    }
}