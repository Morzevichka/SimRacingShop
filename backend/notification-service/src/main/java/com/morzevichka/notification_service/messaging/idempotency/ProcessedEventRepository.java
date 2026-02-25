package com.morzevichka.notification_service.messaging.idempotency;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventIdAndEventTopic(UUID eventId, String eventTopic);
}
