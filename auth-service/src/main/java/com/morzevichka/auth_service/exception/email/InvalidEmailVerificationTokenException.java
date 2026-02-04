package com.morzevichka.auth_service.exception.email;

public class InvalidEmailVerificationTokenException extends RuntimeException {
    public InvalidEmailVerificationTokenException(String message) {
        super(message);
    }
}
