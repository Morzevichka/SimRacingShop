package com.morzevichka.auth_service.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class VerificationCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public String createVerificationCode() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
