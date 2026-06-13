package com.example.notification_service.websocket;

import com.example.notification_service.domain.model.Notification;
import com.example.notification_service.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler implements WebSocketHandler {

    // Map User ID -> Sink.Many (to push messages to that user)
    private final Map<UUID, Sinks.Many<String>> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    public NotificationWebSocketHandler(JwtService jwtService) {
        this.jwtService = jwtService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Extract and validate the JWT token from the query string
        String query = session.getHandshakeInfo().getUri().getQuery();
        UUID userId = extractAndValidateUserId(query);

        if (userId == null) {
            // Reject the WebSocket connection — no valid token
            return session.close();
        }

        // Create a Sink for this user if it doesn't exist
        Sinks.Many<String> sink = userSessions.computeIfAbsent(
                userId,
                id -> Sinks.many().multicast().onBackpressureBuffer()
        );

        Flux<WebSocketMessage> messageFlux = sink.asFlux()
                .map(session::textMessage);

        return session.send(messageFlux)
                .doFinally(signalType -> {
                    // Keep the sink alive for potential reconnections
                });
    }

    /**
     * Extracts and validates the JWT from the ?token= query parameter.
     * Returns the userId UUID if valid, or null if rejected.
     */
    private UUID extractAndValidateUserId(String query) {
        if (query == null) return null;

        String token = null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                token = param.substring("token=".length());
                break;
            }
        }

        if (token == null) return null;

        try {
            if (!jwtService.isTokenValid(token)) return null;
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            return null;
        }
    }

    public void sendNotification(UUID userId, Notification notification) {
        Sinks.Many<String> sink = userSessions.get(userId);
        if (sink != null) {
            try {
                String json = objectMapper.writeValueAsString(notification);
                sink.tryEmitNext(json);
            } catch (JsonProcessingException e) {
                // Log error
            }
        }
    }
}
