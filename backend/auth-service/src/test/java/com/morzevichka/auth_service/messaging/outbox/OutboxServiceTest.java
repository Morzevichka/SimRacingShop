package com.morzevichka.auth_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morzevichka.auth_service.messaging.event.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxServiceTest {

    @InjectMocks
    private OutboxService service;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private OutboxRepository repository;

    @Test
    void publishEvent_shouldSaveEvent() {
        final String eventId = "eventId";
        final String topic = "topic";
        final Event payload = new Event(UUID.randomUUID());

        JsonNode jsonNode = mock(JsonNode.class);

        when(repository.save(any(OutboxEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.valueToTree(any(Event.class)))
                .thenReturn(jsonNode);

        service.publishEvent(eventId, topic, payload);

        verify(repository).save(any(OutboxEvent.class));
    }
}