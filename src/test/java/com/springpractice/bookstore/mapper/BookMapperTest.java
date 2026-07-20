package com.springpractice.bookstore.mapper;

import com.springpractice.bookstore.dto.BookRequestDTO;
import com.springpractice.bookstore.dto.BookResponseDTO;
import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.model.Book;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    @Test
    void toResponseDTO_ShouldMapBookToResponseDTO() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBiography("Author bio");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setPrice(new BigDecimal("19.99"));
        book.setAuthor(author);

        BookResponseDTO dto = BookMapper.toResponseDTO(book);

        assertEquals(1L, dto.id());
        assertEquals("Test Book", dto.title());
        assertEquals("1234567890", dto.isbn());
        assertEquals(new BigDecimal("19.99"), dto.price());
        assertNotNull(dto.author());
        assertEquals("John", dto.author().firstName());
        assertEquals("Doe", dto.author().lastName());
    }

    @Test
    void toEntity_ShouldMapRequestDTOToBook() {
        BookRequestDTO dto = new BookRequestDTO(
                "New Book",
                1L,
                "9876543210",
                new BigDecimal("29.99")
        );

        Author author = new Author();
        author.setId(1L);
        author.setFirstName("Jane");
        author.setLastName("Smith");

        Book book = BookMapper.toEntity(dto, author);

        assertEquals(0L, book.getId());
        assertEquals("New Book", book.getTitle());
        assertEquals("9876543210", book.getIsbn());
        assertEquals(new BigDecimal("29.99"), book.getPrice());
        assertNotNull(book.getAuthor());
        assertEquals(1L, book.getAuthor().getId());
    }

    @Test
    void toEntity_ShouldAllowNullAuthor() {
        BookRequestDTO dto = new BookRequestDTO(
                "Authorless Book",
                null,
                "1111111111",
                new BigDecimal("9.99")
        );

        Book book = BookMapper.toEntity(dto, null);

        assertEquals("Authorless Book", book.getTitle());
        assertNull(book.getAuthor());
    }

    @Test
    void toResponseDTO_ShouldHandleNullAuthor() {
        Book book = new Book();
        book.setId(2L);
        book.setTitle("No Author Book");
        book.setIsbn("2222222222");
        book.setPrice(new BigDecimal("14.99"));
        book.setAuthor(null);

        BookResponseDTO dto = BookMapper.toResponseDTO(book);

        assertEquals("No Author Book", dto.title());
        assertNull(dto.author());
    }
}