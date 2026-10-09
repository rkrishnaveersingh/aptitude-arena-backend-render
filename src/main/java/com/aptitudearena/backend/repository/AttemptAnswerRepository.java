package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, UUID> {
}
