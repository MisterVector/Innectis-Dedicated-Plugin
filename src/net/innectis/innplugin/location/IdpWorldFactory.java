package net.innectis.innplugin.location;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import net.innectis.innplugin.handlers.datasource.FileHandler;
import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.IdpRuntimeException;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EnderChestContents.EnderContentsType;
import net.innectis.innplugin.location.worldgenerators.MapType;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

/**
 * @author Hret
 * <p/>
 * This is a factory object for worlds. This static handler will handle loading worlds
 * from a string or IdpWorldType value.
 * <p/>
 * Additionally it's keep track fo worldSetting objects. *
 */
public class IdpWorldFactory {

    private IdpWorldFactory() {
    }
    /**
     * The map will all the settings of the worlds that are tracked by the IDP.
     */
    private static final HashMap<String, IdpWorldSettings> worldSettings;
    private static final HashMap<String, IdpWorld> worldCache = new HashMap<String, IdpWorld>();
    /** The counter that will give the worldid's (current number is next empty id). */
    private static volatile int worldcounter;
    private static final Object _dynamicSyncLock = new Object();

    /** Register the default world settings */
    static {
        worldSettings = new HashMap<String, IdpWorldSettings>(10);
        int counter = 0;

        worldSettings.put(IdpWorldType.INNECTIS.worldname, new IdpWorldSettingsImpl(counter++, "Main World", MapType.DEFAULT, InventoryType.MAIN, EnderContentsType.NORMAL, 5000, 0, true, false, true, true));
        worldSettings.put(IdpWorldType.NATURALWORLD.worldname, new IdpWorldSettingsImpl(counter++, "Natural World", MapType.DEFAULT, InventoryType.MAIN, EnderContentsType.NORMAL, 5000, 0, true, false, true, true));
        //worldSettings.put(IdpWorldType.OLDWORLD.worldname, new IdpWorldSettingsImpl(counter++, "Old World", MapType.DEFAULT, InventoryType.MAIN, EnderContentsType.NORMAL, 5000, 0, true, false, true, true));
        worldSettings.put(IdpWorldType.RESWORLD.worldname, new IdpWorldSettingsImpl(counter++, "Resource Zone", MapType.DEFAULT, InventoryType.MAIN, EnderContentsType.NORMAL, 2500, 0, true, false, true, 1000, 1000, 70, 10, 5, 0));
        worldSettings.put(IdpWorldType.CREATIVEWORLD.worldname, new IdpWorldSettingsImpl(counter++, "Creative World", MapType.DEFAULT, InventoryType.CREATIVE, EnderContentsType.NONE, 5000, 0, true, false, true, false));
        worldSettings.put(IdpWorldType.NETHER.worldname, new IdpWorldSettingsImpl(counter++, "Nether", MapType.NETHER, InventoryType.MAIN, EnderContentsType.NORMAL, 2000, 0, false, false, false, false));
        worldSettings.put(IdpWorldType.AETHER.worldname, new IdpWorldSettingsImpl(counter++, "The Aether", MapType.AETHER, InventoryType.MAIN, EnderContentsType.NORMAL, 2000, 0, true, true, false, false));
        worldSettings.put(IdpWorldType.EVENTWORLD.worldname, new IdpWorldSettingsImpl(counter++, "Event World", MapType.EVENTWORLD, InventoryType.EVENTWORLD, EnderContentsType.NONE, 700, 0, true, false, false, false));
        worldSettings.put(IdpWorldType.THE_END.worldname, new IdpWorldSettingsImpl(counter++, "The End", MapType.THE_END, InventoryType.MAIN, EnderContentsType.NORMAL, 5000, 0, true, false, false, false));

        worldcounter = counter;
    }

    /**
     * Looks up the IdpWorldsettings for this IdpWorld.
     * @param world
     * @return The worldSettings or NULL if not a valid world.
     */
    static IdpWorldSettings getSettings(IdpWorld world) {
        return getSettings(world.getName());
    }

