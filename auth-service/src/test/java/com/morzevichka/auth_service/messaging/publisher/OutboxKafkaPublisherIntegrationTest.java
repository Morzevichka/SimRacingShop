package com.morzevichka.auth_service.messaging.publisher;

import com.morzevichka.auth_service.messaging.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
public class OutboxKafkaPublisherIntegrationTest {

    @Container
    private final static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Container
    private final static GenericContainer<?> kafka =
            new GenericContainer<>("apache/kafka4.1.1")
                    .withExposedPorts(9092);

    @Autowired
    private OutboxRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void publishOutboxEvents() {

    }
}