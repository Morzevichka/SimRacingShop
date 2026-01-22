package com.morzevichka.auth_service.exception.user;

public class UserEmailNotVerifiedException extends UserException{
    public UserEmailNotVerifiedException() {
        super("You must verified email before sign in");
    }
}
