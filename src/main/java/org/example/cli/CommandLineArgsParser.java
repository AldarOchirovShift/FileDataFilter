package org.example.cli;

import org.example.config.RawCommandLineArgs;

/**
 * A utility class for parsing command line arguments into a structured format.
 * <p>
 * This parser handles various command line options and converts them into a
 * {@link RawCommandLineArgs} object that contains all the extracted parameters.
 * Supported options include:
 * <ul>
 *   <li>{@code -s} - enables short statistics mode (default, mutually exclusive with -f)</li>
 *   <li>{@code -f} - enables full statistics mode (mutually exclusive with -s)</li>
 *   <li>{@code -a} - enables append mode</li>
 *   <li>{@code -o <path>} - specifies the output path (requires a path argument)</li>
 *   <li>{@code -p <prefix>} - specifies the file prefix (requires a prefix argument)</li>
 * </ul>
 * Any arguments not prefixed with '-' are treated as input files. If options are
 * specified multiple times, the last occurrence takes precedence.
 *
 * @see RawCommandLineArgs
 */
public class CommandLineArgsParser {
    /**
     * Parses the command line arguments and returns them as a structured object.
     * <p>
     * The method processes the arguments array, recognizing supported options and
     * their values. Short statistics mode ({@code -s}) is enabled by default.
     *
     * @param args the command line arguments to parse
     * @return a {@link RawCommandLineArgs} object containing all parsed parameters
     * @throws IllegalArgumentException if:
     *         <ul>
     *           <li>An unknown argument is encountered</li>
     *           <li>The {@code -o} option is not followed by a path</li>
     *           <li>The {@code -p} option is not followed by a prefix</li>
     *         </ul>
     */
    public RawCommandLineArgs parse(String[] args) {
        var raw = new RawCommandLineArgs();
        raw.setShortStats(true);

        for (var i = 0; i < args.length; i++) {
            var arg = args[i];
            switch (arg) {
                case "-s" -> {
                    raw.setShortStats(true);
                    raw.setFullStats(false);
                }
                case "-f" -> {
                    raw.setShortStats(false);
                    raw.setFullStats(true);
                }
                case "-a" -> raw.setAppendMode(true);
                case "-o" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Path must be specified after -o");
                    }
                    raw.setOutputPath(args[++i]);
                }
                case "-p" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Prefix must be specified after -p");
                    }
                    raw.setFilePrefix(args[++i]);
                }
                default -> {
                    if (arg.startsWith("-")) {
                        throw new IllegalArgumentException("Unknown argument: " + arg);
                    }
                    raw.getInputFiles().add(arg);
                }
            }
        }

        return raw;
    }
}
