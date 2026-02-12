package com.morzevichka.auth_service.messaging.outbox;

public enum OutboxEventStatus {
    NEW,
    PROCESSED,
    FAILED;
}
