package com.example.library.util;

import java.util.Locale;

public final class LibraryUtils {
    private LibraryUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String normalizeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    public static String requireNonBlank(String value, String name) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(name + " must be provided");
        }
        return value;
    }
}
