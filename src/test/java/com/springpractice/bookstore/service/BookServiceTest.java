package com.springpractice.bookstore.service;

import com.springpractice.bookstore.dto.BookRequestDTO;
import com.springpractice.bookstore.dto.BookResponseDTO;
import com.springpractice.bookstore.exceptions.ResourceNotFoundException;
import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.model.Book;
import com.springpractice.bookstore.repository.AuthorRepository;
import com.springpractice.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, authorRepository);
    }

    @Test
    void getBooks_ShouldReturnListOfBooks() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setPrice(new BigDecimal("19.99"));
        book.setAuthor(author);

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookResponseDTO> result = bookService.getBooks();

        assertEquals(1, result.size());
        assertEquals("Test Book", result.getFirst().title());
        assertEquals("1234567890", result.getFirst().isbn());
        verify(bookRepository).findAll();
    }

    @Test
    void getBooks_ShouldReturnEmptyListWhenNoBooks() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<BookResponseDTO> result = bookService.getBooks();

        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }

    @Test
    void createBook_ShouldCreateAndReturnBook() {
        BookRequestDTO request = new BookRequestDTO(
                "New Book", 1L, "9876543210", new BigDecimal("29.99")
        );

        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Jane");
        author.setLastName("Smith");

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("New Book");
        savedBook.setIsbn("9876543210");
        savedBook.setPrice(new BigDecimal("29.99"));
        savedBook.setAuthor(author);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponseDTO result = bookService.createBook(request);

        assertEquals("New Book", result.title());
        assertEquals("9876543210", result.isbn());
        assertEquals(new BigDecimal("29.99"), result.price());
        assertNotNull(result.author());
        verify(authorRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_ShouldAllowNullAuthor() {
        BookRequestDTO request = new BookRequestDTO(
                "Authorless Book", null, "1111111111", new BigDecimal("9.99")
        );

        Book savedBook = new Book();
        savedBook.setId(2L);
        savedBook.setTitle("Authorless Book");
        savedBook.setIsbn("1111111111");
        savedBook.setPrice(new BigDecimal("9.99"));
        savedBook.setAuthor(null);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponseDTO result = bookService.createBook(request);

        assertEquals("Authorless Book", result.title());
        assertNull(result.author());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBook() {
        BookRequestDTO request = new BookRequestDTO(
                "Updated Book", 1L, "5555555555", new BigDecimal("39.99")
        );

        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Book");
        updatedBook.setIsbn("5555555555");
        updatedBook.setPrice(new BigDecimal("39.99"));
        updatedBook.setAuthor(author);

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        BookResponseDTO result = bookService.updateBook(1L, request);

        assertEquals("Updated Book", result.title());
        assertEquals("5555555555", result.isbn());
        verify(bookRepository).existsById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_ShouldThrowWhenBookNotFound() {
        BookRequestDTO request = new BookRequestDTO(
                "Ghost Book", 1L, "0000000000", new BigDecimal("0.00")
        );

        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.updateBook(99L, request));
        verify(bookRepository).existsById(99L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBook_ShouldDeleteExistingBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_ShouldThrowWhenBookNotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.deleteBook(99L));
        verify(bookRepository).existsById(99L);
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void getBookByIsbn_ShouldReturnBook() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("ISBN Book");
        book.setIsbn("1234567890");
        book.setPrice(new BigDecimal("15.00"));
        book.setAuthor(author);

        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(book));

        BookResponseDTO result = bookService.getBookByIsbn("1234567890");

        assertEquals("ISBN Book", result.title());
        assertEquals("1234567890", result.isbn());
        verify(bookRepository).findByIsbn("1234567890");
    }

    @Test
    void getBookByIsbn_ShouldThrowWhenNotFound() {
        when(bookRepository.findByIsbn("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.getBookByIsbn("nonexistent"));
        verify(bookRepository).findByIsbn("nonexistent");
    }
}