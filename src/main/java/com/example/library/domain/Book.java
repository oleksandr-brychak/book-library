package com.example.library.domain;

import java.util.Objects;

public record Book(String isbn, String title, String author, BookType type) {
    public Book {
        Objects.requireNonNull(isbn, "isbn must be provided");
        Objects.requireNonNull(title, "title must be provided");
        Objects.requireNonNull(author, "author must be provided");
        Objects.requireNonNull(type, "type must be provided");
    }
}
