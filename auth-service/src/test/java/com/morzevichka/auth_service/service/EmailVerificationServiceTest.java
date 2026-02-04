package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.InvalidEmailVerificationTokenException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.kafka.KafkaSender;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import com.morzevichka.auth_service.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {

    @Spy
    @InjectMocks
    private EmailVerificationService emailVerificationService;

    @Mock
    private RedisService redisService;

    @Mock
    private KafkaSender kafkaSender;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Test
    void sendVerification_shouldSaveAndSendToken_whenEmailNotVerified() {
        final String token = "TOKEN";
        final User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .build();

        when(tokenService.createToken()).thenReturn(token);

        emailVerificationService.sendVerification(testUser);

        verify(redisService).saveToken(eq(token), eq(testUser.getId()), any(Duration.class), eq(RedisTokenType.EMAIL_VERIFICATION));

        verify(kafkaSender).send();
    }

    @Test
    void sendVerification_shouldNotSaveAndSendToken_whenEmailVerified() {
        final User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .emailVerified(true)
                .build();

        emailVerificationService.sendVerification(testUser);

        verifyNoInteractions(redisService, kafkaSender, tokenService);
    }

    @Test
    void resendVerification_shouldResendToken_whenEmailNotVerified() {
        final User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .build();

        when(userService.getByEmail(testUser.getEmail())).thenReturn(testUser);

        emailVerificationService.resendVerification(testUser.getEmail());

        verify(emailVerificationService).sendVerification(eq(testUser));
    }

    @Test
    void resendVerification_shouldNotResendToken_whenEmailVerified() {
        final User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .emailVerified(true)
                .build();

        when(userService.getByEmail(testUser.getEmail())).thenReturn(testUser);

        emailVerificationService.resendVerification(testUser.getEmail());

        verify(emailVerificationService, never()).sendVerification(eq(testUser));
    }

    @Test
    void resendVerification_shouldNotResendToken_whenUserNotFound() {
        final String testEmail = "test@gmail.com";

        when(userService.getByEmail(testEmail))
                .thenThrow(new UserNotFoundException("not found"));

        emailVerificationService.resendVerification(testEmail);

        verify(emailVerificationService, never()).sendVerification(any());
    }

    @Test
    void verify_shouldVerifyEmail_whenTokenExists() {
        final String token = "TOKEN";
        final UUID userId = UUID.randomUUID();

        when(tokenService.verifyEmailVerificationToken(token)).thenReturn(userId);

        emailVerificationService.verify(token);

        verify(tokenService).verifyEmailVerificationToken(eq(token));
        verify(userService).verifyEmail(eq(userId));
        verify(redisService).deleteToken(eq(token), eq(RedisTokenType.EMAIL_VERIFICATION));
    }

    @Test
    void verify_shouldNotVerifyEmail_whenTokenNotExists() {
        final String token = "TOKEN";

        when(tokenService.verifyEmailVerificationToken(token)).thenThrow(new InvalidEmailVerificationTokenException("not found"));

        assertThatThrownBy(() -> emailVerificationService.verify(token)).isInstanceOf(InvalidEmailVerificationTokenException.class);

        verify(tokenService).verifyEmailVerificationToken(eq(token));
        verify(userService, never()).verifyEmail(any());
        verify(redisService, never()).getUserIdByToken(any(), eq(RedisTokenType.EMAIL_VERIFICATION));
    }
}