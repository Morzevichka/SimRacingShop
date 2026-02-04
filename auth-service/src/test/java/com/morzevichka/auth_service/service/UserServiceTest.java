package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.dto.UserRegisterDto;
import com.morzevichka.auth_service.exception.user.UserAccountLockedException;
import com.morzevichka.auth_service.exception.user.UserEmailNotVerifiedException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.model.user.Role;
import com.morzevichka.auth_service.model.user.User;
import com.morzevichka.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void getByEmail_shouldReturnUser_whenEmailExists() {
        final String testEmail = "test@gmail.com";
        final User user = User.builder()
                .email(testEmail)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User foundUser = userService.getByEmail(testEmail);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void getByEmail_shouldThrowException_whenEmailNotExists() {
        final String testEmail = "test@gmail.com";

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByEmail(testEmail)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getById_shouldReturnUser_whenIdExists() {
        final UUID uuid = UUID.randomUUID();
        final User user = User.builder().id(uuid).build();

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));

        User foundUser = userService.getById(uuid);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void getById_shouldThrowException_whenIdNotExists() {
        final UUID uuid = UUID.randomUUID();
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(uuid)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        final String testEmail = "test@gmail.com";

        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThat(userService.existsByEmail(testEmail)).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        final String testEmail = "test@gmail.com";

        when(userRepository.existsByEmail(testEmail)).thenReturn(false);

        assertThat(userService.existsByEmail(testEmail)).isFalse();
    }

    @Test
    void existsByLogin_shouldReturnTrue_whenLoginExists() {
        String testLogin = "test";

        when(userRepository.existsByLogin(testLogin)).thenReturn(true);

        assertThat(userService.existsByLogin(testLogin)).isTrue();
    }

    @Test
    void existsByLogin_shouldReturnFalse_whenLoginNotExists() {
        String testLogin = "test";

        when(userRepository.existsByLogin(testLogin)).thenReturn(false);

        assertThat(userService.existsByLogin(testLogin)).isFalse();
    }

    @Test
    void validateUserStatus_shouldValidateUser_whenEmailVerifiedAndAccountNotLocked() {
        final User user = User.builder()
                .emailVerified(true)
                .accountLocked(false)
                .build();

        assertThatCode(() -> userService.validateUserStatus(user)).doesNotThrowAnyException();
    }

    @Test
    void validateUserStatus_shouldThrowUserEmailNotVerifiedException_whenEmailNotVerified() {
        final User user = User.builder()
                .emailVerified(false)
                .accountLocked(false)
                .build();

        assertThatThrownBy(() -> userService.validateUserStatus(user)).isInstanceOf(UserEmailNotVerifiedException.class);
    }

    @Test
    void validateUserStatus_shouldThrowUserAccountLockedException_whenAccountLocked() {
        final User user = User.builder()
                .emailVerified(true)
                .accountLocked(true)
                .build();

        assertThatThrownBy(() -> userService.validateUserStatus(user)).isInstanceOf(UserAccountLockedException.class);
    }

    @Test
    void register_shouldRegisterUser() {
        final UserRegisterDto dto = new UserRegisterDto("test", "test@gmail.com", "123123");

        when(passwordEncoder.encode(dto.password())).thenReturn("hashed");
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.existsByLogin(dto.login())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.register(dto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo(dto.login());
        assertThat(savedUser.getEmail()).isEqualTo(dto.email());
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashed");
        assertThat(savedUser.isEmailVerified()).isFalse();
        assertThat(savedUser.isAccountLocked()).isFalse();
        assertThat(savedUser.getRole()).isEqualByComparingTo(Role.ROLE_USER);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void verifyEmail_shouldVerifyEmail_WhenUserExists() {
        final UUID uuid = UUID.randomUUID();
        final User user = User.builder()
                .id(uuid)
                .emailVerified(false)
                .build();

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));

        userService.verifyEmail(uuid);

        assertThat(user.isEmailVerified()).isTrue();

        verify(userRepository).save(user);
    }

    @Test
    void verifyEmail_shouldThrowException_whenUserNotExists() {
        when(userRepository.findById(any())).thenThrow(UserNotFoundException.class);

        assertThatThrownBy(() -> userService.verifyEmail(UUID.randomUUID())).isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldChangPassword_whenUserExists() {
        final String newPassword = "newPassword";
        final User testUser = User.builder().id(UUID.randomUUID()).passwordHash("password").build();

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        userService.changePassword(testUser.getId(), newPassword);

        assertThat(testUser.getPasswordHash()).isEqualTo(newPassword);

        verify(userRepository).save(any());
    }

    @Test
    void changePassword_shouldThrowException_whenUserNotExists() {
        final String newPassword = "newPassword";
        final User testUser = User.builder().id(UUID.randomUUID()).passwordHash("password").build();

        when(userRepository.findById(testUser.getId())).thenThrow(UserNotFoundException.class);

        assertThatThrownBy(() -> userService.changePassword(testUser.getId(), newPassword)).isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}
