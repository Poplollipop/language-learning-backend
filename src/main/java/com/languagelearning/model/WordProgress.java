package com.languagelearning.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "word_id"}))
@Getter
@Setter
@NoArgsConstructor
public class WordProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WordStatus status;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "last_reviewed_at", nullable = false)
    private Instant lastReviewedAt;

    public WordProgress(User user, Word word, WordStatus status) {
        this.user = user;
        this.word = word;
        this.status = status;
        this.reviewCount = 0;
        this.lastReviewedAt = Instant.now();
    }

    public void review(WordStatus status) {
        this.status = status;
        this.reviewCount++;
        this.lastReviewedAt = Instant.now();
    }
}
