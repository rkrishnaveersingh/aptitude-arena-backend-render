package com.aptitudearena.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "question_text", columnDefinition = "text", nullable = false)
    private String questionText;

    @Column(name = "explanation", columnDefinition = "text")
    private String explanation;

    @Column(name = "difficulty", length = 20, nullable = false)
    private String difficulty;

    @Column(name = "question_type", length = 30, nullable = false)
    private String questionType;

    @Column(name = "marks", precision = 5, scale = 2, nullable = false)
    private BigDecimal marks;

    @Column(name = "negative_marks", precision = 5, scale = 2, nullable = false)
    private BigDecimal negativeMarks;

    @Column(name = "source_exam", length = 150)
    private String sourceExam;

    @Column(name = "source_year")
    private Integer sourceYear;

    @Column(name = "source_question_no", length = 50)
    private String sourceQuestionNo;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
