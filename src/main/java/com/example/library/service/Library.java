package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.domain.BookAvailability;

import java.util.Set;

/**
 * Public service API for library operations (search, borrow, and availability).
 * Thread-safe implementation.
 */
public interface Library {
    /**
     * Adds copies of a book into the library.
     */
    void addBook(Book book, int copies);

    /**
     * Finds books where author starts with the query (case-insensitive).
     */
    Set<BookAvailability> findByAuthor(String authorQuery);

    /**
     * Finds books where title starts with the query (case-insensitive).
     */
    Set<BookAvailability>  findByTitle(String titleQuery);

    /**
     * Finds a single book by ISBN (exact match).
     */
    BookAvailability findByIsbn(String isbn);

    /**
     * Checks whether a book can be borrowed by ISBN.
     */
    boolean canBorrow(String isbn);

    /**
     * Borrows a book by ISBN if available and not a reference book.
     */
    boolean borrow(String isbn);

    /**
     * Remaining available copies for a given ISBN.
     */
    int remainingByIsbn(String isbn);

    /**
     * Remaining available copies for a given title.
     */
    int remainingByTitle(String title);

    /**
     * Remaining available copies for a given author.
     */
    int remainingByAuthor(String author);

    /**
     * Total number of borrowed copies across the library.
     */
    int totalBorrowedCount();
}
