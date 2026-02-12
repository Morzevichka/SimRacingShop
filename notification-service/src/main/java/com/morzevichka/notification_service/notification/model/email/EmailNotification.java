package com.morzevichka.notification_service.notification.model.email;

import com.morzevichka.notification_service.notification.model.Notification;

public interface EmailNotification extends Notification {
    String generateHtml();
    String getSubject();
}
