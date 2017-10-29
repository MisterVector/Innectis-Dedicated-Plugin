package net.innectis.innplugin.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;

/**
 * A manager that keeps track of all the player credentials
 *
 * @author AlphaBlend
 */
public class PlayerCredentialsManager {

    private static List<PlayerCredentials> playerCredentialsList = new ArrayList<PlayerCredentials>();

    /**
     * Gets a player credentials object by player name
     * @param name
     * @return
     */
    public static PlayerCredentials getByName(String name) {
        return getByName(name, false);
    }

    /**
     * Gets a player credentials object by player name
     * @param name
     * @param cacheIfLoaded if true, then if this requires a database query, the
     * resulting player credentials object will be cached
     * @return
     */
    public static PlayerCredentials getByName(String name, boolean cacheIfLoaded) {
        for (PlayerCredentials playerCredentials : playerCredentialsList) {
            if (playerCredentials.getName().equalsIgnoreCase(name)) {
                return playerCredentials;
            }
        }

        // If we're at this point, then it was not found, so check from database
        PlayerCredentials playerCredentials = loadFromDatabaseByName(name);

        if (cacheIfLoaded && playerCredentials != null) {
            playerCredentialsList.add(playerCredentials);
        }

        return playerCredentials;
    }

    /**
     * Gets a player credentials object by unique ID
     * @param playerId
     * @return
     */
    public static PlayerCredentials getByUniqueId(UUID playerId) {
        return getByUniqueId(playerId, false);
    }

    /**
     * Gets a player credentials object by unique ID
     * @param playerId
     * @param cacheIfLoaded if true, then if this requires a database query, the
     * resulting player credentials object will be cached
     * @return
     */
    public static PlayerCredentials getByUniqueId(UUID playerId, boolean cacheIfLoaded) {
        for (PlayerCredentials credentials : playerCredentialsList) {
            if (credentials.getUniqueId().equals(playerId)) {
                return credentials;
            }
        }

        // If we're at this point, then it was not found, so check from database
        PlayerCredentials credentials = loadFromDatabaseByUniqueId(playerId);

        if (credentials != null && cacheIfLoaded) {
            playerCredentialsList.add(credentials);
        }

        return credentials;
    }

    /**
     * Adds the specified credentials to the list
     * @param credentials
     */
    public static void addCredentialsToCache(PlayerCredentials credentials) {
        if (!playerCredentialsList.contains(credentials)) {
            playerCredentialsList.add(credentials);
        }
    }

    /**
     * Loads player's credentials from database by name
     * @param name
     * @return
     */
    private static PlayerCredentials loadFromDatabaseByName(String playerName) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT player_id FROM players WHERE lower(name) = ?;");
            statement.setString(1, playerName.toLowerCase());
            set = statement.executeQuery();

            if (set.next()) {
                String uuidString = set.getString("player_id");
                UUID uuid = UUID.fromString(uuidString);
                PlayerCredentials credentials = new PlayerCredentials(uuid, playerName);

                return credentials;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load player credentials from player name " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }

        return null;
    }

    /**
     * Loads player's credentials from database by unique ID
     * @param playerId
     * @return
     */
    private static PlayerCredentials loadFromDatabaseByUniqueId(UUID playerId) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT name FROM players WHERE player_id = ?;");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            if (set.next()) {
                String name = set.getString("name");
                UUID uuid = UUID.fromString(playerId.toString());
                PlayerCredentials credentials = new PlayerCredentials(uuid, name);

                return credentials;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load player credentials from player ID " + playerId.toString() + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }

        return null;
    }
    
}
