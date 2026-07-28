package com.languagelearning.service;

import com.languagelearning.dto.CourseRequest;
import com.languagelearning.dto.CourseResponse;
import com.languagelearning.model.Course;
import com.languagelearning.repository.CourseRepository;
import com.languagelearning.repository.WordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final WordRepository wordRepository;

    public CourseService(CourseRepository courseRepository, WordRepository wordRepository) {
        this.courseRepository = courseRepository;
        this.wordRepository = wordRepository;
    }

    public CourseResponse create(CourseRequest request) {
        Course course = courseRepository.save(new Course(request.title(), request.description()));
        return CourseResponse.from(course);
    }

    public List<CourseResponse> list() {
        return courseRepository.findAll().stream().map(CourseResponse::from).toList();
    }

    public CourseResponse get(Long id) {
        return CourseResponse.from(findOrThrow(id));
    }

    public CourseResponse update(Long id, CourseRequest request) {
        Course course = findOrThrow(id);
        course.setTitle(request.title());
        course.setDescription(request.description());
        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional
    public void delete(Long id) {
        Course course = findOrThrow(id);
        wordRepository.deleteAll(wordRepository.findByCourseId(id));
        courseRepository.delete(course);
    }

    private Course findOrThrow(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }
}
