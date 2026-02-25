package com.morzevichka.user_service.messaging.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventIdAndEventTopic(UUID eventId, String eventTopic);
}
