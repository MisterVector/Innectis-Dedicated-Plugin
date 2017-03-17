package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.system.window.windows.MiningStickSettingsWindow;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Inventory payload for the mining stick window
 *
 * @author AlphaBlend
 */
public class MiningStickSettingsPayload extends InventoryPayload {

    private MiningStickSettingsWindow miningStickWindow;

    public MiningStickSettingsPayload(MiningStickSettingsWindow miningStickWindow) {
        this.miningStickWindow = miningStickWindow;
    }

    @Override
    public void onInventoryClick(IdpPlayer player, InventoryClickEvent event) {
        event.setCancelled(true);

        Inventory inv = event.getInventory();
        int slot = event.getSlot();
        ClickType clickType = event.getClick();

        miningStickWindow.onInventoryClick(inv, clickType, slot, player);
    }

}
