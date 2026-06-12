package com.example.academic_service.controller;

import com.example.academic_service.controller.dto.EnrollRequest;
import com.example.academic_service.domain.model.Course;
import com.example.academic_service.service.CourseService;
import com.example.academic_service.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic")
public class AcademicController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public AcademicController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PostMapping("/enroll")
    public ResponseEntity<String> enroll(@Valid @RequestBody EnrollRequest request) {
        return ResponseEntity.ok(enrollmentService.enrollStudent(request));
    }
}
