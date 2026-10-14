package com.aptitudearena.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "question_papers", schema = "public")
public class PreviousYearPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exam_code", columnDefinition = "text", nullable = false)
    private String examCode;

    @Column(name = "exam_year", nullable = false)
    private Integer examYear;

    @Column(columnDefinition = "text", nullable = false)
    private String title;

    @Column(name = "paper_stage", columnDefinition = "text")
    private String paperStage;

    @Column(name = "file_path", columnDefinition = "text", nullable = false, unique = true)
    private String filePath;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected PreviousYearPaper() {
    }

    public Long getId() {
        return id;
    }

    public String getExamCode() {
        return examCode;
    }

    public Integer getExamYear() {
        return examYear;
    }

    public String getTitle() {
        return title;
    }

    public String getPaperStage() {
        return paperStage;
    }

    public String getFilePath() {
        return filePath;
    }
}