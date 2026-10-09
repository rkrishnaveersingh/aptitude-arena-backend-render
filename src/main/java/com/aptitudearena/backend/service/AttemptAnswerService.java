package com.aptitudearena.backend.service;

import com.aptitudearena.backend.entity.AttemptAnswer;
import com.aptitudearena.backend.repository.AttemptAnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttemptAnswerService {

    private final AttemptAnswerRepository attemptAnswerRepository;

    public AttemptAnswerService(AttemptAnswerRepository attemptAnswerRepository) {
        this.attemptAnswerRepository = attemptAnswerRepository;
    }

    public Optional<AttemptAnswer> findById(UUID id) {
        return attemptAnswerRepository.findById(id);
    }

    public List<AttemptAnswer> findByAttempt(UUID attemptId) {
        // repository method not present; fallback to in-memory filter
        return attemptAnswerRepository.findAll().stream()
                .filter(a -> a.getAttempt() != null && a.getAttempt().getId().equals(attemptId))
                .toList();
    }

    public AttemptAnswer save(AttemptAnswer answer) {
        return attemptAnswerRepository.save(answer);
    }
}
