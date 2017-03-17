package net.innectis.innplugin.location.data;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.innectis.innplugin.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author Hret
 *
 * The data manager of a IdpChunk.
 * This contains the blockdata of the blocks inside.
 *
 */
public class IdpChunkData extends ChunkDataDAO {

    /** Map of blockdata that is in this chunk. */
    private volatile Map<Long, IdpBlockData> blockdata;
    private Long lastAccess;
    private boolean reclaimImmediately = false;

    private IdpChunkData() {
        this.lastAccess = System.currentTimeMillis();
    }
    /** The blockdataid if this object is from the database. */
    private Long chunkId;

    /**
     * Initializes the IdpBlockData object with the values from the database
     * @param result
     * @throws SQLException
     */
    protected IdpChunkData(Location location) throws SQLException {
        this();
        this.chunkId = getChunkId(location);

        // Get data of chunk
        blockdata = getChunkData(chunkId);
        // If no data, make empty map
        if (blockdata == null) {
            blockdata = new HashMap<Long, IdpBlockData>((int) (4096 * 0.5));
        }
    }

    /**
     * Returns the blockdata for the given block
     * @param location
     * @return
     */
    public IdpBlockData getBlockData(Vector location) {
        lastAccess = System.currentTimeMillis();

        long locationid = LocationUtil.vectorToLocation(location);
        IdpBlockData data = null;

        // Look in the map for the data
        if (!blockdata.containsKey(locationid)) {
            // Not found in cache, put new value in cache
            data = new IdpBlockData(chunkId, locationid);
            blockdata.put(locationid, data);
        } else {
            data = blockdata.get(locationid);
        }

        return data;
    }

    /**
     * Returns the MS the last time the chunkdata was used.x
     * @return
     */
    protected long getLastUsage() {
        return lastAccess;
    }

    /**
     * Sets if this chunk data should be reclaimed immediately
     * @param reclaimImmediately
     */
    public void setReclaimImmediately(boolean reclaimImmediately) {
        this.reclaimImmediately = reclaimImmediately;
    }

    /**
     * Gets if this chunk data should be reclaimed immediately
     * @return
     */
    public boolean getReclaimImmediately() {
        return reclaimImmediately;
    }

    /**
     * Reclaims the object and clears the values
     */
    protected void reclaim() {
        for (IdpBlockData data : blockdata.values()) {
            data.save();
        }
    }

}
