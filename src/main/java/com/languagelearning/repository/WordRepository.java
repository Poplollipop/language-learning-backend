package com.languagelearning.repository;

import com.languagelearning.model.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findByCourseId(Long courseId);

    Page<Word> findByCourseId(Long courseId, Pageable pageable);
}
