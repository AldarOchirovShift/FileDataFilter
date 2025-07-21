package org.example.config;

import org.example.exception.ConfigurationException;

/**
 * A builder class that converts {@link RawCommandLineArgs} into a validated {@link AppConfig}.
 * <p>
 * This class performs the following operations:
 * <ul>
 *   <li>Validates raw command-line arguments (ensures no conflicting options, checks required fields)</li>
 *   <li>Sets default values for optional parameters (output path = empty string, file prefix = empty string)</li>
 *   <li>Constructs an immutable {@link AppConfig} instance</li>
 * </ul>
 *
 * <p><b>Validation rules:</b></p>
 * <ul>
 *   <li>At least one input file must be specified</li>
 * </ul>
 *
 * @see RawCommandLineArgs
 * @see AppConfig
 */
public class AppConfigBuilder {
    /**
     * Builds an {@link AppConfig} from raw command-line arguments.
     * <p>
     * Performs validation and applies default values where necessary:
     * <ul>
     *   <li>If no output path is specified, uses an empty string</li>
     *   <li>If no file prefix is specified, uses an empty string</li>
     * </ul>
     *
     * @param raw the raw command-line arguments to process
     * @return a fully configured and validated {@link AppConfig}
     * @throws ConfigurationException if:
     * <ul>
     *   <li>No input files are provided</li>
     * </ul>
     */
    public AppConfig buildFromRaw(RawCommandLineArgs raw) {
        if (raw.getInputFiles().isEmpty()) {
            throw new ConfigurationException(ConfigStringConstants.Exceptions.NO_INPUT_FILES);
        }

        var shortStats = !raw.isFullStats();
        var fullStats = raw.isFullStats();

        return AppConfig.builder()
                .shortStats(shortStats)
                .fullStats(fullStats)
                .appendMode(raw.isAppendMode())
                .outputPath(raw.getOutputPath() != null
                        ? raw.getOutputPath()
                        : ConfigStringConstants.Options.EMPTY_STRING)
                .filePrefix(raw.getFilePrefix() != null
                        ? raw.getFilePrefix()
                        : ConfigStringConstants.Options.EMPTY_STRING)
                .inputFiles(raw.getInputFiles())
                .build();
    }
}
