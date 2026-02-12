package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.messaging.event.EmailVerificationRequestEvent;
import com.morzevichka.auth_service.messaging.outbox.OutboxEvent;
import com.morzevichka.auth_service.messaging.outbox.OutboxEventStatus;
import com.morzevichka.auth_service.messaging.outbox.OutboxRepository;
import com.morzevichka.auth_service.messaging.outbox.OutboxService;
import com.morzevichka.auth_service.messaging.publisher.OutboxKafkaPublisher;
import com.morzevichka.auth_service.messaging.topic.KafkaTopic;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import com.morzevichka.auth_service.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
public class EmailVerificationService {

    private final UserService userService;
    private final TokenService tokenService;
    private final OutboxService outboxService;
    private final TokenProperties properties;

    @Transactional
    public void sendVerification(User user) {
        if (user.isEmailVerified()) return ;

        final String token = tokenService.createToken();
        log.info("Verification token: {}", token);
        tokenService.saveToken(token, user.getId(), properties.getEmailVerificationTtl(), RedisTokenType.EMAIL_VERIFICATION);

        outboxService.publishEvent(
                KafkaTopic.EMAIL_VERIFICATION,
                new EmailVerificationRequestEvent(UUID.randomUUID(), user.getLogin(), user.getEmail(), token)
        );
    }

    @Transactional
    public void resendVerification(String email) {
        try {
            User user = userService.getByEmail(email);

            if (user.isEmailVerified()) {
                return ;
            }

            sendVerification(user);
        } catch (UserNotFoundException _) {

        }
    }

    public void verify(String token) {
        UUID userId = tokenService.verifyEmailVerificationToken(token);

        userService.verifyEmail(userId);
        tokenService.deleteToken(token, RedisTokenType.EMAIL_VERIFICATION);
    }
}
