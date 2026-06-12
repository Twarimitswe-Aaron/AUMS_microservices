package com.example.academic_service.service;

import com.example.academic_service.domain.model.Course;
import com.example.academic_service.domain.repository.CourseRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Cacheable(value = "courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
