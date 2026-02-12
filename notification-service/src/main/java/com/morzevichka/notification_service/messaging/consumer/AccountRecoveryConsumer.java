package com.morzevichka.notification_service.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morzevichka.notification_service.messaging.Topic;
import com.morzevichka.notification_service.messaging.event.AccountRecoveryEvent;
import com.morzevichka.notification_service.messaging.idempotency.ProcessedEvent;
import com.morzevichka.notification_service.messaging.idempotency.ProcessedEventRepository;
import com.morzevichka.notification_service.notification.model.email.AccountRecoveryNotification;
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
public class AccountRecoveryConsumer {

    private final ProcessedEventRepository repository;
    private final EmailSender emailSender;
    private final ObjectMapper mapper;

    @Transactional
    @KafkaListener(topics = Topic.ACCOUNT_RECOVERY, groupId = "notification-service")
    public void AccountRecoveryEventHandler(
            String stringPayload,
            @Header(KafkaHeaders.RECEIVED_KEY) UUID eventId
    ) throws JsonProcessingException {
        log.info("Received account-recovery-topic with id {}", eventId);

        if (repository.existsByEventIdAndEventTopic(eventId, Topic.ACCOUNT_RECOVERY)) {
            return ;
        }

        AccountRecoveryEvent event = mapper.readValue(stringPayload, AccountRecoveryEvent.class);

        emailSender.send(
                new AccountRecoveryNotification(
                        event.getLogin(),
                        event.getEmail(),
                        event.getToken()
                )
        );

        ProcessedEvent processedEvent = ProcessedEvent.builder()
                .eventId(eventId)
                .eventTopic(Topic.ACCOUNT_RECOVERY)
                .build();

        processedEvent = repository.save(processedEvent);
        log.info("Saved account-recovery-topic with id: {} eventId: {}", processedEvent.getId(), processedEvent.getEventId());
    }
}
