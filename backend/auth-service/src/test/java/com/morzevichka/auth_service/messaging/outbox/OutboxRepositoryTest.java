package com.morzevichka.auth_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
public class OutboxRepositoryTest {

    @Container
    private final static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private OutboxRepository repository;

    private final ObjectMapper mapper = new ObjectMapper();

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private final JsonNode payload = mapper.createObjectNode();

    private final List<OutboxEvent> events = List.of(
            new OutboxEvent("event1", "topic1", payload, OutboxEventStatus.NEW),
            new OutboxEvent("event2", "topic1", payload, OutboxEventStatus.FAILED),
            new OutboxEvent("event3", "topic1", payload, OutboxEventStatus.NEW),
            new OutboxEvent("event4", "topic2", payload, OutboxEventStatus.NEW),
            new OutboxEvent("event5", "topic2", payload, OutboxEventStatus.FAILED),
            new OutboxEvent("event6", "topic2", payload, OutboxEventStatus.NEW)
    );

    @BeforeEach
    void setUp() {
        repository.saveAll(events);
    }

    @AfterEach
    void setDown() {
        repository.deleteAll();
    }

    @Test
    void findUnprocessedEvents_shouldAllEvents_whenStatusNewFailed() {
        List<OutboxEvent> foundEvents = repository.findUnprocessedEvents(
                List.of(OutboxEventStatus.NEW, OutboxEventStatus.FAILED),
                Pageable.ofSize(100)
        );

        Set<String> foundTopics = foundEvents.stream()
                .map(OutboxEvent::getTopic)
                .collect(Collectors.toSet());

        Set<String> topics = events.stream()
                .map(OutboxEvent::getTopic)
                .collect(Collectors.toSet());

        Set<OutboxEventStatus> foundStatuses = foundEvents.stream()
                .map(OutboxEvent::getStatus)
                .collect(Collectors.toSet());

        Set<OutboxEventStatus> statuses = events.stream()
                .map(OutboxEvent::getStatus)
                .collect(Collectors.toSet());

        assertThat(foundEvents.size()).isEqualTo(events.size());
        assertThat(foundTopics).isEqualTo(topics);
        assertThat(foundStatuses).isEqualTo(statuses);
    }

    @Test
    void findUnprocessedEvents_shouldReturnSize1_whenPageSize1() {
        List<OutboxEvent> foundEvents = repository.findUnprocessedEvents(
                List.of(OutboxEventStatus.NEW, OutboxEventStatus.FAILED),
                Pageable.ofSize(1)
        );

        assertThat(foundEvents.size()).isEqualTo(1);
    }
}
