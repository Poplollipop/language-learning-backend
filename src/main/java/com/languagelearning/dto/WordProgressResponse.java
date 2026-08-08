package com.languagelearning.dto;

import com.languagelearning.model.WordProgress;
import com.languagelearning.model.WordStatus;

import java.time.Instant;

public record WordProgressResponse(Long wordId, String term, WordStatus status, int reviewCount, Instant lastReviewedAt) {

    public static WordProgressResponse from(WordProgress progress) {
        return new WordProgressResponse(progress.getWord().getId(), progress.getWord().getTerm(),
            progress.getStatus(), progress.getReviewCount(), progress.getLastReviewedAt());
    }
}
