package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.dto.AccountRecoveryDto;
import com.morzevichka.auth_service.dto.ResetPasswordDto;
import com.morzevichka.auth_service.exception.password.PasswordMismatchException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.messaging.event.AccountRecoveryEvent;
import com.morzevichka.auth_service.messaging.outbox.OutboxService;
import com.morzevichka.auth_service.messaging.topic.KafkaTopic;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import com.morzevichka.auth_service.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
public class AccountRecoveryService {

    private final UserService userService;
    private final TokenService tokenService;
    private final OutboxService outboxService;
    private final TokenProperties properties;

    @Transactional
    public void sendAccountRecoveryLink(AccountRecoveryDto dto) {
        try {
            String token = tokenService.createToken();
            log.info("Account Recovery Token: {}\nLink: http://localhost:8080/account-recovery/reset?token={}", token, token);

            User user = userService.getByEmail(dto.email());
            tokenService.saveToken(token, user.getId(), properties.getAccountRecoveryTtl(), RedisTokenType.ACCOUNT_RECOVERY);

            outboxService.publishEvent(
                    KafkaTopic.ACCOUNT_RECOVERY,
                    new AccountRecoveryEvent(UUID.randomUUID(), user.getLogin(), user.getEmail(), token)
            );
        } catch (UserNotFoundException _) {}
    }

    public void resetPassword(ResetPasswordDto dto) {
        if (!dto.newPassword().equals(dto.repeatPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        UUID userId = tokenService.verifyAccountRecoveryToken(dto.token());
        log.info("Token verified");

        userService.changePassword(userId, dto.newPassword());

        tokenService.deleteToken(dto.token(), RedisTokenType.ACCOUNT_RECOVERY);
    }
}
