package net.innectis.innplugin.handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.player.IdpPlayer;

/**
 * Class that handles player's transaction objects (for currency and points)
 *
 * @author AlphaBlend
 */
public final class TransactionHandler {

    private static Map<UUID, TransactionObject> playerTransactions = new HashMap<UUID, TransactionObject>();

    private TransactionHandler() {
    }

    public enum TransactionType {
        VALUTAS("Valutas", "vT"),
        VALUTAS_TO_BANK("Valutas to bank", "vtB"),
        VALUTAS_TO_PLAYER("Valutas from bank", "vfB"),
        VALUTAS_IN_BANK("Banked Valutas", "bvT"),
        VOTE_POINTS("Vote Points", "vP"),
        PVP_POINTS("PvP Points", "PvPP");

        private String name;
        private String shortName;

        private TransactionType(String name, String shortName) {
            this.name = name;
            this.shortName = shortName;
        }

        /**
         * Gets the name of this transaction type
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the short name of this transaction type
         * @return
         */
        public String getShortName() {
            return shortName;
        }

        /**
         * Gets a transaction type from the specified name
         * @param type
         * @return
         */
        public static TransactionType fromString(String type) {
            for (TransactionType tt : values()) {
                if (tt.getShortName().equalsIgnoreCase(type)) {
                    return tt;
                }
            }

            return null;
        }
    }

    /**
     * Removes the transaction object from the cache
     * @param player
     */
    public static void removeTransactionObjectFromCache(IdpPlayer player) {
        UUID playerId = player.getUniqueId();

        if (playerTransactions.containsKey(playerId)) {
            playerTransactions.remove(playerId);
        }
    }

    /**
     * Gets the transaction object of the specified player
     * @param player
     * @return
     */
    public static TransactionObject getTransactionObject(IdpPlayer player) {
        return getTransactionObject(player.getUniqueId(), player.getName());
    }

    /**
     * Gets the transaction object of the player by their ID
     *
     * @param playerId
     * @return the transaction object of the player
     * or null if the player doesn't exist
     */
    public static TransactionObject getTransactionObject(UUID playerId, String playerName) {
        TransactionObject to = playerTransactions.get(playerId);

        // If the currency is not in cache, load from database
        if (to == null) {
            to = loadPlayerBalance(playerId, playerName);

            IdpPlayer testPlayer = InnPlugin.getPlugin().getPlayer(playerId);

            // Cache the object if we have a player online
            if (testPlayer != null) {
                playerTransactions.put(playerId, to);
            }
        }

        return to;
    }

    /**
     * Loads the balance from the specified player ID into memory
     * @param playerId
     * @return
     */
    private static TransactionObject loadPlayerBalance(UUID playerId, String playerName) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT valutas, valutas_in_bank, valutas_to_bank, valutas_to_player, pvp_points, vote_points FROM players WHERE player_id = ?");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            if (set.next()) {
                int valutas = set.getInt("valutas");
                int valutasInBank = set.getInt("valutas_in_bank");
                int valutasToBank = set.getInt("valutas_to_bank");
                int valutasToPlayer = set.getInt("valutas_to_player");
                int pvpPoints = set.getInt("pvp_points");
                int votePoints = set.getInt("vote_points");

                TransactionObject transaction = new TransactionObject(playerId, playerName, valutas, valutasInBank, valutasToBank, valutasToPlayer, pvpPoints, votePoints);

                return transaction;
            }
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load player balance for " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

}
