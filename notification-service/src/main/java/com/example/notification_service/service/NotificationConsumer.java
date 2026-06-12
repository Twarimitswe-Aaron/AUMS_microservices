package com.example.notification_service.service;

import com.example.notification_service.domain.model.Notification;
import com.example.notification_service.domain.repository.NotificationRepository;
import com.example.notification_service.websocket.NotificationWebSocketHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final NotificationWebSocketHandler webSocketHandler;

    public NotificationConsumer(NotificationRepository notificationRepository, NotificationWebSocketHandler webSocketHandler) {
        this.notificationRepository = notificationRepository;
        this.webSocketHandler = webSocketHandler;
    }

    @KafkaListener(topics = "user-registered-topic", groupId = "notification-group")
    public void consumeUserRegisteredEvent(Map<String, Object> event) {
        try {
            UUID userId = UUID.fromString((String) event.get("userId"));
            String username = (String) event.get("username");

            String message = "Welcome to the Smart University Platform, " + username + "!";
            
            Notification notification = new Notification(userId, message);
            
            notificationRepository.save(notification)
                    .subscribe(savedNotification -> {
                        // Push to WebSocket if user is connected
                        webSocketHandler.sendNotification(userId, savedNotification);
                    });

        } catch (Exception e) {
            // Log Error
        }
    }

    @KafkaListener(topics = "student-enrolled-topic", groupId = "notification-group")
    public void consumeStudentEnrolledEvent(Map<String, Object> event) {
        try {
            UUID studentId = UUID.fromString((String) event.get("studentId"));
            String courseTitle = (String) event.get("courseTitle");

            String message = "You have successfully enrolled in " + courseTitle + "!";
            
            Notification notification = new Notification(studentId, message);
            
            notificationRepository.save(notification)
                    .subscribe(savedNotification -> {
                        // Push to WebSocket if user is connected
                        webSocketHandler.sendNotification(studentId, savedNotification);
                    });

        } catch (Exception e) {
            // Log Error
        }
    }
}
