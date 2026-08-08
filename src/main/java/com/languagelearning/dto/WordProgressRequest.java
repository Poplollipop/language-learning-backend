package com.languagelearning.dto;

import com.languagelearning.model.WordStatus;
import jakarta.validation.constraints.NotNull;

public record WordProgressRequest(@NotNull WordStatus status) {
}
