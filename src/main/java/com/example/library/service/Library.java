package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.domain.BookAvailability;

import java.util.Optional;
import java.util.Set;

public interface Library {
    void addBook(Book book, int copies);

    Set<BookAvailability> findByAuthor(String authorQuery);

    Set<BookAvailability>  findByTitle(String titleQuery);

    BookAvailability findByIsbn(String isbn);

    boolean canBorrow(String isbn);

    boolean borrow(String isbn);

    int totalBorrowedCount();
}
