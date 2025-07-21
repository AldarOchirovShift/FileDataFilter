package org.example.exception;

import lombok.Getter;

/**
 * Base exception for I/O operations failures with file path context.
 * <p>
 * Contains:
 * <ul>
 *   <li>Original error message</li>
 *   <li>File system path where error occurred</li>
 *   <li>Optional root cause exception</li>
 * </ul>
 *
 * @see RuntimeException
 */
@Getter
public class IOExceptionWithPath extends RuntimeException {
    private final String path;

    /**
     * Constructs a new I/O exception with path context.
     * @param message the detail message
     * @param path the filesystem path where error occurred
     */
    public IOExceptionWithPath(String message, String path) {
        super(String.format(ExceptionStringConstants.PATH_TEMPLATE, message, path));
        this.path = path;
    }

    /**
     * Constructs a new I/O exception with path context and cause.
     * @param message the detail message
     * @param path the filesystem path where error occurred
     * @param cause the root cause exception
     */
    public IOExceptionWithPath(String message, String path, Throwable cause) {
        super(String.format(ExceptionStringConstants.PATH_TEMPLATE, message, path), cause);
        this.path = path;
    }
}
