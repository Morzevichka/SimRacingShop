package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.InvalidEmailVerificationTokenException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.kafka.KafkaSender;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import com.morzevichka.auth_service.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisService redisService;
    private final KafkaSender kafkaSender;
    private final UserService userService;
    private final TokenService tokenService;

    @Value("${token.email-verification-ttl}")
    private static Long ttlMillis;

    private static final Duration EMAIL_VERIFICATION_TTL = Duration.ofMillis(ttlMillis);

    public void sendVerification(User user) {
        if (user.isEmailVerified()) {
            return ;
        }

        final String token = tokenService.createToken();
        log.info("Verification token: {}", token);
        redisService.saveToken(token, user.getId(), EMAIL_VERIFICATION_TTL, RedisTokenType.EMAIL_VERIFICATION);
        kafkaSender.send();
    }

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
        redisService.deleteToken(token, RedisTokenType.EMAIL_VERIFICATION);
    }
}
