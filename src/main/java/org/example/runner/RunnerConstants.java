package org.example.runner;

final class RunnerConstants {
    private RunnerConstants() {
    }

    static final String APP_STARTED = "Application started with cli arguments: {}";
    static final String ARGS_PARSED = "Cli arguments parsed: {}";
    static final String APPLICATION_CONFIG = "Application config built: {}";
    static final String NUMBER_FORMAT_ERROR_IN_FILE = "Number format error in file {} at value '{}': {}";
    static final String IO_ERROR = "I/O error while processing file {}: {}";
    static final String PROCESS_FILE_FAILED = "Failed to process file {}: {}";
    static final String CONFIGURATION_ERROR = "Configuration error: {}";
    static final String CONFIGURATION_HINT = "Please run with pattern: java -jar target/file-data-filter-1.0-SNAPSHOT-jar-with-dependencies.jar (-s | -f) [-a] [-o <path>] [-p <prefix>] <input_files>...";
    static final String FAILED_TO_WRITE_TO_FILE = "Failed to write to file: {}";
    static final String FAILED_TO_WRITE_HINT = "Please choose another output directory \nOR Run as administrator: cmd (if Windows) with sudo (if Linux/MacOS)";
    static final String UNEXPECTED_ERROR = "Unexpected error: {}";
    static final String APPLICATION_FINISHED = "Application finished.";
}
