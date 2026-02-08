package com.example.library.service;

import com.example.library.domain.Book;
import com.example.library.domain.BookType;
import com.example.library.repository.InMemoryInventoryRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LibraryServiceTest {
    @Test
    void findsByAuthorTitlePrefixAndIsbn() {
        Library library = new LibraryService(new InMemoryInventoryRepository());
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);
        Book iliad = new Book("9780140449181", "The Iliad", "Homer", BookType.NORMAL);

        library.addBook(odyssey, 2);
        library.addBook(iliad, 1);

        assertThat(library.findByAuthor("Hom"))
                .extracting(availability -> availability.book().title())
                .containsExactlyInAnyOrder("The Odyssey", "The Iliad");

        assertThat(library.findByTitle("the od"))
                .extracting(availability -> availability.book().isbn())
                .containsExactly("9780140449136");

        assertThat(library.findByIsbn("9780140449181"))
                .extracting(availability -> availability.book().author())
                .isEqualTo("Homer");
    }

    @Test
    void borrowsBooksAndTracksOutstanding() {
        Library library = new LibraryService(new InMemoryInventoryRepository());
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);
        Book iliad = new Book("9780140449181", "The Iliad", "Homer", BookType.NORMAL);

        library.addBook(odyssey, 2);
        library.addBook(iliad, 1);

        assertThat(library.canBorrow("9780140449136")).isTrue();
        assertThat(library.borrow("9780140449136")).isTrue();
        assertThat(library.borrow("9780140449136")).isTrue();
        assertThat(library.borrow("9780140449136")).isFalse();
        assertThat(library.totalBorrowedCount()).isEqualTo(2);
        assertThat(library.remainingByIsbn("9780140449136")).isEqualTo(0);
        assertThat(library.remainingByTitle("The Iliad")).isEqualTo(1);
        assertThat(library.remainingByAuthor("Homer")).isEqualTo(1);
    }

    @Test
    void blankQueriesReturnEmptyOptionals() {
        Library library = new LibraryService(new InMemoryInventoryRepository());

        assertThat(library.findByAuthor(" ")).isEmpty();
        assertThat(library.findByTitle("")).isEmpty();
    }

    @Test
    void findByIsbnRejectsBlankIsbn() {
        Library library = new LibraryService(new InMemoryInventoryRepository());

        assertThatThrownBy(() -> library.findByIsbn(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isbn must be provided");
    }
}
