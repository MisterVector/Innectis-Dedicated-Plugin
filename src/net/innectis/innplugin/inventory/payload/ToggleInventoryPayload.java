package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.system.window.windows.ToggleWindow;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Payload for the toggle inventory
 *
 * @author AlphaBlend
 */
public class ToggleInventoryPayload extends InventoryPayload {

    private ToggleWindow toggleWindow = null;

    public ToggleInventoryPayload(ToggleWindow toggleWindow) {
        super();

        this.toggleWindow = toggleWindow;
    }

    @Override
    public void onInventoryClick(IdpPlayer player, InventoryClickEvent event) {
        event.setCancelled(true);

        Inventory inv = event.getInventory();
        ClickType clickType = event.getClick();
        int slot = event.getSlot();

        toggleWindow.onInventoryClick(inv, clickType, slot, player);
    }
    
}
