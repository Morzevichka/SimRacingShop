package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.service.EmailVerificationService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/verify-email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    private static final String VERIFY_EMAIL_VIEW = "verify-email";

    @GetMapping
    public String verification(@RequestParam(required = false) String token, Model model) {
        if (token != null) {
            emailVerificationService.verify(token);
            model.addAttribute("message", "Email successfully verified");
        }

        return VERIFY_EMAIL_VIEW;
    }

    @PostMapping("/resend")
    public String resend(@RequestParam @Email String email, Model model) {
        emailVerificationService.resendVerification(email);
        model.addAttribute("message", "If the email exists, verification link was sent");

        return VERIFY_EMAIL_VIEW;
    }
}
