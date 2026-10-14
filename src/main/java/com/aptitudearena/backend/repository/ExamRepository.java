package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.Exam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    @EntityGraph(attributePaths = "subjects")
    Optional<Exam> findById(UUID id);

    @EntityGraph(attributePaths = "subjects")
    Optional<Exam> findBySlug(String slug);

    @EntityGraph(attributePaths = "subjects")
    Optional<Exam> findFirstByName(String name);

    @EntityGraph(attributePaths = "subjects")
    java.util.List<Exam> findByIsActiveTrue();
}
