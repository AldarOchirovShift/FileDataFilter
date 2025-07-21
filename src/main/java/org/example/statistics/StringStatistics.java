package org.example.statistics;

import lombok.Getter;
import org.example.exception.NonStringValueException;

/**
 * Statistics collector for string values.
 * <p>
 * Tracks and reports statistics on string lengths including:
 * <ul>
 *   <li>Total count of strings processed</li>
 *   <li>The shortest string encountered</li>
 *   <li>The longest string encountered</li>
 * </ul>
 * </p>
 *
 * @see AbstractStatistics
 * @see NonStringValueException
 */
public class StringStatistics extends AbstractStatistics {
    /**
     * The shortest string encountered (by length).
     */
    @Getter
    private String shortest;

    /**
     * The longest string encountered (by length).
     */
    @Getter
    private String longest;

    /**
     * Creates a new string statistics collector.
     *
     * @param fullStats {@code true} to track and report shortest/longest strings,
     *                  {@code false} to track only count
     */
    public StringStatistics(boolean fullStats) {
        super(fullStats);
    }

    /**
     * Adds a string value to the statistics.
     *
     * @param value the string value to record (must not be null)
     * @throws NonStringValueException if the value is not a {@link String}
     */
    @Override
    public void addValue(Object value) {
        if (!(value instanceof String str)) {
            throw new NonStringValueException(value.getClass());
        }
        count++;

        if (shortest == null || str.length() < shortest.length()) {
            shortest = str;
        }
        if (longest == null || str.length() > longest.length()) {
            longest = str;
        }
    }

    /**
     * Generates a formatted statistics report.
     * <p>
     * Format when fullStats is true:
     * {@code "Count: X; Shortest: "Y"; Longest: "Z""}
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
        var result = new StringBuilder(StatisticsStringConstants.COUNT).append(count);

        if (fullStats) {
            result.append(StatisticsStringConstants.MIN)
                    .append(shortest != null ? "\"" + shortest + "\"" : StatisticsStringConstants.NA)
                    .append(StatisticsStringConstants.MAX)
                    .append(longest != null ? "\"" + longest + "\"" : StatisticsStringConstants.NA);
        }

        return result.toString();
    }
}
