package com.morzevichka.notification_service.notification.sender.sms;

import com.morzevichka.notification_service.notification.model.Notification;
import com.morzevichka.notification_service.notification.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsSender implements NotificationSender {

    @Override
    public void send(Notification notification) {

    }
}
