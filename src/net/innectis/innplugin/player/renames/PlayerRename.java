package net.innectis.innplugin.player.renames;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;

/**
 * A class that contains the date of a name change
 * as well as the old and new names
 *
 * @author AlphaBlend
 */
public class PlayerRename {

    private UUID playerId = null;
    private Timestamp changeTime = null;
    private String oldName = null;
    private String newName = null;

    public PlayerRename(UUID playerId, Timestamp changeTime, String oldName, String newName) {
        this.playerId = playerId;
        this.changeTime = changeTime;
        this.oldName = oldName;
        this.newName = newName;
    }

    /**
     * Gets the unique ID of the player that made
     * the rename
     * @return
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Gets the time the rename occurred
     * @return
     */
    public Timestamp getTimestamp() {
        return changeTime;
    }

    /**
     * Gets the old name prior to the rename
     * @return
     */
    public String getOldName() {
        return oldName;
    }

    /**
     * Gets the new name after the rename
     * @return
     */
    public String getNewName() {
        return newName;
    }

    /**
     * Checks if this name is the current name of the player
     * @return
     */
    public boolean isCurrentName() {
        PlayerCredentials credentials = PlayerCredentialsManager.getByName(newName);
        return (credentials != null);
    }

    /**
     * Saves this name change to the database. Not to
     * be called more than once
     */
    public void save() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("INSERT INTO player_renames VALUES (?, ?, ?, ?);");
            statement.setString(1, playerId.toString());
            statement.setTimestamp(2, changeTime);
            statement.setString(3, oldName);
            statement.setString(4, newName);
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to log rename of player " + oldName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }
    
}
