# Infrastructure

This directory contains the Docker Compose configuration required to run the core backing services for the Smart University Management Platform (SUMP).

## Components

We use a single `docker-compose.yml` to spin up the following containers:

1.  **PostgreSQL (Relational Database)**
    *   **Why used:** PostgreSQL is a robust, open-source relational database perfectly suited for transactional data like user accounts and academic records.
    *   **Configuration:** We use the `init-dbs.sql` script mounted into `/docker-entrypoint-initdb.d/` to automatically create three separate logical databases (`registration_db`, `academic_db`, `notification_db`) upon initial startup. This provides strict data separation between our microservices (Database-per-Service pattern) without the overhead of running three separate Postgres containers locally.
2.  **Redis (Cache)**
    *   **Why used:** Used by the Academic service to cache frequently accessed data (like course lists) to reduce database load.
3.  **Kafka & Zookeeper (Event Bus)**
    *   **Why used:** Kafka is a distributed event streaming platform. We use it to decouple our services. Instead of the Registration Service calling the Notification Service directly (synchronously), it publishes an event to Kafka. The Notification Service consumes this event asynchronously. Zookeeper is currently required to manage the Kafka cluster state.

## How to Start

From this directory (`/infrastructure`), run:

```bash
docker-compose up -d
```

To stop:

```bash
docker-compose down
```

## Useful Commands

To check if the databases were created:
```bash
docker exec -it sump-postgres psql -U sump_admin -l
```
