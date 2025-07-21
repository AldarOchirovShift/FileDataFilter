package org.example.statistics;

/**
 * Interface for collecting and reporting statistics on values of various types.
 * <p>
 * Implementations of this interface should track and calculate statistics
 * specific to their value type (e.g., numbers, strings, etc.).
 * </p>
 *
 * @see AbstractStatistics
 * @see NumericStatistics
 * @see StringStatistics
 */
public interface Statistics {
    /**
     * Adds a value to the statistics collection.
     *
     * @param value the value to be added to the statistics
     */
    void addValue(Object value);

    /**
     * Generates a formatted string containing collected statistics.
     * <p>
     * The exact format depends on the implementation.
     * </p>
     *
     * @return formatted statistics string
     */
    String getStatistics();
}
