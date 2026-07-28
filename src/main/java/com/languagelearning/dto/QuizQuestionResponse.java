package com.languagelearning.dto;

import java.util.List;

public record QuizQuestionResponse(Long wordId, String term, List<String> options) {
}
