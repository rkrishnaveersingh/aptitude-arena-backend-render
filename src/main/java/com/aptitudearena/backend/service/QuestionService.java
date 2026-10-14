package com.aptitudearena.backend.service;

import com.aptitudearena.backend.entity.Question;
import com.aptitudearena.backend.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Optional<Question> findById(UUID id) {
        return questionRepository.findWithSubjectById(id);
    }

    public List<Question> findBySubject(UUID subjectId) {
        return questionRepository.findWithSubjectBySubjectId(subjectId);
    }

    public List<Question> findByExam(UUID examId) {
        return questionRepository.findWithSubjectByExamId(examId);
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }
}
