package org.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
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
    /** Default filename for integer values */
    private static final String OUTPUT_INTEGERS_FILENAME = "integers.txt";
    /** Default filename for floating-point values */
    private static final String OUTPUT_FLOATS_FILENAME = "floats.txt";
    /** Default filename for string values */
    private static final String OUTPUT_STRINGS_FILENAME = "strings.txt";

    private final DataBatch<Long> integerBatch = new DataBatch<>();
    private final DataBatch<Double> floatBatch = new DataBatch<>();
    private final DataBatch<String> stringBatch = new DataBatch<>();

    private final Map<String, ExecutorService> fileExecutors;

    private final Path outputDirectory;
    private final String filePrefix;
    private final boolean appendMode;

    private final ConcurrentHashMap<Path, AtomicBoolean> firstFlushPerFile = new ConcurrentHashMap<>();

    /**
     * Constructs a new FileWriter instance.
     *
     * @param pathBuilder the path builder for creating output directory
     * @param outputDirectory the target directory for output files
     * @param filePrefix the prefix to prepend to filenames
     * @param appendMode if true, appends to existing files; if false, overwrites files
     * @throws IllegalArgumentException if outputDirectory or filePrefix are null
     */
    public FileWriter(PathBuilder pathBuilder, String outputDirectory, String filePrefix, boolean appendMode) {
        this.outputDirectory = pathBuilder.buildOutput(outputDirectory);
        this.filePrefix = filePrefix;
        this.appendMode = appendMode;
        this.fileExecutors = createFileExecutors();
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
    }

    /**
     * Closes the writer and releases all resources.
     * <p>
     * Automatically invoked when used in a try-with-resources block.
     * Flushes any remaining data and shuts down all executors.
     * After closing, attempts to add new data will throw IllegalStateException.
     *
     * @implNote This method is thread-safe and idempotent (multiple calls have no additional effect).
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
                OUTPUT_INTEGERS_FILENAME, Executors.newSingleThreadExecutor(),
                OUTPUT_FLOATS_FILENAME,   Executors.newSingleThreadExecutor(),
                OUTPUT_STRINGS_FILENAME,  Executors.newSingleThreadExecutor()
        );
    }

    private <T> void addValue(T value, DataBatch<T> batch, Runnable flushOperation) {
        batch.add(value);
        if (batch.isFull()) {
            flushOperation.run();
        }
    }

    private void flushIntegers() {
        flush(integerBatch, OUTPUT_INTEGERS_FILENAME);
    }

    private void flushFloats() {
        flush(floatBatch, OUTPUT_FLOATS_FILENAME);
    }

    private void flushStrings() {
        flush(stringBatch, OUTPUT_STRINGS_FILENAME);
    }

    private <T> void flush(DataBatch<T> batch, String filename) {
        var batchToWrite = batch.getAndClear();
        var filePath = outputDirectory.resolve(filePrefix + filename);
        fileExecutors.get(filename).submit(() -> writeBatchToFile(filePath, batchToWrite));
    }

    private <T> void writeBatchToFile(Path filePath, List<T> batch) {
        try {
            var parentDir = filePath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
                if (!Files.isWritable(parentDir)) {
                    throw new IOException("No write permissions for directory: " + parentDir);
                }
            }

            var options = getOptions(filePath);

            try (var writer = Files.newBufferedWriter(filePath, options)) {
                for (var item : batch) {
                    writer.write(item.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file: " + filePath, e);
        }
    }

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