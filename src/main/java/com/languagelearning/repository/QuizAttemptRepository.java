package com.languagelearning.repository;

import com.languagelearning.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserIdAndCourseIdOrderByCreatedAtDesc(Long userId, Long courseId);
}
