package org.example.service;

final class ServicesStringConstants {
    private ServicesStringConstants() {
    }

    static final class RegexPatterns {
        static final String INTEGER_PATTERN = "^-?(0|[1-9][0-9]{0,18}(_[0-9]{3})*)$";
        static final String FLOAT_PATTERN = "^-?(0|[1-9]\\d*)[.,]\\d+([eE][-+]?\\d+)?$|"
                + "^-?Infinity$|" + "^NaN$";
    }

    static final class SpecialFloatOptions {
        static final String NAN = "NaN";
        static final String INF = "Infinity";
        static final String NEG_INF = "-Infinity";
    }

    static final class PathOptions {
        static final String USER_DIR = System.getProperty("user.dir");
        static final String INPUT_DIR = "input";
        static final String OUTPUT_DIR = "output";
        static final String OUTPUT_INTEGERS_FILENAME = "integers.txt";
        static final String OUTPUT_FLOATS_FILENAME = "floats.txt";
        static final String OUTPUT_STRINGS_FILENAME = "strings.txt";
    }

    static final class Messages {
        static final String MALFORMED_NUMBER_WARNING = "Skipping malformed number in line: '{}'";
        static final String FAILED_PARSE_DOUBLE_VALUE = "Failed to parse double value";
        static final String FAILED_PARSE_LONG_VALUE = "Failed to parse long value";
        static final String NO_WRITE_PERMISSIONS = "No write permissions";
        static final String FAILED_TO_WRITE_TO_FILE = "Failed to write to file {}";
        static final String PROCESS_FILE_STARTED = "Process file started {}";
        static final String PROCESS_FILE_FINISHED = "Process file finished {}";
        static final String FAILED_TO_CREATE_DIRECTORY = "Failed to create directory: {}";
        static final String OUTPUT_DIRECTORY_READY = "Output directory ready: {}";
        static final String WRITING_TO_FILE_STARTED = "Writing to file started: {}";
        static final String WRITING_TO_FILE_FINISHED = "Writing to file finished : {}";
    }
}
