package com.springpractice.bookstore.service;

import com.springpractice.bookstore.dto.AuthorRequestDTO;
import com.springpractice.bookstore.dto.AuthorResponseDTO;
import com.springpractice.bookstore.exceptions.ResourceNotFoundException;
import com.springpractice.bookstore.mapper.AuthorMapper;
import com.springpractice.bookstore.model.Author;
import com.springpractice.bookstore.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponseDTO> getAuthors() {
        return authorRepository.findAll().stream().map(AuthorMapper::toResponseDTO).toList();
    }

    public AuthorResponseDTO createAuthor(AuthorRequestDTO authorRequestDTO) {
        return AuthorMapper.toResponseDTO(
                authorRepository.save(
                        AuthorMapper.toEntity(authorRequestDTO)
                )
        );
    }

    public AuthorResponseDTO updateAuthor(long id, AuthorRequestDTO authorRequestDTO) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author with id " + id + " not found");
        }
        Author newAuthor = AuthorMapper.toEntity(authorRequestDTO);
        newAuthor.setId(id);
        return AuthorMapper.toResponseDTO(authorRepository.save(newAuthor));
    }

    public void deleteAuthor(long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author with id " + id + " not found");
        }
        authorRepository.deleteById(id);
    }
}
