# Registration Service

## Overview
The Registration Service is responsible for managing student identities, authentication, user profiles, roles, and academic faculties.

## Architecture & Technology Stack
- **Framework:** Spring Boot MVC
- **Database:** PostgreSQL (Relational)
- **Data Access:** Spring Data JPA
- **Security:** Spring Security & JWT
- **Messaging:** Spring Kafka (Producer)

## Why Spring MVC?
This service handles operations like creating a user, updating a profile, and fetching a user by ID. These are classic, database-heavy CRUD (Create, Read, Update, Delete) operations. 

**Traditional Spring MVC (Thread-per-request model) is perfect for this.**
Reactive programming adds unnecessary complexity here. For simple CRUD over a relational database, standard JPA and synchronous requests are easier to reason about, easier to debug, and perform exceptionally well for most standard workloads.

## Event Driven Communication
When a student registers successfully, this service does *not* synchronously call the Notification Service to send a welcome email. Instead, it publishes a `STUDENT_REGISTERED` event to **Kafka**. This ensures that if the Notification Service is down, the registration process still succeeds, and the notification will be processed once the service comes back online (Decoupling).
