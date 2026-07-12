package com.springpractice.bookstore.controller;

import com.springpractice.bookstore.dto.BookRequestDTO;
import com.springpractice.bookstore.dto.BookResponseDTO;
import com.springpractice.bookstore.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponseDTO> getBooks() {
        return bookService.getBooks();
    }

    @PostMapping
    public BookResponseDTO createBook(@Valid @RequestBody BookRequestDTO book) {
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    public BookResponseDTO updateBook(@PathVariable long id, @Valid @RequestBody BookRequestDTO book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable long id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/isbn/{isbn}")
    public BookResponseDTO getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn);
    }
}
