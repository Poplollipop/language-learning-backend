package com.languagelearning.service;

import com.languagelearning.dto.WordProgressRequest;
import com.languagelearning.dto.WordProgressResponse;
import com.languagelearning.model.User;
import com.languagelearning.model.Word;
import com.languagelearning.model.WordProgress;
import com.languagelearning.repository.CourseRepository;
import com.languagelearning.repository.WordProgressRepository;
import com.languagelearning.repository.WordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WordProgressService {

    private final WordProgressRepository wordProgressRepository;
    private final WordRepository wordRepository;
    private final CourseRepository courseRepository;

    public WordProgressService(WordProgressRepository wordProgressRepository, WordRepository wordRepository,
                                CourseRepository courseRepository) {
        this.wordProgressRepository = wordProgressRepository;
        this.wordRepository = wordRepository;
        this.courseRepository = courseRepository;
    }

    public WordProgressResponse review(Long wordId, User user, WordProgressRequest request) {
        Word word = wordRepository.findById(wordId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found"));
        WordProgress progress = wordProgressRepository.findByUserIdAndWordId(user.getId(), wordId)
            .orElseGet(() -> new WordProgress(user, word, request.status()));
        progress.review(request.status());
        return WordProgressResponse.from(wordProgressRepository.save(progress));
    }

    public Page<WordProgressResponse> listByCourse(Long courseId, User user, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
        return wordProgressRepository.findByUserIdAndWord_CourseId(user.getId(), courseId, pageable)
            .map(WordProgressResponse::from);
    }
}
