package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.dto.AccountRecoveryDto;
import com.morzevichka.auth_service.dto.ResetPasswordDto;
import com.morzevichka.auth_service.model.token.RedisTokenType;
import com.morzevichka.auth_service.service.AccountRecoveryService;
import com.morzevichka.auth_service.service.RedisService;
import com.morzevichka.auth_service.service.TokenService;
import com.morzevichka.auth_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/account-recovery")
public class AccountRecoveryController {

    private final AccountRecoveryService accountRecoveryService;
    private final TokenService tokenService;

    private static final String ACCOUNT_RECOVERY_VIEW = "account-recovery";
    private static final String PASSWORD_RESET_VIEW = "password-reset";

    @GetMapping
    public String accountRecoveryView() {
        return ACCOUNT_RECOVERY_VIEW;
    }

    @PostMapping
    public String accountRecovery(@Valid AccountRecoveryDto dto, Model model) {
        accountRecoveryService.sendAccountRecoveryLink(dto);
        model.addAttribute("success", dto.email());
        return ACCOUNT_RECOVERY_VIEW;
    }

    @GetMapping("/reset")
    public String passwordReset(@RequestParam String token, Model model) {
        tokenService.verifyAccountRecoveryToken(token);
        model.addAttribute("token", token);
        return PASSWORD_RESET_VIEW;
    }

    @PostMapping("/reset")
    public String passwordReset(@Valid ResetPasswordDto dto, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("error", br.getAllErrors());
            return PASSWORD_RESET_VIEW;
        }

        accountRecoveryService.resetPassword(dto);
        log.info("Password was changed");
        model.addAttribute("success", "Successful reset password");
        return PASSWORD_RESET_VIEW;
    }
}
