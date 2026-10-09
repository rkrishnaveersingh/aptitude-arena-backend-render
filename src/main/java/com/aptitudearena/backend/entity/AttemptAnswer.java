package com.aptitudearena.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "attempt_answers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_attempt_question", columnNames = {"attempt_id", "question_id"})
})
@Getter
@Setter
public class AttemptAnswer {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private Attempt attempt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "selected_option_id", nullable = true)
    private QuestionOption selectedOption;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "marks_awarded", precision = 10, scale = 2, nullable = false)
    private BigDecimal marksAwarded;

    @Column(name = "answered_at", nullable = false)
    private OffsetDateTime answeredAt;
}
