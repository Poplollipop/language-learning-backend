package com.languagelearning.service;

import com.languagelearning.dto.CourseRequest;
import com.languagelearning.dto.CourseResponse;
import com.languagelearning.model.Course;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final WordRepository wordRepository;
    private final WordProgressRepository wordProgressRepository;

    public CourseService(CourseRepository courseRepository, WordRepository wordRepository,
                          WordProgressRepository wordProgressRepository) {
        this.courseRepository = courseRepository;
        this.wordRepository = wordRepository;
        this.wordProgressRepository = wordProgressRepository;
    }

    public CourseResponse create(CourseRequest request) {
        Course course = courseRepository.save(new Course(request.title(), request.description()));
        return CourseResponse.from(course);
    }

    public Page<CourseResponse> list(Pageable pageable) {
        return courseRepository.findAll(pageable).map(CourseResponse::from);
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
        wordProgressRepository.deleteByWord_CourseId(id);
        wordRepository.deleteAll(wordRepository.findByCourseId(id));
        courseRepository.delete(course);
    }

    private Course findOrThrow(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }
}
