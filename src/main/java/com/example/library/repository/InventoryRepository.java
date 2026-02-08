package com.example.library.repository;

import com.example.library.domain.Book;
import java.util.Optional;
import java.util.Set;

/**
 * Repository abstraction for accessing and mutating the inventory.
 * Implementations are expected to be thread-safe.
 */
public interface InventoryRepository {

    /**
     * Adds copies for a book. In indexed implementations, indexes only need updates when
     * new inventory items are created or book metadata changes.
     */
    void addBook(Book book, int copies);

    /**
     * Finds all inventory items for an exact author match (case-insensitive).
     */
    Optional<Set<InventoryItem>> findByAuthor(String authorQuery);

    /**
     * Finds all inventory items for an exact title match (case-insensitive).
     */
    Optional<Set<InventoryItem>> findByTitle(String titleQuery);

    /**
     * Finds a single inventory item by ISBN (exact match).
     */
    Optional<InventoryItem> findByIsbn(String isbn);

    /**
     * Attempts to borrow one copy by ISBN.
     */
    boolean borrow(String isbn);

    /**
     * Total number of borrowed copies across the inventory.
     */
    int totalBorrowedCount();
}
