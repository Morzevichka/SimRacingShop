package com.morzevichka.notification_service.notification.model.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EmailVerificationNotification implements EmailNotification {

    private final String login;
    private final String email;
    private final String token;

    @Override
    public String generateHtml() {
        return "<h1>Hello, " + login + "! Verify your email</h1>" +
                "<p>Click <a href='http://localhost:8081/verify-email?token=" + token + "'>here</a> to verify.</p>";
    }

    @Override
    public String getSubject() {
        return "Email Verification";
    }

    @Override
    public String getContent() {
        return generateHtml();
    }

    @Override
    public String getRecipient() {
        return email;
    }
}
