package net.innectis.innplugin.loggers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;

/**
 * @author Hret
 * A logger that logs stuff into the database.
 *
 */
abstract class IdpDBLogger implements Logger {

    /** Synclock to support multi threading. */
    private static final Object _synclock = new Object();
    /** The cache itself */
    private ArrayList<Object[]> cache;
    private String statement;
    private int valueCount;
    /** Boolean value to mark if new entries should automaticly be saved * */
    private boolean instantSave = false;

    /**
     *
     * @param cachesize
     * @param statement - without the parameter placeholders!
     * So th
     * @param valuecount
     */
    protected IdpDBLogger(String tablename, String... columnames) {
        this.valueCount = columnames.length;

        this.statement = "INSERT INTO " + tablename + " (";
        if (valueCount > 0) {
            statement += columnames[0];
            for (int i = 1; i < valueCount; i++) {
                statement += ", " + columnames[i];
            }
        }
        this.statement += ") VALUES ";

        cache = new ArrayList<Object[]>(10000);
    }

    /**
     * Adds the values to the cache to be inserted later.
     * @param values
     * The values can be any type of object. If the object has no special handling
     * in this class it will use the toString() method to get the value.<br/>
     * If needed quotes are automaticly added.
     *
     *
     */
    protected void addToCache(Object... values) {
        synchronized (_synclock) {
            cache.add(values);
        }

        if (instantSave) {
            saveCache();
        }
    }

    /**
     * Saves the items in the cache into the database.
     */
    public void saveCache() {
        List<Object[]> list = Collections.synchronizedList(new ArrayList<Object[]>(cache.size()));

        // Get all in the cache and clear it.
        synchronized (_synclock) {
            list.addAll(cache);
            cache = new ArrayList<Object[]>(10000);
        }

        // Nothing to save... skip..
        if (list.isEmpty()) {
            return;
        }

        // Chunk up the cache
        while (list.size() > 0) {
            List<Object[]> sublist = list.subList(0, Math.min(2000, list.size()));
            saveList(sublist);

            list.removeAll(sublist);
        }

    }

    /**
     * Saves the items in the list
     */
    private void saveList(List<Object[]> list) {

        // Make the sql statement
        String statementString = statement;
        if (valueCount > 0) {
            statementString += " (?";
            for (int i = 1; i < valueCount; i++) {
                statementString += ", ?";
            }
            statementString += ")";
        }

        Connection conn = null;
        PreparedStatement prepStatement = null;

        try {
            conn = DBManager.openNewConnection();
            conn.setAutoCommit(false);

            for (Object[] items : list) {
                prepStatement = conn.prepareStatement(statementString);

                for (int i = 1; i <= valueCount; i++) {
                    Object val = (i - 1 < items.length) ? items[i - 1] : null;
                    if (val == null) {
                        prepStatement.setString(i, "");
                    } else if (val instanceof Integer) {
                        prepStatement.setInt(i, (Integer) val);
                    } else if (val instanceof String) {
                        prepStatement.setString(i, (String) val);
                    } else if (val instanceof Date) {
                        java.sql.Timestamp sqldate = new java.sql.Timestamp(((Date) val).getTime());
                        prepStatement.setTimestamp(i, sqldate);
                    } else {
                        prepStatement.setString(i, val.toString());
                    }
                }

                try {
                    prepStatement.executeUpdate();
                } catch (SQLException ex) {
                    InnPlugin.logError("Cannot execute command...", ex);
                }
            }

            conn.commit();
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot get commit changes!", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    InnPlugin.logError("Cannot close connection: " + ex.getMessage());
                }
            }

            DBManager.closePreparedStatement(prepStatement);
        }
    }

    /**
     * Saves the contents of the cache and sets the size to 1.
     * That means that every block logged after it got closed will still be added.
     */
    public void closeLogger() {
        instantSave = true;
        saveCache();
    }

}