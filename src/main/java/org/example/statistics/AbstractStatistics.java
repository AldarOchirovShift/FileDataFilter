package org.example.statistics;

/**
 * Abstract base class for statistics collection implementations.
 * <p>
 * Provides common functionality for tracking count and statistics display mode.
 * Concrete subclasses should implement specific statistical calculations
 * and value handling logic.
 * </p>
 *
 * @see Statistics
 */
public abstract class AbstractStatistics implements Statistics {
    /**
     * The total count of values added to the statistics.
     */
    protected int count = 0;

    /**
     * Flag indicating whether full statistics should be calculated and displayed.
     * When {@code false}, only basic count information will be provided.
     */
    protected boolean fullStats;

    /**
     * Constructs a new statistics collector with the specified display mode.
     *
     * @param fullStats {@code true} to enable calculation and display of full statistics
     *                 (min, max, sum, average), {@code false} to track only count
     */
    public AbstractStatistics(boolean fullStats) {
        this.fullStats = fullStats;
    }
}
