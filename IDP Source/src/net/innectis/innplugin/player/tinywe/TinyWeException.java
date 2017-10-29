package net.innectis.innplugin.player.tinywe;

import net.innectis.innplugin.IdpException;

/**
 * @author Hret
 *
 * Base Exception for exceptions in TinyWE
 *
 */
public class TinyWeException extends IdpException {

    /**
     * Creates a new instance of <code>TinyWeException</code> without detail message.
     */
    public TinyWeException() {
    }

    /**
     * Constructs an instance of <code>TinyWeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TinyWeException(String msg) {
        super(msg);
    }

}
