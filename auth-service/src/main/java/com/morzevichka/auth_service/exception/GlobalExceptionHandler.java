package com.morzevichka.auth_service.exception;

import com.morzevichka.auth_service.exception.user.UserException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public String handlerUserException(UserException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "register";
    }

//    @ExceptionHandler(ValidationException.class)
//    public String handlerValidationError(ValidationException ex, Model model) {
//        model.addAttribute("errorMessage", ex.getMessage());
//    }
}
