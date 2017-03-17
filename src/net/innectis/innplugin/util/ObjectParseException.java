package net.innectis.innplugin.util;

/**
 *
 * @author Hret
 */
public class ObjectParseException extends Exception {

    private Exception innerException;

    /**
     * Creates a new instance of <code>ObjectParseException</code> without detail message.
     */
    public ObjectParseException() {
    }

    /**
     * Constructs an instance of <code>ObjectParseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ObjectParseException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ObjectParseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ObjectParseException(String msg, Exception innerException) {
        this(msg);
        this.innerException = innerException;
    }

    /**
     * Returns the innerexception (if any)
     * @return
     */
    public Exception getInnerException() {
        return innerException;
    }
    
}
