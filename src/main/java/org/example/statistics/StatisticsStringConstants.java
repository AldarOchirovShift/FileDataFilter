package org.example.statistics;

final class StatisticsStringConstants {
    private StatisticsStringConstants() {
    }

    static final String COUNT = "Count: ";
    static final String MIN = "; Min: ";
    static final String MAX = "; Max: ";
    static final String SUM = "; Sum: ";
    static final String AVG = "; Avg: ";

    static final String NA = "\"N/A\"";
    static final String MIN_MAX_NA = MIN + NA + MAX + NA;
    static final String SUM_AVG_NA = SUM + NA + AVG + NA;
    static final String NA_ALL = MIN + NA + MAX + NA + SUM + NA + AVG + NA;
}
