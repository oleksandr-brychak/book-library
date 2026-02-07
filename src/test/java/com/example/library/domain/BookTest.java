package com.example.library.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookTest {
    @Test
    void requiresNonNullFields() {
        assertThatThrownBy(() -> new Book(null, "The Odyssey", "Homer", BookType.NORMAL))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Book("9780140449136", null, "Homer", BookType.NORMAL))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Book("9780140449136", "The Odyssey", null, BookType.NORMAL))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new Book("9780140449136", "The Odyssey", "Homer", null))
                .isInstanceOf(NullPointerException.class);
    }
}
