package net.innectis.innplugin.objects;

import java.util.UUID;
import net.innectis.innplugin.player.InventoryType;

/**
 * Data that describes whose inventory is being
 * viewed, the type being viewed, and whether
 * the live inventory is being viewed
 *
 * @author AlphaBlend
 */
public class ViewedPlayerInventoryData {

    private UUID playerId = null;
    private String playerName = null;
    private InventoryType viewedInventoryType = null;
    private boolean isLiveInventory = false;

    public ViewedPlayerInventoryData(UUID playerId, String playerName, InventoryType viewedInventoryType, boolean isLiveInventory) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.viewedInventoryType = viewedInventoryType;
        this.isLiveInventory = isLiveInventory;
    }

    /**
     * Gets the UUID of the player whose
     * inventory is being viewed
     * @return
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Gets the name of the player whose
     * inventory is being viewed
     * @return
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the inventory type being viewed
     * @return
     */
    public InventoryType getViewedInventoryType() {
        return viewedInventoryType;
    }

    /**
     * Sets the inventory type being viewed
     * @param viewedInventoryType
     */
    public void setViewedInventoryType(InventoryType viewedInventoryType) {
        this.viewedInventoryType = viewedInventoryType;
    }

    /**
     * Checks if the inventory being viewed is live
     * @return
     */
    public boolean isViewingLiveInventory() {
        return isLiveInventory;
    }

}
