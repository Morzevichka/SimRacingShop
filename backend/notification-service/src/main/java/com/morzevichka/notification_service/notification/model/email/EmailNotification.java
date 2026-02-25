package com.morzevichka.notification_service.notification.model.email;


public interface EmailNotification {
    String generateHtml();
    String getSubject();
}
