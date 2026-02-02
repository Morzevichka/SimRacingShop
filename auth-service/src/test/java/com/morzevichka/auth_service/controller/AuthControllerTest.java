package com.morzevichka.auth_service.controller;

import com.morzevichka.auth_service.dto.UserRegisterDto;
import com.morzevichka.auth_service.model.User;
import com.morzevichka.auth_service.service.EmailVerificationService;
import com.morzevichka.auth_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @Test
    void shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void shouldReturnRegisterPage() throws Exception{
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void shouldRegisterUserAndRedirectToLoginPage() throws Exception {
        UserRegisterDto dto = new UserRegisterDto("test", "test@gmail.com", "password");

        User registeredUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@gmail.com")
                .build();

        when(userService.register(any())).thenReturn(registeredUser);
        doNothing().when(emailVerificationService).sendVerification(any());

        mockMvc.perform(
                post("/register")
                        .param("login", "test")
                        .param("email", "test@gmail.com")
                        .param("password", "password")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).register(any());
        verify(emailVerificationService).sendVerification(any());
    }

    @Test
    void shouldRedirectToRegisterPageWhenValidationErrorExists() throws Exception{
        UserRegisterDto dto = new UserRegisterDto("test", "test", "test");

        mockMvc.perform(
                post("/register")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
