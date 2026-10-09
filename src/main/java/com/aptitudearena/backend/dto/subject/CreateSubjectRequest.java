package com.aptitudearena.backend.dto.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSubjectRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 2000) String description
) {}
