package com.morzevichka.notification_service.notification.model.email;

import com.morzevichka.notification_service.notification.model.Notification;
import com.morzevichka.notification_service.notification.sender.NotificationSender;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmailVerificationNotification implements Notification, EmailNotification {

    private final String login;
    private final String email;
    private final String token;
    private final NotificationSender sender;
    private final String baseUrl;

    @Override
    public void send() {
        sender.send(getRecipient(), getSubject(), getContent());
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

    @Override
    public String generateHtml() {
        return "<h1>Hello, " + login + "! Verify your email</h1>" +
                "<p>Click <a href='" + baseUrl + "/verify-email?token=" + token + "'>here</a> to verify.</p>";
    }
}
