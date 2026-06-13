package com.example.notification_service.websocket;

import com.example.notification_service.service.JwtService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

/**
 * Validates the JWT token during the WebSocket upgrade handshake.
 * Clients must pass their JWT as a query parameter: ?token=<jwt>
 * The resolved userId is stored in the session attributes so the
 * handler can access it without re-parsing the query string.
 */
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String query = request.getURI().getQuery();
        if (query == null) return false;

        String token = null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                token = param.substring("token=".length());
                break;
            }
        }

        if (token == null || !jwtService.isTokenValid(token)) {
            return false; // Reject the handshake — invalid or missing token
        }

        UUID userId = jwtService.extractUserId(token);
        if (userId == null) return false;

        // Store userId so the WebSocketHandler can use it without re-parsing
        attributes.put("userId", userId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        // No action needed after handshake
    }
}
