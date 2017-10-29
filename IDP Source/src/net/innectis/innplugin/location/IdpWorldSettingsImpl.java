package net.innectis.innplugin.location;

import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.player.InventoryType;

/**
 * @author Hret
 * <p/>
 * This object contains the settings for a world.
 * Normally this class will not be directly called, but only used trough the interface.
 */
class IdpWorldSettingsImpl implements IdpWorldSettings {

    protected int ticksperanimalspawn = 400; //def 400
    protected int tickspermonsterspawn = 1; // def 1
    protected int monsterspawnlimit = 70; // def 70
    protected int animalspawnlimit = 15; // def 15
    protected int wateranimallimit = 5; // def 5
    protected long staticWorldSeed = 0;
    protected boolean isFlightAllowed = true; // def true
    protected boolean isFlightDefault = false; // def false
    /** The id of the world that is run from runtime */
    protected int worldid;
    /** The ingame world name */
    protected String worldname;
    /** The maptype of this world */
    protected MapType maptype;
    /** The max radius a player can walk in each direction (this is a square) */
    protected int worldSize;
    /** The time it takes for a world to be marked inactive. (marked in minutes)
     * If the unloadtime is '0' the world will never unload */
    protected long unloadTime;
    /** The inventoryType that is used on the given world.
     * If the player switches to a world with a different inventoryType it will be changed. */
    protected InventoryType inventoryType;
    /** The type of the enderchest */
    protected EnderContentsType endertype;
    /** Checks if the world has a daylight cycle */
    protected boolean hasDaylightCycle;
    /** Checks if the world has weather */
    protected boolean hasWeather;

    /**
     * Makes a new IdpWorldSettingsImpl object with the given settings.
     * @param worldid
     * The local runtime worldid
     * @param worldName
     * The name of the world as shown to players.
     * @param maptype
     * The type of map the world uses
     * @param inventory
     * The type of inventory the world uses
     * @param endertype
     * The type of enderchest the world uses
     * @param worldsize
     * The size of the world. </b>
     * With a value of '<b>1000</b>' the coords will go from -1000 to 1000.
     * @param unloadTime
     * The time it takes to unload chunks.
     * @param isFlightAllowed
     * Whether flying is allowed in this world
     * @param isFlyingDefault
     * Whether flying is the default case for this world
     */
    IdpWorldSettingsImpl(int worldid, String worldName, MapType maptype, InventoryType inventory, EnderContentsType endertype, int worldsize, long unloadTime, boolean isFlightAllowed, boolean isFlightDefault, boolean hasDaylightCycle, boolean hasWeather) {
        this.worldid = worldid;
        this.worldname = worldName;
        this.maptype = maptype;
        this.inventoryType = inventory;
        this.endertype = endertype;
        this.worldSize = worldsize;
        this.unloadTime = unloadTime;
        this.isFlightAllowed = isFlightAllowed;
        this.isFlightDefault = isFlightDefault;
        this.hasDaylightCycle = hasDaylightCycle;
        this.hasWeather = hasWeather;
    }

    IdpWorldSettingsImpl(int worldid, String worldName, MapType maptype, InventoryType inventory, EnderContentsType endertype, int worldsize, long unloadTime, boolean isFlightAllowed, boolean isFlightDefault, boolean hasDaylightCycle, int ticksperanimalspawn, int tickspermonsterspawn, int monsterspawnlimit, int animalspawnlimit, int wateranimallimit, long staticWorldSeed) {
        this.worldid = worldid;
        this.worldname = worldName;
        this.maptype = maptype;
        this.inventoryType = inventory;
        this.endertype = endertype;
        this.worldSize = worldsize;
        this.unloadTime = unloadTime;
        this.isFlightAllowed = isFlightAllowed;
        this.isFlightDefault = isFlightDefault;
        this.hasDaylightCycle = hasDaylightCycle;
        this.ticksperanimalspawn = ticksperanimalspawn;
        this.tickspermonsterspawn = tickspermonsterspawn;
        this.monsterspawnlimit = monsterspawnlimit;
        this.animalspawnlimit = animalspawnlimit;
        this.wateranimallimit = wateranimallimit;
        this.staticWorldSeed = staticWorldSeed;
        this.hasWeather = true;
        this.hasDaylightCycle = true;
    }

