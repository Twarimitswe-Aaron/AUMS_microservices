package com.example.notification_service.websocket;

import com.example.notification_service.domain.model.Notification;
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

    public NotificationWebSocketHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // Assume the user passes their ID via query param like ?userId=...
        // In a real application, you would extract this from a JWT token in the handshake!
        String query = session.getHandshakeInfo().getUri().getQuery();
        if (query == null || !query.contains("userId=")) {
            return session.close();
        }

        String userIdStr = query.split("userId=")[1].split("&")[0];
        UUID userId;
        try {
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            return session.close();
        }

        // Create a Sink for this user if it doesn't exist
        Sinks.Many<String> sink = userSessions.computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer());

        Flux<WebSocketMessage> messageFlux = sink.asFlux()
                .map(session::textMessage);

        // Keep the connection alive until the client disconnects
        return session.send(messageFlux)
                .doFinally(signalType -> {
                    // We don't remove the sink on disconnect because a user might have multiple tabs open.
                    // A more advanced implementation would handle multiple sessions per user.
                });
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
