package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.dto.AccountRecoveryDto;
import com.morzevichka.auth_service.dto.ResetPasswordDto;
import com.morzevichka.auth_service.exception.password.PasswordMismatchException;
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
public class AccountRecoveryService {

    private final UserService userService;
    private final KafkaSender kafkaSender;
    private final RedisService redisService;
    private final TokenService tokenService;

    @Value("${token.account-recovery-ttl}")
    private static Long ttlMillis;

    private static final Duration ACCOUNT_RECOVERY_TTL = Duration.ofMinutes(ttlMillis);

    public void sendAccountRecoveryLink(AccountRecoveryDto dto) {
        try {
            String token = tokenService.createToken();
            log.info("Account Recovery Token: {}\nLink: http://localhost:8080/account-recovery/reset?token={}", token, token);

            User user = userService.getByEmail(dto.email());
            redisService.saveToken(token, user.getId(), ACCOUNT_RECOVERY_TTL, RedisTokenType.ACCOUNT_RECOVERY);
            kafkaSender.send();
        } catch (UserNotFoundException _) {
        }
    }

    public void resetPassword(ResetPasswordDto dto) {
        if (!dto.newPassword().equals(dto.repeatPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        UUID userId = tokenService.verifyAccountRecoveryToken(dto.token());

        userService.changePassword(userId, dto.newPassword());

        redisService.deleteToken(dto.token(), RedisTokenType.ACCOUNT_RECOVERY);
    }
}
