package net.innectis.innplugin.external;

/**
 *
 * @author Hret
 *
 * Exception that gets thrown when an external library cannot be initalized.
 */
public class LibraryInitalizationError extends Exception {

    /**
     * Creates a new instance of
     * <code>LibraryInitalizationError</code> without detail message.
     */
    public LibraryInitalizationError() {
    }

    /**
     * Constructs an instance of
     * <code>LibraryInitalizationError</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LibraryInitalizationError(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>LibraryInitalizationError</code> with the specified detail message.
     * @param msg the detail message.
     * @param cause the cause of the error.
     */
    public LibraryInitalizationError(String message, Exception cause) {
        super(message, cause);
    }

}
