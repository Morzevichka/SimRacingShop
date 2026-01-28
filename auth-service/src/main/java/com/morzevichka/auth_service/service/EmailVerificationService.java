package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.EmailVerificationException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.kafka.KafkaSender;
import com.morzevichka.auth_service.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisService redisService;
    private final KafkaSender kafkaSender;
    private final UserService userService;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final Duration DURATION_VERIFICATION_CODE = Duration.ofMinutes(30);

    public void sendVerification(User user) {
        if (user.isEmailVerified()) {
            return ;
        }

        String code = createVerificationCode();
        System.out.println(code);
        redisService.saveVerificationCode(code, user.getId(), DURATION_VERIFICATION_CODE);
        kafkaSender.send();
    }

    public void resendVerification(String email) {
        try {
            User user = userService.getByEmail(email);

            if (user.isEmailVerified()) {
                return ;
            }

            sendVerification(user);
        } catch (UserNotFoundException | EmailVerificationException _) {

        }
    }

    public void verify(String code) {
        UUID userId = redisService.getUserIdByVerificationCode(code)
                .orElseThrow(() -> new EmailVerificationException("Code is expired or invalid"));

        userService.verifyEmail(userId);
        redisService.deleteVerificationCode(code);
    }

    private String createVerificationCode() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
