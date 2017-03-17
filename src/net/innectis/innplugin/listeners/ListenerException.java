package net.innectis.innplugin.listeners;

import net.innectis.innplugin.IdpException;

/**
 *
 * @author Hret
 *
 * Exception that gets thrown if the listenermanager cannot invoke a method in a listener.
 */
public class ListenerException extends IdpException {

    /**
     * Constructs an instance of
     * <code>IdpException</code> without a specified detail message.
     */
    public ListenerException() {
    }

    /**
     * Constructs an instance of
     * <code>IdpException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ListenerException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>IdpException</code> with the specified detail message.
     * @param msg the detail message.
     * @param cause the cause of this exception.
     */
    public ListenerException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
