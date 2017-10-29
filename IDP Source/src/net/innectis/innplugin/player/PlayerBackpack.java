package net.innectis.innplugin.player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.StackBag;
import org.bukkit.util.NumberConversions;

/**
 *
 * @author Hret
 *
 * This class is used to create keep track of the items inside a player's backpack.
 * Itself the class extends the IdpContainer for easy access to manipulation methods.
 * <p/>
 * Different then the IdpContainer, the PlayerBackpack doesn't copy the items. The items within the
 * playerback are the current state of the items.
 *
 */
public class PlayerBackpack extends IdpContainer {

    // The backpack always has a size of 54, but access to slots is
    // dependant on a player's rank
    private static final int BACKPACK_SIZE = 54;

    private final PlayerCredentials ownerCredentials;
    private long bagid;

    /** Makes an empty backpack */
    private PlayerBackpack(PlayerCredentials ownerCredentials) {
        super(new IdpItemStack[BACKPACK_SIZE], BACKPACK_SIZE);
        this.ownerCredentials = ownerCredentials;
    }

    private PlayerBackpack(PlayerCredentials ownerCredentials, long bagid, IdpItemStack[] contents) {
        super(contents, BACKPACK_SIZE);
        this.ownerCredentials = ownerCredentials;
        this.bagid = bagid;
    }

    /**
     * Gets the credentials of the owner of this backpack
     * @return
     */
    public PlayerCredentials getOwnerCredentials() {
        return ownerCredentials;
    }

    /**
     * The name of the player that owns this backpack.
     * @return
     */
    public String getOwner() {
        return ownerCredentials.getName();
    }

    /**
     * Sets the items in the backpack.
     * @param items
     */
    public void setItems(IdpItemStack[] items) {
        if (items.length > BACKPACK_SIZE) {
            throw new RuntimeException("Backpack too large!");
        }

        super.items = items;
    }

    /**
     * Updates the items in this backpack with the
     * specified items. Note that if the size of
     * the items passed in is less than the backpack's
     * size, then only the items in the front will be modified
     * @param items
     */
    public void updateItems(IdpItemStack[] items) {
        if (items.length > BACKPACK_SIZE) {
            throw new RuntimeException("Backpack too large!");
        }

        for (int i = 0; i < items.length; i++) {
            super.setItemAt(i, items[i]);
        }
    }

    /**
     * Saves the player's backpack to the database
     * @returns true when it was succesfully saved.
     */
    public boolean save() {
        PreparedStatement statement = null;

        try {
            StackBag bag = new StackBag(bagid, getItems());
            bag.save();

            statement = DBManager.prepareStatement("UPDATE players SET backpack = ? WHERE player_id = ?");
            statement.setLong(1, bag.getBagid());
            statement.setString(2, ownerCredentials.getUniqueId().toString());
            statement.execute();
            return true;
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to save " + getOwner() + "'s backpack!", ex);
        } finally {
            DBManager.closePreparedStatement(statement);
        }

        return false;
    }

    /**
     * Loads a backpack from the database by the specified player ID
     * @param playerId
     * The ID of the player's backpack to load
     * @return The backpack of the player or a new one.
     * If it returns null something went wrong.
     */
    public static PlayerBackpack loadBackpackFromDB(UUID playerId, String playerName) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = DBManager.prepareStatement("SELECT backpack FROM players WHERE player_id = ? AND backpack IS NOT NULL;");
            statement.setString(1, playerId.toString());
            set = statement.executeQuery();

            PlayerCredentials credentials = PlayerCredentialsManager.getByUniqueId(playerId, true);
            PlayerBackpack pack = null;

            if (set.next()) {
                long bagid = set.getLong("backpack");

                if (bagid > 0) {
                    pack = new PlayerBackpack(credentials, bagid, StackBag.getContentbag(bagid).getContents());
                }
            }

            if (pack == null) {
                pack = new PlayerBackpack(credentials);
            }

            return pack;
        } catch (SQLException ex) {
            InnPlugin.logError("Unable to load backpack for player " + playerName + "!", ex);
        } finally {
            DBManager.closeResultSet(set);
            DBManager.closePreparedStatement(statement);
        }

        return null;
    }

    /**
     * Clears the backpack
     */
    public void clear() {
        super.clearContainer(BACKPACK_SIZE);
    }

    /**
     * Counts how many items will be dropped from this
     * backpack on death
     * @param group
     * @return
     */
    public int getDropAmount(PlayerGroup group) {
        double dropPercent = getDropPercent(group);

        int count = 0;

        // Count how many non-air items exist, so we can calculate
        // the drop count based on this
        for (int i = 0; i < super.items.length; i++) {
            if (super.items[i] == null || super.items[i].getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            count++;
        }

        return NumberConversions.floor(count * dropPercent);
    }

    /**
     * Gets the percentage of items that will drop from
     * this backpack
     * @param group
     * @return
     */
    public double getDropPercent(PlayerGroup group) {
        double dropPercent = 0.0d;

        if (group.equalsOrInherits(PlayerGroup.GOLDY)) {
            dropPercent = 0.5d;
        } else if (group.equalsOrInherits(PlayerGroup.SUPER_VIP)) {
            dropPercent = 0.75d;
        } else {
            // VIP
            dropPercent = 1.0d;
        }

        return dropPercent;
    }

    /**
     * Gets the size of a backpack from the specified player group
     * @param group
     * @return
     */
    public static int getBackpackSize(PlayerGroup group) {
        int size = 0;

        if (group.equalsOrInherits(PlayerGroup.GOLDY)) {
            return 54;
        } else if (group.equals(PlayerGroup.SUPER_VIP)) {
            return 27;
        } else if (group.equals(PlayerGroup.VIP)) {
            return 9;
        }

        return size;
    }
    
}