package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.handlers.TrashHandler;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.windows.TrashWindow;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

/**
 * An implementation of inventory payload
 * involving the trash
 *
 * @author AlphaBlend
 */
public class TrashViewItemsPayload extends InventoryPayload {

    private TrashWindow trashWindow = null;

    public TrashViewItemsPayload(TrashWindow trashWindow) {
        super();

        this.trashWindow = trashWindow;
    }

    @Override
    public void onInventoryClick(IdpPlayer player, InventoryClickEvent event) {
        event.setCancelled(true);

        Inventory inv = event.getInventory();
        ClickType clickType = event.getClick();
        int slot = event.getRawSlot();

        trashWindow.onInventoryClick(inv, clickType, slot, player);
    }

    @Override
    public void onInventoryOpen(IdpPlayer player, InventoryOpenEvent event) {
        TrashHandler.viewerAdded();
    }

    @Override
    public void onInventoryClose(IdpPlayer player, InventoryCloseEvent event) {
        TrashHandler.viewerRemoved();

        // Refresh the trash contents if there are no viewers
        if (TrashHandler.getViewerCount() == 0) {
            PagedInventory trashContent = TrashHandler.getTrashContents();
            trashContent.sort();

            // If the trash no longer has items, we don't
            // need to mark the time to wipe the trash
            if (trashContent.size() == 0) {
                TrashHandler.setTrashStartTime(0L);
            }
        }
    }
    
}
