package com.example.registration_service.service;

import com.example.registration_service.controller.dto.AuthRequest;
import com.example.registration_service.controller.dto.AuthResponse;
import com.example.registration_service.controller.dto.RegisterRequest;
import com.example.registration_service.domain.model.User;
import com.example.registration_service.domain.repository.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.kafkaTemplate = kafkaTemplate;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        var user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        userRepository.save(user);

        // Publish Kafka event
        var event = new com.example.registration_service.domain.event.UserRegisteredEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                LocalDateTime.now()
        );
        kafkaTemplate.send("user-registered-topic", user.getId().toString(), event);

        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}
