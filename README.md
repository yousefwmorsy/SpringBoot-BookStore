# BookStore API

A Spring Boot REST API for a bookstore management system with authentication and authorization features.

## Table of Contents

1. [Project Overview](#project-overview)
2. [Folder Structure](#folder-structure)
3. [Features & Endpoints](#features--endpoints)
4. [Global Error Handler](#global-error-handler)
5. [Authentication Implementation](#authentication-implementation)
6. [Dependencies](#dependencies)

## Project Overview

This is a Spring Boot REST API for a bookstore management system that provides functionality to manage books and
authors, with user authentication capabilities.

## Folder Structure

```
src/
├── main/
│   ├── java/com/springpractice/bookstore/
│   │   ├── config/                 # Security configuration
│   │   ├── controller/             # REST controllers (AuthController, BookController, AuthorController)
│   │   ├── dto/                    # Data Transfer Objects for request/response bodies
│   │   ├── exceptions/             # Custom exceptions and global exception handler
│   │   ├── mapper/                 # Object mapping utilities
│   │   ├── model/                  # JPA entities (Author, Book, User, RefreshToken)
│   │   ├── repository/             # Data access layer using Spring Data JPA
│   │   └── service/                # Business logic services (AuthService, BookService, AuthorService, etc.)
│   └── resources/
│       └── application.yaml        # Application configuration
└── test/
    └── java/com/springpractice/bookstore/
```

## Features & Endpoints

### Authentication Endpoints

- **POST /api/v1/auth/register** - Register a new user
- **POST /api/v1/auth/login** - Login and get access/refresh tokens
- **POST /api/v1/auth/refresh** - Refresh access token using refresh token
- **POST /api/v1/auth/logout** - Logout (invalidate refresh token)

### Book Management Endpoints

- **GET /api/v1/books** - Get all books
- **POST /api/v1/books** - Create a new book
- **PUT /api/v1/books/{id}** - Update an existing book
- **DELETE /api/v1/books/{id}** - Delete a book
- **GET /api/v1/books/isbn/{isbn}** - Get book by ISBN

### Author Management Endpoints

- **GET /api/v1/authors** - Get all authors
- **POST /api/v1/authors** - Create a new author
- **PUT /api/v1/authors/{id}** - Update an existing author
- **DELETE /api/v1/authors/{id}** - Delete an author

## Global Error Handler

The application includes a `GlobalExceptionHandler` that handles various types of exceptions:

- **Validation errors**: Catches MethodArgumentNotValidException and returns field-specific error messages
- **Resource not found errors**: Handles ResourceNotFoundException with 404 status
- **Username already exists errors**: Handles UsernameAlreadyExistsException with 409 status
- **Invalid refresh token errors**: Handles InvalidRefreshTokenException with 401 status
- **Bad credentials errors**: Handles BadCredentialsException with 401 status

## Authentication Implementation

The application implements JWT-based authentication with the following features:

- BCrypt password encoding for user passwords
- Access tokens valid for 15 minutes
- Refresh tokens valid for 7 days
- OAuth2 resource server configuration for token validation
- Stateless session management using SessionCreationPolicy.STATELESS
- Security configuration that permits public access to auth endpoints and requires authentication for all other
  endpoints

## Dependencies

Key dependencies include:

- **Spring Boot Starter Web**: For building REST APIs
- **Spring Data JPA**: For database operations with Spring Data
- **PostgreSQL Driver**: Database connectivity for PostgreSQL
- **Spring Security**: Authentication and authorization framework
- **JWT libraries (jjwt)**: JSON Web Token implementation for authentication
- **Validation starters**: For request validation

The application uses PostgreSQL as the database, with JPA/Hibernate for ORM operations.