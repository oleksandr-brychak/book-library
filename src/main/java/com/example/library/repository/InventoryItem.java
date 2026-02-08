package com.example.library.repository;

import com.example.library.domain.Book;
import com.example.library.domain.BookAvailability;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable inventory record. Updates return new instances.
 */
public record InventoryItem(Book book, int totalCopies, int borrowedCopies) {
    public InventoryItem {
        Objects.requireNonNull(book, "book must be provided");
        if (totalCopies < 0) {
            throw new IllegalArgumentException("totalCopies must be >= 0");
        }
        if (borrowedCopies < 0) {
            throw new IllegalArgumentException("borrowedCopies must be >= 0");
        }
        if (borrowedCopies > totalCopies) {
            throw new IllegalArgumentException("borrowedCopies must be <= totalCopies");
        }
    }

    public static InventoryItem create(Book book, int totalCopies) {
        return new InventoryItem(book, totalCopies, 0);
    }

    public int availableCopies() {
        return totalCopies - borrowedCopies;
    }

    public InventoryItem addCopies(int copies) {
        if (copies <= 0) {
            throw new IllegalArgumentException("copies must be > 0");
        }
        return new InventoryItem(book, totalCopies + copies, borrowedCopies);
    }

    public Optional<InventoryItem> borrowOne() {
        if (borrowedCopies >= totalCopies) {
            return Optional.empty();
        }
        return Optional.of(new InventoryItem(book, totalCopies, borrowedCopies + 1));
    }

    public BookAvailability toAvailability() {
        return new BookAvailability(book, availableCopies());
    }
}
