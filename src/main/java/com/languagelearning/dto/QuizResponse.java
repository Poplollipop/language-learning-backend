package com.languagelearning.dto;

import java.util.List;

public record QuizResponse(Long courseId, List<QuizQuestionResponse> questions) {
}
