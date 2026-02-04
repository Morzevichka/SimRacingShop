package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.InvalidEmailVerificationTokenException;
import com.morzevichka.auth_service.exception.account_recovery.InvalidAccountRecoveryTokenException;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedisService redisService;

    private final SecureRandom secureRandom = new SecureRandom();

    public String createToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public UUID verifyAccountRecoveryToken(String token) {
        return redisService.getUserIdByToken(token, RedisTokenType.ACCOUNT_RECOVERY)
                .orElseThrow(() -> new InvalidAccountRecoveryTokenException("Link is expired or invalid"));
    }

    public UUID verifyEmailVerificationToken(String token) {
        return redisService.getUserIdByToken(token, RedisTokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new InvalidEmailVerificationTokenException("Link is expired or invalid"));
    }
}
