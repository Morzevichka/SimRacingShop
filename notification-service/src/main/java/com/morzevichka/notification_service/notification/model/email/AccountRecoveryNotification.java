package com.morzevichka.notification_service.notification.model.email;

import com.morzevichka.notification_service.notification.model.Notification;
import com.morzevichka.notification_service.notification.sender.NotificationSender;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountRecoveryNotification implements Notification, EmailNotification {

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

    @Override
    public String generateHtml() {
        return "<h1>Hello, " + login + "!</h1>" +
                "<p>Your password change link is <b>" + baseUrl +"/account-recovery/reset?token=" + token + "</b></p>";
    }
}
