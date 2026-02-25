package com.morzevichka.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AccountRecoveryDto(
        @Email(message = "Not match email form")
        @NotBlank(message = "Email field must not be blank")
        String email
) {}
