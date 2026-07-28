package com.languagelearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuizAnswerRequest(
    @NotNull Long wordId,
    @NotBlank String selectedMeaning
) {
}
