package net.innectis.innplugin.external;

import net.innectis.innplugin.IdpRuntimeException;

/**
 *
 * @author Hret
 *
 * This exception gets thrown when a dependency is not available that is needed to execute a function.
 */
public class MissingDependencyException extends IdpRuntimeException {

    /**
     * Creates a new instance of
     * <code>NotLoadedPluginException</code> without detail message.
     */
    public MissingDependencyException() {
    }

    /**
     * Constructs an instance of
     * <code>NotLoadedPluginException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public MissingDependencyException(String msg) {
        super(msg);
    }

}
