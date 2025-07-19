package org.example.config;

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
 *   <li>Short stats ({@code -s}) and full stats ({@code -f}) cannot be enabled simultaneously</li>
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
     * @throws IllegalArgumentException if:
     * <ul>
     *   <li>Both {@code -s} and {@code -f} flags are set</li>
     *   <li>No input files are provided</li>
     * </ul>
     */
    public AppConfig buildFromRaw(RawCommandLineArgs raw) {
        validate(raw);

        var shortStats = !raw.isFullStats();
        var fullStats = raw.isFullStats();

        return AppConfig.builder()
                .shortStats(shortStats)
                .fullStats(fullStats)
                .appendMode(raw.isAppendMode())
                .outputPath(raw.getOutputPath() != null ? raw.getOutputPath() : "")
                .filePrefix(raw.getFilePrefix() != null ? raw.getFilePrefix() : "")
                .inputFiles(raw.getInputFiles())
                .build();
    }

    /**
     * Validates the raw command-line arguments.
     *
     * @param raw the arguments to validate
     * @throws IllegalArgumentException if arguments are invalid (see {@link #buildFromRaw})
     */
    private void validate(RawCommandLineArgs raw) {
        if (raw.isShortStats() && raw.isFullStats()) {
            throw new IllegalArgumentException("Cannot use both -s and -f simultaneously");
        }
        if (raw.getInputFiles().isEmpty()) {
            throw new IllegalArgumentException("No input files specified");
        }
    }
}
