package net.innectis.innplugin.player.renames;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;

/**
 * This class handles everytime a player changes
 * their username.
 *
 * @author AlphaBlend
 */
public class PlayerRenameHandler {

    private static List<PlayerRename> playerRenames = new ArrayList<PlayerRename>();

    /**
     * Logs a rename of a player to the database
     * @param playerId
     * @param time
     * @param oldName
     * @param newName
     */
    public static void logRenamedPlayer(UUID playerId, Timestamp time, String oldName, String newName) {
        PlayerRename rename = new PlayerRename(playerId, time, oldName, newName);
        rename.save();

        playerRenames.add(rename);
    }

    /**
     * Gets the rename history for the specified player by ID,
     * sorted by the date they joined the server after they
     * renamed themselves
     * @param playerId
     * @return
     */
    public static List<PlayerRename> getRenameHistory(UUID playerId) {
        List<PlayerRename> renameHistory = new ArrayList<PlayerRename>();

        for (PlayerRename rename : playerRenames) {
            if (rename.getPlayerId().equals(playerId)) {
                renameHistory.add(rename);
            }
        }

        return renameHistory;
    }

    /**
     * Gets a list of all renames of the specified player by their ID
     * @param playerId
     * @return
     */
    public static List<PlayerRename> getPlayerRenames(UUID playerId) {
        List<PlayerRename> renames = new ArrayList<PlayerRename>();

        for (PlayerRename rename : playerRenames) {
            if (rename.getPlayerId().equals(playerId)) {
                renames.add(rename);
            }
        }

        return renames;
    }

    /**
     * Gets a list of player names from the specified old name
     * @param oldName
     * @return
     */
    public static List<String> getPlayerRenames(String oldName) {
        List<UUID> searchIDs = new ArrayList<UUID>();

        // First search to get all unique IDs with this old name
        for (PlayerRename rename : playerRenames) {
            if (rename.getOldName().equalsIgnoreCase(oldName)) {
                UUID playerId = rename.getPlayerId();

                if (!searchIDs.contains(playerId)) {
                    searchIDs.add(playerId);
                }
            }
        }

        List<String> playerNames = new ArrayList<String>();

        // Only search for player renames if the original search
        // found at least one result
        if (searchIDs.size() > 0) {
            for (UUID playerId : searchIDs) {
                PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId);
                playerNames.add(credentials.getName());
            }
        }

        return playerNames;
    }

    /**
     * Loads all player renames from the database
     */
    public static void loadPlayerRenames() {
        InnPlugin.logCustom(ChatColor.GREEN, "Loading player renames...");

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT * FROM player_renames");
            set = statement.executeQuery();

            while (set.next()) {
                UUID playerId = UUID.fromString(set.getString("player_id"));
                Timestamp renameTime = set.getTimestamp("rename_time");
                String oldName = set.getString("old_name");
                String newName = set.getString("new_name");

                PlayerRename rename = new PlayerRename(playerId, renameTime, oldName, newName);
                playerRenames.add(rename);
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load player renames!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
            DBManager.closeResultSet(set);
        }
    }
    
}
