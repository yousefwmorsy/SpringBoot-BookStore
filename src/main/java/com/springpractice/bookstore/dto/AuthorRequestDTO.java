package com.springpractice.bookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequestDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String biography
) {
}