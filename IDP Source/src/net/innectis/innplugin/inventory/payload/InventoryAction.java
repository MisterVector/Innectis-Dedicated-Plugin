package net.innectis.innplugin.inventory.payload;

/**
 * An enum of actions related to inventory payloads
 *
 * @author AlphaBlend
 */
public enum InventoryAction {

    // Indicates that the inventory of a player should
    // be closed with a delay to allow the bukkit
    // event to finish processing
    CLOSE_PLAYER_INVENTORY_LATER;

}
