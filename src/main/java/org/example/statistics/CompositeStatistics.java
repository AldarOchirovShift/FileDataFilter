package org.example.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * A composite statistics collector that manages multiple type-specific statistics collectors.
 * <p>
 * This class maintains separate statistics collectors for {@code String}, {@code Long}, and {@code Double} values,
 * allowing for type-safe operations and aggregated statistics reporting. Each type is handled by
 * an appropriate {@link Statistics} implementation.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * CompositeStatistics stats = new CompositeStatistics(true);
 * stats.addString("hello");
 * stats.addLong(100L);
 * stats.addDouble(3.14);
 * System.out.println(stats.getFullStatistics());
 * }
 * </pre>
 * </p>
 *
 * @see Statistics
 * @see IntegerStatistics
 * @see DoubleStatistics
 * @see StringStatistics
 */
public class CompositeStatistics {
    /**
     * Map of statistics collectors by value type.
     */
    private final Map<Class<?>, Statistics> statsCollectors = new HashMap<>();

    /**
     * Class object for String type for map lookup.
     */
    private final Class<String> stringClass = String.class;

    /**
     * Class object for Long type for map lookup.
     */
    private final Class<Long> longClass = Long.class;

    /**
     * Class object for Double type for map lookup.
     */
    private final Class<Double> doubleClass = Double.class;

    /**
     * Constructs a new composite statistics collector with the specified statistics detail level.
     *
     * @param fullStats {@code true} to enable full statistics collection (min, max, avg, etc.),
     *                  {@code false} to collect only basic count information
     */
    public CompositeStatistics(boolean fullStats) {
        statsCollectors.put(longClass, new IntegerStatistics(fullStats));
        statsCollectors.put(doubleClass, new DoubleStatistics(fullStats));
        statsCollectors.put(stringClass, new StringStatistics(fullStats));
    }

    /**
     * Adds a string value to the string statistics collector.
     *
     * @param string the string value to record
     */
    public void addString(String string) {
        statsCollectors.get(stringClass).addValue(string);
    }

    /**
     * Adds a long value to the numeric statistics collector.
     *
     * @param value the long value to record
     */
    public void addLong(long value) {
        statsCollectors.get(longClass).addValue(value);
    }

    /**
     * Adds a double value to the floating-point statistics collector.
     *
     * @param value the double value to record
     */
    public void addDouble(double value) {
        statsCollectors.get(doubleClass).addValue(value);
    }

    /**
     * Returns formatted statistics for all collected values.
     * <p>
     * The output contains separate sections for each value type, showing either full statistics
     * or just counts depending on the constructor parameter.
     * </p>
     *
     * @return formatted string containing all collected statistics, with each type's statistics
     *         on a separate line in the format "{TypeName}: {Statistics}"
     */
    public String getFullStatistics() {
        var sb = new StringBuilder();
        statsCollectors.forEach((type, stats) -> sb.append(type.getSimpleName()).append(": ")
                .append(stats.getStatistics()).append("\n"));
        return sb.toString();
    }
}
