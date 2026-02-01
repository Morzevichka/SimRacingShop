package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.dto.UserRegisterDto;
import com.morzevichka.auth_service.model.User;
import com.morzevichka.auth_service.service.EmailVerificationService;
import com.morzevichka.auth_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @GetMapping("/login")
    public String loginPage(
            HttpServletRequest request,
            Model model
    ) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object error = session.getAttribute("LOGIN_ERROR");
            if (error != null) {
                model.addAttribute("errorMessage", error);
                session.removeAttribute("LOGIN_ERROR");
            }
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @RequestBody UserRegisterDto dto,
            BindingResult br,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (br.hasErrors()) {
            model.addAttribute("errorMessage", br.getAllErrors());
            return "register";
        }

        User user = userService.register(dto);
        emailVerificationService.sendVerification(user);
        redirectAttributes.addFlashAttribute("success", "Successful registration");
        return "redirect:/login";
    }
}
