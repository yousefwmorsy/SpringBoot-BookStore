package com.springpractice.bookstore.mapper;

import com.springpractice.bookstore.dto.AuthorRequestDTO;
import com.springpractice.bookstore.dto.AuthorResponseDTO;
import com.springpractice.bookstore.model.Author;

public class AuthorMapper {

    private AuthorMapper() {
    }

    public static AuthorResponseDTO toResponseDTO(Author author) {
        return new AuthorResponseDTO(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getBiography()
        );
    }

    public static Author toEntity(AuthorRequestDTO dto) {
        Author author = new Author();
        author.setFirstName(dto.firstName());
        author.setLastName(dto.lastName());
        author.setBiography(dto.biography());
        return author;
    }
}