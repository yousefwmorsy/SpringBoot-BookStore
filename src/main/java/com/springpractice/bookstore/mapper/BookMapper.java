package com.springpractice.bookstore.mapper;

import com.springpractice.bookstore.dto.BookRequestDTO;
import com.springpractice.bookstore.dto.BookResponseDTO;
import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.model.Book;

public class BookMapper {

    private BookMapper() {
    }

    public static BookResponseDTO toResponseDTO(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                AuthorMapper.toResponseDTO(book.getAuthor()),
                book.getIsbn(),
                book.getPrice()
        );
    }

    public static Book toEntity(BookRequestDTO dto, Author author) {
        Book book = new Book();
        book.setTitle(dto.title());
        book.setIsbn(dto.isbn());
        book.setPrice(dto.price());
        book.setAuthor(author);
        return book;
    }
}