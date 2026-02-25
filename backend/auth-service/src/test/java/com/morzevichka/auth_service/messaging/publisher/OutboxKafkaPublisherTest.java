package com.morzevichka.auth_service.messaging.publisher;

import com.morzevichka.auth_service.messaging.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class OutboxKafkaPublisherTest {

    @InjectMocks
    private OutboxKafkaPublisher publisher;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private OutboxRepository repository;

    @Test
    void publishOutboxEvents() {

    }
}
