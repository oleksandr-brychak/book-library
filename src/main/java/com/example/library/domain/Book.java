package com.example.library.domain;

import java.util.Objects;

public record Book(String isbn, String title, String author, BookType type) {
    public Book {
        Objects.requireNonNull(isbn, "isbn");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(author, "author");
        Objects.requireNonNull(type, "type");
    }
}
