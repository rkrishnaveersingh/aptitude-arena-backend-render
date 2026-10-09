package com.aptitudearena.backend.service;

import com.aptitudearena.backend.entity.Exam;
import com.aptitudearena.backend.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public Optional<Exam> findById(UUID id) {
        return examRepository.findById(id);
    }

    public Optional<Exam> findBySlug(String slug) {
        return examRepository.findBySlug(slug);
    }

    public List<Exam> findAllActive() {
        return examRepository.findByIsActiveTrue();
    }

    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }
}
