package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Implementation of inventory payload dealing with
 * autofill inventories
 *
 * @author AlphaBlend
 */
public class AutoRefillInventoryPayload extends InventoryPayload {

    private int size = 0;

    public AutoRefillInventoryPayload(int size) {
        super();

        this.size = size;
    }

    @Override
    public void onInventoryClick(IdpPlayer player, InventoryClickEvent event) {
        event.setCancelled(true);

        // Dont allow moving, just adding or removing items on left click
        // Note: Do not use shift-click, there is a wierd bug that it will transfer the whole chest into inventory...
        if (event.isLeftClick()) {
            // If in chest, add to inv.
            if (event.getCurrentItem() != null && event.getRawSlot() < size) {
                player.addItemToInventory(IdpItemStack.fromBukkitItemStack(event.getCurrentItem()));
            } else {
                // If in inventory, clear the item
                event.setCurrentItem(null);
            }
        }
    }

}
