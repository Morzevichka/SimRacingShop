package com.morzevichka.notification_service.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morzevichka.notification_service.messaging.Topic;
import com.morzevichka.notification_service.messaging.event.EmailVerificationRequestEvent;
import com.morzevichka.notification_service.messaging.idempotency.ProcessedEvent;
import com.morzevichka.notification_service.messaging.idempotency.ProcessedEventRepository;
import com.morzevichka.notification_service.notification.model.email.EmailVerificationNotification;
import com.morzevichka.notification_service.notification.sender.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationConsumer {

    private final ProcessedEventRepository repository;
    private final EmailSender emailSender;
    private final ObjectMapper mapper;

    @Transactional
    @KafkaListener(topics = Topic.EMAIL_VERIFICATION_REQUEST, groupId = "notification-service")
    public void emailVerificationRequestHandler(
            String stringPayload,
            @Header(KafkaHeaders.RECEIVED_KEY) UUID eventId
    ) throws JsonProcessingException {
        log.info("Received email-verification-request-topic with id {}", eventId);
        log.info("{}", stringPayload);

        if (repository.existsByEventIdAndEventTopic(eventId, Topic.EMAIL_VERIFICATION_REQUEST)) {
            return ;
        }

        EmailVerificationRequestEvent event = mapper.readValue(stringPayload, EmailVerificationRequestEvent.class);

        emailSender.send(
                new EmailVerificationNotification(
                        event.getLogin(),
                        event.getEmail(),
                        event.getToken()
                )
        );

        ProcessedEvent processedEvent = ProcessedEvent.builder()
                .eventId(eventId)
                .eventTopic(Topic.EMAIL_VERIFICATION_REQUEST)
                .build();

        processedEvent = repository.save(processedEvent);
        log.info("Saved email-verification-request-topic with id: {} eventId: {}", processedEvent.getId(), processedEvent.getEventId());
    }
}
