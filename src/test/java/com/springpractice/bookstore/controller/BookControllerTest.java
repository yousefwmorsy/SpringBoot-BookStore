package com.springpractice.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springpractice.bookstore.dto.BookRequestDTO;
import com.springpractice.bookstore.dto.BookResponseDTO;
import com.springpractice.bookstore.exceptions.ResourceNotFoundException;
import com.springpractice.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@WithMockUser
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getBooks_ShouldReturnList() throws Exception {
        BookResponseDTO book = new BookResponseDTO(1L, "Test Book", null, "1234567890", new BigDecimal("19.99"));
        when(bookService.getBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].isbn").value("1234567890"));
    }

    @Test
    void getBooks_ShouldReturnEmptyList() throws Exception {
        when(bookService.getBooks()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createBook_ShouldReturnCreatedBook() throws Exception {
        BookRequestDTO request = new BookRequestDTO("New Book", 1L, "9876543210", new BigDecimal("29.99"));
        BookResponseDTO response = new BookResponseDTO(1L, "New Book", null, "9876543210", new BigDecimal("29.99"));

        when(bookService.createBook(any(BookRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.isbn").value("9876543210"));
    }

    @Test
    void createBook_WithInvalidBody_ShouldReturn400() throws Exception {
        String invalidJson = """
                {
                    "title": "",
                    "authorId": null,
                    "isbn": "",
                    "price": -1
                }
                """;

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook() throws Exception {
        BookRequestDTO request = new BookRequestDTO("Updated", 1L, "5555555555", new BigDecimal("39.99"));
        BookResponseDTO response = new BookResponseDTO(1L, "Updated", null, "5555555555", new BigDecimal("39.99"));

        when(bookService.updateBook(eq(1L), any(BookRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deleteBook_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getBookByIsbn_ShouldReturnBook() throws Exception {
        BookResponseDTO book = new BookResponseDTO(1L, "ISBN Book", null, "1234567890", new BigDecimal("15.00"));
        when(bookService.getBookByIsbn("1234567890")).thenReturn(book);

        mockMvc.perform(get("/api/v1/books/isbn/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("ISBN Book"))
                .andExpect(jsonPath("$.isbn").value("1234567890"));
    }

    @Test
    void getBookByIsbn_WhenNotFound_ShouldReturn404() throws Exception {
        when(bookService.getBookByIsbn("nonexistent"))
                .thenThrow(new ResourceNotFoundException("Book with ISBN (nonexistent) not found"));

        mockMvc.perform(get("/api/v1/books/isbn/nonexistent"))
                .andExpect(status().isNotFound());
    }
}