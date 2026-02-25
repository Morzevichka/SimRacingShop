package com.morzevichka.auth_service.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private Duration accountRecoveryTtl;
    private Duration emailVerificationTtl;
}
