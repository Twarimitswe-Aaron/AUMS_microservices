package com.example.notification_service.controller;

import com.example.notification_service.domain.model.Notification;
import com.example.notification_service.domain.repository.NotificationRepository;
import com.example.notification_service.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for reading and managing notifications.
 * All endpoints require a valid JWT (userId is extracted from the token).
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final JwtService jwtService;

    public NotificationController(NotificationRepository notificationRepository,
                                   JwtService jwtService) {
        this.notificationRepository = notificationRepository;
        this.jwtService = jwtService;
    }

    /**
     * Returns all notifications for the authenticated user, newest first.
     * The userId is extracted from the Authorization header JWT.
     */
    @GetMapping
    public Mono<ResponseEntity<List<Notification>>> getNotifications(
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = extractUserId(authHeader);
        if (userId == null) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .collectList()
                .map(ResponseEntity::ok);
    }

    /**
     * Marks a single notification as read.
     */
    @PatchMapping("/{id}/read")
    public Mono<ResponseEntity<Notification>> markAsRead(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = extractUserId(authHeader);
        if (userId == null) {
            return Mono.just(ResponseEntity.status(401).<Notification>build());
        }

        return notificationRepository.findById(id)
                .<ResponseEntity<Notification>>flatMap(notification -> {
                    if (!notification.getUserId().equals(userId)) {
                        return Mono.just(ResponseEntity.status(403).<Notification>build());
                    }
                    notification.setRead(true);
                    return notificationRepository.save(notification)
                            .map(ResponseEntity::ok);
                })
                .defaultIfEmpty(ResponseEntity.<Notification>notFound().build());
    }

    private UUID extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        try {
            if (!jwtService.isTokenValid(token)) return null;
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            return null;
        }
    }
}
