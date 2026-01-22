package com.morzevichka.auth_service.dto;

import jakarta.validation.constraints.*;

public record UserRegisterDto(
        @Size(min = 3, message = "Login must be longer than 3 characters")
        @NotBlank(message = "Login must not be blank")
        String login,

        @Email(message = "Not match email form")
        @NotBlank(message = "Email must not be blank")
        String email,

        @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{}|\\\\:;\"'<>,.?/~`]+$", message = "Password must include upper, lower, number and special character")
        @Size(min = 8, message = "Password must contain more than 8 characters")
        @NotBlank(message = "Password must not be blank")
        String password
) {}
