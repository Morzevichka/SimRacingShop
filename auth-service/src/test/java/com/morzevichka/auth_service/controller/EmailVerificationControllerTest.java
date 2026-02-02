package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.exception.email.EmailVerificationException;
import com.morzevichka.auth_service.service.EmailVerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailVerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmailVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @Test
    void shouldReturnVerifyEmailPageWithSuccessAttributeWhenVerificationSuccessful() throws Exception {
        doNothing().when(emailVerificationService).verify(any());

        mockMvc.perform(
                get("/verify-email")
                        .param("code", "code")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("verify-email"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void shouldReturnVerifyEmailPageWithErrorAttributeWhenVerificationNotSuccess() throws Exception {
        doThrow(EmailVerificationException.class).when(emailVerificationService).verify(any());

        mockMvc.perform(
                get("/verify-email")
                        .param("code", "code")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("verify-email"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void shouldRedirectVerificationPageWhenResendCalled() throws Exception {
        doNothing().when(emailVerificationService).resendVerification(any());

        mockMvc.perform(
                post("/verify-email/resend")
                        .param("email", "test@gmail.com")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/verify-email"))
                .andExpect(flash().attributeExists("message"));
    }
}
