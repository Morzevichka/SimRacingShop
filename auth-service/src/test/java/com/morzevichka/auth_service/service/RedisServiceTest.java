package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.util.DataRedisTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String PREFIX = "email:verify:";

    @AfterEach
    void setDown() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void shouldSaveVerificationCode() {
        String code = "CODE";
        UUID uuid = UUID.randomUUID();

        redisService.saveVerificationCode(code, uuid, Duration.ofMinutes(1));

        String savedUUID = redisTemplate.opsForValue()
                .get(PREFIX + code);

        assertThat(savedUUID).isEqualTo(uuid.toString());
    }

    @Test
    void shouldReturnUserIdIfVerificationCodeExists() {
        UUID userId = UUID.randomUUID();

        redisTemplate.opsForValue().set(
                PREFIX + "CODE",
                userId.toString(),
                Duration.ofMinutes(1)
        );

        Optional<UUID> userIdFound = redisService.getUserIdByVerificationCode("CODE");

        assertThat(userIdFound).isPresent();
        assertThat(userIdFound.get()).isEqualTo(userId);
    }

    @Test
    void shouldReturnOptionalEmptyIfVerificationCodeNotExists() {
        Optional<UUID> userIdFound = redisService.getUserIdByVerificationCode("CODE");

        assertThat(userIdFound).isEmpty();
    }

    @Test
    void shouldDeleteVerificationCodeIfExists() {
        UUID userId = UUID.randomUUID();
        redisTemplate.opsForValue().set(PREFIX + "CODE", userId.toString());

        redisService.deleteVerificationCode("CODE");

        Object userIdFound = redisTemplate.opsForValue().get(PREFIX + "CODE");

        assertThat(userIdFound).isNull();
    }
}

