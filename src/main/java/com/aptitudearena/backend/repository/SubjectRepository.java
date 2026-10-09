package com.aptitudearena.backend.repository;

import com.aptitudearena.backend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
	java.util.Optional<Subject> findByName(String name);
}
