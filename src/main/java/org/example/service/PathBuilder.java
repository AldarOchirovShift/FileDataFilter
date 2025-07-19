package org.example.service;

import java.nio.file.Path;

/**
 * A utility class for constructing file paths relative to the application's workspace.
 * <p>
 * This builder creates paths for input and output files, ensuring they are either:
 * <ul>
 *   <li>Resolved relative to the application's working directory (if relative paths are provided)</li>
 *   <li>Used as-is (if absolute paths are provided)</li>
 * </ul>
 *
 * <h2>Typical Usage</h2>
 * <pre>
 * PathBuilder builder = new PathBuilder();
 * Path input = builder.buildInput("data.txt");  // → $WORK_DIR/input/data.txt
 * Path output = builder.buildOutput("result.txt"); // → $WORK_DIR/output/result.txt
 * </pre>
 */
public class PathBuilder {
    /**
     * The current user working directory (where the JVM was launched from).
     */
    private static final String USER_DIR = System.getProperty("user.dir");

    /**
     * Default input subdirectory name ("input").
     */
    private static final String INPUT_DIR = "input";

    /**
     * Default output subdirectory name ("output").
     */
    private static final String OUTPUT_DIR = "output";

    /**
     * Builds a path for an input file.
     *
     * @param inputPath the input file path (relative or absolute)
     * @return resolved Path object
     * @throws IllegalArgumentException if the path is invalid
     * @see #build(String, String)
     */
    public Path buildInput(String inputPath) {
        return build(INPUT_DIR, inputPath);
    }

    /**
     * Builds a path for an output file.
     *
     * @param outputPath the output file path (relative or absolute)
     * @return resolved Path object
     * @throws IllegalArgumentException if the path is invalid
     * @see #build(String, String)
     */
    public Path buildOutput(String outputPath) {
        return build(OUTPUT_DIR, outputPath);
    }

    /**
     * Internal path construction method.
     *
     * @param dirPath the subdirectory (either "input" or "output")
     * @param path the file path to resolve
     * @return constructed Path
     * @throws IllegalArgumentException if Path.of() throws exception
     */
    private Path build(String dirPath, String path) {
        var resultPath = Path.of(path);
        resultPath = !resultPath.isAbsolute()
                ? Path.of(USER_DIR).resolve(dirPath).resolve(path)
                : resultPath;
        return resultPath;
    }
}
