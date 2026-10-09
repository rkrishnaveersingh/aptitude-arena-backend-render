package com.aptitudearena.backend.controller;

import com.aptitudearena.backend.dto.exam.CreateExamRequest;
import com.aptitudearena.backend.dto.exam.ExamResponse;
import com.aptitudearena.backend.dto.exam.UpdateExamRequest;
import com.aptitudearena.backend.entity.Exam;
import com.aptitudearena.backend.entity.ExamQuestion;
import com.aptitudearena.backend.entity.Question;
import com.aptitudearena.backend.entity.Subject;
import com.aptitudearena.backend.repository.ExamQuestionRepository;
import com.aptitudearena.backend.service.ExamService;
import com.aptitudearena.backend.service.QuestionService;
import com.aptitudearena.backend.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;
    private final SubjectService subjectService;
    private final QuestionService questionService;
    private final ExamQuestionRepository examQuestionRepository;

    public ExamController(ExamService examService,
                          SubjectService subjectService,
                          QuestionService questionService,
                          ExamQuestionRepository examQuestionRepository) {
        this.examService = examService;
        this.subjectService = subjectService;
        this.questionService = questionService;
        this.examQuestionRepository = examQuestionRepository;
    }

    @GetMapping
    public List<ExamResponse> findAllActive() {
        return examService.findAllActive().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> findById(@PathVariable UUID id) {
        return examService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ExamResponse> findBySlug(@PathVariable String slug) {
        return examService.findBySlug(slug)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExamResponse> create(@Valid @RequestBody CreateExamRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        Exam exam = new Exam();
        exam.setName(request.name());
        exam.setSlug(request.slug());
        exam.setDescription(request.description());
        exam.setDurationMinutes(request.durationMinutes());
        exam.setTotalQuestions(request.totalQuestions());
        exam.setTotalMarks(request.totalMarks());
        exam.setNegativeMarking(request.negativeMarking());
        exam.setActive(true);
        exam.setCreatedAt(now);
        exam.setUpdatedAt(now);
        exam.setSubjects(findSubjects(request.subjectIds()));

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(examService.save(exam)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponse> update(@PathVariable UUID id,
                                              @Valid @RequestBody UpdateExamRequest request) {
        if (!id.equals(request.id())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path id must match request id");
        }

        Exam exam = examService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
        exam.setName(request.name());
        exam.setSlug(request.slug());
        exam.setDescription(request.description());
        exam.setDurationMinutes(request.durationMinutes());
        exam.setTotalQuestions(request.totalQuestions());
        exam.setTotalMarks(request.totalMarks());
        exam.setNegativeMarking(request.negativeMarking());
        exam.setActive(request.isActive());
        exam.setUpdatedAt(OffsetDateTime.now());
        exam.setSubjects(findSubjects(request.subjectIds()));

        return ResponseEntity.ok(toResponse(examService.save(exam)));
    }

        @PostMapping("/{examId}/questions/{questionId}")
        public ResponseEntity<ExamQuestion> addQuestion(@PathVariable UUID examId,
                               @PathVariable UUID questionId) {
        Exam exam = examService.findById(examId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
        Question question = questionService.findById(questionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        int nextOrder = examQuestionRepository.findAll().stream()
            .filter(examQuestion -> examQuestion.getExam().getId().equals(examId))
            .map(ExamQuestion::getQuestionOrder)
            .filter(order -> order != null)
            .max(Integer::compareTo)
            .orElse(0) + 1;

        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setId(new ExamQuestion.ExamQuestionId(examId, questionId));
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setQuestionOrder(nextOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(examQuestionRepository.save(examQuestion));
        }

    private Set<Subject> findSubjects(Set<UUID> subjectIds) {
        return subjectIds.stream()
                .map(id -> subjectService.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Subject not found: " + id)))
                .collect(Collectors.toSet());
    }

    private ExamResponse toResponse(Exam exam) {
        return new ExamResponse(
                exam.getId(),
                exam.getName(),
                exam.getSlug(),
                exam.getDescription(),
                exam.getDurationMinutes(),
                exam.getTotalQuestions(),
                exam.getTotalMarks(),
                exam.getNegativeMarking(),
                exam.isActive(),
                exam.getCreatedAt(),
                exam.getUpdatedAt(),
                exam.getSubjects().stream().map(Subject::getId).collect(Collectors.toSet()));
    }
}