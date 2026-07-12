package com.springpractice.bookstore.service;

import com.springpractice.bookstore.dto.BookRequestDTO;
import com.springpractice.bookstore.dto.BookResponseDTO;
import com.springpractice.bookstore.exceptions.ResourceNotFoundException;
import com.springpractice.bookstore.mapper.BookMapper;
import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.model.Book;
import com.springpractice.bookstore.repository.AuthorRepository;
import com.springpractice.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public List<BookResponseDTO> getBooks() {
        return bookRepository.findAll().stream().map(BookMapper::toResponseDTO).toList();
    }

    public BookResponseDTO createBook(BookRequestDTO bookRequestDTO) {
        Author author = authorRepository.findById(bookRequestDTO.authorId()).orElse(null);
        return BookMapper.toResponseDTO(
                bookRepository.save(
                        BookMapper.toEntity(bookRequestDTO, author)
                )
        );
    }

    public BookResponseDTO updateBook(long id, BookRequestDTO bookRequestDTO) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        }
        Author author = authorRepository.findById(bookRequestDTO.authorId()).orElse(null);
        Book newBook = BookMapper.toEntity(bookRequestDTO, author);
        newBook.setId(id);
        return BookMapper.toResponseDTO(bookRepository.save(newBook));
    }

    public void deleteBook(long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        }
        bookRepository.deleteById(id);
    }

    public BookResponseDTO getBookByIsbn(String isbn) {
        return BookMapper.toResponseDTO(
                bookRepository.findByIsbn(isbn).orElseThrow(
                        () -> new ResourceNotFoundException("Book with ISBN (" + isbn + ") not found")
                )
        );
    }
}
