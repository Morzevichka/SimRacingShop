package com.morzevichka.notification_service.messaging.idempotency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "processed_events")
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "event_topic", nullable = false)
    private String eventTopic;

    @CurrentTimestamp
    @Column(name = "processed_at", nullable = false)
    private Timestamp processedAt;
}
