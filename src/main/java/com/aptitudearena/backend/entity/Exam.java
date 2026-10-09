package com.aptitudearena.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "exams", indexes = {
        @Index(name = "idx_exams_slug", columnList = "slug")
})
@Getter
@Setter
public class Exam {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "slug", length = 180, nullable = false, unique = true)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "total_marks", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalMarks;

    @Column(name = "negative_marking", precision = 5, scale = 2, nullable = false)
    private BigDecimal negativeMarking;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "exam_subjects",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();
}
