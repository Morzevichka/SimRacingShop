package com.morzevichka.auth_service.messaging.publisher;

import com.morzevichka.auth_service.messaging.outbox.OutboxEvent;
import com.morzevichka.auth_service.messaging.outbox.OutboxEventStatus;
import com.morzevichka.auth_service.messaging.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxService service;

    private final static int EVENT_BATCH = 50;
    private final static List<OutboxEventStatus> STATUSES = List.of(
            OutboxEventStatus.NEW,
            OutboxEventStatus.FAILED
    );
    private final static int MAX_RETRY = 5;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutboxEvents() {
        List<OutboxEvent> events = service.findUnprocessedEvents(STATUSES, EVENT_BATCH);

        if (events.isEmpty()) {
            return ;
        }

        processEvent(events);

        log.info("Unprocessed Events: {}", events.size());
        log.info("Published events {} out of {}",
                events.stream().filter(event -> event.getStatus().equals(OutboxEventStatus.PROCESSED)).count(),
                events.size()
        );

        service.saveAllEvents(events);
    }

    private void processEvent(List<OutboxEvent> events) {
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventId(), event.getPayload().toString()).get();
                successPublish(event);
                log.info("Event published, id: {} eventId: {}", event.getId(), event.getEventId());
            } catch (Exception e) {
                failedPublish(event);
                log.error("Failed to publish event, id: {} eventId: {}", event.getEventId(), e.getMessage());
            }
        }
    }

    private void successPublish(OutboxEvent event) {
        event.setStatus(OutboxEventStatus.PROCESSED);
    }

    private void failedPublish(OutboxEvent event) {
        if (event.getRetryCount() > MAX_RETRY) {
            event.setStatus(OutboxEventStatus.DEAD);
            return;
        }
        event.setStatus(OutboxEventStatus.FAILED);
        event.setRetryCount(event.getRetryCount() + 1);
        event.setAvailableAt(Instant.now().plusSeconds(event.calculateRetry()));
    }
}
