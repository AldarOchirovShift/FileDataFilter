package org.example.service;

import org.example.exception.FileWriteException;
import org.example.statistics.CompositeStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A concurrent file writer that asynchronously writes different data types to separate files.
 * <p>
 * Implements {@link AutoCloseable} and is designed to be used with try-with-resources:
 * <pre>{@code
 * try (FileWriter writer = new FileWriter(...)) {
 *     // work with writer
 * }
 * }</pre>
 * <p>
 * This class provides thread-safe operations for writing three types of data:
 * <ul>
 *   <li>Long integers - written to integers.txt</li>
 *   <li>Double values - written to floats.txt</li>
 *   <li>String values - written to strings.txt</li>
 * </ul>
 * <p>
 * Data is collected in batches and written either when the batch is full or when the writer is closed.
 * Each data type uses a dedicated single-threaded executor for asynchronous writing.
 *
 * <p>Example usage:
 * <pre>{@code
 * PathBuilder pathBuilder = new PathBuilder();
 * try (FileWriter writer = new FileWriter(pathBuilder, "output_dir", "prefix_", false)) {
 *     writer.addInteger(42L);
 *     writer.addFloat(3.14);
 *     writer.addString("Hello World");
 * }
 * }</pre>
 *
 * @see DataBatch
 * @see PathBuilder
 */
public class FileWriter implements AutoCloseable {
    /**
     * Default filename for integer values (modified by prefix if specified)
     */
    private final DataBatch<Long> integerBatch = new DataBatch<>();

    /**
     * Default filename for floating-point values (modified by prefix if specified)
     */
    private final DataBatch<Double> floatBatch = new DataBatch<>();

    /**
     * Default filename for string values (modified by prefix if specified)
     */
    private final DataBatch<String> stringBatch = new DataBatch<>();

    private final Map<String, ExecutorService> fileExecutors;
    private final static Logger LOGGER = LoggerFactory.getLogger(FileWriter.class);
    private final Set<Path> loggedFiles = ConcurrentHashMap.newKeySet();
    private final CompositeStatistics statistics;

    private final Path outputDirectory;
    private final String filePrefix;
    private final boolean appendMode;

    /**
     * Tracks first write operation per file for TRUNCATE_EXISTING mode.
     * Key: File path, Value: Atomic flag (true = requires truncation)
     */
    private final ConcurrentHashMap<Path, AtomicBoolean> firstFlushPerFile = new ConcurrentHashMap<>();

    /**
     * Constructs a new FileWriter instance.
     *
     * @param pathBuilder     the path builder for creating output directory
     * @param outputDirectory the target directory for output files
     * @param filePrefix      the prefix to prepend to filenames
     * @param appendMode      if true, appends to existing files; if false, overwrites files
     * @param statistics      to collect statistics
     * @throws FileWriteException if outputDirectory or filePrefix are null
     */
    public FileWriter(PathBuilder pathBuilder, String outputDirectory, String filePrefix, boolean appendMode, CompositeStatistics statistics) {
        this.outputDirectory = pathBuilder.buildOutput(outputDirectory);
        this.filePrefix = filePrefix;
        this.appendMode = appendMode;
        this.fileExecutors = createFileExecutors();
        this.statistics = statistics;

        checkOutputDirectory();
    }

    /**
     * Verifies and prepares the output directory for file operations.
     *
     * <p>This method performs the following operations:
     * <ol>
     *   <li>Creates the output directory and all necessary parent directories if they don't exist</li>
     *   <li>Verifies write permissions for the output directory</li>
     *   <li>Logs a confirmation message when the directory is ready</li>
     * </ol>
     *
     * @throws FileWriteException if directory creation fails or write permissions are insufficient
     */
    private void checkOutputDirectory() {
        try {
            Files.createDirectories(outputDirectory);
            if (!Files.isWritable(outputDirectory)) {
                throw new FileWriteException(
                        ServicesStringConstants.Messages.NO_WRITE_PERMISSIONS,
                        outputDirectory.toString());
            }

            LOGGER.info(ServicesStringConstants.Messages.OUTPUT_DIRECTORY_READY, outputDirectory);
        } catch (IOException e) {
            throw new FileWriteException(
                    ServicesStringConstants.Messages.FAILED_TO_CREATE_DIRECTORY,
                    outputDirectory.toString(), e);
        }
    }

    /**
     * Adds a long integer value to be written.
     * <p>
     * The actual write operation may be deferred until the batch is full.
     *
     * @param value the long value to add
     * @throws IllegalStateException if the writer is closed
     */
    public void addInteger(long value) {
        addValue(value, integerBatch, this::flushIntegers);
        statistics.addLong(value);
    }

    /**
     * Adds a double value to be written.
     * <p>
     * The actual write operation may be deferred until the batch is full.
     *
     * @param value the double value to add
     * @throws IllegalStateException if the writer is closed
     */
    public void addFloat(double value) {
        addValue(value, floatBatch, this::flushFloats);
        statistics.addDouble(value);
    }

