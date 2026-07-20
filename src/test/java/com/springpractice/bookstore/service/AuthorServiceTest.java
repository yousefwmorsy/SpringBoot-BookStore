package com.springpractice.bookstore.service;

import com.springpractice.bookstore.dto.AuthorRequestDTO;
import com.springpractice.bookstore.dto.AuthorResponseDTO;
import com.springpractice.bookstore.exceptions.ResourceNotFoundException;
import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        authorService = new AuthorService(authorRepository);
    }

    @Test
    void getAuthors_ShouldReturnList() {
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBiography("Bio");

        when(authorRepository.findAll()).thenReturn(List.of(author));

        List<AuthorResponseDTO> result = authorService.getAuthors();

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().firstName());
        verify(authorRepository).findAll();
    }

    @Test
    void getAuthors_ShouldReturnEmptyList() {
        when(authorRepository.findAll()).thenReturn(List.of());

        List<AuthorResponseDTO> result = authorService.getAuthors();

        assertTrue(result.isEmpty());
        verify(authorRepository).findAll();
    }

    @Test
    void createAuthor_ShouldCreateAndReturn() {
        AuthorRequestDTO request = new AuthorRequestDTO("Jane", "Smith", "Bio");

        Author savedAuthor = new Author();
        savedAuthor.setId(1L);
        savedAuthor.setFirstName("Jane");
        savedAuthor.setLastName("Smith");
        savedAuthor.setBiography("Bio");

        when(authorRepository.save(any(Author.class))).thenReturn(savedAuthor);

        AuthorResponseDTO result = authorService.createAuthor(request);

        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());
        assertEquals("Bio", result.biography());
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void updateAuthor_ShouldUpdateAndReturn() {
        AuthorRequestDTO request = new AuthorRequestDTO("Updated", "Author", "Updated Bio");

        Author updatedAuthor = new Author();
        updatedAuthor.setId(1L);
        updatedAuthor.setFirstName("Updated");
        updatedAuthor.setLastName("Author");
        updatedAuthor.setBiography("Updated Bio");

        when(authorRepository.existsById(1L)).thenReturn(true);
        when(authorRepository.save(any(Author.class))).thenReturn(updatedAuthor);

        AuthorResponseDTO result = authorService.updateAuthor(1L, request);

        assertEquals("Updated", result.firstName());
        assertEquals("Author", result.lastName());
        assertEquals("Updated Bio", result.biography());
        verify(authorRepository).existsById(1L);
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void updateAuthor_ShouldThrowWhenNotFound() {
        AuthorRequestDTO request = new AuthorRequestDTO("Ghost", "Writer", "?");

        when(authorRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> authorService.updateAuthor(99L, request));
        verify(authorRepository).existsById(99L);
        verify(authorRepository, never()).save(any());
    }

    @Test
    void deleteAuthor_ShouldDeleteExisting() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        authorService.deleteAuthor(1L);

        verify(authorRepository).existsById(1L);
        verify(authorRepository).deleteById(1L);
    }

    @Test
    void deleteAuthor_ShouldThrowWhenNotFound() {
        when(authorRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> authorService.deleteAuthor(99L));
        verify(authorRepository).existsById(99L);
        verify(authorRepository, never()).deleteById(any());
    }
}