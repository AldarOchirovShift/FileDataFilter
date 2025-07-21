package org.example.statistics;

import org.example.exception.NonStringValueException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringStatisticsTest {
    @Test
    public void addValue_whenValidStringAdded_thenUpdatesCount() {
        var stats = new StringStatistics(true);
        stats.addValue("test");
        stats.addValue("hello");
        assertEquals(2, stats.count);
    }

    @Test
    public void addValue_whenNonStringAdded_thenThrowsNonStringValueException() {
        var stats = new StringStatistics(true);
        assertThrows(NonStringValueException.class, () -> stats.addValue(123));
    }

    @Test
    public void addValue_whenEmptyStringAdded_thenTreatsAsShortest() {
        var stats = new StringStatistics(true);
        stats.addValue("");
        stats.addValue("long string");
        assertEquals("", stats.getShortest());
    }

    @Test
    public void addValue_whenMultipleStringsAdded_thenCorrectlyUpdatesShortestAndLongest() {
        var stats = new StringStatistics(true);
        stats.addValue("apple");
        stats.addValue("banana");
        stats.addValue("kiwi");
        assertEquals("kiwi", stats.getShortest());
        assertEquals("banana", stats.getLongest());
    }

    @Test
    public void getStatistics_whenFullStatsTrueAndDataExists_thenReturnsFormattedStats() {
        var stats = new StringStatistics(true);
        stats.addValue("a");
        stats.addValue("bb");
        var expected = "Count: 2; Min: \"a\"; Max: \"bb\"";
        assertEquals(expected, stats.getStatistics());
    }

    @Test
    public void getStatistics_whenFullStatsTrueAndNoData_thenReturnsNAStats() {
        var stats = new StringStatistics(true);
        var expected = "Count: 0; Min: \"N/A\"; Max: \"N/A\"";
        assertEquals(expected, stats.getStatistics());
    }

    @Test
    public void getStatistics_whenFullStatsFalse_thenReturnsOnlyCount() {
        var stats = new StringStatistics(false);
        stats.addValue("test");
        assertEquals("Count: 1", stats.getStatistics());
    }

    @Test
    public void addValue_whenEqualLengthStringsAdded_thenKeepsFirstAsShortestAndLongest() {
        var stats = new StringStatistics(true);
        stats.addValue("abc");
        stats.addValue("def");
        assertEquals("abc", stats.getShortest());
        assertEquals("abc", stats.getLongest());
    }
}
