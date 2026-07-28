package com.languagelearning.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(
    @NotBlank String title,
    String description
) {
}
