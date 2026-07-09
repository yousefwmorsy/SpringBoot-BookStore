package com.springpractice.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record BookRequestDTO(
        @NotBlank String title,
        @NotNull Long authorId,
        @NotBlank String isbn,
        @PositiveOrZero BigDecimal price
) {
}
