package com.morzevichka.notification_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
    private String username;
}
