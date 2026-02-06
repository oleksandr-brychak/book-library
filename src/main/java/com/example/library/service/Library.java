package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.domain.BookAvailability;

import java.util.Optional;

public interface Library {
    void addBook(Book book, int copies);

    Optional<BookAvailability> findByAuthor(String authorQuery);

    Optional<BookAvailability> findByTitle(String titleQuery);

    Optional<BookAvailability> findByIsbn(String isbn);

    boolean canBorrow(String isbn);

    boolean borrow(String isbn);

    int totalBorrowedCount();
}
