package com.morzevichka.auth_service.config;

import com.morzevichka.auth_service.security.CustomAuthenticationFailerHandler;
import com.morzevichka.auth_service.security.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;



@Configuration
public class AuthorizationServerConfig {

    private static final String[] WHITE_LIST = {
            "/error/**",
            "/error",
            "/actuator/**",
            "/images/**",
            "/css/**",
            "/assets/**",
            "/favicon.ico",
            "/js/**",
            "/register",
            "/login",
            "/verify-email",
            "/verify-email/**"
    };

    @Bean
    @Order(1)
    SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) {
        OAuth2AuthorizationServerConfigurer oAuth2AuthorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();

        return http
                .securityMatcher(oAuth2AuthorizationServerConfigurer.getEndpointsMatcher())
                .with(oAuth2AuthorizationServerConfigurer, configurer -> configurer
                        .oidc(Customizer.withDefaults())
                )
                .authorizeHttpRequests(req -> req
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                                .defaultAuthenticationEntryPointFor(
                                        new LoginUrlAuthenticationEntryPoint("/login"),
                                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                                )
                )
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthenticationProvider customAuthenticationProvider,
            CustomAuthenticationFailerHandler customAuthenticationFailerHandler
    ) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(customAuthenticationProvider)
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .failureHandler(customAuthenticationFailerHandler)
                )
                .build();
    }


    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}