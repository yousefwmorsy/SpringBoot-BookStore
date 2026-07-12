package com.springpractice.bookstore.dto;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken
) {
}
