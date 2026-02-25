package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.InvalidEmailVerificationTokenException;
import com.morzevichka.auth_service.exception.account_recovery.InvalidAccountRecoveryTokenException;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private final SecureRandom secureRandom = new SecureRandom();

    public void saveToken(String token, UUID userId, Duration TTL, RedisTokenType type) {
        String oldToken = redisTemplate.opsForValue().get(type.buildUserKey(userId.toString()));

        if (oldToken != null) {
            redisTemplate.delete(type.buildTokenKey(oldToken));
            log.info("Old token deleted: {}", oldToken);
        }

        redisTemplate.opsForValue().set(
                type.buildTokenKey(token),
                userId.toString(),
                TTL
        );
        log.info("Saved {} -> {}", type.buildTokenKey(token), userId);

        redisTemplate.opsForValue().set(
                type.buildUserKey(userId.toString()),
                token,
                TTL
        );
        log.info("Saved {} -> {}", type.buildUserKey(userId.toString()), token);
    }

    public Optional<UUID> getUserIdByToken(String token, RedisTokenType type) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(type.buildTokenKey(token)))
                .map(UUID::fromString);
    }

    public void deleteToken(String token, RedisTokenType type) {
        getUserIdByToken(token, type).ifPresent(userId -> {
            log.info("Found {}", type.buildTokenKey(token));

            redisTemplate.delete(type.buildTokenKey(token));
            log.info("Deleted {}", type.buildTokenKey(token));

            redisTemplate.delete(type.buildUserKey(userId.toString()));
            log.info("Deleted {}", type.buildUserKey(userId.toString()));
        });
    }

    public String createToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public UUID verifyAccountRecoveryToken(String token) {
        return getUserIdByToken(token, RedisTokenType.ACCOUNT_RECOVERY)
                .orElseThrow(() -> new InvalidAccountRecoveryTokenException("Link is expired or invalid"));
    }

    public UUID verifyEmailVerificationToken(String token) {
        return getUserIdByToken(token, RedisTokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new InvalidEmailVerificationTokenException("Link is expired or invalid"));
    }
}
