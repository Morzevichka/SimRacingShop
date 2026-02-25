package com.morzevichka.user_service.messaging.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCreatedEvent extends Event{
    private String email;
    private String login;

    public UserCreatedEvent(UUID eventId, String email, String login) {
        super(eventId);
        this.email = email;
        this.login = login;
    }
}
