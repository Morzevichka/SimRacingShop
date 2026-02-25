package com.morzevichka.auth_service.exception.email;

public class InvalidEmailVerificationTokenException extends EmailException {
    public InvalidEmailVerificationTokenException(String message) {
        super(message);
    }
}
