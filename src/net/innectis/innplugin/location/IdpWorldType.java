package net.innectis.innplugin.location;

import java.util.HashMap;

/**
 * @author Hret
 * <p/>
 * This enum shows the different types of worlds used in the IDP.
 * <p/>
 * If a world is non-existing in this enum (and not dynamicly loaded) it means
 * that the given world is not tracked by the IDP and should not be used.
 * <p/>
 * Methods in the enum are 'default' or 'package' protected by design. These
 * methods should not be used outside the <code>net.innectis.innplugin.location</code>
 * package.
 *
 */
public enum IdpWorldType {

    /** The main innectis world */
    INNECTIS("innectis"),
    /** A world where lots conform naturally to the environment **/
    NATURALWORLD("naturalworld"),
    /** Old world - this replaces current innectis on a temporary basis, to transfer lots over */
    OLDWORLD("oldworld"),
    /** Resource world */
    RESWORLD("resworld"),
    /** Creative world map */
    CREATIVEWORLD("creativeworld"),
    /** Nether world */
    NETHER("nether"),
    /** aether */
    AETHER("aether"),
    /** The End */
    THE_END("endworld"),
    /** event world */
    EVENTWORLD("eventworld"),
    /** This is a dynamicly loaded world. */
    DYNAMIC(null),
    /**
     * This is not a valid world type. <br/>
     * This is used as a placeholder for non-tracked worlds.
     */
    NONE(null);
//
    /**
     * The name of the world which is also the name of the folder its stored in.</br>
     * Can be null!
     */
    public final String worldname;

    private IdpWorldType(String worldname) {
        this.worldname = worldname;
    }
    /**
     * STATIC METHODS
     */
    /** Cache with all worlds for faster access, */
    private static final HashMap<String, IdpWorldType> worldtypes;

    static {
        worldtypes = new HashMap<String, IdpWorldType>(values().length);
        // Caching of the enum
        for (IdpWorldType settings : values()) {
            // Only load normal world (not dynamic and none)
            if (settings.worldname != null) {
                worldtypes.put(settings.worldname, settings);
            }
        }
    }

    /**
     * Adds a dynamic world to the list.
     * @param world
     */
    static Boolean addDynamicWorld(IdpWorld world) {
        synchronized (worldtypes) {
            // Check if not already exists
            if (!worldtypes.containsKey(world.getName())) {
                worldtypes.put(world.getName(), DYNAMIC);
                return true;
            }
            return false;
        }
    }

    /**
     * Removes a dynamic world to the list.
     * @param world
     */
    static Boolean removeDynamicWorld(IdpWorld world) {
        synchronized (worldtypes) {
            // Only allow non-dynamic worlds to be unloaded
            if (worldtypes.containsKey(world.getName())
                    && worldtypes.get(world.getName()) == DYNAMIC) {
                worldtypes.remove(world.getName());
                return true;
            }
            return false;
        }
    }

    /**
     * Looks up the worldtype of the given world.
     * @param world
     * @return none if world not found
     */
    static IdpWorldType getIdpWorldtype(IdpWorld world) {
        synchronized (worldtypes) {
            if (worldtypes.containsKey(world.getName())) {
                return worldtypes.get(world.getName());
            }

            return NONE;
        }
    }

    /**
     * Gets the IdpWorldType from the specified world name
     * @param worldName
     * @return
     */
    public static IdpWorldType getIdpWorldType(String worldName) {
        for (IdpWorldType type : worldtypes.values()) {
            if (type.worldname == null) {
                continue;
            }

            if (type.worldname.equalsIgnoreCase(worldName)) {
                return type;
            }
        }

        return null;
    }

    /**
     * Removes this type from the worldtype list.
     * This method should only be used when the worlds are loaded
     */
    @Deprecated
    protected void remove() {
        synchronized (worldtypes) {
            worldtypes.remove(worldname);
        }
    }

}
