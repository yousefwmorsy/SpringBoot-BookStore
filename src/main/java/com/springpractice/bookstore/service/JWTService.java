package com.springpractice.bookstore.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JWTService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMinutes;

    public JWTService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-minutes}") long accessTokenExpirationMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    }

    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }
}