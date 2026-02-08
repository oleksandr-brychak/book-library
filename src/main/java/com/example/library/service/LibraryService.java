package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.domain.BookAvailability;
import com.example.library.domain.BookType;
import com.example.library.repository.InventoryItem;
import com.example.library.repository.InventoryRepository;
import com.example.library.util.LibraryUtils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stateless service layer; thread-safe.
 */
public class LibraryService implements Library {
    private final InventoryRepository repository;

    public LibraryService(InventoryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    public void addBook(Book book, int copies) {
        Objects.requireNonNull(book, "book");
        if (copies <= 0) {
            throw new IllegalArgumentException("copies must be positive");
        }
        repository.addBook(book, copies);
    }

    @Override
    public Set<BookAvailability> findByAuthor(String author) {
        if (LibraryUtils.isBlank(author)) return Set.of();

        return repository.findByAuthor(author)
                .map(items -> items.stream()
                        .map(InventoryItem::toAvailability)
                        .collect(Collectors.toUnmodifiableSet()))
                .orElse(Set.of());
    }

    @Override
    public Set<BookAvailability> findByTitle(String title) {
        if (LibraryUtils.isBlank(title)) return Set.of();

        return repository.findByTitle(title)
                .map(items -> items.stream()
                        .map(InventoryItem::toAvailability)
                        .collect(Collectors.toUnmodifiableSet()))
                .orElse(Set.of());
    }

    @Override
    public BookAvailability findByIsbn(String isbn) {
        if (LibraryUtils.isBlank(isbn)) {
            throw new IllegalArgumentException("isbn must be provided");
        }
        return repository.findByIsbn(isbn)
                .map(InventoryItem::toAvailability)
                .orElseThrow(() -> new NoSuchElementException("No book with isbn: " + isbn));
    }

    @Override
    public boolean canBorrow(String isbn) {
        if (LibraryUtils.isBlank(isbn)) return false;

        return repository.findByIsbn(isbn)
                .filter(item -> item.book().type() != BookType.REFERENCE)
                .filter(item -> item.availableCopies() > 0)
                .isPresent();
    }

    @Override
    public boolean borrow(String isbn) {
        if (LibraryUtils.isBlank(isbn)) return false;
        return repository.borrow(isbn);
    }

    @Override
    public int totalBorrowedCount() {
        return repository.totalBorrowedCount();
    }

    @Override
    public int remainingByIsbn(String isbn) {
        if (LibraryUtils.isBlank(isbn)) {
            return 0;
        }
        return repository.findByIsbn(isbn)
                .map(InventoryItem::availableCopies)
                .orElse(0);
    }

    @Override
    public int remainingByTitle(String title) {
        if (LibraryUtils.isBlank(title)) {
            return 0;
        }
        return repository.findByTitle(title)
                .map(items -> items.stream().mapToInt(InventoryItem::availableCopies).sum())
                .orElse(0);
    }

    @Override
    public int remainingByAuthor(String author) {
        if (LibraryUtils.isBlank(author)) {
            return 0;
        }
        return repository.findByAuthor(author)
                .map(items -> items.stream().mapToInt(InventoryItem::availableCopies).sum())
                .orElse(0);
    }

}
