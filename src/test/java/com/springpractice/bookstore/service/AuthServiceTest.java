package com.springpractice.bookstore.service;

import com.springpractice.bookstore.dto.AuthResponseDTO;
import com.springpractice.bookstore.dto.LoginRequestDTO;
import com.springpractice.bookstore.dto.RefreshRequestDTO;
import com.springpractice.bookstore.dto.RegisterRequestDTO;
import com.springpractice.bookstore.exceptions.InvalidRefreshTokenException;
import com.springpractice.bookstore.exceptions.UsernameAlreadyExistsException;
import com.springpractice.bookstore.model.RefreshToken;
import com.springpractice.bookstore.model.User;
import com.springpractice.bookstore.repository.RefreshTokenRepository;
import com.springpractice.bookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, refreshTokenRepository,
                passwordEncoder, authenticationManager, jwtService, 7L);
    }

    @Test
    void register_ShouldCreateUser() {
        RegisterRequestDTO request = new RegisterRequestDTO("newuser", "password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");

        authService.register(request);

        verify(userRepository).existsByUsername("newuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowWhenUsernameTaken() {
        RegisterRequestDTO request = new RegisterRequestDTO("existing", "password123");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class,
                () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldAuthenticateAndReturnTokens() {
        LoginRequestDTO request = new LoginRequestDTO("user", "pass");

        User user = new User("user", "encoded-pass");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken("user")).thenReturn("access-token");

        AuthResponseDTO result = authService.login(request);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertNotNull(result.refreshToken());
        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername("user");
        verify(jwtService).generateAccessToken("user");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refresh_WithValidToken_ShouldReturnNewAccessToken() {
        User user = new User("user", "pass");
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token, user, Instant.now().plus(1, ChronoUnit.DAYS)
        );

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateAccessToken("user")).thenReturn("new-access-token");

        AuthResponseDTO result = authService.refresh(new RefreshRequestDTO(token));

        assertEquals("new-access-token", result.accessToken());
        assertEquals(token, result.refreshToken());
        verify(refreshTokenRepository).findByToken(token);
        verify(jwtService).generateAccessToken("user");
    }

    @Test
    void refresh_WithExpiredToken_ShouldThrowAndDelete() {
        User user = new User("user", "pass");
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token, user, Instant.now().minus(1, ChronoUnit.HOURS)
        );

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refresh(new RefreshRequestDTO(token)));
        verify(refreshTokenRepository).deleteByToken(token);
    }

    @Test
    void refresh_WithInvalidToken_ShouldThrow() {
        String token = "invalid-token";

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class,
                () -> authService.refresh(new RefreshRequestDTO(token)));
        verify(refreshTokenRepository, never()).deleteByToken(any());
    }

    @Test
    void logout_ShouldDeleteToken() {
        String token = "some-token";
        authService.logout(new RefreshRequestDTO(token));

        verify(refreshTokenRepository).deleteByToken(token);
    }
}