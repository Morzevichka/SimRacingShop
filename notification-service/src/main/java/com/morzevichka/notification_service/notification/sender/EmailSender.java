package com.morzevichka.notification_service.notification.sender;

import com.morzevichka.notification_service.config.MailProperties;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender implements NotificationSender {

    private final JavaMailSender sender;
    private final MailProperties properties;

    private static final String PERSONAL_NAME = "SimRacingShop";

    @Override
    public void send(String recipient, String subject, String content) {
        try {
            MimeMessage message = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(properties.getUsername(), PERSONAL_NAME);

            sender.send(message);
            log.info("Email was send to {} from {} with context: {}", message.getAllRecipients(), message.getFrom(), message.getContent().toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }
}
