package com.aptitudearena.backend.service;

import com.aptitudearena.backend.entity.Attempt;
import com.aptitudearena.backend.repository.AttemptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;

    public AttemptService(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    public Optional<Attempt> findById(UUID id) {
        return attemptRepository.findById(id);
    }

    public List<Attempt> findByUser(UUID userId) {
        // repository method not yet present; use findAll and filter could be expensive
        // Prefer adding a repository method findByUser_Id(UUID userId) if acceptable.
        return attemptRepository.findAll().stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId))
                .toList();
    }

    public Attempt save(Attempt attempt) {
        return attemptRepository.save(attempt);
    }
}
