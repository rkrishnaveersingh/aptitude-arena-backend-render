package com.aptitudearena.backend.controller;

import com.aptitudearena.backend.dto.question.CreateQuestionRequest;
import com.aptitudearena.backend.dto.question.QuestionOptionResponse;
import com.aptitudearena.backend.dto.question.QuestionResponse;
import com.aptitudearena.backend.entity.Question;
import com.aptitudearena.backend.entity.QuestionOption;
import com.aptitudearena.backend.entity.Subject;
import com.aptitudearena.backend.repository.QuestionOptionRepository;
import com.aptitudearena.backend.service.QuestionService;
import com.aptitudearena.backend.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final SubjectService subjectService;
    private final QuestionOptionRepository questionOptionRepository;

    public QuestionController(QuestionService questionService,
                              SubjectService subjectService,
                              QuestionOptionRepository questionOptionRepository) {
        this.questionService = questionService;
        this.subjectService = subjectService;
        this.questionOptionRepository = questionOptionRepository;
    }

    @GetMapping
    public List<QuestionResponse> findBySubject(@RequestParam UUID subjectId) {
        return toResponses(questionService.findBySubject(subjectId));
    }

    @GetMapping("/exam/{examId}")
    public List<QuestionResponse> findByExam(@PathVariable UUID examId) {
        return toResponses(questionService.findByExam(examId));
    }

    private List<QuestionResponse> toResponses(List<Question> questions) {
        Map<UUID, List<QuestionOptionRepository.OptionView>> optionsByQuestionId = questions.isEmpty()
            ? Map.of()
            : questionOptionRepository.findOptionsByQuestionIds(
                questions.stream().map(Question::getId).toList())
            .stream()
            .collect(Collectors.groupingBy(QuestionOptionRepository.OptionView::getQuestionId));
        return questions.stream()
            .map(question -> toResponse(question, optionsByQuestionId
                .getOrDefault(question.getId(), List.of())
                .stream()
                .map(this::toResponse)
                .toList()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> findById(@PathVariable UUID id) {
        return questionService.findById(id)
                .map(question -> toResponse(question,
                questionOptionRepository.findOptionsByQuestionIds(List.of(id)).stream()
                    .map(this::toResponse)
                    .toList()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody CreateQuestionRequest request) {
        Subject subject = subjectService.findById(request.subjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
        OffsetDateTime now = OffsetDateTime.now();
        Question question = new Question();
        question.setSubject(subject);
        question.setQuestionText(request.questionText());
        question.setExplanation(request.explanation());
        question.setDifficulty(request.difficulty());
        question.setQuestionType(request.questionType());
        question.setMarks(request.marks());
        question.setNegativeMarks(request.negativeMarks());
        question.setSourceExam(request.sourceExam());
        question.setSourceYear(request.sourceYear());
        question.setSourceQuestionNo(request.sourceQuestionNo());
        question.setActive(true);
        question.setCreatedAt(now);
        question.setUpdatedAt(now);
        Question savedQuestion = questionService.save(question);

        List<QuestionOption> options = request.options().stream().map(optionRequest -> {
            QuestionOption option = new QuestionOption();
            option.setQuestion(savedQuestion);
            option.setOptionLabel(optionRequest.optionLabel());
            option.setOptionText(optionRequest.optionText());
            option.setCorrect(optionRequest.isCorrect());
            return questionOptionRepository.save(option);
        }).toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedQuestion,
                options.stream().map(this::toResponse).toList()));
    }

    private QuestionOptionResponse toResponse(QuestionOption option) {
        return new QuestionOptionResponse(
                option.getId(), option.getOptionLabel(), option.getOptionText(), option.isCorrect());
    }

    private QuestionOptionResponse toResponse(QuestionOptionRepository.OptionView option) {
        return new QuestionOptionResponse(
                option.getId(), option.getOptionLabel(), option.getOptionText(), option.getCorrect());
    }

    private QuestionResponse toResponse(Question question, List<QuestionOptionResponse> options) {
        return new QuestionResponse(
                question.getId(),
                question.getSubject().getId(),
                question.getQuestionText(),
                question.getExplanation(),
                question.getDifficulty(),
                question.getQuestionType(),
                question.getMarks(),
                question.getNegativeMarks(),
                question.getSourceExam(),
                question.getSourceYear(),
                question.getSourceQuestionNo(),
                question.isActive(),
                question.getCreatedAt(),
                question.getUpdatedAt(),
                options);
    }
}