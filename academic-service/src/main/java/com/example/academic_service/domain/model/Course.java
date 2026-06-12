package com.example.academic_service.domain.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "courses")
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "current_enrollment", nullable = false)
    private Integer currentEnrollment = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Course() {}

    public Course(String title, String code, Integer capacity) {
        this.title = title;
        this.code = code;
        this.capacity = capacity;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getCode() { return code; }
    public Integer getCapacity() { return capacity; }
    public Integer getCurrentEnrollment() { return currentEnrollment; }
    
    public void setCurrentEnrollment(Integer currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
}
