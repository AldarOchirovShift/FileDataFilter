package org.example.statistics;

import org.example.exception.NonNumericValueException;

/**
 * Abstract base class for numeric statistics implementations.
 * <p>
 * Provides common functionality for numeric value validation and statistics collection.
 * Concrete subclasses should implement specific numeric operations for different number types.
 * </p>
 *
 * @see AbstractStatistics
 * @see NonNumericValueException
 */
public abstract class NumericStatistics extends AbstractStatistics {
    /**
     * Creates a new numeric statistics collector with specified detail level.
     *
     * @param fullStats {@code true} to enable full statistics collection,
     *                  {@code false} to collect only basic count information
     */
    public NumericStatistics(boolean fullStats) {
        super(fullStats);
    }

    /**
     * Validates that the input value is a numeric type.
     *
     * @param value the value to check
     * @throws NonNumericValueException if the value is not an instance of {@link Number}
     */
    protected void checkNumberType(Object value) {
        if (!(value instanceof Number)) {
            throw new NonNumericValueException(value.getClass());
        }
    }
}
