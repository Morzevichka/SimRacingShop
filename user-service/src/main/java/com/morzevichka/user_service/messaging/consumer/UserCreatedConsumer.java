package com.morzevichka.user_service.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morzevichka.user_service.messaging.KafkaTopic;
import com.morzevichka.user_service.messaging.event.UserCreatedEvent;
import com.morzevichka.user_service.messaging.idempotency.ProcessedEvent;
import com.morzevichka.user_service.messaging.idempotency.ProcessedEventRepository;
import com.morzevichka.user_service.service.UserService;
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
public class UserCreatedConsumer {

    private final ProcessedEventRepository repository;
    private final UserService userService;
    private final ObjectMapper mapper;

    @Transactional
    @KafkaListener(topics = KafkaTopic.USER_CREATED, groupId = "user-service")
    public void userCreatedEventHandler(
            String payload,
            @Header(KafkaHeaders.RECEIVED_KEY)UUID eventId
    ) throws JsonProcessingException {
        log.info("Received user-created-topic with id {}", eventId);

        if (repository.existsByEventIdAndEventTopic(eventId, KafkaTopic.USER_CREATED)) {
            return ;
        }

        UserCreatedEvent event = mapper.readValue(payload, UserCreatedEvent.class);

        userService.addUser(event.getEmail(), event.getLogin());

        ProcessedEvent processedEvent = ProcessedEvent.builder()
                .eventId(eventId)
                .eventTopic(KafkaTopic.USER_CREATED)
                .build();

        processedEvent = repository.save(processedEvent);
        log.info("Saved user-created-topic with id: {} eventId: {}", processedEvent.getId(), processedEvent.getEventId());
    }
}
