package com.languagelearning.dto;

import com.languagelearning.model.Word;

import java.time.Instant;

public record WordResponse(Long id, String term, String meaning, String example, Long courseId, Instant createdAt) {

    public static WordResponse from(Word word) {
        return new WordResponse(word.getId(), word.getTerm(), word.getMeaning(), word.getExample(),
            word.getCourse().getId(), word.getCreatedAt());
    }
}
