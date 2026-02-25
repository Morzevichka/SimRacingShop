package com.morzevichka.notification_service.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountRecoveryEvent extends Event {
    private String login;
    private String email;
    private String token;

    public AccountRecoveryEvent(UUID eventId, String login, String email, String token) {
        super(eventId);
        this.login = login;
        this.email = email;
        this.token = token;
    }
}