    /**
     * Looks up the IdpWorldSettings for the world with given name.
     * @param world
     * @return
     */
    private static IdpWorldSettings getSettings(String world) {
        synchronized (worldSettings) {
            return worldSettings.get(world);
        }
    }

    /**
     * Looks up the world with the given name on the server.
     *
     * @param name
     * @return The world if it exists, or null if not.
     */
    public static IdpWorld getWorld(String name) {
        if (StringUtil.stringIsNullOrEmpty(name)) {
            return null;
        }

        IdpWorld world = worldCache.get(name.toLowerCase());

        if (world == null) {
            World bukkitworld = Bukkit.getWorld(name);

            if (bukkitworld != null) {
                world = new IdpWorld(bukkitworld);
                worldCache.put(name.toLowerCase(), world);
            }
        }

        return world;
    }

    /**
     * Looks up the world that belongs to the enumtype.
     * <p/>
     * Note: supplying this method with the
     * <code>DYNAMIC</code> value will
     * result in an IdpRuntimeException!
     * <p/>
     * @param type
     * @return The world or null if not found.
     */
    public static IdpWorld getWorld(IdpWorldType type) {
        switch (type) {
            case DYNAMIC:
                throw new IdpRuntimeException("Cannot load a dynamic world with the enum value!");
            case NONE:
                return null;
            default:
                return getWorld(type.worldname);
        }
    }

    /**
     * Register (and load) a new dynamic world with the given settings.
     * @param dataname
     * The name of the world.
     * <p/>
     * This name must be longer then 4 character and not already be used by a different (also loaded) world.
     * Supplying an invalid name will result in an <code>IdpRuntimeException</code>.
     * @param settings
     * The settings to use for this world.
     * @return
     */
    public static int registerDynamicWorld(String dataname, IdpDynamicWorldSettings settings) {
        synchronized (_dynamicSyncLock) {
            if (dataname == null || dataname.length() < 4) {
                throw new IdpRuntimeException("Invalid world data name");
            }

            synchronized (worldSettings) {
                if (getSettings(dataname) != null) {
                    throw new IdpRuntimeException("Trying to add a duplicate world!");
                }

                // Get the next ID.
                settings.setWorldid(worldcounter++);
                // Add it
                worldSettings.put(dataname, settings);
            }

            try {
                WorldCreator wc = getWorldCreator(dataname, settings);
                IdpWorld world = new IdpWorld(Bukkit.createWorld(wc));
                worldCache.put(world.getName().toLowerCase(), world);

                IdpWorldType.addDynamicWorld(world);
                return settings.getWorldId();
            } catch (Exception ex) {
                InnPlugin.logError("Failed to register a Dynamic World!", ex);
                // On failure remove the worldsettings.
                synchronized (worldSettings) {
                    worldSettings.remove(dataname);
                }
                return -1;
            }
        }
    }

