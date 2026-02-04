package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.model.token.RedisTokenType;
import com.morzevichka.auth_service.util.DataRedisTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@DataRedisTest
public class RedisServiceTest {

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:8.2-alpine"))
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void setDown() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void saveToken_shouldSaveEmailVerificationToken() {
        String token = "TOKEN";
        UUID uuid = UUID.randomUUID();

        redisService.saveToken(token, uuid, Duration.ofMinutes(1), RedisTokenType.EMAIL_VERIFICATION);

        String savedUUID = redisTemplate.opsForValue()
                .get(RedisTokenType.EMAIL_VERIFICATION.buildKey(token));

        assertThat(savedUUID).isEqualTo(uuid.toString());
    }

    @Test
    void getUserIdByToken_shouldReturnUserId_whenEmailVerificationTokenExists() {
        String token = "TOKEN";
        UUID userId = UUID.randomUUID();

        redisTemplate.opsForValue().set(
                RedisTokenType.EMAIL_VERIFICATION.buildKey(token),
                userId.toString(),
                Duration.ofMinutes(1)
        );

        Optional<UUID> userIdFound = redisService.getUserIdByToken(token, RedisTokenType.EMAIL_VERIFICATION);

        assertThat(userIdFound).isPresent();
        assertThat(userIdFound.get()).isEqualTo(userId);
    }

    @Test
    void getUserIdByToken_shouldReturnOptionalEmpty_whenEmailVerificationTokenNotExists() {
        Optional<UUID> userIdFound = redisService.getUserIdByToken("TOKEN", RedisTokenType.EMAIL_VERIFICATION);

        assertThat(userIdFound).isEmpty();
    }

    @Test
    void saveToken_shouldSaveAccountRecoveryToken() {
        String token = "TOKEN";
        UUID uuid = UUID.randomUUID();

        redisService.saveToken(token, uuid, Duration.ofMinutes(1), RedisTokenType.ACCOUNT_RECOVERY);

        String savedUUID = redisTemplate.opsForValue()
                .get(RedisTokenType.ACCOUNT_RECOVERY.buildKey(token));

        assertThat(savedUUID).isEqualTo(uuid.toString());
    }

    @Test
    void getUserIdByToken_shouldReturnUserId_whenAccountRecoveryTokenExists() {
        String token = "TOKEN";
        UUID userId = UUID.randomUUID();

        redisTemplate.opsForValue().set(
                RedisTokenType.ACCOUNT_RECOVERY.buildKey(token),
                userId.toString(),
                Duration.ofMinutes(1)
        );

        Optional<UUID> userIdFound = redisService.getUserIdByToken(token, RedisTokenType.ACCOUNT_RECOVERY);

        assertThat(userIdFound).isPresent();
        assertThat(userIdFound.get()).isEqualTo(userId);
    }

    @Test
    void getUserIdByToken_shouldReturnOptionalEmpty_whenAccountRecoveryTokenNotExists() {
        Optional<UUID> userIdFound = redisService.getUserIdByToken("TOKEN", RedisTokenType.ACCOUNT_RECOVERY);

        assertThat(userIdFound).isEmpty();
    }

    @Test
    void deleteToken_shouldDeleteToken_whenEmailVerificationTokenExists() {
        redisTemplate.opsForValue().set( RedisTokenType.EMAIL_VERIFICATION.buildKey("TOKEN"), UUID.randomUUID().toString());

        redisService.deleteToken("TOKEN", RedisTokenType.EMAIL_VERIFICATION);

        Object userIdFound = redisTemplate.opsForValue().get(RedisTokenType.EMAIL_VERIFICATION.buildKey("TOKEN"));

        assertThat(userIdFound).isNull();
    }

    @Test
    void deleteToken_shouldDeleteToken_whenAccountRecoveryTokenExists() {
        redisTemplate.opsForValue().set(RedisTokenType.ACCOUNT_RECOVERY.buildKey("TOKEN"), UUID.randomUUID().toString());

        redisService.deleteToken("TOKEN", RedisTokenType.ACCOUNT_RECOVERY);

        Object userIdFound = redisTemplate.opsForValue().get(RedisTokenType.ACCOUNT_RECOVERY.buildKey("TOKEN"));

        assertThat(userIdFound).isNull();
    }
}

