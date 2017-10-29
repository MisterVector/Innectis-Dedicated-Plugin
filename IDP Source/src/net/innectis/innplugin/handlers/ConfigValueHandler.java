package net.innectis.innplugin.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;

/**
 *
 * @author Hret
 *
 * Handler to get or save configuration values from the database.
 */
public final class ConfigValueHandler {

    private ConfigValueHandler() {
    }

    /**
     * Tries to save a variable into the database.
     * <br/>Both key and value must NOT be null!
     * @param key
     * @param value
     * @return true if succeed.
     */
    public static boolean saveValue(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }

        if (value == null) {
            throw new IllegalArgumentException("value cannot be null!");
        }

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement(" INSERT INTO configvalues (ckey, cvalue) VALUES (?,?) ON DUPLICATE KEY UPDATE cvalue = ? ");
            statement.setString(1, key);
            statement.setString(2, value);
            statement.setString(3, value);
            statement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot update configvalue for '" + key + "' (value: '" + value + "')");
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * Gets the value from the database.
     * <br/>Key must NOT be null!
     * @param key
     * @return The value or NULL if non-existant or the sql threw an error!
     */
    public static String getValue(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DBManager.prepareStatement(" SELECT cvalue FROM configvalues WHERE ckey = ?");
            statement.setString(1, key);
            result = statement.executeQuery();

            if (result.next()) {
                return result.getString("cvalue");
            }

        } catch (SQLException ex) {
            InnPlugin.logError("Cannot update configvalue for '" + key + "'");
        } finally {
            DBManager.closeResultSet(result);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * Deletes the value from the database.
     * <br/>Key must NOT be null!
     * @param key
     * @return true if succeed
     */
    public static boolean deleteValue(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null!");
        }

        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement(" DELETE FROM configvalues WHERE ckey = ?");
            statement.setString(1, key);

            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot delete configvalue '" + key + "'");
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

}
