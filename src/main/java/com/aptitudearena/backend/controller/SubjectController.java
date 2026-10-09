package com.aptitudearena.backend.controller;

import com.aptitudearena.backend.dto.subject.CreateSubjectRequest;
import com.aptitudearena.backend.dto.subject.SubjectResponse;
import com.aptitudearena.backend.entity.Subject;
import com.aptitudearena.backend.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public List<SubjectResponse> findAll() {
        return subjectService.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> findById(@PathVariable UUID id) {
        return subjectService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody CreateSubjectRequest request) {
        Subject subject = new Subject();
        subject.setName(request.name());
        subject.setDescription(request.description());
        subject.setActive(true);
        subject.setCreatedAt(OffsetDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(subjectService.save(subject)));
    }

    private SubjectResponse toResponse(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                subject.isActive(),
                subject.getCreatedAt());
    }
}