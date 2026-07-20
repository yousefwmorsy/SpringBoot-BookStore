package com.springpractice.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springpractice.bookstore.dto.AuthorRequestDTO;
import com.springpractice.bookstore.dto.AuthorResponseDTO;
import com.springpractice.bookstore.exceptions.ResourceNotFoundException;
import com.springpractice.bookstore.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@WithMockUser
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAuthors_ShouldReturnList() throws Exception {
        AuthorResponseDTO author = new AuthorResponseDTO(1L, "John", "Doe", "Bio");
        when(authorService.getAuthors()).thenReturn(List.of(author));

        mockMvc.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));
    }

    @Test
    void getAuthors_ShouldReturnEmptyList() throws Exception {
        when(authorService.getAuthors()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createAuthor_ShouldReturnCreatedAuthor() throws Exception {
        AuthorRequestDTO request = new AuthorRequestDTO("Jane", "Smith", "Bio of Jane");
        AuthorResponseDTO response = new AuthorResponseDTO(1L, "Jane", "Smith", "Bio of Jane");

        when(authorService.createAuthor(any(AuthorRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void createAuthor_WithInvalidBody_ShouldReturn400() throws Exception {
        String invalidJson = """
                {
                    "firstName": "",
                    "lastName": "",
                    "biography": ""
                }
                """;

        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor() throws Exception {
        AuthorRequestDTO request = new AuthorRequestDTO("Updated", "Author", "Updated Bio");
        AuthorResponseDTO response = new AuthorResponseDTO(1L, "Updated", "Author", "Updated Bio");

        when(authorService.updateAuthor(eq(1L), any(AuthorRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void deleteAuthor_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/authors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthor_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Author with id 99 not found"))
                .when(authorService).deleteAuthor(99L);

        mockMvc.perform(delete("/api/v1/authors/99"))
                .andExpect(status().isNotFound());
    }
}