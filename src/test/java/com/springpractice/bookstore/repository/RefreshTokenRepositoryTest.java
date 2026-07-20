package com.springpractice.bookstore.repository;

import com.springpractice.bookstore.model.RefreshToken;
import com.springpractice.bookstore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("testuser", "encoded-pass"));
    }

    @Test
    void save_ShouldPersistRefreshToken() {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token, user, Instant.now().plus(7, ChronoUnit.DAYS)
        );

        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        assertNotNull(saved.getId());
        assertEquals(token, saved.getToken());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    @Test
    void findByToken_ShouldReturnToken() {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token, user, Instant.now().plus(7, ChronoUnit.DAYS)
        );
        refreshTokenRepository.save(refreshToken);

        Optional<RefreshToken> found = refreshTokenRepository.findByToken(token);

        assertTrue(found.isPresent());
        assertEquals(token, found.get().getToken());
        assertEquals(user.getId(), found.get().getUser().getId());
    }

    @Test
    void findByToken_ShouldReturnEmptyWhenNotFound() {
        Optional<RefreshToken> found = refreshTokenRepository.findByToken("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void deleteByToken_ShouldRemoveToken() {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token, user, Instant.now().plus(7, ChronoUnit.DAYS)
        );
        refreshTokenRepository.save(refreshToken);

        refreshTokenRepository.deleteByToken(token);

        assertFalse(refreshTokenRepository.findByToken(token).isPresent());
    }

    @Test
    void deleteByToken_ShouldOnlyDeleteSpecifiedToken() {
        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();

        refreshTokenRepository.save(new RefreshToken(token1, user, Instant.now().plus(7, ChronoUnit.DAYS)));
        refreshTokenRepository.save(new RefreshToken(token2, user, Instant.now().plus(7, ChronoUnit.DAYS)));

        refreshTokenRepository.deleteByToken(token1);

        assertFalse(refreshTokenRepository.findByToken(token1).isPresent());
        assertTrue(refreshTokenRepository.findByToken(token2).isPresent());
    }

    @Test
    void isExpired_ShouldBeFalseForFutureToken() {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token, user, Instant.now().plus(7, ChronoUnit.DAYS)
        );
        refreshTokenRepository.save(refreshToken);

        RefreshToken found = refreshTokenRepository.findByToken(token).get();

        assertFalse(found.isExpired());
    }

    @Test
    void isExpired_ShouldBeTrueForPastToken() {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token, user, Instant.now().minus(1, ChronoUnit.HOURS)
        );
        refreshTokenRepository.save(refreshToken);

        RefreshToken found = refreshTokenRepository.findByToken(token).get();

        assertTrue(found.isExpired());
    }
}