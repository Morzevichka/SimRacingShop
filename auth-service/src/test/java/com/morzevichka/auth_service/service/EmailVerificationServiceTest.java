package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.exception.email.EmailVerificationException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.kafka.KafkaSender;
import com.morzevichka.auth_service.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private VerificationCodeGenerator verificationCodeGenerator;

    @Test
    void shouldSendVerificationCodeWhenEmailNotVerified() {
        User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .build();

        when(verificationCodeGenerator.createVerificationCode())
                .thenReturn("CODE123");

        emailVerificationService.sendVerification(testUser);

        verify(redisService).saveVerificationCode(
                eq("CODE123"),
                eq(testUser.getId()),
                any()
        );

        verify(kafkaSender).send();
    }

    @Test
    void shouldNotSendVerificationCodeWhenEmailVerified() {
        User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .emailVerified(true)
                .build();

        emailVerificationService.sendVerification(testUser);

        verifyNoInteractions(redisService, kafkaSender, verificationCodeGenerator);
    }

    @Test
    void shouldResendVerificationCodeWhenEmailNotVerified() {
        User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .build();

        when(userService.getByEmail(testUser.getEmail())).thenReturn(testUser);
        doNothing().when(emailVerificationService).sendVerification(testUser);

        emailVerificationService.resendVerification(testUser.getEmail());

        verify(emailVerificationService).sendVerification(any());
    }

    @Test
    void shouldNotResendVerificationCodeWhenEmailVerified() {
        User testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .emailVerified(true)
                .build();

        when(userService.getByEmail(testUser.getEmail())).thenReturn(testUser);

        emailVerificationService.resendVerification(testUser.getEmail());

        verify(emailVerificationService, never()).sendVerification(any());
    }

    @Test
    void shouldNotResendVerificationCodeWhenUserNotFound() {
        when(userService.getByEmail("test@gmail.com"))
                .thenThrow(new UserNotFoundException("test@gmail.com"));

        emailVerificationService.resendVerification("test@gmail.com");

        verify(emailVerificationService, never()).sendVerification(any());
    }

    @Test
    void shouldVerifyEmailWhenCodeExists() {
        String code = "CODE";

        when(redisService.getUserIdByVerificationCode(code)).thenReturn(Optional.of(UUID.randomUUID()));
        doNothing().when(userService).verifyEmail(any());
        doNothing().when(redisService).deleteVerificationCode(any());

        emailVerificationService.verify(code);

        verify(redisService).getUserIdByVerificationCode(any());
        verify(userService).verifyEmail(any());
        verify(redisService).deleteVerificationCode(any());
    }

    @Test
    void shouldNotVerifyEmailWhenCodeNotExists() {
        String code = "CODE";

        when(redisService.getUserIdByVerificationCode(code))
                .thenThrow(new EmailVerificationException("not found"));

        assertThrows(EmailVerificationException.class, () -> emailVerificationService.verify(code));

        verify(redisService).getUserIdByVerificationCode(code);
        verify(userService, never()).verifyEmail(any());
        verify(redisService, never()).deleteVerificationCode(any());
    }
}