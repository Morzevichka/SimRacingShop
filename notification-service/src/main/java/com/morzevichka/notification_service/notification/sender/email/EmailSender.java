package com.morzevichka.notification_service.notification.sender.email;

import com.morzevichka.notification_service.config.MailProperties;
import com.morzevichka.notification_service.notification.model.Notification;
import com.morzevichka.notification_service.notification.model.email.EmailNotification;
import com.morzevichka.notification_service.notification.sender.NotificationSender;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender implements NotificationSender {

    private final JavaMailSender sender;
    private final MailProperties properties;

    @Override
    public void send(Notification notification) {
        if (!(notification instanceof EmailNotification emailNotification)) {
            throw new IllegalArgumentException("Notification must be EmailNotification");
        }

        try {
            MimeMessage message = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(emailNotification.getRecipient());
            helper.setSubject(emailNotification.getSubject());
            helper.setText(emailNotification.getContent(), true);
            helper.setFrom(properties.getUsername(), "SimRacingShop");

            sender.send(message);
            log.info("Email was send to {} from {} with context: {}", message.getAllRecipients(), message.getFrom(), message.getContent());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }
}
