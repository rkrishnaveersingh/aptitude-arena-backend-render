package com.aptitudearena.backend.service;

import com.aptitudearena.backend.entity.Exam;
import com.aptitudearena.backend.entity.ExamQuestion;
import com.aptitudearena.backend.entity.Question;
import com.aptitudearena.backend.entity.Subject;
import com.aptitudearena.backend.repository.ExamQuestionRepository;
import com.aptitudearena.backend.repository.ExamRepository;
import com.aptitudearena.backend.repository.QuestionRepository;
import com.aptitudearena.backend.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true", matchIfMissing = false)
public class DevelopmentExamSeeder implements CommandLineRunner {

    private static final String SUBJECT_NAME = "Quantitative Aptitude";
    private static final String EXAM_SLUG = "quantitative-aptitude-practice-test";

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;

    public DevelopmentExamSeeder(ExamRepository examRepository,
                                 ExamQuestionRepository examQuestionRepository,
                                 SubjectRepository subjectRepository,
                                 QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.subjectRepository = subjectRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Subject subject = subjectRepository.findByName(SUBJECT_NAME).orElse(null);
        if (subject == null) {
            return;
        }

        List<Question> questions = questionRepository.findBySubject_Id(subject.getId()).stream()
                .collect(Collectors.toMap(Question::getId, question -> question,
                        (first, duplicate) -> first))
                .values()
                .stream()
                .sorted(Comparator.comparing(Question::getId))
                .toList();

        Exam exam = examRepository.findBySlug(EXAM_SLUG)
            .or(() -> examRepository.findFirstByName("Quantitative Aptitude Practice Test"))
            .orElse(null);
        if (exam == null) {
            if (examRepository.count() > 0) {
                return;
            }
            exam = createExam(questions, subject);
            exam = examRepository.save(exam);
        } else {
            exam.getSubjects().add(subject);
        }

        exam.setDurationMinutes(15);
        exam.setUpdatedAt(OffsetDateTime.now());

        List<ExamQuestion> examQuestions = new ArrayList<>(
                examQuestionRepository.findByExam_IdOrderByQuestionOrderAsc(exam.getId()));
        Map<UUID, ExamQuestion> questionsById = examQuestions.stream()
                .collect(Collectors.toMap(examQuestion -> examQuestion.getQuestion().getId(),
                        examQuestion -> examQuestion));

        for (Question question : questions) {
            if (questionsById.containsKey(question.getId())) {
                continue;
            }

            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setId(new ExamQuestion.ExamQuestionId(exam.getId(), question.getId()));
            examQuestion.setExam(exam);
            examQuestion.setQuestion(question);
            examQuestions.add(examQuestion);
        }

        int maxOrder = examQuestions.stream()
                .map(ExamQuestion::getQuestionOrder)
                .filter(order -> order != null)
                .max(Integer::compareTo)
                .orElse(0);
        int temporaryOrder = maxOrder + 1;
        for (int index = 0; index < examQuestions.size(); index++) {
            examQuestions.get(index).setQuestionOrder(temporaryOrder + index);
        }
        examQuestionRepository.saveAllAndFlush(examQuestions);

        for (int index = 0; index < examQuestions.size(); index++) {
            examQuestions.get(index).setQuestionOrder(index + 1);
        }
        examQuestionRepository.saveAllAndFlush(examQuestions);

        exam.setTotalQuestions(examQuestions.size());
        examRepository.save(exam);
    }

    private Exam createExam(List<Question> questions, Subject subject) {
        OffsetDateTime now = OffsetDateTime.now();
        Exam exam = new Exam();
        exam.setName("Quantitative Aptitude Practice Test");
        exam.setSlug(EXAM_SLUG);
        exam.setDescription("Quantitative aptitude practice examination");
        exam.setDurationMinutes(15);
        exam.setTotalQuestions(questions.size());
        exam.setTotalMarks(questions.stream()
                .map(Question::getMarks)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        exam.setNegativeMarking(BigDecimal.ZERO);
        exam.setActive(true);
        exam.setCreatedAt(now);
        exam.setUpdatedAt(now);
        exam.setSubjects(new HashSet<>(Set.of(subject)));
        return exam;
    }
}