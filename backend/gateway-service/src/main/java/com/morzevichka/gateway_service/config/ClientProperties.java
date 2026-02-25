package com.morzevichka.gateway_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oauth2")
public class ClientProperties {
    private String clientRegistrationId;
    private String clientId;
    private String clientSecret;
    private String issuerUri;
}
