package com.example.authservice;

import com.example.authservice.controller.AuthController;
import com.example.authservice.dto.CountersDto;
import com.example.authservice.dto.JwtAuthenticationDto;
import com.example.authservice.dto.RegisterRequest;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import com.example.authservice.security.JwtService;
import com.example.authservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
// отключаем фильтры безопасности, чтобы не ловить 401/403 в тестах
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("GET /api/auth/counters — возвращает корректные счётчики")
    void testGetCounters() throws Exception {
        CountersDto counters = new CountersDto(
                10L, // employeesCount
                5L,  // companiesCount
                20L  // vacanciesCount
        );

        when(userService.getCounters()).thenReturn(counters);

        mockMvc.perform(get("/api/auth/counters")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeesCount").value(10))
                .andExpect(jsonPath("$.companiesCount").value(5))
                .andExpect(jsonPath("$.vacanciesCount").value(20));
    }

    @Test
    @DisplayName("POST /api/auth/sign-up — успешная регистрация нового пользователя")
    void testRegisterUser() throws Exception {
        // тело запроса
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setLogin("new_user");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.ROLE_EMPLOYEE);

        // сущность пользователя, которую вернёт сервис
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setLogin("new_user");
        savedUser.setPassword("encoded_password");
        savedUser.setRole(Role.ROLE_EMPLOYEE);

        // токены, которые вернёт JwtService
        JwtAuthenticationDto tokens = new JwtAuthenticationDto();
        tokens.setToken("test-jwt-token");
        tokens.setRefreshToken("test-refresh-token");

        // моки под реальное поведение контроллера
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        when(userService.registerUserWithRole(
                eq("new_user"),
                eq("encoded_password"),
                eq(Role.ROLE_EMPLOYEE)
        )).thenReturn(savedUser);

        when(jwtService.generateAuthToken(
                eq("new_user"),
                eq("ROLE_EMPLOYEE")
        )).thenReturn(tokens);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("test-refresh-token"));
    }

    @Test
    @DisplayName("POST /api/auth/sign-in — успешный вход в систему")
    void testLoginUser() throws Exception {
        String login = "test_user";
        String rawPassword = "secret123";

        // json тела запроса
        String loginRequestJson = """
                {
                  "login": "%s",
                  "password": "%s"
                }
                """.formatted(login, rawPassword);

        // пользователь в базе
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setLogin(login);
        existingUser.setPassword("encoded_secret");
        existingUser.setRole(Role.ROLE_EMPLOYEE);

        // токены, которые вернет JwtService
        JwtAuthenticationDto tokens = new JwtAuthenticationDto();
        tokens.setToken("jwt-login-token");
        tokens.setRefreshToken("refresh-login-token");

        when(userService.findByLogin(login)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(rawPassword, "encoded_secret")).thenReturn(true);

        when(jwtService.generateAuthToken(
                eq(login),
                eq("ROLE_EMPLOYEE")
        )).thenReturn(tokens);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-login-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-login-token"));
    }
}
