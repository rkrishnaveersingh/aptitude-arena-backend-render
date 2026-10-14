package com.aptitudearena.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aptitudearena.backend.entity.Exam;
import com.aptitudearena.backend.entity.ExamQuestion;
import com.aptitudearena.backend.entity.Question;
import com.aptitudearena.backend.entity.Subject;
import com.aptitudearena.backend.repository.ExamQuestionRepository;
import com.aptitudearena.backend.repository.ExamRepository;
import com.aptitudearena.backend.repository.QuestionRepository;
import com.aptitudearena.backend.repository.SubjectRepository;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DevelopmentExamSeederTest {

    private final ExamRepository examRepository = mock(ExamRepository.class);
    private final ExamQuestionRepository examQuestionRepository = mock(ExamQuestionRepository.class);
    private final SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private final QuestionRepository questionRepository = mock(QuestionRepository.class);

    @Test
    void skipsWritesWhenSeededExamAndQuestionOrderAlreadyMatch() {
        Subject subject = subject();
        Question firstQuestion = question(1);
        Question secondQuestion = question(2);
        OffsetDateTime originalUpdatedAt = OffsetDateTime.parse("2026-01-01T00:00:00Z");
        Exam exam = exam(subject, originalUpdatedAt);
        List<ExamQuestion> examQuestions = List.of(
                examQuestion(exam, firstQuestion, 1),
                examQuestion(exam, secondQuestion, 2));
        when(subjectRepository.findByName("Quantitative Aptitude")).thenReturn(Optional.of(subject));
        when(questionRepository.findBySubject_Id(subject.getId()))
                .thenReturn(List.of(firstQuestion, secondQuestion));
        when(examRepository.findBySlug("quantitative-aptitude-practice-test"))
                .thenReturn(Optional.of(exam));
        when(examQuestionRepository.findByExam_IdOrderByQuestionOrderAsc(exam.getId()))
                .thenReturn(examQuestions);

        seeder().run();

        verify(examQuestionRepository, never()).saveAllAndFlush(anyList());
        verify(examRepository, never()).save(exam);
        assertEquals(originalUpdatedAt, exam.getUpdatedAt());
    }

    @Test
    void insertsMissingQuestionsAndPreservesSequentialOrdering() {
        Subject subject = subject();
        Question firstQuestion = question(1);
        Question secondQuestion = question(2);
        Question thirdQuestion = question(3);
        Exam exam = exam(subject, OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        ExamQuestion firstLink = examQuestion(exam, firstQuestion, 1);
        ExamQuestion secondLink = examQuestion(exam, secondQuestion, 2);
        when(subjectRepository.findByName("Quantitative Aptitude")).thenReturn(Optional.of(subject));
        when(questionRepository.findBySubject_Id(subject.getId()))
                .thenReturn(List.of(firstQuestion, secondQuestion, thirdQuestion));
        when(examRepository.findBySlug("quantitative-aptitude-practice-test"))
                .thenReturn(Optional.of(exam));
        when(examQuestionRepository.findByExam_IdOrderByQuestionOrderAsc(exam.getId()))
                .thenReturn(List.of(firstLink, secondLink));

        seeder().run();

        verify(examQuestionRepository, times(2)).saveAllAndFlush(anyList());
        verify(examRepository).save(exam);
        assertEquals(1, firstLink.getQuestionOrder());
        assertEquals(2, secondLink.getQuestionOrder());
        assertEquals(3, exam.getTotalQuestions());
    }

    private DevelopmentExamSeeder seeder() {
        return new DevelopmentExamSeeder(
                examRepository, examQuestionRepository, subjectRepository, questionRepository);
    }

    private static Subject subject() {
        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Quantitative Aptitude");
        return subject;
    }

    private static Question question(long leastSignificantBits) {
        Question question = new Question();
        question.setId(new UUID(0, leastSignificantBits));
        return question;
    }

    private static Exam exam(Subject subject, OffsetDateTime updatedAt) {
        Exam exam = new Exam();
        exam.setId(UUID.randomUUID());
        exam.setSubjects(new HashSet<>(List.of(subject)));
        exam.setDurationMinutes(15);
        exam.setTotalQuestions(2);
        exam.setUpdatedAt(updatedAt);
        return exam;
    }

    private static ExamQuestion examQuestion(Exam exam, Question question, int order) {
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setId(new ExamQuestion.ExamQuestionId(exam.getId(), question.getId()));
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setQuestionOrder(order);
        return examQuestion;
    }
}