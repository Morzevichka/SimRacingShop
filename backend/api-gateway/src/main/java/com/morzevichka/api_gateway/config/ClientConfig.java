package com.morzevichka.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ClientProperties.class)
public class ClientConfig {

    private final ClientProperties properties;

    @Bean
    ClientRegistration clientRegistration() {
        return ClientRegistrations.fromIssuerLocation(properties.getIssuerUri())
                .registrationId(properties.getClientRegistrationId())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile")
                .build();
    }
}
