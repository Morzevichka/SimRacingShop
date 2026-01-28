package com.morzevichka.auth_service.exception.user;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String email) {
        super("User with " + email + " was not found");
    }
}
