package net.innectis.innplugin.external;

/**
 * An interface that describes an external library class
 *
 * @author AlphaBlend
 */
public interface IExternalLibrary {
    /**
     * Indicates if this library is an alternative version. Its purpose is
     * to determine if the original library is not loaded, and simply
     * means there is an alternate version
     * @return
     */
    public boolean isAlternative();
    
    /**
     * This method initializes the support for an library if needed.
     * @return true if initalized, false if error.
     */
    public void initialize() throws LibraryInitalizationError;
    
}
