package net.innectis.innplugin.external;

/**
 * An enum that lists every external plugin with an API that we use
 * for various features
 *
 * @author AlphaBlend
 */
public enum LibraryType {

    NOCHEATPLUS("nocheat"),
    WORLDEDIT("worldedit"),
    VOTIFIER("votifier");

    private final String properName;
    private final String[] dependencies;

    private LibraryType(String properName) {
        this.properName = properName;
        this.dependencies = new String[0];
    }

    private LibraryType(String properName, String... dependencies) {
        this.properName = properName;
        this.dependencies = dependencies;
    }

    /**
     * Returns the proper name. This is required as the proper name
     * is used to get a plugin object, and the name is case-sensitive
     * @return
     */
    public String getName() {
        return properName;
    }

    /**
     * Gets any dependencies of this external library
     * @return
     */
    public String[] getDependencies() {
        return dependencies;
    }

}
