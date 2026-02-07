package com.example.library.repository;

import com.example.library.domain.Book;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

public class InMemoryInventoryRepository implements InventoryRepository {

    private final ConcurrentMap<String, InventoryItem> inventoryByIsbn = new ConcurrentHashMap<>();
    /**
     * Exact-match index of normalized author name to inventory items.
     * Entries reference the same InventoryItem instances as {@code inventoryByIsbn},
     * so borrow updates are reflected without reindexing.
     */
    private final ConcurrentMap<String, Set<InventoryItem>> authorIndex = new ConcurrentHashMap<>();
    /**
     * Exact-match index of normalized title to inventory items.
     * Entries reference the same InventoryItem instances as {@code inventoryByIsbn},
     * so borrow updates are reflected without reindexing.
     */
    private final ConcurrentMap<String, Set<InventoryItem>> titleIndex = new ConcurrentHashMap<>();

    /**
     * Indexes only need updates when new inventory items are created or book metadata changes.
     */
    @Override
    public void addBook(Book book, int copies) {
        requireNonNull(book, "book must be provided");
        if (copies <= 0) {
            throw new IllegalArgumentException("copies must be positive");
        }
        String isbn = requireNonBlank(book.isbn(), "isbn");

        InventoryItem created = new InventoryItem(book, copies);
        InventoryItem existing = inventoryByIsbn.putIfAbsent(isbn, created);

        if (existing == null) {
            indexExact(authorIndex, book.author(), created);
            indexExact(titleIndex, book.title(), created);
            return;
        }
        if (!existing.book().equals(book)) {
            throw new IllegalArgumentException("ISBN already exists with different book details");
        }
        existing.addCopies(copies);
    }

    @Override
    public Optional<InventoryItem> findByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) return Optional.empty();
        return Optional.ofNullable(inventoryByIsbn.get(isbn));
    }

    /**
     * Indexes reference InventoryItem directly, so borrowedCopies changes are visible without reindexing.
     */
    @Override
    public boolean borrow(String isbn) {
        if (isbn == null || isbn.isBlank()) return false;
        InventoryItem item = inventoryByIsbn.get(isbn);
        return item != null && item.tryBorrowOne();
    }

    @Override
    public Optional<Set<InventoryItem>> findByAuthor(String author) {
        requireNonNull(author, "author must be provided");
        String normalized = normalize(author);
        Set<InventoryItem> exactMatches = authorIndex.get(normalized);
        if (exactMatches == null || exactMatches.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(exactMatches);
    }

    @Override
    public Optional<Set<InventoryItem>> findByTitle(String titleQuery) {
        String normalized = normalize(titleQuery);
        Set<InventoryItem> exactMatches = titleIndex.get(normalized);
        if (exactMatches == null || exactMatches.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(exactMatches);
    }

    @Override
    public List<InventoryItem> findAll() {
        return List.copyOf(new ArrayList<>(inventoryByIsbn.values()));
    }

    private synchronized void indexExact(ConcurrentMap<String, Set<InventoryItem>> index, String value, InventoryItem item) {
        String normalized = normalize(value);
        if (normalized.isBlank()) {
            return;
        }
        index.computeIfAbsent(normalized, k -> ConcurrentHashMap.newKeySet()).add(item);
    }

    private String normalize(String input) {
        return input == null ? "" : input.toLowerCase(Locale.ROOT);
    }

    private static String requireNonBlank(String s, String name) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(name + " must be provided");
        return s;
    }
}
