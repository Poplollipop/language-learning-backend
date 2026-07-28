package com.languagelearning.dto;

import java.util.List;

public record QuizResultResponse(
    Long attemptId,
    Long courseId,
    int score,
    int total,
    List<QuizAnswerResult> results
) {
}
