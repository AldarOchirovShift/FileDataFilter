package org.example.statistics;

import org.example.exception.NonNumericValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntegerStatisticsTest {
    @Test
    public void addValue_whenValidNumbersAdded_thenUpdatesCount() {
        var stats = new IntegerStatistics(true);
        stats.addValue(10);
        stats.addValue(5L);
        stats.addValue(15);
        assertEquals(3, stats.count);
    }

    @Test
    public void addValue_whenNonNumberAdded_thenThrowsNonNumericValueException() {
        var stats = new IntegerStatistics(true);
        assertThrows(NonNumericValueException.class, () -> stats.addValue("not a number"));
    }

    @Test
    public void getStatistics_whenFullStatsTrueAndDataExists_thenReturnsFormattedStats() {
        var stats = new IntegerStatistics(true);
        stats.addValue(10);
        stats.addValue(20);
        var expected = "Count: 2, Min: 10, Max: 20, Sum: 30; Avg: 15,000";
        assertEquals(expected, stats.getStatistics());
    }

    @Test
    public void getStatistics_whenFullStatsTrueAndNoData_thenReturnsNAStats() {
        var stats = new IntegerStatistics(true);
        var expected = "Count: 0, Min: \"N/A\", Max: \"N/A\", Sum: \"N/A\"; Avg: \"N/A\"";
        assertEquals(expected, stats.getStatistics());
    }

    @Test
    public void getStatistics_whenFullStatsTrueAndOverflow_thenReturnsNAStats() {
        var stats = new IntegerStatistics(true);
        stats.addValue(Long.MAX_VALUE);
        stats.addValue(1);
        var expected = "Count: 2, Min: 1, Max: 9223372036854775807, Sum: \"N/A\"; Avg: \"N/A\"";
        assertEquals(expected, stats.getStatistics());
    }

    @Test
    public void getStatistics_whenFullStatsFalse_thenReturnsOnlyCount() {
        var stats = new IntegerStatistics(false);
        stats.addValue(10);
        stats.addValue(20);
        assertEquals("Count: 2", stats.getStatistics());
    }

    @Test
    public void addValue_whenMixedNumbersAdded_thenCorrectlyUpdatesMinMaxSum() {
        var stats = new IntegerStatistics(true);
        stats.addValue(-5);
        stats.addValue(100);
        stats.addValue(0);
        assertEquals(-5, stats.getMin());
        assertEquals(100, stats.getMax());
        assertEquals(95, stats.getSum());
    }
}
