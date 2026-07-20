package com.springpractice.bookstore.repository;

import com.springpractice.bookstore.model.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void save_ShouldPersistAuthor() {
        Author author = new Author();
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBiography("Famous author");

        Author saved = authorRepository.save(author);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
    }

    @Test
    void findById_ShouldReturnAuthor() {
        Author author = new Author();
        author.setFirstName("Jane");
        author.setLastName("Smith");
        author.setBiography("Bio");
        Author saved = authorRepository.save(author);

        Optional<Author> found = authorRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Jane", found.get().getFirstName());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotFound() {
        Optional<Author> found = authorRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllAuthors() {
        Author author1 = new Author();
        author1.setFirstName("Alice");
        author1.setLastName("A");
        author1.setBiography("Bio A");
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setFirstName("Bob");
        author2.setLastName("B");
        author2.setBiography("Bio B");
        authorRepository.save(author2);

        List<Author> authors = authorRepository.findAll();

        assertEquals(2, authors.size());
    }

    @Test
    void deleteById_ShouldRemoveAuthor() {
        Author author = new Author();
        author.setFirstName("ToDelete");
        author.setLastName("Author");
        author.setBiography("Will be deleted");
        Author saved = authorRepository.save(author);

        authorRepository.deleteById(saved.getId());

        assertFalse(authorRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void existsById_ShouldReturnTrueForExisting() {
        Author author = new Author();
        author.setFirstName("Existing");
        author.setLastName("Author");
        author.setBiography("Exists");
        Author saved = authorRepository.save(author);

        assertTrue(authorRepository.existsById(saved.getId()));
    }

    @Test
    void existsById_ShouldReturnFalseForNonExisting() {
        assertFalse(authorRepository.existsById(999L));
    }
}