package com.morzevichka.notification_service.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRecoveryEvent {
    private UUID eventId;
    private String login;
    private String email;
    private String token;
}
