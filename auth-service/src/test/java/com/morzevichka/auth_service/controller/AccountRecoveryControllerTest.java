package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.dto.AccountRecoveryDto;
import com.morzevichka.auth_service.exception.account_recovery.InvalidAccountRecoveryTokenException;
import com.morzevichka.auth_service.exception.password.PasswordMismatchException;
import com.morzevichka.auth_service.service.AccountRecoveryService;
import com.morzevichka.auth_service.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(AccountRecoveryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountRecoveryService accountRecoveryService;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void getAccountRecoveryView_shouldReturnView() throws Exception {
        mockMvc.perform(get("/account-recovery"))
                .andExpect(status().isOk())
                .andExpect(view().name("account-recovery"));
    }

    @Test
    void postAccountRecovery_shouldReturnView_WhenEmailExists() throws Exception {
        AccountRecoveryDto testDto = new AccountRecoveryDto("test@gmail.com");

        doNothing().when(accountRecoveryService).sendAccountRecoveryLink(any());

        mockMvc.perform(post("/account-recovery").param("email", testDto.email()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"))
                .andExpect(view().name("account-recovery"));

        verify(accountRecoveryService).sendAccountRecoveryLink(eq(testDto));
    }

    @Test
    void getPasswordReset_shouldReturnViewWithTokenAttribute_WhenTokenValid() throws Exception {
        final String token = "TOKEN";

        when(tokenService.verifyAccountRecoveryToken(token)).thenReturn(UUID.randomUUID());

        mockMvc.perform(get("/account-recovery/reset").queryParam("token", token))
                .andExpect(status().isOk())
                .andExpect(model().attribute("token", token))
                .andExpect(view().name("password-reset"));

        verify(tokenService).verifyAccountRecoveryToken(eq(token));
    }

    @Test
    void getPasswordReset_shouldReturnViewWithErrorAttribute_WhenTokenInvalid() throws Exception {
        final String token = "TOKEN";

        when(tokenService.verifyAccountRecoveryToken(token)).thenThrow(new InvalidAccountRecoveryTokenException("not found"));

        mockMvc.perform(get("/account-recovery/reset").queryParam("token", token))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("token", nullValue()))
                .andExpect(view().name("password-reset"));

        verify(tokenService).verifyAccountRecoveryToken(eq(token));
    }

    @Test
    void postPasswordReset_shouldReturnView_WhenPasswordMatch() throws Exception {
        final String token = "TOKEN";
        final String password = "password";

        doNothing().when(accountRecoveryService).resetPassword(any());

        mockMvc.perform(
                post("/account-recovery/reset")
                        .param("token", token)
                        .param("newPassword", password)
                        .param("repeatPassword", password)
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"))
                .andExpect(view().name("password-reset"));

        verify(accountRecoveryService).resetPassword(any());
    }

    @Test
    void postPasswordReset_shouldReturnViewWithErrorAttribute_WhenPasswordMismatch() throws Exception {
        final String token = "TOKEN";
        final String password = "password";
        final String repeatPassword = "repeatPassword";

        doThrow(new PasswordMismatchException("not match")).when(accountRecoveryService).resetPassword(any());

        mockMvc.perform(post("/account-recovery/reset")
                        .param("token", token)
                        .param("newPassword", password)
                        .param("repeatPassword", repeatPassword)
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("password-reset"));

        verify(accountRecoveryService).resetPassword(any());
    }

    @Test
    void postPasswordReset_shouldReturnWithErrorAttribute_WhenDtoInvalid() throws Exception {
        final String token = "TOKEN";
        final String password = "пароль";

        mockMvc.perform(
                post("/account-recovery/reset")
                        .param("token", token)
                        .param("newPassword", password)
                        .param("repeatPassword", password)
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("password-reset"));

        verify(accountRecoveryService, never()).resetPassword(any());
    }
}
