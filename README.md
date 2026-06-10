# Smart University Management Platform (SUMP)

## Overview
This repository contains the **Smart University Management Platform (SUMP)**, a distributed microservices-based application designed to manage student registration, academic records, and real-time notifications.

This project was specifically designed to demonstrate a **mixed architectural style**, where traditional synchronous request-response (Spring MVC) is used alongside reactive, event-driven streaming (Spring WebFlux + Kafka) only where it naturally fits the business requirements.

## Architecture

The system is composed of several independent services and infrastructure components:

### 1. Registration Service (Spring Boot MVC)
- **Responsibility:** Owns student identity, authentication, user management, and registration data.
- **Why MVC?:** Operations like user creation, login, and profile updates are typical CRUD operations. Traditional Spring Boot MVC is perfectly suited for this, and Reactive programming would add unnecessary complexity without significant performance gains.
- **Database:** PostgreSQL (`registration_db`)

### 2. Academic Service (Spring Boot MVC)
- **Responsibility:** Owns academic operations, courses, enrollments, grades, and timetables.
- **Why MVC?:** Academic transactions (e.g., enrolling in a course, submitting a grade) are heavily database-driven and require strict ACID compliance. 
- **Database:** PostgreSQL (`academic_db`)
- **Cache:** Redis (for caching frequently accessed data like course catalogs).

### 3. Notification Service (Spring WebFlux + Project Reactor)
- **Responsibility:** Handles all communication (WebSockets, Emails, SMS) and consumes domain events asynchronously.
- **Why Reactive?:** This service is designed to handle high concurrency (e.g., 5,000 students connected simultaneously via WebSockets). It spends most of its time waiting for network I/O (sending emails, pushing updates). Reactive programming (Event Loop, non-blocking I/O) shines here, allowing the service to manage thousands of connections with a small number of threads.
- **Database:** PostgreSQL (`notification_db`) (Using R2DBC for reactive data access).

### 4. API Gateway (Spring Cloud Gateway)
- **Responsibility:** Acts as the single entry point for the frontend, routing requests to the appropriate backend service.

### 5. Infrastructure
- **Kafka:** The central event bus. Services communicate asynchronously by publishing and consuming events (e.g., `STUDENT_REGISTERED`, `GRADE_PUBLISHED`).
- **PostgreSQL:** Relational database for persistent storage.
- **Redis:** In-memory data structure store used as a cache.

## Project Structure
We have split the project into logical domains:
- `/infrastructure`: Contains the Docker Compose setup for our databases and message broker.
- `/api-gateway`: The Spring Cloud Gateway routing layer.
- `/registration-service`: The Spring Boot MVC application for identity.
- `/academic-service`: The Spring Boot MVC application for academics.
- `/notification-service`: The Spring WebFlux application for real-time alerts.
- `/frontend`: The SvelteKit frontend (to be implemented).

## How to Run
Follow the `README.md` files in each sub-directory, starting with `/infrastructure`.
