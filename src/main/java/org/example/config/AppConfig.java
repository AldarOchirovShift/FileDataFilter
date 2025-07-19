package org.example.config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Represents the final, validated application configuration.
 * <p>
 * This immutable class holds all runtime settings for the application after processing
 * command-line arguments. Instances are created via {@link AppConfigBuilder}.
 *
 * <p><b>Configuration parameters:</b></p>
 * <ul>
 *   <li>{@code shortStats} - if {@code true}, enables concise statistics output (exclusive with {@code fullStats})</li>
 *   <li>{@code fullStats} - if {@code true}, enables detailed statistics output (exclusive with {@code shortStats})</li>
 *   <li>{@code appendMode} - if {@code true}, output files will be appended rather than overwritten</li>
 *   <li>{@code outputPath} - filesystem path for output files (default: current directory)</li>
 *   <li>{@code filePrefix} - prefix to use for generated files (default: empty string)</li>
 *   <li>{@code inputFiles} - list of input files to process (always non-empty)</li>
 * </ul>
 *
 * <p>This class is immutable and thread-safe. All fields are mandatory except where noted.</p>
 *
 * @see AppConfigBuilder
 * @see lombok.Builder (creates instances via builder pattern)
 * @see lombok.Getter (provides getter methods)
 * @see lombok.ToString (provides human-readable toString())
 */
@Getter
@Builder
@ToString
public class AppConfig {
    /**
     * Flag indicating whether short statistics format should be used.
     * Mutually exclusive with {@link #fullStats}.
     */
    private final boolean shortStats;

    /**
     * Flag indicating whether full statistics format should be used.
     * Mutually exclusive with {@link #shortStats}.
     */
    private final boolean fullStats;

    /**
     * Flag indicating whether output files should be opened in append mode.
     */
    private final boolean appendMode;

    /**
     * Target directory for output files.
     * <p>
     * Defaults to current directory if not specified in command-line arguments.
     */
    private final String outputPath;

    /**
     * Prefix to prepend to generated filenames.
     * <p>
     * Empty string if no prefix was specified.
     */
    private final String filePrefix;

    /**
     * List of input files to process.
     * <p>
     * Guaranteed to be non-empty (validated during construction).
     */
    private final List<String> inputFiles;
}
