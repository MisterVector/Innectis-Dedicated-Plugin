package net.innectis.innplugin.system.window.windows;

import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.system.window.PagedInventory;
import net.innectis.innplugin.system.window.WindowSettings;
import org.bukkit.inventory.Inventory;

/**
 * A window class supporting interactions in a trash window
 *
 * @author AlphaBlend
 */
public class TrashWindow extends Window {

    public TrashWindow(PagedInventory trashInventory) {
        super(WindowSettings.TRASH, trashInventory);
    }

    @Override
    public void handlePagedInventoryClick(PagedInventory pagedInventory, Inventory inv, int slot, IdpPlayer player) {
        int windowPage = player.getWindowPage();
        int inventorySlot = (slot - super.getSettings().getInventoryStartIndex());
        IdpItemStack trashStack = pagedInventory.getItemAt(windowPage, inventorySlot);

        // Don't do anything if nothing is here
        if (trashStack == null || trashStack.getMaterial() == IdpMaterial.AIR) {
            // Make sure the item clicked contains no item
            inv.setItem(slot, null);
            return;
        }

        int itemAmount = trashStack.getAmount();
        int itemAcquisitionSize = player.getItemAcquisitionSize();
        int minAmount = Math.min(itemAmount, itemAcquisitionSize);

        IdpContainer container = new IdpContainer(player.getInventory());
        int maxAccept = container.getMaximumAcceptAmount(trashStack, minAmount);

        if (maxAccept == 0) {
            player.printError("Your inventory is too full!");
            return;
        }

        container.addMaterialToStack(trashStack, maxAccept);

        IdpPlayerInventory playerInventory = player.getInventory();
        playerInventory.setItems(container.getItems());
        playerInventory.updateBukkitInventory();
        IdpMaterial trashMaterial = trashStack.getMaterial();

        if (maxAccept == itemAmount) {
            trashStack = IdpItemStack.EMPTY_ITEM;
        } else {
            trashStack.setAmount(itemAmount - maxAccept);
        }

        inv.setItem(slot, trashStack.toBukkitItemstack());
        pagedInventory.setItemAt(windowPage, inventorySlot, trashStack);

        player.printInfo("Added " + maxAccept + " item" + (maxAccept > 1 ? "s" : "") + " of " + trashMaterial);
    }
    
}
