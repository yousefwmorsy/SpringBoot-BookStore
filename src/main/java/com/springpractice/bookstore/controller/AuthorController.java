package com.springpractice.bookstore.controller;

import com.springpractice.bookstore.exceptions.ResourceNotFoundException;
import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.repository.AuthorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }

    @PostMapping
    public Author createAuthor(@Valid @RequestBody Author author) {
        return authorRepository.save(author);
    }

    @PutMapping("/{id}")
    public Author updateAuthor(@PathVariable long id, @Valid @RequestBody Author author) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author with id " + id + " not found");
        }
        author.setId(id);
        return authorRepository.save(author);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author with id " + id + " not found");
        }
        authorRepository.deleteById(id);
    }
}
