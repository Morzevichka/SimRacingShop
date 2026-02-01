package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.dto.UserRegisterDto;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.model.Role;
import com.morzevichka.auth_service.model.User;
import com.morzevichka.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnUserWhenEmailExists() {
        String testEmail = "test@gmail.com";
        User user = User.builder()
                .email(testEmail)
                .build();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        User foundUser = userService.getByEmail(testEmail);

        assertNotNull(foundUser);
        assertEquals(foundUser.getEmail(), user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        String testEmail = "test@gmail.com";

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getByEmail(testEmail));
    }

    @Test
    void shouldReturnUserWhenIdExists() {
        UUID uuid = UUID.randomUUID();
        User user = User.builder().id(uuid).build();

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));

        User foundUser = userService.getById(uuid);

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundById() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getById(uuid));
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        String testEmail = "test@gmail.com";

        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertTrue(userService.existsByEmail(testEmail));
        assertFalse(userService.existsByEmail("not" + testEmail));
    }

    @Test
    void shouldReturnTrueWhenLoginExists() {
        String testLogin = "test";

        when(userRepository.existsByLogin(testLogin)).thenReturn(true);

        assertThat(userService.existsByLogin(testLogin)).isTrue();
        assertThat(userService.existsByLogin("not" + testLogin)).isFalse();
    }

    @Test
    void shouldNotThrowExceptionWhenEmailVerifiedAndAccountNotLocked() {
        User user = User.builder()
                .emailVerified(true)
                .accountLocked(false)
                .build();

        assertThatCode(() -> userService.validateUserStatus(user)).doesNotThrowAnyException();
    }

    @Test
    void shouldReturnUserWhenRegistrationSuccessful() {
        UserRegisterDto dto = new UserRegisterDto("test", "test@gmail.com", "123123");

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
    void shouldVerifyEmail() {
        UUID uuid = UUID.randomUUID();
        User user = User.builder()
                .id(uuid)
                .emailVerified(false)
                .build();

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.verifyEmail(uuid);

        assertThat(user.isEmailVerified()).isTrue();

        verify(userRepository).save(user);
    }
}
