package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.dto.UserRegisterDto;
import com.morzevichka.auth_service.model.user.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    private static final String LOGIN_VIEW = "login";
    public static final String REGISTER_VIEW = "register";

    @GetMapping("/login")
    public String loginView(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object error = session.getAttribute("LOGIN_ERROR");
            if (error != null) {
                model.addAttribute("error", error);
                session.removeAttribute("LOGIN_ERROR");
            }
        }

        return LOGIN_VIEW;
    }

    @GetMapping("/register")
    public String registerView() {
        return REGISTER_VIEW;
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterDto dto, BindingResult br, Model model, RedirectAttributes redirectAttributes) {
        if (br.hasErrors()) {
            model.addAttribute("error", br.getAllErrors());
            return REGISTER_VIEW;
        }

        User user = userService.register(dto);
        emailVerificationService.sendVerification(user);
        redirectAttributes.addFlashAttribute("success", "Successful registration");
        return "redirect:/" + LOGIN_VIEW;
    }
}