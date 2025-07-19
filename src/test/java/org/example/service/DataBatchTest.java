package org.example.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataBatchTest {
    private final DataBatch<String> batch = new DataBatch<>();

    @Test
    void add_whenSingleItemAdded_thenListShouldContainIt() {
        batch.add("test");
        assertTrue(batch.isNotEmpty());
    }

    @Test
    void add_whenNullAdded_thenListShouldContainNull() {
        batch.add(null);
        assertTrue(batch.isNotEmpty());
    }

    @Test
    void isFull_whenEmpty_thenShouldReturnFalse() {
        assertFalse(batch.isFull());
    }

    @Test
    void isFull_whenBelowLimit_thenShouldReturnFalse() {
        batch.add("test");
        assertFalse(batch.isFull());
    }

    @Test
    void isFull_whenAtLimit_thenShouldReturnTrue() {
        for (int i = 0; i < 1000; i++) {
            batch.add("test" + i);
        }
        assertTrue(batch.isFull());
    }

    @Test
    void isFull_whenAboveLimit_thenShouldReturnTrue() {
        for (int i = 0; i < 1001; i++) {
            batch.add("test" + i);
        }
        assertTrue(batch.isFull());
    }

    @Test
    void getAndClear_whenEmpty_thenShouldReturnEmptyList() {
        var result = batch.getAndClear();
        assertTrue(result.isEmpty());
        assertFalse(batch.isNotEmpty());
    }

    @Test
    void getAndClear_whenContainsItems_thenShouldReturnAllItems() {
        batch.add("first");
        batch.add("second");

        var result = batch.getAndClear();

        assertEquals(2, result.size());
        assertEquals("first", result.get(0));
        assertEquals("second", result.get(1));
        assertFalse(batch.isNotEmpty());
    }

    @Test
    void getAndClear_whenCalledTwice_thenSecondCallShouldReturnEmpty() {
        batch.add("test");
        batch.getAndClear();

        var result = batch.getAndClear();

        assertTrue(result.isEmpty());
    }

    @Test
    void isNotEmpty_whenEmpty_thenShouldReturnFalse() {
        assertFalse(batch.isNotEmpty());
    }

    @Test
    void isNotEmpty_whenHasItems_thenShouldReturnTrue() {
        batch.add("test");
        assertTrue(batch.isNotEmpty());
    }

    @Test
    void isNotEmpty_whenCleared_thenShouldReturnFalse() {
        batch.add("test");
        batch.getAndClear();
        assertFalse(batch.isNotEmpty());
    }
}
