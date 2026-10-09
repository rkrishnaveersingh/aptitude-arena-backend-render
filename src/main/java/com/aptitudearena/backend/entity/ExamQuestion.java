package com.aptitudearena.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "exam_questions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_exam_question_order", columnNames = {"exam_id", "question_order"})
})
@Getter
@Setter
public class ExamQuestion {

    @EmbeddedId
    private ExamQuestionId id;

    @ManyToOne(optional = false)
    @MapsId("examId")
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(optional = false)
    @MapsId("questionId")
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Embeddable
    @Getter
    @Setter
    public static class ExamQuestionId implements Serializable {
        @Column(name = "exam_id", nullable = false)
        private UUID examId;

        @Column(name = "question_id", nullable = false)
        private UUID questionId;

        public ExamQuestionId() {}

        public ExamQuestionId(UUID examId, UUID questionId) {
            this.examId = examId;
            this.questionId = questionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ExamQuestionId)) return false;
            ExamQuestionId that = (ExamQuestionId) o;
            return Objects.equals(examId, that.examId) && Objects.equals(questionId, that.questionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(examId, questionId);
        }
    }
}