    /**
     * Adds a string value to be written.
     * <p>
     * The actual write operation may be deferred until the batch is full.
     *
     * @param value the string value to add
     * @throws IllegalStateException if the writer is closed
     */
    public void addString(String value) {
        addValue(value, stringBatch, this::flushStrings);
        statistics.addString(value);
    }

    /**
     * Closes the writer and releases all resources.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Flushes all remaining data batches</li>
     *   <li>Shuts down executors with 1-minute timeout</li>
     *   <li>Throws {@link FileWriteException} if flushing fails</li>
     * </ul>
     * After closing, attempts to add new data will throw {@link IllegalStateException}.
     */
    @Override
    public void close() {
        if (integerBatch.isNotEmpty()) {
            flushIntegers();
        }
        if (floatBatch.isNotEmpty()) {
            flushFloats();
        }
        if (stringBatch.isNotEmpty()) {
            flushStrings();
        }

        loggedFiles.forEach(path ->
                LOGGER.info(ServicesStringConstants.Messages.WRITING_TO_FILE_FINISHED, path));
        firstFlushPerFile.clear();
        fileExecutors.values().forEach(ExecutorService::shutdown);
    }

    /**
     * Creates executor services for each output file.
     * <p>
     * Can be overridden by subclasses to customize execution strategy.
     * By default creates a separate SingleThreadExecutor for each file type.
     *
     * @return a map of filenames to their corresponding ExecutorService instances
     */
    protected Map<String, ExecutorService> createFileExecutors() {
        return Map.of(
                ServicesStringConstants.PathOptions.OUTPUT_INTEGERS_FILENAME, Executors.newSingleThreadExecutor(),
                ServicesStringConstants.PathOptions.OUTPUT_FLOATS_FILENAME, Executors.newSingleThreadExecutor(),
                ServicesStringConstants.PathOptions.OUTPUT_STRINGS_FILENAME, Executors.newSingleThreadExecutor()
        );
    }

    private <T> void addValue(T value, DataBatch<T> batch, Runnable flushOperation) {
        batch.add(value);
        if (batch.isFull()) {
            flushOperation.run();
        }
    }

    /**
     * Forces immediate write of all buffered integers.
     * <p>
     * Used automatically when batch is full or writer is closed.
     *
     * @throws FileWriteException if write operation fails
     */
    private void flushIntegers() {
        flush(integerBatch, ServicesStringConstants.PathOptions.OUTPUT_INTEGERS_FILENAME);
    }

    /**
     * Forces immediate write of all buffered floats.
     * <p>
     * Used automatically when batch is full or writer is closed.
     *
     * @throws FileWriteException if write operation fails
     */
    private void flushFloats() {
        flush(floatBatch, ServicesStringConstants.PathOptions.OUTPUT_FLOATS_FILENAME);
    }

    /**
     * Forces immediate write of all buffered strings.
     * <p>
     * Used automatically when batch is full or writer is closed.
     *
     * @throws FileWriteException if write operation fails
     */
    private void flushStrings() {
        flush(stringBatch, ServicesStringConstants.PathOptions.OUTPUT_STRINGS_FILENAME);
    }

    /**
     * Internal batch flush operation.
     *
     * @param batch    Data to write
     * @param filename Target filename (without prefix/path)
     */
    private <T> void flush(DataBatch<T> batch, String filename) {
        var batchToWrite = batch.getAndClear();
        var filePath = outputDirectory.resolve(filePrefix + filename);
        fileExecutors.get(filename).submit(() -> writeBatchToFile(filePath, batchToWrite));
    }

    /**
     * Performs actual file write operation.
     *
     * @param filePath Full target file path
     * @param batch    Data batch to write
     * @throws FileWriteException if directory creation or file write fails
     */
    private <T> void writeBatchToFile(Path filePath, List<T> batch) {
        try {
            if (loggedFiles.add(filePath)) {
                LOGGER.info(ServicesStringConstants.Messages.WRITING_TO_FILE_STARTED, filePath);
            }

            var options = getOptions(filePath);
            try (var writer = Files.newBufferedWriter(filePath, options)) {
                for (var item : batch) {
                    writer.write(item.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LOGGER.error(ServicesStringConstants.Messages.FAILED_TO_WRITE_TO_FILE, filePath);
            throw new FileWriteException(ServicesStringConstants.Messages.FAILED_TO_WRITE_TO_FILE, filePath.toString(), e);
        }
    }

    /**
     * Determines file open options based on write mode.
     *
     * @param filePath Target file path
     * @return Array of StandardOpenOption values
     */
    private StandardOpenOption[] getOptions(Path filePath) {
        StandardOpenOption[] options;
        if (appendMode) {
            options = new StandardOpenOption[]{
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            };
        } else {
            var firstFlush = firstFlushPerFile.computeIfAbsent(
                    filePath,
                    k -> new AtomicBoolean(true)
            );

            var isFirstFlush = firstFlush.compareAndSet(true, false);

            if (isFirstFlush) {
                options = new StandardOpenOption[]{
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                };
            } else {
                options = new StandardOpenOption[]{
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                };
            }
        }
        return options;
    }
}