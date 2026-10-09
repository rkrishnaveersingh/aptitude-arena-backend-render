package com.aptitudearena.backend.controller;

import com.aptitudearena.backend.dto.attempt.AttemptAnswerRequest;
import com.aptitudearena.backend.dto.attempt.AttemptResponse;
import com.aptitudearena.backend.dto.attempt.ResultResponse;
import com.aptitudearena.backend.dto.attempt.StartAttemptRequest;
import com.aptitudearena.backend.dto.attempt.SubmitAttemptRequest;
import com.aptitudearena.backend.entity.AppUser;
import com.aptitudearena.backend.entity.Attempt;
import com.aptitudearena.backend.entity.AttemptAnswer;
import com.aptitudearena.backend.entity.Exam;
import com.aptitudearena.backend.entity.Question;
import com.aptitudearena.backend.entity.QuestionOption;
import com.aptitudearena.backend.repository.QuestionOptionRepository;
import com.aptitudearena.backend.service.AttemptAnswerService;
import com.aptitudearena.backend.service.AttemptService;
import com.aptitudearena.backend.service.ExamService;
import com.aptitudearena.backend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/attempts")
public class AttemptController {

    private final AttemptService attemptService;
    private final AttemptAnswerService attemptAnswerService;
    private final ExamService examService;
    private final QuestionService questionService;
    private final QuestionOptionRepository questionOptionRepository;

    public AttemptController(AttemptService attemptService,
                             AttemptAnswerService attemptAnswerService,
                             ExamService examService,
                             QuestionService questionService,
                             QuestionOptionRepository questionOptionRepository) {
        this.attemptService = attemptService;
        this.attemptAnswerService = attemptAnswerService;
        this.examService = examService;
        this.questionService = questionService;
        this.questionOptionRepository = questionOptionRepository;
    }

    @GetMapping
    public List<AttemptResponse> findMyAttempts(@AuthenticationPrincipal AppUser user) {
        return attemptService.findByUser(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttemptResponse> findById(@PathVariable UUID id,
                                                    @AuthenticationPrincipal AppUser user) {
        Attempt attempt = attemptService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));
        requireOwner(attempt, user);
        return ResponseEntity.ok(toResponse(attempt));
    }

    @PostMapping
    public ResponseEntity<AttemptResponse> start(@Valid @RequestBody StartAttemptRequest request,
                                                 @AuthenticationPrincipal AppUser user) {
        Exam exam = examService.findById(request.examId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));
        OffsetDateTime now = OffsetDateTime.now();
        Attempt attempt = new Attempt();
        attempt.setUser(user);
        attempt.setExam(exam);
        attempt.setStartedAt(now);
        attempt.setStatus("IN_PROGRESS");
        attempt.setScore(BigDecimal.ZERO);
        attempt.setCorrectAnswers(0);
        attempt.setWrongAnswers(0);
        attempt.setUnanswered(exam.getTotalQuestions());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(attemptService.save(attempt)));
    }

    @PostMapping("/submit")
    @Transactional
    public ResultResponse submit(@Valid @RequestBody SubmitAttemptRequest request,
                                 @AuthenticationPrincipal AppUser user) {
        Attempt attempt = attemptService.findById(request.attemptId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found"));
        requireOwner(attempt, user);
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Attempt has already been submitted");
        }

        Set<UUID> answeredQuestionIds = new HashSet<>();
        BigDecimal score = BigDecimal.ZERO;
        int correct = 0;
        int wrong = 0;
        OffsetDateTime submittedAt = OffsetDateTime.now();

        for (AttemptAnswerRequest answerRequest : request.answers()) {
            if (!answeredQuestionIds.add(answerRequest.questionId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate question answer");
            }

            Question question = questionService.findById(answerRequest.questionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
            AttemptAnswer answer = new AttemptAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            answer.setAnsweredAt(submittedAt);
            answer.setMarksAwarded(BigDecimal.ZERO);

            if (answerRequest.selectedOptionId() != null) {
                QuestionOption option = questionOptionRepository.findById(answerRequest.selectedOptionId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Option not found"));
                if (!option.getQuestion().getId().equals(question.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Selected option does not belong to the question");
                }
                answer.setSelectedOption(option);
                answer.setIsCorrect(option.isCorrect());
                if (option.isCorrect()) {
                    answer.setMarksAwarded(question.getMarks());
                    score = score.add(question.getMarks());
                    correct++;
                } else {
                    answer.setMarksAwarded(question.getNegativeMarks().negate());
                    score = score.subtract(question.getNegativeMarks());
                    wrong++;
                }
            }

            attemptAnswerService.save(answer);
        }

        int unanswered = Math.max(request.answers().size() - correct - wrong, 0);
        attempt.setSubmittedAt(submittedAt);
        attempt.setStatus("SUBMITTED");
        attempt.setScore(score);
        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setUnanswered(unanswered);
        attemptService.save(attempt);

        return new ResultResponse(attempt.getId(), score, correct, wrong, unanswered);
    }

    private void requireOwner(Attempt attempt, AppUser user) {
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Attempt does not belong to the current user");
        }
    }

    private AttemptResponse toResponse(Attempt attempt) {
        List<AttemptAnswerRequest> answers = attemptAnswerService.findByAttempt(attempt.getId()).stream()
                .map(answer -> new AttemptAnswerRequest(
                        answer.getQuestion().getId(),
                        answer.getSelectedOption() == null ? null : answer.getSelectedOption().getId()))
                .toList();

        return new AttemptResponse(
                attempt.getId(),
                attempt.getUser().getId(),
                attempt.getExam().getId(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                attempt.getStatus(),
                attempt.getScore(),
                attempt.getCorrectAnswers(),
                attempt.getWrongAnswers(),
                attempt.getUnanswered(),
                answers);
    }
}