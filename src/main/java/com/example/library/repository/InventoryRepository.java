package com.example.library.repository;

import com.example.library.domain.Book;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    void addBook(Book book, int copies);

    Optional<InventoryItem> findByAuthor(String authorQuery);

    Optional<InventoryItem> findByTitle(String titleQuery);

    Optional<InventoryItem> findByIsbn(String isbn);

    boolean borrow(String isbn);

    List<InventoryItem> findAll();
}
