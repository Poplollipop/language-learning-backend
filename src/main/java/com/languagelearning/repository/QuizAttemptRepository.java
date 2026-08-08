package com.languagelearning.repository;

import com.languagelearning.model.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Page<QuizAttempt> findByUserIdAndCourseIdOrderByCreatedAtDesc(Long userId, Long courseId, Pageable pageable);
}
