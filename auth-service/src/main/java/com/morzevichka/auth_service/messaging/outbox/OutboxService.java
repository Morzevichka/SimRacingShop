package com.morzevichka.auth_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.morzevichka.auth_service.messaging.event.EmailVerificationRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper mapper;

    public void publishEvent(String topic, Object payload) {
        publishEvent(UUID.randomUUID().toString(), topic, payload);
    }

    public void publishEvent(String eventId, String topic, Object payload) {
        OutboxEvent event = new OutboxEvent(
                eventId,
                topic,
                mapper.valueToTree(payload),
                OutboxEventStatus.NEW
        );

        event = outboxRepository.save(event);

        log.info("Event saved: {} {}", event.getId(), event.getEventId());
    }
}
