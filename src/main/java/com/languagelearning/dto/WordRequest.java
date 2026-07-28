package com.languagelearning.dto;

import jakarta.validation.constraints.NotBlank;

public record WordRequest(
    @NotBlank String term,
    @NotBlank String meaning,
    String example
) {
}
