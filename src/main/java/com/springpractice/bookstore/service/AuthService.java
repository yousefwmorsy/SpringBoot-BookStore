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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final long refreshTokenExpirationDays;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTService jwtService,
            @org.springframework.beans.factory.annotation.Value("${jwt.refresh-token-expiration-days}") long refreshTokenExpirationDays) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    @Transactional
    public void register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }
        User user = new User(request.username(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameAlreadyExistsException(request.username()));

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = createRefreshToken(user);

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponseDTO refresh(RefreshRequestDTO request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(InvalidRefreshTokenException::new);

        if (storedToken.isExpired()) {
            refreshTokenRepository.deleteByToken(storedToken.getToken());
            throw new InvalidRefreshTokenException();
        }

        String newAccessToken = jwtService.generateAccessToken(storedToken.getUser().getUsername());
        return new AuthResponseDTO(newAccessToken, storedToken.getToken());
    }

    @Transactional
    public void logout(RefreshRequestDTO request) {
        refreshTokenRepository.deleteByToken(request.refreshToken());
    }

    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS);
        refreshTokenRepository.save(new RefreshToken(token, user, expiry));
        return token;
    }
}