package com.morzevichka.auth_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;

@Configuration
public class ClientConfig {

    @Value("${oauth2.client_id}")
    private String clientId;

    @Value("${oauth2.client_secret}")
    private String clientSecret;

    private static final String REGISTERED_CLIENT_ID = "auth-server";

    @Bean
    RegisteredClientRepository registeredClientRepository() {
//        RegisteredClient publicClient = RegisteredClient.withId(UUID.randomUUID().toString())
//                .clientId("public-web-client")
//                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
//                .authorizationGrantTypes(types -> {
//                    types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
//                    types.add(AuthorizationGrantType.REFRESH_TOKEN);
//                })
//                .redirectUri("http://localhost:8080/login/oauth2/code/" + REGISTERED_CLIENT_ID)
//                .postLogoutRedirectUri("http://localhost:8080/")
//                .scopes(scope -> {
//                    scope.add("openid");
//                    scope.add("profile");
//                })
//                .clientSettings(ClientSettings.builder()
//                        .requireAuthorizationConsent(false)
//                        .requireProofKey(true)
//                        .build()
//                )
//                .tokenSettings(TokenSettings.builder()
//                        .refreshTokenTimeToLive(Duration.ofMinutes(60))
//                        .reuseRefreshTokens(false)
//                        .build()
//                )
//                .build();


        RegisteredClient confidentClient = RegisteredClient.withId(REGISTERED_CLIENT_ID)
                .clientId(clientId)
                .clientSecret("{noop}" + clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantTypes(types -> {
                    types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    types.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .redirectUri("http://localhost:8080/login/oauth2/code/" + REGISTERED_CLIENT_ID)
                .postLogoutRedirectUri("http://localhost:8080/")
                .scopes(scope -> {
                    scope.add("openid");
                    scope.add("profile");
                })
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .requireProofKey(false)
                        .build()
                )
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .refreshTokenTimeToLive(Duration.ofMinutes(60))
                        .reuseRefreshTokens(false)
                        .build()
                )
                .build();

        return new InMemoryRegisteredClientRepository(confidentClient);
    }
}
