package net.innectis.innplugin.player.tinywe;

/**
 *@author Hret
 *
 * Exception thrown when a tinywe action is not finished.
 */
public class TWEActionNotFinishedException extends TinyWeException {

    /**
     * Creates a new instance of <code>TWEActionNotFinishedException</code> without detail message.
     */
    public TWEActionNotFinishedException() {
    }

    /**
     * Constructs an instance of <code>TWEActionNotFinishedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TWEActionNotFinishedException(String msg) {
        super(msg);
    }
    
}
