package com.languagelearning.repository;

import com.languagelearning.model.WordProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordProgressRepository extends JpaRepository<WordProgress, Long> {

    Optional<WordProgress> findByUserIdAndWordId(Long userId, Long wordId);

    Page<WordProgress> findByUserIdAndWord_CourseId(Long userId, Long courseId, Pageable pageable);

    void deleteByWordId(Long wordId);

    void deleteByWord_CourseId(Long courseId);
}
