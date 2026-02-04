package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.InvalidEmailVerificationTokenException;
import com.morzevichka.auth_service.exception.account_recovery.InvalidAccountRecoveryTokenException;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

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

        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.get(RedisTokenType.ACCOUNT_RECOVERY.buildTokenKey(token))).thenReturn(UUID.randomUUID().toString());

        UUID userId = tokenService.verifyAccountRecoveryToken(token);

        assertThat(userId).isNotNull();
    }

    @Test
    void verifyAccountRecoveryToken_shouldThrowException_whenTokenNotExists() {
        String token = "TOKEN";

        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.get(RedisTokenType.ACCOUNT_RECOVERY.buildTokenKey(token))).thenReturn(null);

        assertThatThrownBy(() -> tokenService.verifyAccountRecoveryToken(token)).isInstanceOf(InvalidAccountRecoveryTokenException.class);
    }

    @Test
    void verifyEmailVerificationToken_shouldReturnUserId_WhenEmailVerificationTokenExists() {
        String token = "TOKEN";

        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.get(RedisTokenType.EMAIL_VERIFICATION.buildTokenKey(token))).thenReturn(UUID.randomUUID().toString());

        UUID userId = tokenService.verifyEmailVerificationToken(token);

        assertThat(userId).isNotNull();
    }

    @Test
    void verifyEmailVerificationToken_shouldThrowException_whenTokenNotExists() {
        String token = "TOKEN";

        ValueOperations<String, String> operations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(operations);
        when(operations.get(RedisTokenType.EMAIL_VERIFICATION.buildTokenKey(token))).thenReturn(null);

        assertThatThrownBy(() -> tokenService.verifyEmailVerificationToken(token)).isInstanceOf(InvalidEmailVerificationTokenException.class);
    }
}
