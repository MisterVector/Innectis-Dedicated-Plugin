package net.innectis.innplugin.player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;

/**
 * Describes a player, both by name, and by unique ID
 *
 * @author AlphaBlend
 */
public class PlayerCredentials {

    private UUID uniqueId;
    private String name;
    private boolean isValidPlayer = false;

    public PlayerCredentials(UUID uniqueId, String name) {
        this(uniqueId, name, true);
    }

    public PlayerCredentials(UUID uniqueId, String name, boolean isValidPlayer) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.isValidPlayer = isValidPlayer;
    }

    /**
     * Gets the unique ID of this player
     * @return
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Gets the name of this player
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this player
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets if these credentials belong to a valid player
     * @return
     */
    public boolean isValidPlayer() {
        return isValidPlayer;
    }

    /**
     * Updates the player's name in database according to their unique ID
     */
    public void update() {
        PreparedStatement statement = null;

        try {
            statement = DBManager.prepareStatement("UPDATE players SET name = ? WHERE player_id = ?;");
            statement.setString(1, name);
            statement.setString(2, uniqueId.toString());
            statement.executeUpdate();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to update player with UUID " + uniqueId + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlayerCredentials)) {
            return false;
        }

        PlayerCredentials credentials = (PlayerCredentials) obj;

        return credentials.getUniqueId().equals(uniqueId);
    }
    
}
