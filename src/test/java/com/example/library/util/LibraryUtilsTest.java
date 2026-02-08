package com.example.library.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LibraryUtilsTest {
    @Test
    void isBlankDetectsNullEmptyAndWhitespace() {
        assertThat(LibraryUtils.isBlank(null)).isTrue();
        assertThat(LibraryUtils.isBlank("")).isTrue();
        assertThat(LibraryUtils.isBlank("   ")).isTrue();
        assertThat(LibraryUtils.isBlank("x")).isFalse();
    }

    @Test
    void normalizeLowerHandlesNullAndCase() {
        assertThat(LibraryUtils.normalizeLower(null)).isEqualTo("");
        assertThat(LibraryUtils.normalizeLower("HOMER")).isEqualTo("homer");
    }

    @Test
    void requireNonBlankValidatesInput() {
        assertThatThrownBy(() -> LibraryUtils.requireNonBlank(null, "isbn"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isbn must be provided");

        assertThatThrownBy(() -> LibraryUtils.requireNonBlank("  ", "isbn"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("isbn must be provided");

        assertThat(LibraryUtils.requireNonBlank("abc", "isbn")).isEqualTo("abc");
    }
}
