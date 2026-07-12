package com.springpractice.bookstore.dto;

import java.math.BigDecimal;

public record BookResponseDTO(
        Long id,
        String title,
        AuthorResponseDTO author,
        String isbn,
        BigDecimal price
) {
}