package net.innectis.innplugin.location;

import net.innectis.innplugin.objects.EnderChestContents;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.player.InventoryType;

/**
 * @author Hret
 * <p/>
 * This is the interface that contains all methods that are available for the worldsettings.
 */
public interface IdpWorldSettings {

    /** The worldid (get on runtime) of this world */
    public int getWorldId();

    /** The name of the world */
    public String getWorldName();

    /** The maptype of this world */
    public MapType getMaptype();

    /** The type of enderchests this world uses */
    public EnderChestContents.EnderContentsType getEnderchestType();

    /** The inventoryType that is used on the given world.
     * If the player switches to a world with a different inventoryType it will be changed. */
    public InventoryType getInventoryType();

    /**
     * The size of the world.
     * Ea. the max amount of block that players may walk in any direction.
     */
    public int getWorldSize();

    /** The time it takes for a world to be marked inactive. (marked in minutes)
     * If the unloadtime is '0' the world will never unload */
    public long getUnloadTimeMilis();

    /**
     * The amount of animals that can be inside a chunk
     * @return
     */
    public int getAnimalSpawnLimit();

    /**
     * The amount of monsters that can be inside a chunk
     * @return
     */
    public int getMonsterSpawnLimit();

    /**
     * Amount of ticks between animal spawns
     * @return
     */
    public int getTicksPerAnimalSpawn();

    /**
     * Amount of ticks between monster spawns
     * @return
     */
    public int getTicksPerMonsterSpawn();

    /**
     * The amount of water animals that can be inside a chunk
     * @return
     */
    public int getWaterAnimalLimit();

    /**
     * A static seed value for the given world
     * @return
     */
    public long getStaticSeed();

    /**
     * A boolean that checks if flying is allowed in this world
     * @return
     */
    public boolean isFlightAllowed();

    /**
     * A boolean that checks if flying is in use by default in this world
     * @return
     */
    public boolean isFlightDefault();

    /**
     *  Determines if this world has a daylight cycle
     *  @return
     */
    public boolean hasDaylightCycle();

    /**
     * Determines if this world has weather
     * @return
     */
    public boolean hasWeather();

}
