package com.example.academic_service.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class EnrollRequest {
    @NotNull
    private UUID studentId;
    
    @NotNull
    private UUID courseId;

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
}
