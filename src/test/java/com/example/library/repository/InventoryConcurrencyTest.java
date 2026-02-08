package com.example.library.repository;

import com.example.library.domain.Book;
import com.example.library.domain.BookType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryConcurrencyTest {
    @Test
    void concurrentBorrowDoesNotExceedAvailableCopies() throws InterruptedException {
        InMemoryInventoryRepository repository = new InMemoryInventoryRepository();
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);
        int copies = 1000;

        repository.addBook(odyssey, copies);

        int threads = 20;
        int attemptsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger successes = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < attemptsPerThread; j++) {
                        if (repository.tryBorrow(odyssey.isbn())) {
                            successes.incrementAndGet();
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        boolean finished = done.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertThat(finished).isTrue();
        assertThat(successes.get()).isEqualTo(copies);
        assertThat(repository.findByIsbn(odyssey.isbn()))
                .isPresent()
                .get()
                .extracting(InventoryItem::availableCopies)
                .isEqualTo(0);
    }

    @Test
    void concurrentAddBookAccumulatesCopies() throws InterruptedException {
        InMemoryInventoryRepository repository = new InMemoryInventoryRepository();
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);
        int threads = 25;
        int addsPerThread = 40;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < addsPerThread; j++) {
                        repository.addBook(odyssey, 1);
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        boolean finished = done.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        int expectedCopies = threads * addsPerThread;
        assertThat(finished).isTrue();
        assertThat(repository.findByIsbn(odyssey.isbn()))
                .isPresent()
                .get()
                .extracting(InventoryItem::availableCopies)
                .isEqualTo(expectedCopies);
    }

    @Test
    void concurrentAddAndBorrowKeepsCountsConsistent() throws InterruptedException {
        InMemoryInventoryRepository repository = new InMemoryInventoryRepository();
        Book odyssey = new Book("9780140449136", "The Odyssey", "Homer", BookType.NORMAL);

        int addThreads = 10;
        int borrowThreads = 10;
        int addsPerThread = 50;
        int borrowsPerThread = 50;

        ExecutorService executor = Executors.newFixedThreadPool(addThreads + borrowThreads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(addThreads + borrowThreads);
        AtomicInteger borrowSuccesses = new AtomicInteger();

        for (int i = 0; i < addThreads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < addsPerThread; j++) {
                        repository.addBook(odyssey, 1);
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        for (int i = 0; i < borrowThreads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < borrowsPerThread; j++) {
                        if (repository.tryBorrow(odyssey.isbn())) {
                            borrowSuccesses.incrementAndGet();
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        boolean finished = done.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        int totalAdds = addThreads * addsPerThread;
        int totalBorrows = borrowSuccesses.get();

        assertThat(finished).isTrue();
        assertThat(totalBorrows).isLessThanOrEqualTo(totalAdds);
        assertThat(repository.findByIsbn(odyssey.isbn()))
                .isPresent()
                .get()
                .extracting(InventoryItem::availableCopies)
                .isEqualTo(totalAdds - totalBorrows);
    }
}
