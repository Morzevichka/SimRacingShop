package com.morzevichka.auth_service.service;

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

    private static final String PREFIX_REDIS_KEY = "email:verify:";

    public void saveVerificationCode(String code, UUID value, Duration TTL) {
        redisTemplate.opsForValue().set(
                PREFIX_REDIS_KEY + code,
                value.toString(),
                TTL
        );
    }

    public Optional<UUID> getUserIdByVerificationCode(String code) {
        String userId = redisTemplate.opsForValue().get(PREFIX_REDIS_KEY + code);

        if (Objects.isNull(userId)) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public void deleteVerificationCode(String key) {
        redisTemplate.delete(PREFIX_REDIS_KEY + key);
    }
}
