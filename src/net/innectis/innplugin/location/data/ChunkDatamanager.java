package net.innectis.innplugin.location.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import org.bukkit.Location;

/**
 *
 * @author Hret
 *
 * Factory class to keep track of chunkdata objects, and to create them if needed.
 *
 */
public final class ChunkDatamanager {

    private static int RECLAIM_AGE = 5 * 60 * 1000; // 5 min
    private static final int CACHE_SIZE = 10000;
    private static final Object _synclock = new Object();
    private static boolean reclaiming = false;

    private ChunkDatamanager() {
    }
    /** Reference of the cached chunks */
    private static final HashMap<String, IdpChunkData> cachedChunks = new HashMap<String, IdpChunkData>(CACHE_SIZE);

    /**
     * Returns the chunkdataobject for the given chunklocation.
     * It will cache chunk data inside the manager.
     * If the chunk data is not in cache it will get loaded from the database.
     * @param location
     * @return
     */
    public static IdpChunkData getChunkData(Location location) {
        return getChunkData(location, false);
    }

    /**
     * Returns the chunkdataobject for the given chunklocation.
     * It will cache chunk data inside the manager.
     * If the chunk data is not in cache it will get loaded from the database.
     * @param location
     * @param reclaimImmediatelyIfNotCached
     * @return
     */
    public static IdpChunkData getChunkData(Location location, boolean reclaimImmediatelyIfNotCached) {
        synchronized (_synclock) {
            String key = locationToString(location);
            IdpChunkData data = null;

            if (!cachedChunks.containsKey(key)) {
                try {
                    data = new IdpChunkData(location);

                    if (reclaimImmediatelyIfNotCached) {
                        data.setReclaimImmediately(true);
                    }

                    cachedChunks.put(key, data);
                } catch (SQLException ex) {
                    Logger.getLogger(ChunkDatamanager.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                data = cachedChunks.get(key);
            }

            return data;
        }
    }

    /**
     * Converts the location to a simple lookup key for the cachemap.
     * @param loc - the location
     * @return key - consisting out of the xzy coords and the world's name.
     */
    private static String locationToString(Location loc) {
        IdpWorld world = IdpWorldFactory.getWorld(loc.getWorld().getName());
        return loc.getBlockX() + "_" + loc.getBlockZ() + "_" + loc.getBlockY() + "_" + world.getSettings().getWorldId();
    }

    /**
     * returns the size of chunks that are cached
     * @return
     */
    public static int cacheSize() {
        synchronized (_synclock) {
            return cachedChunks.size();
        }
    }

    /**
     * Reclaims chunk data objects that have been expired.
     * <p/>
     * This method will make a copy of the current cache. <br/>
     * Next it will lookup in the copied cache for the entries that are expired. <br/>
     *
     * Then it will loop for those in the real cache and recheck the age before clearing. <br/>
     * <p/>
     * The interactions with the real cache are in a synclock to avoid errors
     * with ::getChunkData.
     * To avoid spending a large amount of time in these locks the expire check
     * is done outside of the synlocks.
     * <br/>
     * When entering the second synclock this should reduce the amount of things
     * to check and therefor the time spend in the lock.
     */
    public static void reclaimUnusedChunks() {
        if (reclaiming) {
            return; // dont reclaim twice
        }

        reclaiming = true;

        try {
            HashMap<String, IdpChunkData> localData = new HashMap<String, IdpChunkData>(CACHE_SIZE);
            synchronized (_synclock) {
                // Make a copy
                localData.putAll(cachedChunks);
            }

            IdpChunkData data;
            long reclaimborder = System.currentTimeMillis() - RECLAIM_AGE;
            List<String> expiredKeys = new ArrayList<String>((int) (CACHE_SIZE * 0.25));

            // Lookup expired sessions
            for (String key : localData.keySet()) {
                data = localData.get(key);

                if (data.getLastUsage() < reclaimborder || data.getReclaimImmediately()) {
                    expiredKeys.add(key);
                }
            }
            // Dont need this anymore...
            localData = null;

            // Reclaim the expired sessions we found in the tempdata.
            synchronized (_synclock) {
                for (String key : expiredKeys) {
                    data = cachedChunks.get(key);
                    // Check again just to be sure it didn't change.
                    if (data.getLastUsage() < reclaimborder || data.getReclaimImmediately()) {
                        // Remove from map and reclaim!
                        cachedChunks.remove(key);
                        data.reclaim();
                    }
                }
            }
        } finally {
            reclaiming = false;
        }
    }

    /**
     * Reclaims all chunk data objects
     *
     * @deprecated
     * This will pause the current thread till it can reclaim!
     * Do not use unless absolutly needed, if you want to clear unused chunks use
     * <code>reclaimUnusedChunks</code> instead.
     */
    @Deprecated
    public static void reclaimAllChunks() {
        InnPlugin.logInfo("Reclaiming all chunkdata...");
        int oldage = RECLAIM_AGE;
        RECLAIM_AGE = Integer.MIN_VALUE;
        // Wait till its done with the current reclaiming
        while (reclaiming) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
        reclaimUnusedChunks();
        RECLAIM_AGE = oldage;
        InnPlugin.logInfo("Chunkdata reclaimed!");
    }

}
