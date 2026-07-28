package com.languagelearning.dto;

import com.languagelearning.model.Course;

import java.time.Instant;

public record CourseResponse(Long id, String title, String description, Instant createdAt) {

    public static CourseResponse from(Course course) {
        return new CourseResponse(course.getId(), course.getTitle(), course.getDescription(), course.getCreatedAt());
    }
}
