package com.languagelearning.service;

import com.languagelearning.dto.WordRequest;
import com.languagelearning.dto.WordResponse;
import com.languagelearning.model.Course;
import com.languagelearning.model.Word;
import com.languagelearning.repository.CourseRepository;
import com.languagelearning.repository.WordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WordService {

    private final WordRepository wordRepository;
    private final CourseRepository courseRepository;

    public WordService(WordRepository wordRepository, CourseRepository courseRepository) {
        this.wordRepository = wordRepository;
        this.courseRepository = courseRepository;
    }

    public WordResponse create(Long courseId, WordRequest request) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        Word word = wordRepository.save(new Word(request.term(), request.meaning(), request.example(), course));
        return WordResponse.from(word);
    }

    public List<WordResponse> listByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
        return wordRepository.findByCourseId(courseId).stream().map(WordResponse::from).toList();
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

    public void delete(Long id) {
        wordRepository.delete(findOrThrow(id));
    }

    private Word findOrThrow(Long id) {
        return wordRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found"));
    }
}
