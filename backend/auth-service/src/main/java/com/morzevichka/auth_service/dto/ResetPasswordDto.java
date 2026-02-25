package com.morzevichka.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
        String token,

        @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{}|\\\\:;\"'<>,.?/~`]+$", message = "Password must include upper, lower, number and special character")
        @Size(min = 8, message = "Password must contain more than 8 characters")
        @NotBlank(message = "Password must not be blank")
        String newPassword,

        @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{}|\\\\:;\"'<>,.?/~`]+$", message = "Password must include upper, lower, number and special character")
        @Size(min = 8, message = "Password must contain more than 8 characters")
        @NotBlank(message = "Password must not be blank")
        String repeatPassword
) {}
