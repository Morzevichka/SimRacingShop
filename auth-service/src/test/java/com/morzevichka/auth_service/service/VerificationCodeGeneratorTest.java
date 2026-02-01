package com.morzevichka.auth_service.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VerificationCodeGeneratorTest {

    private final VerificationCodeGenerator generator = new VerificationCodeGenerator();

    @Test
    void shouldGenerateNotNullCode() {
        String code = generator.createVerificationCode();

        assertThat(code).isNotNull().isNotBlank();
    }

    @Test
    void shouldGenerateDifferentCodes() {
        String code1 = generator.createVerificationCode();
        String code2 = generator.createVerificationCode();

        assertThat(code1).isNotEqualTo(code2);
    }

    @Test
    void shouldGenerateUrlSavedCode() {
        String code = generator.createVerificationCode();

        assertThat(code).matches("[A-Za-z0-9-_=]+");
    }
}
