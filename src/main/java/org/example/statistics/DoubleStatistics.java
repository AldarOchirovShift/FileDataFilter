package org.example.statistics;

import lombok.Getter;
import org.example.exception.NonNumericValueException;

/**
 * Statistics collector for double-precision floating-point values.
 * <p>
 * Tracks count, minimum, maximum, sum and average of added values.
 * Inherits from {@link NumericStatistics} to provide numeric type validation.
 * </p>
 *
 * @see NumericStatistics
 */
public class DoubleStatistics extends NumericStatistics {
    /**
     * Minimum value encountered (initialized to {@link Double#MAX_VALUE}).
     */
    @Getter
    private double min = Double.MAX_VALUE;

    /**
     * Maximum value encountered (initialized to {@link Double#MIN_VALUE}).
     */
    @Getter
    private double max = Double.MIN_VALUE;

    /**
     * Sum of all added values.
     */
    @Getter
    private double sum = 0;

    /**
     * Creates a new statistics collector with specified detail level.
     *
     * @param fullStats {@code true} to track full statistics (min/max/sum/avg),
     *                  {@code false} to track only count
     */
    public DoubleStatistics(boolean fullStats) {
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
        var num = ((Number) value).doubleValue();
        count++;
        min = Math.min(min, num);
        max = Math.max(max, num);
        sum += num;
    }

    /**
     * Returns formatted statistics based on collection settings.
     * <p>
     * Format when fullStats is true:
     * {@code "Count: X; Min: Y.YYY; Max: Z.ZZZ; Sum: W.WWW; Avg: V.VVV"}
     * </p>
     * <p>
     * Format when fullStats is false:
     * {@code "Count: X"}
     * </p>
     *
     * @return formatted statistics string
     */
    @Override
    public String getStatistics() {
        var stats = new StringBuilder(StatisticsStringConstants.COUNT).append(count);

        if (!fullStats) {
            return stats.toString();
        }

        if (count == 0) {
            stats.append(StatisticsStringConstants.NA_ALL);
        } else {
            stats.append(StatisticsStringConstants.MIN).append(String.format("%.3f", min))
                    .append(StatisticsStringConstants.MAX).append(String.format("%.3f", max))
                    .append(StatisticsStringConstants.SUM).append(String.format("%.3f", sum))
                    .append(StatisticsStringConstants.AVG).append(String.format("%.3f", sum / count));
        }

        return stats.toString();
    }
}
