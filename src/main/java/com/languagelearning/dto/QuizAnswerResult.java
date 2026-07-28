package com.languagelearning.dto;

public record QuizAnswerResult(
    Long wordId,
    String term,
    String correctMeaning,
    String selectedMeaning,
    boolean correct
) {
}
