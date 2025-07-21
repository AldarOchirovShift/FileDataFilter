package org.example.service;

import org.example.exception.NumberParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

/**
 * A service class for processing files containing different data types.
 * <p>
 * This class reads an input file line by line, classifies each line as either:
 * <ul>
 *   <li>Integer number (supports underscores as separators)</li>
 *   <li>Floating-point number (supports both '.' and ',' as decimal separators and scientific notation)</li>
 *   <li>String (any content that doesn't match number patterns)</li>
 * </ul>
 * and delegates the writing of processed data to a {@link FileWriter}.
 */
public class FileProcessor {
    /**
     * Pattern for matching integer numbers with optional thousands separators.
     * Supports:
     * <ul>
     *   <li>Optional negative sign</li>
     *   <li>Digits with optional underscore thousands separators (e.g., "1_000_000")</li>
     *   <li>No leading zeros (except for single zero)</li>
     * </ul>
     */
    private static final Pattern INTEGER_PATTERN = Pattern.compile(ServicesStringConstants
            .RegexPatterns.INTEGER_PATTERN);

    /**
     * Pattern for matching floating-point numbers in various formats.
     * Supports:
     * <ul>
     *   <li>Regular decimal notation (e.g., "3.14", "-0,001")</li>
     *   <li>Scientific notation (e.g., "1.23e-10")</li>
     *   <li>Special values: "Infinity", "-Infinity", "NaN" (case insensitive)</li>
     * </ul>
     */
    private static final Pattern FLOAT_PATTERN = Pattern.compile(
            ServicesStringConstants.RegexPatterns.FLOAT_PATTERN,
            Pattern.CASE_INSENSITIVE
    );
    private static final String MAX_LONG_STR = Long.toString(Long.MAX_VALUE);
    private static final String MIN_LONG_STR = Long.toString(Long.MIN_VALUE);
    private final static Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);

    private final PathBuilder pathBuilder;
    private final FileWriter fileWriter;

    /**
     * Constructs a FileProcessor with dependencies.
     *
     * @param pathBuilder helper for building file paths
     * @param fileWriter  writer for outputting processed data
     */
    public FileProcessor(PathBuilder pathBuilder, FileWriter fileWriter) {
        this.pathBuilder = pathBuilder;
        this.fileWriter = fileWriter;
    }

    /**
     * Processes the input file line by line.
     *
     * @param inputFile path to the input file
     * @throws IOException          if an I/O error occurs while reading the file
     * @throws NumberParseException if the string does not contain a parsable number
     */
    public void processFile(String inputFile) throws IOException {
        var inputPath = pathBuilder.buildInput(inputFile);
        LOGGER.info(ServicesStringConstants.Messages.PROCESS_FILE_STARTED, inputPath);
        try (var reader = Files.newBufferedReader(inputPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line.trim());
            }
        }
        LOGGER.info(ServicesStringConstants.Messages.PROCESS_FILE_FINISHED, inputPath);
    }

    /**
     * Processes a single line, classifying it as integer, float or string.
     *
     * @param line the line to process
     */
    private void processLine(String line) {
        if (line.isEmpty()) return;

        try {
            if (isInteger(line)) {
                fileWriter.addInteger(parseLong(line));
            } else if (isFloat(line)) {
                fileWriter.addFloat(parseDouble(line));
            } else {
                fileWriter.addString(line);
            }
        } catch (NumberParseException e) {
            LOGGER.warn(ServicesStringConstants.Messages.MALFORMED_NUMBER_WARNING, line);
        }
    }

    /**
     * Checks if a string is a valid long integer with optional underscores.
     * Valid examples: "123", "-456", "1_000_000".
     * Rejects: null, empty strings, numbers outside long range, and malformed formats.
     *
     * @param s String to check (may be null)
     * @return true if parsable as long with correct formatting
     */
    private boolean isInteger(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        if (!INTEGER_PATTERN.matcher(s).matches()) {
            return false;
        }

        var cleanNum = s.replace("_", "");
        var isNegative = s.startsWith("-");
        var maxLength = isNegative ? MIN_LONG_STR.length() : MAX_LONG_STR.length();

        if (cleanNum.length() > maxLength) {
            return false;
        }

        if (cleanNum.length() == maxLength) {
            return isNegative
                    ? cleanNum.compareTo(MIN_LONG_STR) >= 0
                    : cleanNum.compareTo(MAX_LONG_STR) <= 0;
        }

        return true;
    }

    /**
     * Checks if a string is a valid floating-point number within Java's Double range.
     *
     * <p><b>Supported formats:</b>
     * <ul>
     *   <li>Regular decimals: {@code "3.14"}, {@code "-0,001"}</li>
     *   <li>Scientific notation: {@code "1.23e-10"}, {@code "1,23E+20"}</li>
     *   <li><b>Special values</b>:
     *     {@code "Infinity"}, {@code "-Infinity"}, {@code "NaN"}</li>
     * </ul>
     *
     * <p><b>Rejects:</b>
     * <ul>
     *   <li>Numbers that overflow to Infinity (e.g. {@code "1.8e309"})</li>
     *   <li>Malformed numbers (e.g. {@code "1.2.3"}, {@code "1e"})</li>
     *   <li>{@code null} or empty strings</li>
     * </ul>
     *
     * @param s the string to check (may be null)
     * @return true if the string represents a valid double number
     */
    private boolean isFloat(String s) {
        if (s == null || !FLOAT_PATTERN.matcher(s).matches()) {
            return false;
        }

        if (s.equalsIgnoreCase(ServicesStringConstants.SpecialFloatOptions.NAN)
                || s.equalsIgnoreCase(ServicesStringConstants.SpecialFloatOptions.INF)) {
            return true;
        }

        try {
            var d = Double.parseDouble(s.replace(',', '.'));
            return !Double.isInfinite(d)
                    || s.equalsIgnoreCase(ServicesStringConstants.SpecialFloatOptions.NEG_INF);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a string into a long integer, removing any underscore separators.
     *
     * @param s the string to parse
     * @return parsed long value
     * @throws NumberParseException if the string cannot be parsed as a valid long integer
     *                              (contains the original problematic value)
     */
    private long parseLong(String s) {
        try {
            return Long.parseLong(s.replace("_", ""));
        } catch (NumberFormatException e) {
            throw new NumberParseException(ServicesStringConstants.Messages.FAILED_PARSE_LONG_VALUE, s, e);
        }
    }

    /**
     * Parses a string into a double, handling special cases.
     * Converts commas to dots as decimal separators.
     *
     * @param s the string to parse
     * @return parsed double value
     * @throws NumberParseException if the string cannot be parsed as a valid double
     *                              (contains the original problematic value)
     */
    private double parseDouble(String s) {
        try {
            s = s.replace(",", ".");
            if (s.equalsIgnoreCase(ServicesStringConstants.SpecialFloatOptions.INF)) {
                return Double.POSITIVE_INFINITY;
            } else if (s.equalsIgnoreCase(ServicesStringConstants.SpecialFloatOptions.NEG_INF)) {
                return Double.NEGATIVE_INFINITY;
            } else if (s.equalsIgnoreCase(ServicesStringConstants.SpecialFloatOptions.NAN)) {
                return Double.NaN;
            }
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new NumberParseException(ServicesStringConstants.Messages.FAILED_PARSE_DOUBLE_VALUE, s, e);
        }
    }
}
