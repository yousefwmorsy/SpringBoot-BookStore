package com.springpractice.bookstore.controller;

import com.springpractice.bookstore.model.Book;
import com.springpractice.bookstore.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable long id, @RequestBody Book book) {
        Book existingBook = bookRepository.findById(id).orElse(null);
        if (existingBook == null) {
            return null;
        }
        book.setId(id);
        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable long id) {
        bookRepository.deleteById(id);
    }

    @GetMapping("/isbn/{isbn}")
    public Book getBooksByIsbn(@PathVariable String isbn) {
        return bookRepository.findByIsbn(isbn).orElse(null);
    }
}
