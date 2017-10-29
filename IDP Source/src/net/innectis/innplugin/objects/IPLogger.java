package net.innectis.innplugin.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;

/**
 *
 * @author AlphaBlend
 *
 * Logs connections from players when they join the server
 */
public class IPLogger {

    private IPLogger() {}

    /**
     * Logs a connection to the database
     */
    public static void logConnection(UUID playerId, String playerName, String ip, Timestamp timestamp) {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO ip_log (player_id, ip, logtime) VALUES (?, ?, ?) ");
            statement.setString(1, playerId.toString());
            statement.setString(2, ip);
            statement.setTimestamp(3, timestamp);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Cannot log connection from player " + playerName + " with ip " + ip + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    /**
     * Gets the last IP used to connect to Innectis from this player
     * represented by their ID
     * @param playerId
     * @return
     */
    public static String getLastUsedIP(UUID playerId, String playerName) {
        String ip = null;

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT DISTINCT ip FROM ip_log WHERE player_id = ? ORDER BY logtime DESC LIMIT 1;");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            if (set.next()) {
                ip = set.getString("ip");
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Umable to get last used IP for player " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return ip;
    }

}
