package com.example.academic_service.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentEnrolledEvent(
        UUID enrollmentId,
        UUID studentId,
        UUID courseId,
        String courseCode,
        String courseTitle,
        LocalDateTime enrolledAt
) {}
