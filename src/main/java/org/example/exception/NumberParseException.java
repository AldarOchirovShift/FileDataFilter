package org.example.exception;

import lombok.Getter;

/**
 * Thrown when numeric value parsing fails.
 * <p>
 * Contains:
 * <ul>
 *   <li>Original invalid value</li>
 *   <li>Detailed error message</li>
 *   <li>Optional root cause exception</li>
 * </ul>
 *
 * @see AppException
 */
@Getter
public class NumberParseException extends AppException {
    private final String value;

    public NumberParseException(String message, String value, Throwable cause) {
        super(String.format(ExceptionStringConstants.NUMBER_PARSE_TEMPLATE, message, value), cause);
        this.value = value;
    }
}
