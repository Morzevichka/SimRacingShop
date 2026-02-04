package com.morzevichka.notification_service.notification.sender;

import com.morzevichka.notification_service.notification.model.Notification;
import com.morzevichka.notification_service.notification.model.email.EmailNotification;

public interface NotificationSender {

    void send(Notification notification);
}
