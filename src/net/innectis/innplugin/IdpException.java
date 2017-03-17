package net.innectis.innplugin;

/**
 *
 * @author Hret
 *
 * The parent exception for exceptions specified in the IDP. (if not assigned to others)
 */
public class IdpException extends Exception {

    /**
     * Creates a new instance of <code>IdpException</code> without detail message.
     */
    public IdpException() {
    }

    /**
     * Constructs an instance of <code>IdpException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IdpException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>IdpException</code> with the specified detail message.
     * @param msg the detail message.
     * @param cause the cause of this exception.
     */
    public IdpException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
}
