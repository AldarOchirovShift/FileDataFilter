package org.example.exception;

/**
 * Thrown when a non-string value is provided where a string value is expected.
 * <p>
 * This exception indicates that an operation requiring a string input
 * received an incompatible type instead. It extends the base {@link AppException}
 * to provide more specific error information.
 * </p>
 *
 * @see AppException
 */
public class NonStringValueException extends AppException {
    /**
     * Constructs a new exception with a message specifying the actual received type.
     *
     * @param actualType the class of the non-string value that triggered this exception
     *                   (will be shown in the error message)
     */
    public NonStringValueException(Class<?> actualType) {
        super("String value required, got " + actualType.getSimpleName());
    }
}
