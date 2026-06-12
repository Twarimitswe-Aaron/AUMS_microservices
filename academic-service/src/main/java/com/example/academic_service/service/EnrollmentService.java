package com.example.academic_service.service;

import com.example.academic_service.controller.dto.EnrollRequest;
import com.example.academic_service.domain.event.StudentEnrolledEvent;
import com.example.academic_service.domain.model.Enrollment;
import com.example.academic_service.domain.repository.CourseRepository;
import com.example.academic_service.domain.repository.EnrollmentRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public String enrollStudent(EnrollRequest request) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            throw new IllegalStateException("Course has reached maximum capacity");
        }

        // Increment enrollment count
        course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
        courseRepository.save(course);

        // Save enrollment
        var enrollment = new Enrollment(request.getStudentId(), course);
        enrollmentRepository.save(enrollment);

        // Publish event to Kafka
        var event = new StudentEnrolledEvent(
                enrollment.getId(),
                request.getStudentId(),
                course.getId(),
                course.getCode(),
                course.getTitle(),
                enrollment.getEnrolledAt()
        );
        kafkaTemplate.send("student-enrolled-topic", request.getStudentId().toString(), event);

        return "Successfully enrolled in " + course.getTitle();
    }
}
