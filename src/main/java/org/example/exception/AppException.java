package org.example.exception;

/**
 * Base exception class for all application-specific exceptions.
 * <p>
 * Provides consistent exception handling across the application.
 * All custom exceptions should extend this class.
 *
 * @see RuntimeException
 */
public class AppException extends RuntimeException {
    /**
     * Constructs a new application exception with the specified detail message.
     * @param message the detail message (saved for later retrieval by getMessage())
     */
    public AppException(String message) {
        super(message);
    }

    /**
     * Constructs a new application exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause (saved for later retrieval by getCause())
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
