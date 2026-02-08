package com.example.library.repository;

import com.example.library.domain.Book;
import com.example.library.domain.BookType;
import com.example.library.util.LibraryUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Thread-safe in-memory repository. All public operations are synchronized.
 * Indexes are updated during {@link #addBook(Book, int)} so reads after the method returns observe updated indexes.
 */
public class InMemoryInventoryRepository implements InventoryRepository {

    private final Map<String, InventoryItem> inventoryByIsbn = new HashMap<>();
    /**
     * Exact-match index of normalized author name to ISBNs.
     */
    private final Map<String, Set<String>> authorIndex = new HashMap<>();
    /**
     * Exact-match index of normalized title to ISBNs.
     */
    private final Map<String, Set<String>> titleIndex = new HashMap<>();

    /**
     * Indexes only need updates when new inventory items are created or book metadata changes.
     */
    @Override
    public synchronized void addBook(Book book, int copies) {
        requireNonNull(book, "book must be provided");
        if (copies <= 0) {
            throw new IllegalArgumentException("copies must be positive");
        }
        String isbn = LibraryUtils.requireNonBlank(book.isbn(), "isbn");

        InventoryItem existing = inventoryByIsbn.get(isbn);
        if (existing == null) {
            InventoryItem created = InventoryItem.create(book, copies);
            inventoryByIsbn.put(isbn, created);
            indexExact(authorIndex, book.author(), isbn);
            indexExact(titleIndex, book.title(), isbn);
            return;
        }
        if (!existing.book().equals(book)) {
            throw new IllegalArgumentException("ISBN already exists with different book details");
        }
        inventoryByIsbn.put(isbn, existing.addCopies(copies));
    }

    @Override
    public synchronized Optional<InventoryItem> findByIsbn(String isbn) {
        if (LibraryUtils.isBlank(isbn)) return Optional.empty();
        return Optional.ofNullable(inventoryByIsbn.get(isbn));
    }

    /**
     * Indexes reference ISBNs, so borrow updates are reflected without reindexing.
     */
    @Override
    public synchronized boolean borrow(String isbn) {
        if (LibraryUtils.isBlank(isbn)) return false;
        InventoryItem item = inventoryByIsbn.get(isbn);
        if (item == null) {
            return false;
        }
        if (item.book().type() == BookType.REFERENCE) {
            return false;
        }
        return item.borrowOne()
                .map(updated -> {
                    inventoryByIsbn.put(isbn, updated);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public synchronized Optional<Set<InventoryItem>> findByAuthor(String author) {
        requireNonNull(author, "author must be provided");
        String normalized = LibraryUtils.normalizeLower(author);
        Set<String> isbns = authorIndex.get(normalized);
        if (isbns == null || isbns.isEmpty()) {
            return Optional.empty();
        }
        Set<InventoryItem> items = new HashSet<>();
        for (String isbn : isbns) {
            InventoryItem item = inventoryByIsbn.get(isbn);
            if (item != null) {
                items.add(item);
            }
        }
        return items.isEmpty() ? Optional.empty() : Optional.of(Set.copyOf(items));
    }

    @Override
    public synchronized Optional<Set<InventoryItem>> findByTitle(String titleQuery) {
        requireNonNull(titleQuery, "title must be provided");
        String normalized = LibraryUtils.normalizeLower(titleQuery);
        Set<String> isbns = titleIndex.get(normalized);
        if (isbns == null || isbns.isEmpty()) {
            return Optional.empty();
        }
        Set<InventoryItem> items = new HashSet<>();
        for (String isbn : isbns) {
            InventoryItem item = inventoryByIsbn.get(isbn);
            if (item != null) {
                items.add(item);
            }
        }
        return items.isEmpty() ? Optional.empty() : Optional.of(Set.copyOf(items));
    }

    @Override
    public synchronized int totalBorrowedCount() {
        return inventoryByIsbn.values().stream()
                .mapToInt(InventoryItem::borrowedCopies)
                .sum();
    }

    private void indexExact(Map<String, Set<String>> index, String value, String isbn) {
        String normalized = LibraryUtils.normalizeLower(value);
        if (LibraryUtils.isBlank(normalized)) {
            return;
        }
        index.computeIfAbsent(normalized, k -> new HashSet<>()).add(isbn);
    }
}
