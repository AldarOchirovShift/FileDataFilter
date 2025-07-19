package org.example.service;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic batch container that holds items until a specified limit is reached.
 * The batch can be used to collect items and then retrieve them in bulk while clearing
 * the internal storage. <b>This implementation is not thread-safe.</b>
 *
 * @param <T> the type of elements in this batch
 */
class DataBatch<T> {
    /**
     * The maximum number of items this batch can hold before being considered full.
     */
    private static final int BATCH_LIMIT = 1000;
    /**
     * Internal storage for the batch items.
     */
    private final List<T> list = new ArrayList<>();

    /**
     * Adds an item to the batch.
     *
     * @param value the item to be added to the batch (can be null)
     */
    public void add(T value) {
        list.add(value);
    }

    /**
     * Checks if the batch has reached its capacity.
     *
     * @return true if the number of items in the batch is equal to or greater than BATCH_LIMIT,
     *         false otherwise
     */
    public boolean isFull() {
        return list.size() >= BATCH_LIMIT;
    }

    /**
     * Retrieves all items from the batch and clears the internal storage.
     *
     * @return a new List containing all items that were in the batch
     */
    public List<T> getAndClear() {
        List<T> copy = new ArrayList<>(list);
        list.clear();
        return copy;
    }

    /**
     * Checks if the batch contains any items.
     *
     * @return true if the batch contains one or more items, false if empty
     */
    public boolean isNotEmpty() {
        return !list.isEmpty();
    }
}