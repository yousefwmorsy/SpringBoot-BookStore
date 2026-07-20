package com.springpractice.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springpractice.bookstore.dto.AuthResponseDTO;
import com.springpractice.bookstore.dto.LoginRequestDTO;
import com.springpractice.bookstore.dto.RefreshRequestDTO;
import com.springpractice.bookstore.dto.RegisterRequestDTO;
import com.springpractice.bookstore.exceptions.InvalidRefreshTokenException;
import com.springpractice.bookstore.exceptions.UsernameAlreadyExistsException;
import com.springpractice.bookstore.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ShouldReturn201() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("newuser", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_WithDuplicateUsername_ShouldReturn409() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("existing", "password123");

        doThrow(new UsernameAlreadyExistsException("existing"))
                .when(authService).register(any(RegisterRequestDTO.class));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_WithShortPassword_ShouldReturn400() throws Exception {
        String invalidJson = """
                {
                    "username": "user",
                    "password": "short"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnTokens() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("user", "pass");
        AuthResponseDTO response = new AuthResponseDTO("access-token", "refresh-token");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturn401() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("wrong", "credentials");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authService).login(any(LoginRequestDTO.class));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_ShouldReturnNewTokens() throws Exception {
        RefreshRequestDTO request = new RefreshRequestDTO("valid-refresh-token");
        AuthResponseDTO response = new AuthResponseDTO("new-access-token", "valid-refresh-token");

        when(authService.refresh(any(RefreshRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("valid-refresh-token"));
    }

    @Test
    void refresh_WithInvalidToken_ShouldReturn401() throws Exception {
        RefreshRequestDTO request = new RefreshRequestDTO("invalid-token");

        doThrow(new InvalidRefreshTokenException())
                .when(authService).refresh(any(RefreshRequestDTO.class));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_ShouldReturn204() throws Exception {
        RefreshRequestDTO request = new RefreshRequestDTO("some-token");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}