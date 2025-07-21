package org.example.runner;

import org.example.cli.CommandLineArgsParser;
import org.example.config.AppConfig;
import org.example.config.AppConfigBuilder;
import org.example.exception.AppException;
import org.example.exception.ConfigurationException;
import org.example.exception.FileWriteException;
import org.example.exception.NumberParseException;
import org.example.service.FileProcessor;
import org.example.service.FileWriter;
import org.example.service.PathBuilder;
import org.example.statistics.CompositeStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The main application runner class that orchestrates file processing workflow.
 *
 * <p>This class serves as the entry point for the application and performs following operations:
 * <ol>
 *   <li>Parses command line arguments</li>
 *   <li>Builds application configuration</li>
 *   <li>Processes input files</li>
 *   <li>Handles errors and logs statistics</li>
 * </ol>
 *
 * <p><b>Usage Example:</b>
 * <pre>{@code
 * java -jar target/file-data-filter-1.0-SNAPSHOT-jar-with-dependencies.jar -f -a -o directory -p prefix_ in1.txt in2.txt
 * }</pre>
 *
 * <p><b>Error Handling:</b>
 * <ul>
 *   <li>{@link ConfigurationException} - for invalid configuration (exit code 3)</li>
 *   <li>{@link FileWriteException} - for file system errors (exit code 2)</li>
 *   <li>General {@link Exception} - for unexpected errors (exit code 1)</li>
 * </ul>
 *
 * @see CommandLineArgsParser
 * @see AppConfigBuilder
 * @see FileProcessor
 */
public class Runner {
    private final static Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    /**
     * Main application entry point.
     *
     * @param args command line arguments containing:
     *             <ul>
     *               <li>Input file paths</li>
     *               <li>Output directory (-o option)</li>
     *               <li>Other configuration flags</li>
     *             </ul>
     */
    public static void main(String[] args) {
        LOGGER.info(RunnerConstants.APP_STARTED, getArgs(args));
        try {
            var parser = new CommandLineArgsParser();
            var rawArgs = parser.parse(args);
            LOGGER.info(RunnerConstants.ARGS_PARSED, rawArgs);

            var configBuilder = new AppConfigBuilder();
            var config = configBuilder.buildFromRaw(rawArgs);
            LOGGER.info(RunnerConstants.APPLICATION_CONFIG, config);

            var statistics = new CompositeStatistics(config.isFullStats());
            processFiles(config, statistics);
            LOGGER.info(statistics.getFullStatistics());

        } catch (ConfigurationException e) {
            handleConfigurationError(e);
        }  catch (FileWriteException e) {
            handleFileWriteError(e);
        } catch (Exception e) {
            handleUnexpectedError(e);
        }
        LOGGER.info(RunnerConstants.APPLICATION_FINISHED);
    }

    /**
     * Joins command line arguments into a single string for logging.
     *
     * @param args raw command line arguments
     * @return space-separated arguments string
     */
    private static String getArgs(String[] args) {
        var result = new StringBuilder();
        for (var arg : args) {
            result.append(arg);
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * Processes all input files according to configuration.
     *
     * @param config the application configuration
     * @param statistics collector for processing statistics
     * @throws FileWriteException if output file operations fail
     */
    private static void processFiles(AppConfig config, CompositeStatistics statistics) {
        var pathBuilder = new PathBuilder();
        try (var fileWriter = new FileWriter(pathBuilder, config.getOutputPath(),
                config.getFilePrefix(), config.isAppendMode(), statistics)) {

            var fileProcessor = new FileProcessor(pathBuilder, fileWriter);
            for (var inputFile : config.getInputFiles()) {
                processSingleFile(fileProcessor, inputFile);
            }
        }
    }

    /**
     * Processes a single input file and handles file-specific errors.
     *
     * @param processor the file processor instance
     * @param inputFile path to the input file
     */
    private static void processSingleFile(FileProcessor processor, String inputFile) {
        try {
            processor.processFile(inputFile);
        } catch (NumberParseException e) {
            LOGGER.error(RunnerConstants.NUMBER_FORMAT_ERROR_IN_FILE, inputFile, e.getValue(), e.getMessage());
        } catch (IOException e) {
            LOGGER.error(RunnerConstants.IO_ERROR, inputFile, e.getMessage());
        } catch (AppException e) {
            LOGGER.error(RunnerConstants.PROCESS_FILE_FAILED, inputFile, e.getMessage());
        }
    }

    /**
     * Handles unexpected application errors.
     *
     * @param e the caught exception
     */
    private static void handleUnexpectedError(Exception e) {
        LOGGER.error(RunnerConstants.UNEXPECTED_ERROR, e.getMessage(), e);
        System.exit(1);
    }

    /**
     * Handles configuration errors with user-friendly messages.
     *
     * @param e the configuration exception
     */
    private static void handleConfigurationError(ConfigurationException e) {
        LOGGER.error(RunnerConstants.CONFIGURATION_ERROR, e.getMessage());
        LOGGER.error(RunnerConstants.CONFIGURATION_HINT);
        System.exit(2);
    }

    /**
     * Handles file write errors with user-friendly messages.
     *
     * @param e the file write exception
     */
    private static void handleFileWriteError(FileWriteException e) {
        LOGGER.error(RunnerConstants.FAILED_TO_WRITE_TO_FILE, e.getMessage());
        LOGGER.error(RunnerConstants.FAILED_TO_WRITE_HINT);
        System.exit(3);
    }
}
