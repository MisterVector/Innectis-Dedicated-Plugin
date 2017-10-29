package net.innectis.innplugin.objects;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.InnPlugin;

/**
 * Holds the currency for a player
 *
 * @author AlphaBlend
 */
public class TransactionObject {

    private UUID playerId;
    private String playerName;
    private int valutas;
    private int valutasInBank;
    private int valutasToBank;
    private int valutasToPlayer;
    private int pvpPoints;
    private int votePoints;

    public TransactionObject(UUID playerId, String playerName, int valutas, int valutasInBank, int valutasToBank, int valutasToPlayer, int pvpPoints, int votePoints) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.valutas = valutas;
        this.valutasInBank = valutasInBank;
        this.valutasToBank = valutasToBank;
        this.valutasToPlayer = valutasToPlayer;
        this.pvpPoints = pvpPoints;
        this.votePoints = votePoints;
    }

    /**
     * Gets a value by its specified transaction type
     * @param tt the transaction type to get the value of
     * @return
     */
    public int getValue(TransactionType tt) {
        int value;

        switch (tt) {
            case VALUTAS:
                value = valutas;
                break;
            case VALUTAS_IN_BANK:
                value = valutasInBank;
                break;
            case VALUTAS_TO_BANK:
                value = valutasToBank;
                break;
            case VALUTAS_TO_PLAYER:
                value = valutasToPlayer;
                break;
            case PVP_POINTS:
                value = pvpPoints;
                break;
            case VOTE_POINTS:
                value = votePoints;
                break;
            default:
                throw new IllegalStateException("TransactionType " + tt + " not supported");
        }
        return value;
    }

    /**
     * Adds a value of the specified transaction type to the player
     * This immediately updates to the database, so no need to do it manually
     *
     * @param value a value greater than zero
     * @param tt
     */
    public void addValue(int value, TransactionType tt) {
        int targetValue;

        switch (tt) {
            case VALUTAS:
                valutas += value;
                targetValue = valutas;
                break;
            case VALUTAS_IN_BANK:
                valutasInBank += value;
                targetValue = valutasInBank;
                break;
            case VALUTAS_TO_BANK:
                valutasToBank += value;
                targetValue = valutasToBank;
                break;
            case VALUTAS_TO_PLAYER:
                valutasToPlayer += value;
                targetValue = valutasToPlayer;
                break;
            case PVP_POINTS:
                pvpPoints += value;
                targetValue = pvpPoints;
                break;
            case VOTE_POINTS:
                votePoints += value;
                targetValue = votePoints;
                break;
            default:
                throw new IllegalStateException("TransactionType " + tt + " not supported");
        }

        updateDatabase(targetValue, tt);
    }

    /**
     * Subtracts a vslue of the specified transaction type from the player
     * This immediately updates to the database, so no need to do it manually
     *
     * @param value a value greater than zero
     * @param tt
     */
    public void subtractValue(int value, TransactionType tt) {
        int targetValue;

        switch (tt) {
            case VALUTAS:
                valutas -= value;
                targetValue = valutas;
                break;
            case VALUTAS_IN_BANK:
                valutasInBank -= value;
                targetValue = valutasInBank;
                break;
            case VALUTAS_TO_BANK:
                valutasToBank -= value;
                targetValue = valutasToBank;
                break;
            case VALUTAS_TO_PLAYER:
                valutasToPlayer -= value;
                targetValue = valutasToPlayer;
                break;
            case PVP_POINTS:
                pvpPoints -= value;
                targetValue = pvpPoints;
                break;
            case VOTE_POINTS:
                votePoints -= value;
                targetValue = votePoints;
                break;
            default:
                throw new IllegalStateException("TransactionType " + tt + " not supported");
        }

        updateDatabase(targetValue, tt);
    }

    /**
     * Sets the value of the specified transaction type
     * This immediately updates to the database, so no need to do it manually
     *
     * @param value a value greater than zero
     * @param tt
     */
    public void setValue(int value, TransactionType tt) {
        switch (tt) {
            case VALUTAS:
                valutas = value;
                break;
            case VALUTAS_IN_BANK:
                valutasInBank = value;
                break;
            case VALUTAS_TO_BANK:
                valutasToBank = value;
                break;
            case VALUTAS_TO_PLAYER:
                valutasToPlayer = value;
                break;
            case PVP_POINTS:
                pvpPoints = value;
                break;
            case VOTE_POINTS:
                votePoints = value;
                break;
            default:
                throw new IllegalStateException("TransactionType " + tt + " not supported");
        }

        updateDatabase(value, tt);
    }

    /**
     * Updates the value of the specified transaction type to the database
     *
     * @param value
     * @param tt
     */
    private void updateDatabase(Number value, TransactionType tt) {
        PreparedStatement statement = null;

        try {
            String columnName;

            switch (tt) {
                case VALUTAS:
                    columnName = "valutas";
                    break;
                case VALUTAS_IN_BANK:
                    columnName = "valutas_in_bank";
                    break;
                case VALUTAS_TO_BANK:
                    columnName = "valutas_to_bank";
                    break;
                case VALUTAS_TO_PLAYER:
                    columnName = "valutas_to_player";
                    break;
                case PVP_POINTS:
                    columnName = "pvp_points";
                    break;
                case VOTE_POINTS:
                    columnName = "vote_points";
                    break;
                default:
                    throw new IllegalStateException("TransactionType " + tt + " not supported");
            }

            statement = DBManager.prepareStatement("UPDATE players SET " + columnName + " = ? WHERE player_id = ?");
            statement.setInt(1, value.intValue());
            statement.setString(2, playerId.toString());
            statement.execute();
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to update " + tt.getName() + " in database for player " + playerName + "!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }
    }

}
