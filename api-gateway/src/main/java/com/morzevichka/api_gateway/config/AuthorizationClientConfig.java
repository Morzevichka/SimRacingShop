package com.morzevichka.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class AuthorizationClientConfig {

    @Value("${oauth2.client_id}")
    private String clientId;

    @Value("${oauth2.client_secret}")
    private String clientSecret;

    private static final String CLIENT_REGISTRATION_ID = "auth-server";

    private static final String[] WHITE_LIST = {
            "/.well-known/**",
            "/internal/**",
            "/error/**",
            "/error"
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(req -> req
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.clientRegistration());
    }

    @Bean
    OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId(CLIENT_REGISTRATION_ID)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/" + CLIENT_REGISTRATION_ID)
                .scope("openid", "profile")
                .authorizationUri("http://localhost:8081/oauth2/authorize")
                .tokenUri("http://localhost:8081/oauth2/token")
                .jwkSetUri("http://localhost:8081/oauth2/jwks")
                .clientSettings(ClientRegistration.ClientSettings.builder()
                        .requireProofKey(false)
                        .build()
                )
                .build();
    }

    @Bean
    OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
