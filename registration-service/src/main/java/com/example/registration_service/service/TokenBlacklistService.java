package com.example.registration_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Maintains a Redis-based token blacklist to support server-side logout.
 * When a user logs out, their token is stored in Redis with a TTL matching
 * the token's remaining validity period. The JWT filter checks this list
 * before granting access.
 */
@Service
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Adds a token to the blacklist for the given TTL duration.
     */
    public void blacklist(String token, Duration ttl) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "revoked", ttl);
    }

    /**
     * Returns true if the token has been revoked.
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
