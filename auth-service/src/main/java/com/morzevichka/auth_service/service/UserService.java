package com.morzevichka.auth_service.service;

import com.morzevichka.auth_service.dto.UserRegisterDto;
import com.morzevichka.auth_service.exception.email.EmailVerificationException;
import com.morzevichka.auth_service.exception.user.UserAccountLockedException;
import com.morzevichka.auth_service.exception.user.UserAlreadyExistsException;
import com.morzevichka.auth_service.exception.user.UserEmailNotVerifiedException;
import com.morzevichka.auth_service.exception.user.UserNotFoundException;
import com.morzevichka.auth_service.kafka.KafkaSender;
import com.morzevichka.auth_service.model.Role;
import com.morzevichka.auth_service.model.User;
import com.morzevichka.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    public void validateUserStatus(User user) {
        if (!user.isEmailVerified()) {
            throw new UserEmailNotVerifiedException();
        }

        if (user.isAccountLocked()) {
            throw new UserAccountLockedException();
        }
    }

    public User register(UserRegisterDto dto) {
        if (existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException(dto.email() + " is already exists");
        }

        if (existsByLogin(dto.login())) {
            throw new UserAlreadyExistsException(dto.login() + " is already taken");
        }

        User user = User.builder()
                .login(dto.login())
                .email(dto.email())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .role(Role.ROLE_USER)
                .emailVerified(false)
                .accountLocked(false)
                .build();

        return userRepository.save(user);
    }

    public void verifyEmail(UUID userId) {
        User user = getById(userId);
        user.verifyEmail();
        userRepository.save(user);
    }
}
