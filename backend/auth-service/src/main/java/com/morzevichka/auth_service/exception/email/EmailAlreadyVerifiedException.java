package com.morzevichka.auth_service.exception.email;

public class EmailAlreadyVerifiedException extends EmailException {
    public EmailAlreadyVerifiedException(String message) {
        super(message);
    }
}
