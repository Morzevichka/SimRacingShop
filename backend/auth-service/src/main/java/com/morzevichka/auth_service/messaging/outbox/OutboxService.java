package com.morzevichka.auth_service.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morzevichka.auth_service.messaging.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository repository;
    private final ObjectMapper mapper;

    public void publishEvent(String topic, Event payload) {
        publishEvent(UUID.randomUUID().toString(), topic, payload);
    }

    public void publishEvent(String eventId, String topic, Event payload) {
        OutboxEvent event = new OutboxEvent(
                eventId,
                topic,
                mapper.valueToTree(payload),
                OutboxEventStatus.NEW
        );

        event = repository.save(event);

        log.info("Event saved: {} {}", event.getId(), event.getEventId());
    }

    public List<OutboxEvent> findUnprocessedEvents(List<OutboxEventStatus> statuses, int eventBatch) {
        return repository.findUnprocessedEvents(statuses, Pageable.ofSize(eventBatch));
    }

    public void saveAllEvents(List<OutboxEvent> events) {
        repository.saveAll(events);
    }
}
