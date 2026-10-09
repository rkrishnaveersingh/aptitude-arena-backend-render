package com.aptitudearena.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "question_options", uniqueConstraints = {
        @UniqueConstraint(name = "uk_question_option_label", columnNames = {"question_id", "option_label"})
})
@Getter
@Setter
public class QuestionOption {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "option_label", length = 10, nullable = false)
    private String optionLabel;

    @Column(name = "option_text", columnDefinition = "text", nullable = false)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;
}
