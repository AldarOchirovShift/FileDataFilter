package org.example.statistics;

import org.example.exception.NonNumericValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleStatisticsTest {
    @Test
    public void addValue_whenValidNumbersAdded_thenUpdatesCount() {
        var stats = new DoubleStatistics(true);
        stats.addValue(10.5);
        stats.addValue(5L);
        stats.addValue(15.75f);
        assertEquals(3, stats.count);
    }

    @Test
    public void addValue_whenNonNumberAdded_thenThrowsNonNumericValueException() {
        var stats = new DoubleStatistics(true);
        assertThrows(NonNumericValueException.class, () -> stats.addValue("text"));
    }

    @Test
    public void getStatistics_whenFullStatsTrueAndDataExists_thenReturnsFormattedStats() {
        var stats = new DoubleStatistics(true);
        stats.addValue(10.25);
        stats.addValue(20.5);
        var expected = "Count: 2; Min: 10,250; Max: 20,500; Sum: 30,750; Avg: 15,375";
        assertEquals(expected, stats.getStatistics());
    }

    @Test
    public void getStatistics_whenFullStatsTrueAndNoData_thenReturnsNAStats() {
        var stats = new DoubleStatistics(true);
        var expected = "Count: 0; Min: \"N/A\"; Max: \"N/A\"; Sum: \"N/A\"; Avg: \"N/A\"";
        assertEquals(expected, stats.getStatistics());
    }

    @Test
    public void getStatistics_whenFullStatsFalse_thenReturnsOnlyCount() {
        var stats = new DoubleStatistics(false);
        stats.addValue(10.1);
        stats.addValue(20.2);
        assertEquals("Count: 2", stats.getStatistics());
    }

    @Test
    public void addValue_whenMixedNumbersAdded_thenCorrectlyUpdatesMinMaxSum() {
        var stats = new DoubleStatistics(true);
        stats.addValue(-5.5);
        stats.addValue(100.75);
        stats.addValue(0.0);
        assertEquals(-5.5, stats.getMin(), 0.001);
        assertEquals(100.75, stats.getMax(), 0.001);
        assertEquals(95.25, stats.getSum(), 0.001);
    }

    @Test
    public void addValue_whenAddingDoubleMaxValue_thenUpdatesCorrectly() {
        var stats = new DoubleStatistics(true);
        stats.addValue(Double.MAX_VALUE);
        stats.addValue(1.0);
        assertEquals(1.0, stats.getMin(), 0.001);
        assertEquals(Double.MAX_VALUE, stats.getMax(), 0.001);
    }
}
