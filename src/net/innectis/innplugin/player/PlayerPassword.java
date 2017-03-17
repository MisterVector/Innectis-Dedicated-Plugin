package net.innectis.innplugin.player;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * A class that contains all the details of a single
 * player's password settings
 *
 * @author AlphaBlend
 */
public class PlayerPassword {

    private UUID playerId;
    String password;
    Timestamp timestamp;

    public PlayerPassword(UUID playerId, String password, Timestamp timestamp) {
        this.playerId = playerId;
        this.password = password;
        this.timestamp = timestamp;
    }

    /**
     * Returns the ID of the player that set the password
     * @return
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Returns the password that was set
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the timestamp of when this password was set
     * @return
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
}
