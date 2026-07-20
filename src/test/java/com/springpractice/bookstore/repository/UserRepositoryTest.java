package com.springpractice.bookstore.repository;

import com.springpractice.bookstore.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_ShouldPersistUser() {
        User user = new User("testuser", "encoded-password");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
        assertEquals("encoded-password", saved.getPassword());
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        User user = new User("johndoe", "encoded-pass");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("johndoe");

        assertTrue(found.isPresent());
        assertEquals("johndoe", found.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmptyWhenNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void existsByUsername_ShouldReturnTrueForExisting() {
        User user = new User("existinguser", "encoded-pass");
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("existinguser"));
    }

    @Test
    void existsByUsername_ShouldReturnFalseForNonExisting() {
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void existsByUsername_ShouldBeCaseSensitive() {
        User user = new User("CaseSensitive", "encoded-pass");
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("CaseSensitive"));
        assertFalse(userRepository.existsByUsername("casesensitive"));
    }
}