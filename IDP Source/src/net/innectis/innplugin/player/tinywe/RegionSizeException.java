package net.innectis.innplugin.player.tinywe;

/**
 *
 * @author Hret
 *
 * This is an exception that is thrown when the size of a TinyWE region is to large.
 **/
public class RegionSizeException extends TinyWeException {

    /**
     * Creates a new instance of <code>RegionSizeException</code> without detail message.
     */
    public RegionSizeException() {
    }

    /**
     * Constructs an instance of <code>RegionSizeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RegionSizeException(String msg) {
        super(msg);
    }
    
}
