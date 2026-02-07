package com.example.library.repository;

import com.example.library.domain.Book;
import com.example.library.domain.BookAvailability;
import com.example.library.domain.BookType;

import java.util.Objects;

public final class InventoryItem {
    private final Book book;
    private int totalCopies;
    private int borrowedCopies;

    public InventoryItem(Book book, int totalCopies) {
        if (totalCopies < 0) throw new IllegalArgumentException("totalCopies < 0");
        this.book = Objects.requireNonNull(book, "book");
        this.totalCopies = totalCopies;
        this.borrowedCopies = 0;
    }

    public Book book() {
        return book;
    }

    public int totalCopies() {
        return totalCopies;
    }

    public int borrowedCopies() {
        return borrowedCopies;
    }

    public synchronized int availableCopies() {
        return totalCopies - borrowedCopies;
    }

    public synchronized void addCopies(int copies) {
        if (copies <= 0) throw new IllegalArgumentException("copies must be > 0");
        totalCopies += copies;
    }

    public synchronized boolean tryBorrowOne() {
        if (book.type() == BookType.REFERENCE) return false;
        if (borrowedCopies >= totalCopies) return false;
        borrowedCopies++;
        return true;
    }

    public BookAvailability toAvailability() {
        return new BookAvailability(book, availableCopies());
    }
}
