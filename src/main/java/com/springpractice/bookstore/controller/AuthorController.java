package com.springpractice.bookstore.controller;

import com.springpractice.bookstore.dto.AuthorRequestDTO;
import com.springpractice.bookstore.dto.AuthorResponseDTO;
import com.springpractice.bookstore.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public List<AuthorResponseDTO> getAuthors() {
        return authorService.getAuthors();
    }

    @PostMapping
    public AuthorResponseDTO createAuthor(@Valid @RequestBody AuthorRequestDTO author) {
        return authorService.createAuthor(author);
    }

    @PutMapping("/{id}")
    public AuthorResponseDTO updateAuthor(@PathVariable long id, @Valid @RequestBody AuthorRequestDTO author) {
        return authorService.updateAuthor(id, author);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable long id) {
        authorService.deleteAuthor(id);
    }
}
