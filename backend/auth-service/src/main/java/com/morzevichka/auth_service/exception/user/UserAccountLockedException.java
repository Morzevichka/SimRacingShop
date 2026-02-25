package com.morzevichka.auth_service.exception.user;


public class UserAccountLockedException extends UserException{
    public UserAccountLockedException() {
        super("You account is locked. Contact with our supports");
    }
}
