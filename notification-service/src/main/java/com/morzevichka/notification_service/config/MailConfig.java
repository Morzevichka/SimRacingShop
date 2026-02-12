package com.morzevichka.notification_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {

    private final MailProperties properties;
}
