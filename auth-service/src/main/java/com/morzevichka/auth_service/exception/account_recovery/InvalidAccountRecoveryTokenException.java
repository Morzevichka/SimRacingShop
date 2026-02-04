package com.morzevichka.auth_service.exception.account_recovery;

public class InvalidAccountRecoveryTokenException extends RuntimeException {
    public InvalidAccountRecoveryTokenException(String message) {
        super(message);
    }
}
