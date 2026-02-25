package com.morzevichka.notification_service.notification.sender;

public interface NotificationSender {

    void send(String recipient, String subject, String content);
}
