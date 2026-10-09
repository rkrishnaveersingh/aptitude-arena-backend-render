package com.aptitudearena.backend.service;

import com.aptitudearena.backend.entity.Subject;
import com.aptitudearena.backend.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Optional<Subject> findById(UUID id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }
}
