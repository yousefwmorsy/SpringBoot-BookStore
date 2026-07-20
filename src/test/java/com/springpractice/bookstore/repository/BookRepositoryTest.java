package com.springpractice.bookstore.repository;

import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBiography("Bio");
        author = authorRepository.save(author);
    }

    @Test
    void findByIsbn_ShouldReturnBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setPrice(new BigDecimal("19.99"));
        book.setAuthor(author);
        bookRepository.save(book);

        Optional<Book> found = bookRepository.findByIsbn("1234567890");

        assertTrue(found.isPresent());
        assertEquals("Test Book", found.get().getTitle());
        assertEquals("1234567890", found.get().getIsbn());
    }

    @Test
    void findByIsbn_ShouldReturnEmptyWhenNotFound() {
        Optional<Book> found = bookRepository.findByIsbn("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void save_ShouldPersistBook() {
        Book book = new Book();
        book.setTitle("New Book");
        book.setIsbn("9876543210");
        book.setPrice(new BigDecimal("29.99"));
        book.setAuthor(author);

        Book saved = bookRepository.save(book);

        assertNotNull(saved.getId());
        assertEquals("New Book", saved.getTitle());
        assertEquals(author.getId(), saved.getAuthor().getId());
    }

    @Test
    void findAll_ShouldReturnAllBooks() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setIsbn("1111111111");
        book1.setPrice(new BigDecimal("10.00"));
        book1.setAuthor(author);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setIsbn("2222222222");
        book2.setPrice(new BigDecimal("20.00"));
        book2.setAuthor(author);
        bookRepository.save(book2);

        List<Book> books = bookRepository.findAll();

        assertEquals(2, books.size());
    }

    @Test
    void deleteById_ShouldRemoveBook() {
        Book book = new Book();
        book.setTitle("To Delete");
        book.setIsbn("0000000000");
        book.setPrice(new BigDecimal("5.00"));
        book.setAuthor(author);
        Book saved = bookRepository.save(book);

        bookRepository.deleteById(saved.getId());

        assertFalse(bookRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void existsById_ShouldReturnTrueForExisting() {
        Book book = new Book();
        book.setTitle("Existing");
        book.setIsbn("9999999999");
        book.setPrice(new BigDecimal("15.00"));
        book.setAuthor(author);
        Book saved = bookRepository.save(book);

        assertTrue(bookRepository.existsById(saved.getId()));
    }

    @Test
    void existsById_ShouldReturnFalseForNonExisting() {
        assertFalse(bookRepository.existsById(999L));
    }
}