    /**
     * Makes a new IdpWorldSettingsImpl object with the given settings without an ID.
     * @param worldid
     * The local runtime worldid
     * @param worldName
     * The name of the world as shown to players.
     * @param maptype
     * The type of map the world uses
     * @param inventory
     * The type of inventory the world uses
     * @param endertype
     * The type of enderchest the world uses
     * @param worldsize
     * The size of the world. </b>
     * With a value of '<b>1000</b>' the coords will go from -1000 to 1000.
     * @param unloadTime
     * The time it takes to unload chunks.
     * @param spawnlocation
     * The default spawnlocation. (if none supply null)
     */
    protected IdpWorldSettingsImpl(String worldName, MapType maptype, InventoryType inventory, EnderContentsType endertype, int worldsize, long unloadTime) {
        this.worldid = -1;
        this.worldname = worldName;
        this.maptype = maptype;
        this.inventoryType = inventory;
        this.endertype = endertype;
        this.worldSize = worldsize;
        this.unloadTime = unloadTime;

        // Don't make this an option for dynamic worlds
        this.hasDaylightCycle = true;
    }

    /**
     * The maptype for the world belonging to these settings.
     * @return the maptype.
     */
    @Override
    public MapType getMaptype() {
        return maptype;
    }

    /**
     * The enderchesttype for the world belonging to these settings.
     * @return
     */
    @Override
    public EnderContentsType getEnderchestType() {
        return endertype;
    }

    /**
     * Unload time in milliseconds
     * @return unload time
     */
    @Override
    public long getUnloadTimeMilis() {
        return unloadTime * 60 * 1000;
    }

    /**
     * Returns the friendly inname name of the world
     * @return
     */
    @Override
    public String getWorldName() {
        return worldname;
    }

    /**
     * The ID of the world.
     * <b>This can change when the server is restarted!</b>
     * @return
     */
    @Override
    public int getWorldId() {
        return worldid;
    }

    /**
     * The inventory type that is used on this world.
     * @return
     */
    @Override
    public InventoryType getInventoryType() {
        return inventoryType;
    }

    /**
     * The size of the world in each direction.
     * <p/>
     * If this will return a value of '<b>1000</b>' the coords should be going
     * from -1000 to 1000.
     * <p/>
     * @return The size of the world in 1 direction from the spawn.
     */
    @Override
    public int getWorldSize() {
        return worldSize;
    }

    /**
     * The amount of animals that can be inside a chunk
     * @return
     */
    @Override
    public int getAnimalSpawnLimit() {
        return animalspawnlimit;
    }

    /**
     * The amount of monsters that can be inside a chunk
     * @return
     */
    @Override
    public int getMonsterSpawnLimit() {
        return monsterspawnlimit;
    }

    /**
     * Amount of ticks between animal spawns
     * @return
     */
    @Override
    public int getTicksPerAnimalSpawn() {
        return ticksperanimalspawn;
    }

    /**
     * Amount of ticks between monster spawns
     * @return
     */
    @Override
    public int getTicksPerMonsterSpawn() {
        return tickspermonsterspawn;
    }

    /**
     * The amount of water animals that can be inside a chunk
     * @return
     */
    @Override
    public int getWaterAnimalLimit() {
        return wateranimallimit;
    }

    /**
     * A static world seed, or 0 if none
     * @return
     */
    @Override
    public long getStaticSeed() {
        return staticWorldSeed;
    }

    @Override
    public boolean isFlightAllowed() {
        return isFlightAllowed;
    }

    @Override
    public boolean isFlightDefault() {
        return isFlightDefault;
    }

    @Override
    public boolean hasDaylightCycle() {
        return hasDaylightCycle;
    }

    @Override
    public boolean hasWeather() {
        return hasWeather;
    }

}
