package com.example.library.app;

import com.example.library.domain.Book;
import com.example.library.domain.BookType;
import com.example.library.repository.InMemoryInventoryRepository;
import com.example.library.service.Library;
import com.example.library.service.LibraryService;

public class App {
    public static void main(String[] args) {
        Library library = new LibraryService(new InMemoryInventoryRepository());

        library.addBook(new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL), 3);
        library.addBook(new Book("9780140449181", "The Iliad", "Homer", BookType.NORMAL), 2);
        library.addBook(new Book("9780199535569", "Oxford English Dictionary", "Oxford", BookType.REFERENCE), 1);

        System.out.println("By author Homer:");
        library.findByAuthor("Homer").ifPresentOrElse(
                availability -> System.out.println("- " + availability.book().title()
                        + " (available " + availability.availableCopies() + ")"),
                () -> System.out.println("- no matches")
        );

        System.out.println("Can borrow The Odyssey: " + library.canBorrow("9780140449136"));
        System.out.println("Borrow The Odyssey: " + library.borrow("9780140449136"));
        System.out.println("Borrow Reference Book: " + library.borrow("9780199535569"));
        System.out.println("Total borrowed: " + library.totalBorrowedCount());

        library.findByIsbn("9780140449136").ifPresent(availability ->
                System.out.println("By ISBN: " + availability.book().title() + " available " + availability.availableCopies())
        );
    }
}
