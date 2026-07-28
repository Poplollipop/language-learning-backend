package com.languagelearning.dto;

import com.languagelearning.model.QuizAttempt;

import java.time.Instant;

public record QuizAttemptSummaryResponse(Long id, int score, int total, Instant createdAt) {

    public static QuizAttemptSummaryResponse from(QuizAttempt attempt) {
        return new QuizAttemptSummaryResponse(attempt.getId(), attempt.getScore(), attempt.getTotal(),
            attempt.getCreatedAt());
    }
}
