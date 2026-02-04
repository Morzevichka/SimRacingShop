package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.InvalidEmailVerificationTokenException;
import com.morzevichka.auth_service.exception.account_recovery.InvalidAccountRecoveryTokenException;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RedisService redisService;

    @Test
    void createToken_shouldGenerateNotNullToken() {
        String code = tokenService.createToken();

        assertThat(code).isNotNull().isNotBlank();
    }

    @Test
    void createToken_shouldGenerateDifferentTokens() {
        String code1 = tokenService.createToken();
        String code2 = tokenService.createToken();

        assertThat(code1).isNotEqualTo(code2);
    }

    @Test
    void createToken_shouldGenerateUrlSavedToken() {
        String code = tokenService.createToken();

        assertThat(code).matches("[A-Za-z0-9-_=]+");
    }

    @Test
    void verifyAccountRecoveryToken_shouldReturnUserId_whenAccountRecoveryTokenExists() {
        String token = "TOKEN";
        when(redisService.getUserIdByToken(token, RedisTokenType.ACCOUNT_RECOVERY)).thenReturn(Optional.of(UUID.randomUUID()));

        UUID userId = tokenService.verifyAccountRecoveryToken(token);

        assertThat(userId).isNotNull();

        verify(redisService).getUserIdByToken(token, RedisTokenType.ACCOUNT_RECOVERY);
    }

    @Test
    void verifyAccountRecoveryToken_shouldThrowException_whenTokenNotExists() {
        String token = "TOKEN";

        when(redisService.getUserIdByToken(token, RedisTokenType.ACCOUNT_RECOVERY)).thenThrow(new InvalidAccountRecoveryTokenException("not found"));

        assertThatThrownBy(() -> tokenService.verifyAccountRecoveryToken(token)).isInstanceOf(InvalidAccountRecoveryTokenException.class);

        verify(redisService).getUserIdByToken(token, RedisTokenType.ACCOUNT_RECOVERY);
    }

    @Test
    void verifyEmailVerificationToken_shouldReturnUserId_WhenEmailVerificationTokenExists() {
        String token = "TOKEN";
        when(redisService.getUserIdByToken(token, RedisTokenType.EMAIL_VERIFICATION)).thenReturn(Optional.of(UUID.randomUUID()));

        UUID userId = tokenService.verifyEmailVerificationToken(token);

        assertThat(userId).isNotNull();

        verify(redisService).getUserIdByToken(token, RedisTokenType.EMAIL_VERIFICATION);
    }

    @Test
    void verifyEmailVerificationToken_shouldThrowException_whenTokenNotExists() {
        String token = "TOKEN";

        when(redisService.getUserIdByToken(token, RedisTokenType.EMAIL_VERIFICATION)).thenThrow(new InvalidEmailVerificationTokenException("not found"));

        assertThatThrownBy(() -> tokenService.verifyEmailVerificationToken(token)).isInstanceOf(InvalidEmailVerificationTokenException.class);

        verify(redisService).getUserIdByToken(token, RedisTokenType.EMAIL_VERIFICATION);
    }
}
