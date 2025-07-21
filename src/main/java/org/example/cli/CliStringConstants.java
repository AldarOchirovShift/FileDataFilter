package org.example.cli;

final class CliStringConstants {
    private CliStringConstants() {
    }

    static final class Options {
        static final String SHORT_STATS_ARG = "-s";
        static final String FULL_STATS_ARG = "-f";
        static final String APPEND_MODE_ARG = "-a";
        static final String OUTPUT_PATH_ARG = "-o";
        static final String FILE_PREFIX_ARG = "-p";
    }

    static final class Exceptions {
        static final String PATH_REQUIRED = "Path must be specified after -o";
        static final String PREFIX_REQUIRED = "Prefix must be specified after -p";
        static final String UNKNOWN_ARG = "Unknown argument: ";
    }
}
