package net.innectis.innplugin.location.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;

/**
 *
 * @author Hret
 *
 * The DataAccessObject for the IdpBlockData class
 * This contains the database logic of the IdpBlockData class.
 *
 * <b>Access to this class should be strictly package!</b>
 */
class BlockDataDAO {

    BlockDataDAO() {
    }
    // <editor-fold defaultstate="collapsed" desc="Queries">
    private static final String _query_values_deleteSingle = "DELETE FROM chunk_data WHERE chunkid = ? AND location = ? AND `key` = ?";
    private static final String _query_values_deleteAll = "DELETE FROM chunk_data WHERE chunkid = ? AND location = ?";
    private static final String _query_values_update = "UPDATE chunk_data SET value = ? where chunkid = ? AND location = ? AND `key` = ? ";
    private static final String _query_values_insert = "INSERT INTO chunk_data (chunkid, location, `key`, value) VALUES (?,?,?,?) ";
    // </editor-fold>

    /**
     * Directly removes all values from the database
     */
    protected void removeLocationFromDatabase(Long locationid, Long chunkid) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement(_query_values_deleteAll);
            statement.setLong(1, chunkid);
            statement.setLong(2, locationid);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot remove IdpBlockData (" + locationid + "/" + chunkid + ")!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Updates the value in the tracker if it has been changed.
     * @param locationid
     * @param chunkid
     * @param tracker
     */
    protected void updateTracker(Long locationid, Long chunkid, DBValueTracker<String, String> tracker) {
        try {
            updateTracker(DBManager.getConnection(), locationid, chunkid, tracker);
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot update value ('" + tracker.getValue() + "') of key '" + tracker.getKey() + "' in IdpBlockData!", ex);
        }
    }

    private void updateTracker(Connection transaction, Long locationid, Long chunkid, DBValueTracker<String, String> tracker) {
        switch (tracker.getTrackerState()) {
            // Handle the value that is changed.
            case CHANGED: {
                // If value == null DELETE
                if (tracker.getValue() == null) {
                    PreparedStatement statement = null;

                    try {
                        statement = transaction.prepareStatement(_query_values_deleteSingle);
                        statement.setLong(1, chunkid);
                        statement.setLong(2, locationid);
                        statement.setString(3, tracker.getKey());
                        statement.execute();

                        tracker.setTrackerState(DBValueTracker.TrackerState.DELETED);
                    } catch (SQLException ex) {
                        InnPlugin.logError("Cannot remove key '" + tracker.getKey() + "' in IdpBlockData (" + locationid + "/" + chunkid + ")!", ex);
                    } finally {
                        DBManager.closePreparedStatement(statement);
                    }

                    // UPDATE
                } else {
                    PreparedStatement statement = null;

                    try {
                        statement = transaction.prepareStatement(_query_values_update);
                        // VALUE, CHUNKID, LOCID, KEY
                        statement.setString(1, tracker.getValue());
                        statement.setLong(2, chunkid);
                        statement.setLong(3, locationid);
                        statement.setString(4, tracker.getKey());
                        statement.execute();

                        tracker.setTrackerState(DBValueTracker.TrackerState.UNCHANGED);
                    } catch (SQLException ex) {
                        InnPlugin.logError("Cannot update value ('" + tracker.getValue() + "') of key '" + tracker.getKey() + "' in IdpBlockData!", ex);
                    } finally {
                        DBManager.closePreparedStatement(statement);
                    }
                }

                break;
            }

            // Insert the new value
            case NEW: {
                // Extra check, dont insert null values!
                if (tracker.getValue() != null) {
                    PreparedStatement statement = null;

                    try {
                        statement = transaction.prepareStatement(_query_values_insert);
                        // CHUNKID, LOCID, KEY, VALUE
                        statement.setLong(1, chunkid);
                        statement.setLong(2, locationid);
                        statement.setString(3, tracker.getKey());
                        statement.setString(4, tracker.getValue());
                        statement.execute();

                        tracker.setTrackerState(DBValueTracker.TrackerState.UNCHANGED);
                    } catch (SQLException ex) {
                        InnPlugin.logError("Cannot insert value ('" + tracker.getValue() + "') of key '" + tracker.getKey() + "' in IdpBlockData!");
                        // Cant insert so updater
                        tracker.setTrackerState(DBValueTracker.TrackerState.CHANGED);
                        updateTracker(locationid, chunkid, tracker);
                    } finally {
                        DBManager.closePreparedStatement(statement);
                    }
                }

                break;
            }

            // Unchanged values
            case UNCHANGED: {
                // Do nothing
            }
        }
    }

    /**
     * Saves all the values to the database.
     * @return
     */
    protected boolean saveAllToDatabase(Long locationid, Long chunkid, List<DBValueTracker<String, String>> values) {
        Connection transaction = null;
        try {
            transaction = DBManager.openNewConnection();
            transaction.setAutoCommit(false);

            for (DBValueTracker tracker : values) {
                updateTracker(transaction, locationid, chunkid, tracker);
            }

            transaction.commit();
            return true;
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot save values in IdpBlockData (" + locationid + "/" + chunkid + ")!", ex);
        } finally {
            if (transaction != null) {
                try {
                    transaction.close();
                } catch (SQLException ex) {
                    InnPlugin.logError("Cannot close connection: " + ex.getMessage());
                }
            }
        }
        return false;
    }

}
