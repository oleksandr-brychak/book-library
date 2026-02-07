package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.domain.BookType;
import com.example.library.repository.InMemoryInventoryRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LibraryServiceTest {
    @Test
    void findsByAuthorTitleAndIsbn() {
        Library library = new LibraryService(new InMemoryInventoryRepository());
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);
        Book iliad = new Book("9780140449181", "The Iliad", "Homer", BookType.NORMAL);

        library.addBook(odyssey, 2);
        library.addBook(iliad, 1);

        assertThat(library.findByAuthor("homer"))
                .isPresent()
                .get()
                .extracting(availability -> availability.book().author())
                .isEqualTo("Homer");

        assertThat(library.findByTitle("Odyssey"))
                .isPresent()
                .get()
                .extracting(availability -> availability.book().isbn())
                .isEqualTo("9780140449136");

        assertThat(library.findByIsbn("9780140449181"))
                .get()
                .extracting(availability -> availability.book().author())
                .isEqualTo("Homer");
    }

    @Test
    void borrowsBooksAndTracksOutstanding() {
        Library library = new LibraryService(new InMemoryInventoryRepository());
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);

        library.addBook(odyssey, 2);

        assertThat(library.canBorrow("9780140449136")).isTrue();
        assertThat(library.borrow("9780140449136")).isTrue();
        assertThat(library.borrow("9780140449136")).isTrue();
        assertThat(library.borrow("9780140449136")).isFalse();
        assertThat(library.totalBorrowedCount()).isEqualTo(2);
    }

    @Test
    void preventsBorrowingReferenceBooks() {
        Library library = new LibraryService(new InMemoryInventoryRepository());
        Book reference = new Book("9780199535569", "Oxford English Dictionary", "Oxford", BookType.REFERENCE);

        library.addBook(reference, 1);

        assertThat(library.canBorrow("9780199535569")).isFalse();
        assertThat(library.borrow("9780199535569")).isFalse();
        assertThat(library.totalBorrowedCount()).isZero();
    }

    @Test
    void blankQueriesReturnEmptyOptionals() {
        Library library = new LibraryService(new InMemoryInventoryRepository());

        assertThat(library.findByAuthor(" ")).isEmpty();
        assertThat(library.findByTitle("")).isEmpty();
    }
}
