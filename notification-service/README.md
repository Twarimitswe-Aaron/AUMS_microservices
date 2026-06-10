# Notification Service

## Overview
The Notification Service is responsible for all outbound communication, including real-time WebSocket streams, email mocks, and SMS alerts.

## Architecture & Technology Stack
- **Framework:** Spring WebFlux (Reactive)
- **Database:** PostgreSQL (Using R2DBC - Reactive Relational Database Connectivity)
- **Messaging:** Reactor Kafka (Reactive Kafka Consumer)
- **Real-time:** WebSockets

## Why Reactive (Spring WebFlux)?
**This is where Reactive Programming shines.**

Imagine 5,000 students connect to a live dashboard via WebSockets.
- **Traditional MVC:** Would require 5,000 active threads, quickly exhausting memory and CPU context-switching overhead.
- **Reactive (WebFlux):** Uses an Event Loop and non-blocking I/O. It can handle these 5,000 connections with just a handful of threads (typically 1 per CPU core).

When this service needs to send 2,000 emails because grades were published, it doesn't block a thread while waiting for the SMTP server to respond. Instead, the thread goes back to the pool to do other work, and an event is triggered when the SMTP server replies. This allows massive concurrency for I/O bound tasks.

## Responsibilities
This service consumes domain events (e.g., `STUDENT_REGISTERED`) from Kafka and maps them to asynchronous notification flows.
