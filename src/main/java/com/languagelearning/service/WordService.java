package com.languagelearning.service;

import com.languagelearning.dto.WordRequest;
import com.languagelearning.dto.WordResponse;
import com.languagelearning.model.Course;
import com.languagelearning.model.Word;
import com.languagelearning.repository.CourseRepository;
import com.languagelearning.repository.WordProgressRepository;
import com.languagelearning.repository.WordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WordService {

    private final WordRepository wordRepository;
    private final CourseRepository courseRepository;
    private final WordProgressRepository wordProgressRepository;

    public WordService(WordRepository wordRepository, CourseRepository courseRepository,
                        WordProgressRepository wordProgressRepository) {
        this.wordRepository = wordRepository;
        this.courseRepository = courseRepository;
        this.wordProgressRepository = wordProgressRepository;
    }

    public WordResponse create(Long courseId, WordRequest request) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        Word word = wordRepository.save(new Word(request.term(), request.meaning(), request.example(), course));
        return WordResponse.from(word);
    }

    public Page<WordResponse> listByCourse(Long courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
        return wordRepository.findByCourseId(courseId, pageable).map(WordResponse::from);
    }

    public WordResponse get(Long id) {
        return WordResponse.from(findOrThrow(id));
    }

    public WordResponse update(Long id, WordRequest request) {
        Word word = findOrThrow(id);
        word.setTerm(request.term());
        word.setMeaning(request.meaning());
        word.setExample(request.example());
        return WordResponse.from(wordRepository.save(word));
    }

    @Transactional
    public void delete(Long id) {
        Word word = findOrThrow(id);
        wordProgressRepository.deleteByWordId(id);
        wordRepository.delete(word);
    }

    private Word findOrThrow(Long id) {
        return wordRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found"));
    }
}
