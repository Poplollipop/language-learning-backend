package com.languagelearning.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record QuizSubmissionRequest(
    @NotEmpty @Valid List<QuizAnswerRequest> answers
) {
}
