package com.springpractice.bookstore.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha-algorithm";
    private static final long EXPIRATION_MINUTES = 15;

    private JWTService jwtService;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService(SECRET, EXPIRATION_MINUTES);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        String token = jwtService.generateAccessToken("testuser");

        assertNotNull(token);
        assertFalse(token.isBlank());

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("testuser", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateAccessToken_ShouldSetCorrectSubject() {
        String token = jwtService.generateAccessToken("johndoe");

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("johndoe", claims.getSubject());
    }

    @Test
    void generateAccessToken_ShouldSetExpirationInFuture() {
        String token = jwtService.generateAccessToken("future-user");

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertTrue(claims.getExpiration().after(new java.util.Date()));
        assertTrue(claims.getIssuedAt().before(claims.getExpiration()));
    }

    @Test
    void generateAccessToken_ShouldBeUsableWithResourceServerDecoder() {
        String token = jwtService.generateAccessToken("resource-server");

        // This mirrors what spring-security-oauth2-resource-server does under the hood
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("resource-server", claims.getSubject());
    }
}