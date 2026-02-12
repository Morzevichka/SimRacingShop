package com.morzevichka.auth_service.messaging.publisher;

import com.morzevichka.auth_service.messaging.outbox.OutboxEvent;
import com.morzevichka.auth_service.messaging.outbox.OutboxEventStatus;
import com.morzevichka.auth_service.messaging.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxRepository outboxRepository;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publicOutboxEvents() {
        List<OutboxEvent> events = outboxRepository.findUnprocessedEvents(
                List.of(OutboxEventStatus.NEW, OutboxEventStatus.FAILED),
                Pageable.ofSize(50)
        );

        if (events.isEmpty()) {
            return ;
        }
        log.info("Unprocessed Events: {}", events.size());

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventId(), event.getPayload().toString());
                log.info("Event published, id: {} eventId: {}", event.getId(), event.getEventId());
                event.setStatus(OutboxEventStatus.PROCESSED);
            } catch (Exception e) {
                log.error("Failed to publish event, id: {} eventId: {}", event.getEventId(), e.getMessage());
                event.setStatus(OutboxEventStatus.FAILED);
                event.setCreatedAt(Timestamp.from(Instant.now().plusSeconds(5L)));
            }
        }

        log.info("Published events {} out of {}",
                events.stream().filter(event -> event.getStatus().equals(OutboxEventStatus.PROCESSED)).count(),
                events.size()
        );

        outboxRepository.saveAll(events);
    }
}
