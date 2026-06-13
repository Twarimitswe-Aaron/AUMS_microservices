package com.example.registration_service.controller;

import com.example.registration_service.controller.dto.AuthRequest;
import com.example.registration_service.controller.dto.AuthResponse;
import com.example.registration_service.controller.dto.RegisterRequest;
import com.example.registration_service.service.AuthService;
import com.example.registration_service.service.JwtService;
import com.example.registration_service.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthService authService,
                          JwtService jwtService,
                          TokenBlacklistService tokenBlacklistService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @Valid @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    /**
     * Invalidates the current JWT by adding it to the Redis blacklist.
     * The token remains in Redis until its natural expiry time.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                Date expiration = jwtService.extractExpiration(jwt);
                long remainingMillis = expiration.getTime() - System.currentTimeMillis();
                if (remainingMillis > 0) {
                    tokenBlacklistService.blacklist(jwt, Duration.ofMillis(remainingMillis));
                }
            } catch (Exception ignored) {
                // Token is already invalid — nothing to blacklist
            }
        }
        return ResponseEntity.noContent().build();
    }
}
