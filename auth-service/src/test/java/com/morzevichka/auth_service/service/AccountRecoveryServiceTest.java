package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.dto.AccountRecoveryDto;
import com.morzevichka.auth_service.dto.ResetPasswordDto;
import com.morzevichka.auth_service.exception.account_recovery.InvalidAccountRecoveryTokenException;
import com.morzevichka.auth_service.exception.password.PasswordMismatchException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.kafka.KafkaSender;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import com.morzevichka.auth_service.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountRecoveryServiceTest {

    @InjectMocks
    private AccountRecoveryService accountRecoveryService;

    @Mock
    private UserService userService;

    @Mock
    private KafkaSender kafkaSender;

    @Mock
    private RedisService redisService;

    @Mock
    private TokenService tokenService;

    @Test
    void sendAccountRecoveryLink_shouldCreateTokenAndSendMessage_whenUserExists() {
        final AccountRecoveryDto testDto = new AccountRecoveryDto("test@gmail.com");
        final User testUser = User.builder()
                .id(UUID.randomUUID())
                .email(testDto.email())
                .build();
        final String token = "TOKEN";

        when(tokenService.createToken()).thenReturn(token);
        when(userService.getByEmail(testDto.email())).thenReturn(testUser);

        accountRecoveryService.sendAccountRecoveryLink(testDto);

        InOrder inOrder = inOrder(tokenService, userService, redisService, kafkaSender);

        inOrder.verify(tokenService).createToken();
        inOrder.verify(userService).getByEmail(eq(testDto.email()));
        inOrder.verify(redisService).saveToken(eq(token), eq(testUser.getId()), any(Duration.class), eq(RedisTokenType.ACCOUNT_RECOVERY));
        inOrder.verify(kafkaSender).send();
    }

    @Test
    void sendAccountRecoveryLink_shouldDoNothing_whenUserNotExists() {
        AccountRecoveryDto testDto = new AccountRecoveryDto("test@gmail.com");

        when(tokenService.createToken()).thenReturn("TOKEN");
        when(userService.getByEmail(testDto.email())).thenThrow(new UserNotFoundException("not found"));

        accountRecoveryService.sendAccountRecoveryLink(testDto);

        verify(redisService, never()).saveToken(any(), any(), any(), any());
        verify(kafkaSender, never()).send();
    }

    @Test
    void resetPassword_shouldResetPasswordAndDeleteToken_whenPasswordsMatch() {
        final String token = "TOKEN";
        final String password = "password";
        final UUID userId = UUID.randomUUID();
        final ResetPasswordDto testDto = new ResetPasswordDto(token, password, password);

        when(tokenService.verifyAccountRecoveryToken(testDto.token())).thenReturn(userId);

        accountRecoveryService.resetPassword(testDto);

        InOrder inOrder = inOrder(tokenService, userService, redisService);

        inOrder.verify(tokenService).verifyAccountRecoveryToken(eq(testDto.token()));
        inOrder.verify(userService).changePassword(eq(userId), eq(testDto.newPassword()));
        inOrder.verify(redisService).deleteToken(eq(testDto.token()), eq(RedisTokenType.ACCOUNT_RECOVERY));
    }

    @Test
    void resetPassword_shouldThrowException_whenPasswordsMismatch() {
        final String token = "TOKEN";
        final String password = "password";
        final String otherPassword = "notPassword";
        final ResetPasswordDto testDto = new ResetPasswordDto(token, password, otherPassword);

        assertThatThrownBy(() -> accountRecoveryService.resetPassword(testDto)).isInstanceOf(PasswordMismatchException.class);

        verify(userService, never()).changePassword(any(), any());
        verify(redisService, never()).deleteToken(any(), any());
    }

    @Test
    void resetPassword_shouldThrowException_whenTokenNotExists() {
        final String token = "TOKEN";
        final String password = "password";
        final ResetPasswordDto testDto = new ResetPasswordDto(token, password, password);

        when(tokenService.verifyAccountRecoveryToken(token)).thenThrow(new InvalidAccountRecoveryTokenException("not found"));

        assertThatThrownBy(() -> accountRecoveryService.resetPassword(testDto)).isInstanceOf(InvalidAccountRecoveryTokenException.class);

        verify(userService, never()).changePassword(any(), any());
        verify(redisService, never()).deleteToken(any(), any());
    }
}
