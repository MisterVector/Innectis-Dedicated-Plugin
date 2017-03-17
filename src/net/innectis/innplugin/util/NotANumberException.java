package net.innectis.innplugin.util;

/**
 *
 * @author Hret
 *
 *
 * This exception is thrown when a formatter tries to format something into a numaric value when the value itself is not numeric.
 * For instance:
 * "a" converterd to an Integer could throw this exception
 */
public class NotANumberException extends NumberFormatException {

    /**
     * Creates a new instance of <code>NotANumberException</code> without detail message.
     */
    public NotANumberException() {
        super("NANException");
    }

    /**
     * Constructs an instance of <code>NotANumberException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NotANumberException(String msg) {
        super(msg);
    }
    
}
