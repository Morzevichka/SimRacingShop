package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.model.token.RedisTokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(String token, UUID userId, Duration TTL, RedisTokenType type) {
        redisTemplate.opsForValue().set(
                type.buildKey(token),
                userId.toString(),
                TTL
        );
    }

    public Optional<UUID> getUserIdByToken(String token, RedisTokenType type) {
        String userId = redisTemplate.opsForValue().get(type.buildKey(token));

        if (userId == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public void deleteToken(String token, RedisTokenType type) {
        redisTemplate.delete(type.buildKey(token));
    }
}
