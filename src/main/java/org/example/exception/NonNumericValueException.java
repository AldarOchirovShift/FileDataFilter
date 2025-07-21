package org.example.exception;

/**
 * Thrown when a non-numeric value is provided where a numeric value is expected.
 * <p>
 * This exception extends {@link AppException} and is typically used in numeric operations
 * to indicate that an input of an invalid type was encountered.
 * </p>
 *
 * @see AppException
 */
public class NonNumericValueException extends AppException {
    /**
     * Constructs a new exception with a detailed message indicating the actual type received.
     *
     * @param actualType the class of the non-numeric value that caused the exception
     */
    public NonNumericValueException(Class<?> actualType) {
        super("Numeric value required, got " + actualType.getSimpleName());
    }
}
