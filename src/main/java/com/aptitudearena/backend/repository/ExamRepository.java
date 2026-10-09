package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    Optional<Exam> findBySlug(String slug);
    Optional<Exam> findFirstByName(String name);
    java.util.List<Exam> findByIsActiveTrue();
}
