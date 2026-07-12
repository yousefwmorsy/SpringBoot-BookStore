package com.springpractice.bookstore.dto;

public record AuthorResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String biography
) {
}
