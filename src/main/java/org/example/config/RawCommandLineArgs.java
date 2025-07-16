package org.example.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents raw command-line arguments parsed into a structured format.
 * <p>
 * This class holds the parsed values of command-line options and input files.
 * It is typically populated by {@link org.example.cli.CommandLineArgsParser}.
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *   <li>{@code shortStats} - if {@code true}, enables short statistics mode (mutually exclusive with {@code fullStats})</li>
 *   <li>{@code fullStats} - if {@code true}, enables full statistics mode (mutually exclusive with {@code shortStats})</li>
 *   <li>{@code appendMode} - if {@code true}, enables append mode for output operations</li>
 *   <li>{@code outputPath} - the output path specified by the {@code -o} option (nullable)</li>
 *   <li>{@code filePrefix} - the file prefix specified by the {@code -p} option (nullable)</li>
 *   <li>{@code inputFiles} - list of input files (non-option arguments)</li>
 * </ul>
 *
 * @see org.example.cli.CommandLineArgsParser
 * @see lombok.Data (provides getters, setters, toString, equals, and hashCode)
 */
@Data
public class RawCommandLineArgs {
    private boolean shortStats;
    private boolean fullStats;
    private boolean appendMode;
    private String outputPath;
    private String filePrefix;
    private List<String> inputFiles = new ArrayList<>();
}