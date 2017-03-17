package net.innectis.innplugin.location.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import org.bukkit.Location;

/**
 *
 * @author Hret
 *
 * The DataAccessObject for the IdpChunk class.
 * This contains the database logic of the IdpChunkData class.
 *
 * <b>Access to this class should be strictly package!</b>
 */
class ChunkDataDAO {

    // <editor-fold defaultstate="collapsed" desc="Queries">
    private static final String _chunk_Data_SelectIDFunction = "SELECT getChunkId(?,?,?,?)";
    private static final String _chunk_Data_Select = "SELECT location, `key`, value FROM chunk_data WHERE chunkid = ? ORDER BY location";
    // </editor-fold>

    ChunkDataDAO() {
    }

    /**
     * Gets the chunkid from the database.
     * or, if none, creates one.
     */
    protected Long getChunkId(Location location) {
        PreparedStatement selectStatement = null;
        ResultSet result = null;

        try {
            selectStatement = DBManager.prepareStatement(_chunk_Data_SelectIDFunction);
            selectStatement.setInt(1, location.getBlockX());
            selectStatement.setInt(2, location.getBlockZ());
            selectStatement.setInt(3, location.getBlockY());
            selectStatement.setString(4, location.getWorld().getName());

            result = selectStatement.executeQuery();

            if (result.next()) {
                return result.getLong(1);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot generate key for blockdata", ex);
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(selectStatement);
        }

        return null;
    }

    /**
     * Returns the chunkdata of the chunk with the given ID
     * @param chunkid
     * @return
     */
    protected static Map<Long, IdpBlockData> getChunkData(Long chunkid) {
        if (chunkid != null) {
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = DBManager.prepareStatement(_chunk_Data_Select);
                statement.setLong(1, chunkid);
                result = statement.executeQuery();

                Map<Long, IdpBlockData> blockdata = new HashMap<Long, IdpBlockData>(result.getFetchSize());
                List<DBValueTracker<String, String>> values = null;

                Long currentLocation = null, rowLocation;
                String key, value;

                while (result.next()) {
                    rowLocation = result.getLong("location");

                    if (currentLocation == null || currentLocation != rowLocation) {
                        if (values != null) {
                            blockdata.put(currentLocation, new IdpBlockData(chunkid, currentLocation, values));
                        }
                        values = new ArrayList<DBValueTracker<String, String>>();
                        currentLocation = rowLocation;
                    }

                    key = result.getString("key");
                    value = result.getString("value");
                    values.add(new DBValueTracker<String, String>(key, value, DBValueTracker.TrackerState.UNCHANGED));
                }

                // Also set last block!
                if (values != null) {
                    blockdata.put(currentLocation, new IdpBlockData(chunkid, currentLocation, values));
                }

                return blockdata;
            } catch (SQLException ex) {
                InnPlugin.logError("Cannot get chunk data!", ex);
            } finally {
                DBManager.closeResultSet(result);
                DBManager.closePreparedStatement(statement);
            }
        }

        return null;
    }

}
