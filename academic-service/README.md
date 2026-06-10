# Academic Service

## Overview
The Academic Service owns the core business logic of the university: managing courses, handling student enrollments, processing grades, and managing attendance.

## Architecture & Technology Stack
- **Framework:** Spring Boot MVC
- **Database:** PostgreSQL (Relational)
- **Data Access:** Spring Data JPA
- **Caching:** Redis
- **Messaging:** Spring Kafka (Producer)

## Why Spring MVC?
Similar to the Registration Service, academic operations are highly transactional. When a student enrolls in a course, we need strict ACID (Atomicity, Consistency, Isolation, Durability) guarantees to prevent race conditions (e.g., enrolling more students than the course capacity). Synchronous Spring MVC with JPA handles transaction boundaries very clearly and safely.

## Why Redis?
Course catalogs and timetables are read frequently but updated infrequently. By caching these responses in Redis, we drastically reduce the load on the PostgreSQL database, improving read latency.

## Event Driven Communication
This service publishes events to Kafka, such as `STUDENT_ENROLLED` and `GRADE_PUBLISHED`. The Notification service listens to these to alert the student.
