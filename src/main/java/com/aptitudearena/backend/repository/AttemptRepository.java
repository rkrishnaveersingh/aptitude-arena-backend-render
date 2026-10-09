package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttemptRepository extends JpaRepository<Attempt, UUID> {
}
