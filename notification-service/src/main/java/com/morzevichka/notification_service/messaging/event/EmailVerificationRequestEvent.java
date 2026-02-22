package com.morzevichka.notification_service.messaging.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailVerificationRequestEvent extends Event{
    private String login;
    private String email;
    private String token;

    public EmailVerificationRequestEvent(UUID eventId, String login, String email, String token) {
        super(eventId);
        this.login = login;
        this.email = email;
        this.token = token;
    }
}
