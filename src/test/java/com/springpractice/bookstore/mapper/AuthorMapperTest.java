package com.springpractice.bookstore.mapper;

import com.springpractice.bookstore.dto.AuthorRequestDTO;
import com.springpractice.bookstore.dto.AuthorResponseDTO;
import com.springpractice.bookstore.model.Author;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorMapperTest {

    @Test
    void toResponseDTO_ShouldMapAuthorToResponseDTO() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBiography("Famous author");

        AuthorResponseDTO dto = AuthorMapper.toResponseDTO(author);

        assertEquals(1L, dto.id());
        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
        assertEquals("Famous author", dto.biography());
    }

    @Test
    void toResponseDTO_ShouldMapAuthorWithoutId() {
        Author author = new Author();
        author.setFirstName("Jane");
        author.setLastName("Smith");
        author.setBiography("Another author");

        AuthorResponseDTO dto = AuthorMapper.toResponseDTO(author);

        assertEquals(0L, dto.id());
        assertEquals("Jane", dto.firstName());
        assertEquals("Smith", dto.lastName());
        assertEquals("Another author", dto.biography());
    }

    @Test
    void toEntity_ShouldMapRequestDTOToAuthor() {
        AuthorRequestDTO dto = new AuthorRequestDTO(
                "Alice",
                "Johnson",
                "Bio of Alice"
        );

        Author author = AuthorMapper.toEntity(dto);

        assertEquals(0L, author.getId());
        assertEquals("Alice", author.getFirstName());
        assertEquals("Johnson", author.getLastName());
        assertEquals("Bio of Alice", author.getBiography());
    }

    @Test
    void toEntity_ShouldNotSetId() {
        AuthorRequestDTO dto = new AuthorRequestDTO(
                "Bob",
                "Brown",
                "Bio of Bob"
        );

        Author author = AuthorMapper.toEntity(dto);

        assertEquals(0L, author.getId());
    }
}