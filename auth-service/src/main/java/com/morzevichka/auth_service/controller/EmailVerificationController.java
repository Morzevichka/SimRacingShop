package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.exception.email.EmailVerificationException;
import com.morzevichka.auth_service.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @GetMapping("/verify-email")
    public String verification(
            @RequestParam String code,
            Model model
    ) {
        try {
            emailVerificationService.verify(code);
            model.addAttribute(
                    "success",
                    "Email successfully verified"
            );
        } catch (EmailVerificationException ex) {
            model.addAttribute(
                    "error",
                    "This link is expired or invalid"
            );
        }

        return "verify-email";
    }

    @GetMapping("/verify-email/resend")
    public String resendPage() {
        return "verify-email-resend";
    }

    @PostMapping("/verify-email/resend")
    public String resend(
            @RequestParam String email,
            RedirectAttributes redirectAttributes
    ) {
        emailVerificationService.resendVerification(email);
        redirectAttributes.addFlashAttribute(
                "message",
                "If the email exists, verification link was sent"
        );

        return "redirect:/login";
    }
}
