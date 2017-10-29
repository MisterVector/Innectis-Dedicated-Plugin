package net.innectis.innplugin.player;

import net.innectis.innplugin.IdpException;

/**
 *
 * @author Hret
 *
 * Thrown when the switching of the inventory doesn't go as it should.
 */
public class InventorySwitchException extends IdpException {

    /**
     * Creates a new instance of <code>InventorySwitchException</code> without detail message.
     */
    public InventorySwitchException() {
    }

    /**
     * Constructs an instance of <code>InventorySwitchException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InventorySwitchException(String msg) {
        super(msg);
    }
    
}
