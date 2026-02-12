package com.morzevichka.notification_service.notification.model.email;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountRecoveryNotification implements EmailNotification {

    private final String login;
    private final String email;
    private final String token;

    @Override
    public String generateHtml() {
        return "<h1>Hello, " + login + "!</h1>" +
                "<p>Your password change link is <b>http://localhost:8081/account-recovery/reset?token=" + token + "</b></p>";
    }

    @Override
    public String getSubject() {
        return "Account Recovery";
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
