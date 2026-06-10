# API Gateway

## Overview
This is the API Gateway for the Smart University Management Platform (SUMP). It uses **Spring Cloud Gateway**.

## Why Spring Cloud Gateway?
In a microservices architecture, having a single entry point (Gateway) for all clients (frontend, mobile apps) provides several benefits:
- **Routing:** Clients don't need to know the IP addresses and ports of individual services. They just call the gateway (e.g., `http://localhost:8080/api/v1/students`), and the gateway routes the request to the `registration-service`.
- **Cross-cutting concerns:** We can implement authentication, rate limiting, and CORS configuration in one place rather than repeating it in every service.
- **Protocol Translation:** The gateway can translate between protocols (e.g., HTTP to WebSockets).

## Configuration
Routing rules will be defined in `src/main/resources/application.properties` (or `.yml`).
