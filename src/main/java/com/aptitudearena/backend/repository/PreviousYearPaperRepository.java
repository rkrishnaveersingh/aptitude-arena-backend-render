package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.PreviousYearPaper;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PreviousYearPaperRepository extends JpaRepository<PreviousYearPaper, Long> {

    @Query("select p.examCode as examCode, p.examYear as examYear "
            + "from PreviousYearPaper p "
            + "where p.isActive = true and p.examCode in :examCodes "
            + "group by p.examCode, p.examYear "
            + "order by p.examCode asc, p.examYear desc")
    List<PaperAvailability> findAvailableExamYears(@Param("examCodes") Collection<String> examCodes);

    List<PreviousYearPaper> findByExamCodeAndExamYearAndIsActiveTrueOrderByIdAsc(
            String examCode, Integer examYear);

    interface PaperAvailability {
        String getExamCode();

        Integer getExamYear();
    }
}