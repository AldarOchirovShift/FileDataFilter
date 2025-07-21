package org.example.statistics;

import lombok.Getter;
import org.example.exception.NonNumericValueException;

/**
 * Statistics collector for integer (long) numeric values.
 * <p>
 * Tracks count, minimum, maximum, sum and average of added values.
 * Inherits numeric type validation from {@link NumericStatistics}.
 * Uses {@code long} type internally to handle large integer values.
 * </p>
 *
 * @see NumericStatistics
 */
public class IntegerStatistics extends NumericStatistics {
    /**
     * Minimum value encountered (initialized to {@link Long#MAX_VALUE}).
     */
    @Getter
    private long min = Long.MAX_VALUE;

    /**
     * Maximum value encountered (initialized to {@link Long#MIN_VALUE}).
     */
    @Getter
    private long max = Long.MIN_VALUE;

    /**
     * Accumulated sum of all values.
     */
    @Getter
    private long sum = 0;

    /**
     * Indicates whether the sum of values has overflowed during accumulation.
     */
    private boolean sumOverflow = false;

    /**
     * Creates a new integer statistics collector.
     *
     * @param fullStats {@code true} to enable full statistics collection
     *                  (min, max, sum, average), {@code false} for count only
     */
    public IntegerStatistics(boolean fullStats) {
        super(fullStats);
    }

    /**
     * Adds a numeric value to the statistics.
     *
     * @param value the value to add (must be instance of {@link Number})
     * @throws NonNumericValueException if value is not a numeric type
     */
    @Override
    public void addValue(Object value) {
        checkNumberType(value);
        var num = ((Number) value).longValue();
        count++;
        min = Math.min(min, num);
        max = Math.max(max, num);

        if (sumOverflow) {
            return;
        }

        if (willAdditionOverflow(sum, num)) {
            sumOverflow = true;
            sum = 0;
        } else {
            sum += num;
        }
    }

    /**
     * Generates formatted statistics string.
     * <p>
     * When fullStats is enabled, returns string in format:
     * {@code "Count: X, Min: Y, Max: Z, Sum: W; Avg: V.VVV"}
     * </p>
     * <p>
     * When fullStats is disabled, returns:
     * {@code "Count: X"}
     * </p>
     *
     * @return formatted statistics string
     */
    @Override
    public String getStatistics() {
        var stats = new StringBuilder();
        stats.append("Count: ").append(count);

        if (!fullStats) {
            return stats.toString();
        }

        var minMaxStats = (count == 0)
                ? ", Min: \"N/A\", Max: \"N/A\""
                : ", Min: " + min + ", Max: " + max;
        stats.append(minMaxStats);

        if (count == 0 || sumOverflow) {
            stats.append(", Sum: \"N/A\"; Avg: \"N/A\"");
        } else {
            stats.append(", Sum: ").append(sum)
                    .append("; Avg: ").append(String.format("%.3f", (double) sum / count));
        }

        return stats.toString();
    }

    /**
     * Checks if the addition of two {@code long} values will result in an overflow.
     *
     * <p>This method evaluates whether {@code a + b} exceeds {@link Long#MAX_VALUE} (overflow)
     * or is less than {@link Long#MIN_VALUE} (underflow).</p>
     *
     * @param a the first operand
     * @param b the second operand (can be positive or negative)
     * @return {@code true} if {@code a + b} would overflow or underflow, {@code false} otherwise
     * @see Long#MAX_VALUE
     * @see Long#MIN_VALUE
     */
    private static boolean willAdditionOverflow(long a, long b) {
        if (b > 0) {
            return a > Long.MAX_VALUE - b;
        } else if (b < 0) {
            return a < Long.MIN_VALUE - b;
        }
        return false;
    }
}
