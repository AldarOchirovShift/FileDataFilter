package org.example.exception;

/**
 * Thrown when application configuration is invalid or inconsistent.
 * <p>
 * Typical cases:
 * <ul>
 *   <li>Invalid command line arguments</li>
 *   <li>Conflicting configuration parameters</li>
 *   <li>Missing required configuration values</li>
 * </ul>
 *
 * @see AppException
 */
public class ConfigurationException extends AppException {
    /**
     * Constructs a new configuration exception with the specified detail message.
     * @param message the detail message describing the configuration error
     */
    public ConfigurationException(String message) {
        super(message);
    }
}