    /**
     * Unregister (and unload) a dynamic world with the given settings.
     * <p/>
     * If there are players inside this world. They will be teleported to spawn.
     * The world will be saved and unloaded.
     * <p/>
     * @param world
     * The world to remove. This must be a Dynamic world! If supplied with a
     * non dynamic world will result in an <code>IdpRuntimeException</code>!
     * @param deleteworldfiles
     * If the worldfolder should be deleted.
     * @return
     */
    public static void removeDynamicWorld(IdpWorld world, Boolean deleteworldfiles) {
        synchronized (_dynamicSyncLock) {
            if (world.getWorldType() != IdpWorldType.DYNAMIC) {
                throw new IdpRuntimeException("This is not a dynamic world.");
            }

            // Remove the WorldType (Which will disallow teleporting)
            IdpWorldType.removeDynamicWorld(world);

            // Remove all players in the world
            for (IdpPlayer player : world.getPlayers()) {
                player.teleport(WarpHandler.getSpawn(), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
            }

            // Save it
            world.saveWorld();

            // Unload chunks
            for (Chunk c : world.getHandle().getLoadedChunks()) {
                world.getHandle().unloadChunk(c);
            }

            // add reference to worldfile
            File worldfile = world.getHandle().getWorldFolder();

            // Unload the world (dont save, already done that)
            Bukkit.unloadWorld(world.getHandle(), false);
            worldCache.remove(world.getName().toLowerCase());

            // Remove the settings
            synchronized (worldSettings) {
                // Add it
                worldSettings.remove(world.getName());
            }

            // Delete world files
            if (deleteworldfiles) {
                FileHandler.deleteDirectory(worldfile);
            }
        }
    }

    /**
     * This loads and creates the worlds
     * @param server
     */
    @SuppressWarnings("deprecation")
    public static void initializeWorlds(Server server) {
        IdpWorld world;
        IdpWorldSettings settings;

        // Loop through all worlds
        for (IdpWorldType worldType : IdpWorldType.values()) {
            switch (worldType) {
                case NONE:
                case DYNAMIC:
                    continue;
            }

            settings = getSettings(worldType.worldname);
            if (settings == null) {
                // We dont want to load this world...
                // Remove the reference
                worldType.remove();
                continue;
                // throw new IdpRuntimeException("Trying to track a world without any settings! (" + worldType + ")");
            }

            // Load or create the world (it does both?)
            // Altough these methods are deprecated, keep it, as the new system is designed that you would have to look on name..
            world = getWorld(worldType);
            World bukkitWorld = null;

            // Check if its a world, and if its already loaded
            if (world == null) {
                WorldCreator wc = getWorldCreator(worldType.worldname, settings);
                bukkitWorld = server.createWorld(wc);
            } else {
                bukkitWorld = world.getHandle();
            }

            bukkitWorld.setDifficulty(Difficulty.NORMAL);

            // If this world does not have a daylight cycle, make sure to
            // turn it off in the world's game rules
            if (!settings.hasDaylightCycle()) {
                bukkitWorld.setGameRuleValue("doDaylightCycle", "false");

                // Set the time so that it appears bright enough
                bukkitWorld.setTime(1200);
            }

            // Turn off weather for all worlds that don't have it
            if (!settings.hasWeather()) {
                bukkitWorld.setGameRuleValue("doWeatherCycle", "false");
            }

            switch (worldType) {
                case EVENTWORLD:
                case CREATIVEWORLD:
                    bukkitWorld.setSpawnFlags(false, false);
            }

            // Setup the world border for each world
            WorldBorder border = bukkitWorld.getWorldBorder();
            border.setDamageAmount(0);
            border.setWarningDistance(0);
            border.setCenter(0, 0);
            border.setSize(settings.getWorldSize() * 2);

            // TODO: remove mob cap code entirely if this is no problem in the future
            /*if (world != null) {
                world.getHandle().setTicksPerAnimalSpawns(settings.getTicksPerAnimalSpawn());
                world.getHandle().setTicksPerMonsterSpawns(settings.getTicksPerMonsterSpawn());
                world.getHandle().setMonsterSpawnLimit(settings.getMonsterSpawnLimit());
                world.getHandle().setAnimalSpawnLimit(settings.getAnimalSpawnLimit());
                world.getHandle().setWaterAnimalSpawnLimit(settings.getWaterAnimalLimit());
            }*/
        }
    }

    /**
     * Constructs a WorldCreator object from worldsettings (and with random Seed).
     * @param worldname
     * @param settings
     * @return
     */
    private static WorldCreator getWorldCreator(String worldname, IdpWorldSettings settings) {
        WorldCreator wc = new WorldCreator(worldname);
        wc.environment(settings.getMaptype().getEnvironment());
        wc.seed(new Random(System.currentTimeMillis()).nextInt(100000000));
        ChunkGenerator gen = settings.getMaptype().getChunkGenerator();
        wc.generator(gen);
        return wc;
    }

}
