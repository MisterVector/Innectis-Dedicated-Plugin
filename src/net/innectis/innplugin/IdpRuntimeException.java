package net.innectis.innplugin;

/**
 *
 * @author Hret
 *
 * The parent exception for exceptions that can occur at runtime in the IDP.
 * When these are thrown its due to code being in a wrong state.
 */
public class IdpRuntimeException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>IdpException</code> without detail message.
     */
    public IdpRuntimeException() {
    }

    /**
     * Constructs an instance of
     * <code>IdpRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IdpRuntimeException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>IdpRuntimeException</code> with the specified detail message.
     * @param msg the detail message.
     * @param cause the cause of this exception.
     */
    public IdpRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
