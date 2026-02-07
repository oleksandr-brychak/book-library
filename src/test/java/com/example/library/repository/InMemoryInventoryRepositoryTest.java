package com.example.library.repository;

import com.example.library.domain.Book;
import com.example.library.domain.BookType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryInventoryRepositoryTest {
    @Test
    void findsByExactAuthorAndTitleCaseInsensitive() {
        InMemoryInventoryRepository repository = new InMemoryInventoryRepository();
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);
        Book iliad = new Book("9780140449181", "The Iliad", "Homer", BookType.NORMAL);

        repository.addBook(odyssey, 2);
        repository.addBook(iliad, 1);

        assertThat(repository.findByAuthor("homer")).isPresent();
        assertThat(repository.findByAuthor("homer").get())
                .extracting(item -> item.book().title())
                .containsExactlyInAnyOrder("The Odyssey", "The Iliad");

        assertThat(repository.findByTitle("the odyssey")).isPresent();
        assertThat(repository.findByTitle("the odyssey").get())
                .extracting(item -> item.book().isbn())
                .containsExactly("9780140449136");
    }

    @Test
    void exactMatchingDoesNotReturnSubstrings() {
        InMemoryInventoryRepository repository = new InMemoryInventoryRepository();
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);

        repository.addBook(odyssey, 1);

        assertThat(repository.findByAuthor("Hom")).isEmpty();
        assertThat(repository.findByTitle("Odys")).isEmpty();
    }

    @Test
    void borrowDoesNotExceedAvailableCopies() {
        InMemoryInventoryRepository repository = new InMemoryInventoryRepository();
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);

        repository.addBook(odyssey, 1);

        assertThat(repository.borrow("9780140449136")).isTrue();
        assertThat(repository.borrow("9780140449136")).isFalse();

        assertThat(repository.findByIsbn("9780140449136"))
                .isPresent()
                .get()
                .extracting(InventoryItem::availableCopies)
                .isEqualTo(0);
    }

    @Test
    void addBookIncrementsCopiesForSameIsbn() {
        InMemoryInventoryRepository repository = new InMemoryInventoryRepository();
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);

        repository.addBook(odyssey, 1);
        repository.addBook(odyssey, 2);

        assertThat(repository.findByIsbn("9780140449136"))
                .isPresent()
                .get()
                .extracting(InventoryItem::availableCopies)
                .isEqualTo(3);
    }
}
