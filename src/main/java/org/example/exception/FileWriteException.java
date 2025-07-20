package org.example.exception;

/**
 * Thrown when file writing operations fail.
 * <p>
 * <b>Typical cases:</b>
 * <ul>
 *   <li>Permission denied</li>
 *   <li>Disk full</li>
 *   <li>File system errors</li>
 * </ul>
 * Always contains the target file path where failure occurred.
 *
 * @see IOExceptionWithPath
 */
public class FileWriteException extends IOExceptionWithPath {
    /**
     * Constructs a new file write exception.
     * @param message the detail message describing the write failure
     * @param path the target file path where write failed
     */
    public FileWriteException(String message, String path) {
        super(message, path);
    }

    /**
     * Constructs a new file write exception with cause.
     * @param message the detail message
     * @param path the target file path
     * @param cause the underlying I/O exception that caused the failure
     */
    public FileWriteException(String message, String path, Throwable cause) {
        super(message, path, cause);
    }
}
