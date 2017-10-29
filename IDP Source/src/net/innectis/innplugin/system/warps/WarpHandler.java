package net.innectis.innplugin.system.warps;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.PlayerGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Class that handles warp locations
 *
 * @author Hret
 */
public final class WarpHandler {

    private WarpHandler() {
    }
    private static final HashMap<String, IdpWarp> warps = new HashMap<String, IdpWarp>(20);
    private static Location spawnlocation;
    private static InnectisLot spawnlot;
    private static Location tutorialLocation;

    /**
     * Load the warps from the database.
     */
    public static void initializeWarps() {
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement("SELECT name, world, locx, locy, locz, yaw, comment, settings FROM warps;");
            result = statement.executeQuery();

            String name;
            while (result.next()) {
                name = result.getString("name");
                World world = Bukkit.getWorld(result.getString("world"));

                Location loc = new Location(world, result.getInt("locx"), result.getInt("locy"), result.getInt("locz"), result.getInt("yaw"), 0);
                warps.put(name.toLowerCase(), new IdpWarp(name, loc, result.getString("comment"), result.getLong("settings")));
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Could not load warps!", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Lookup the warp with the given name.
     *
     * @param name
     * @return
     */
    public static IdpWarp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    /**
     * Looks up all warps and returns the list
     *
     * @return
     */
    public static List<IdpWarp> getWarps() {
        return new ArrayList(warps.values());
    }

    /**
     * Adds a new warp to the list (and db)
     *
     * @param warp
     * @return
     */
    public static boolean addWarp(IdpWarp warp) {
        if (warps.containsKey(warp.getName().toLowerCase())) {
            return false;
        }

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO warps (name,world,locx,locy,locz,yaw,settings,comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, warp.getName());
            statement.setString(2, warp.getLocation().getWorld().getName());
            statement.setInt(3, warp.getLocation().getBlockX());
            statement.setInt(4, warp.getLocation().getBlockY());
            statement.setInt(5, warp.getLocation().getBlockZ());
            statement.setFloat(6, warp.getLocation().getYaw());
            statement.setLong(7, warp.getSettings());
            statement.setString(8, warp.getComment());

            if (statement.executeUpdate() > 0) {
                warps.put(warp.getName().toLowerCase(), warp);
                return true;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot save new warp! ", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;

    }

    /**
     * Deletes the given warp from the DB and cache
     *
     * @param warp
     * @return
     */
    public static boolean deleteWarp(IdpWarp warp) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement(" DELETE FROM warps WHERE name = ? ");
            statement.setString(1, warp.getName());

            if (statement.executeUpdate() == 1) {
                warps.remove(warp.getName().toLowerCase());
                return true;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot delete warp! ", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * Returns the main spawn location, and does not check for guests
     *
     * @return
     */
    public static Location getSpawn() {
        return getSpawn(PlayerGroup.NONE);
    }

    /**
     * Returns the main spawn location, checking for guests
     *
     * @param group
     * @return
     */
    public static Location getSpawn(PlayerGroup group) {
        if (group == PlayerGroup.GUEST && getTutorialLocation() != null) {
            return getTutorialLocation();
        }

        if (spawnlocation == null) {
            IdpWarp warp = getWarp("spawn");

            if (warp != null) {
                spawnlocation = warp.getLocation();
            } else {
                World world = Bukkit.getWorld(IdpWorldType.INNECTIS.worldname);
                spawnlocation = new Location(world, 0, world.getHighestBlockYAt(0, 0) + 1, 0, 0, 0);
            }
        }

        return spawnlocation;
    }

    /**
     * Gets the lot on the spawnlocation.
     *
     * @return
     */
    public static InnectisLot getSpawnLot() {
        if (spawnlot == null) {
            spawnlot = LotHandler.getLot(getSpawn());
        }
        return spawnlot;
    }

    /**
     * Gets the warp for the jail
     *
     * @return
     */
    public static IdpWarp getJail() {
        IdpWarp warp = getWarp("jail");
        if (warp == null) {
            warp = new IdpWarp("Spawn", getSpawn(), "", 0);
        }
        return warp;
    }

    /**
     * Clears the spawnlocations so they are recalculated on next use.
     */
    public static void resetSpawnLocations() {
        synchronized (WarpHandler.class) {
            tutorialLocation = null;
            spawnlot = null;
            spawnlocation = null;
        }
    }

    /**
     * Gets the tutorial lot
     *
     * @return
     */
    private static Location getTutorialLocation() {
        if (tutorialLocation == null) {
            IdpWarp tutorialWarp = getWarp("tutorial");

            if (tutorialWarp != null) {
                tutorialLocation = tutorialWarp.getLocation();
            }
        }

        if (tutorialLocation != null) {
            return tutorialLocation;
        } else {
            return null;
        }
    }
    
}
