package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * The base payload class for a series of payload
 * classes intended to be passed to bukkit's
 * inventory events, whereby they will be used
 * for design simplicity as well as a way to
 * indicate if an event should be handled a
 * special way
 *
 * @author AlphaBlend
 */
public abstract class InventoryPayload {

    private InventoryAction[] actions;

    public InventoryPayload(InventoryAction... actions) {
        this.actions = actions;
    }

    /**
     * Checks if this payload has the specified
     * inventory action
     * @param action
     * @return
     */
    public boolean hasAction(InventoryAction action) {
        for (InventoryAction a : actions) {
            if (a == action) {
                return true;
            }
        }

        return false;
    }

    public void onInventoryClick(IdpPlayer player, InventoryClickEvent event) {

    }

    public void onInventoryOpen(IdpPlayer player, InventoryOpenEvent event) {

    }

    public void onInventoryClose(IdpPlayer player, InventoryCloseEvent event) {

    }

}
