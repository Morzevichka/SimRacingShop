package com.morzevichka.auth_service.exception;

import com.morzevichka.auth_service.exception.email.InvalidEmailVerificationTokenException;
import com.morzevichka.auth_service.exception.account_recovery.InvalidAccountRecoveryTokenException;
import com.morzevichka.auth_service.exception.password.PasswordMismatchException;
import com.morzevichka.auth_service.exception.user.UserException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public String userExceptionHandler(UserException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "register";
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public String passwordMismatchExceptionHandler(PasswordMismatchException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "password-reset";
    }

    @ExceptionHandler(InvalidEmailVerificationTokenException.class)
    public String invalidEmailVerificationTokenExceptionHandler(InvalidEmailVerificationTokenException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "verify-email";
    }

    @ExceptionHandler(InvalidAccountRecoveryTokenException.class)
    public String invalidAccountRecoveryTokenExceptionHandler(InvalidAccountRecoveryTokenException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("token", null);
        return "password-reset";
    }
}
