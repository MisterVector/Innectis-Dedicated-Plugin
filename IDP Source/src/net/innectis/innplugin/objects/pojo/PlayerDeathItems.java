package net.innectis.innplugin.objects.pojo;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;

/**
 * This class contains items that will both be removed from
 * the player as well as items the player will keep
 *
 * @author AlphaBlend
 */
public class PlayerDeathItems {

    private IdpItemStack[] keepItems = null;
    private IdpItemStack[] droppedItems = null;

    public PlayerDeathItems(IdpItemStack[] keepItems) {
        this(keepItems, new IdpItemStack[36]);
    }

    public PlayerDeathItems(IdpItemStack[] keepItems, IdpItemStack[] droppedItems) {
        this.keepItems = keepItems;
        this.droppedItems = droppedItems;
    }

    /**
     * Returns the items that will be kept
     * @return
     */
    public IdpItemStack[] getItemsToKeep() {
        return keepItems;
    }

    /**
     * Checks if there are any items that will be kept
     * @return
     */
    public boolean hasItemsToKeep() {
        for (IdpItemStack stack : keepItems) {
            if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * Returns the items that will be dropped
     * @return
     */
    public IdpItemStack[] getDroppedItems() {
        return droppedItems;
    }

    /**
     * Gets the size of the dropped items
     * @return
     */
    public int getDroppedItemsCount() {
        int count = 0;

        for (IdpItemStack stack : droppedItems) {
            if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            count++;
        }

        return count;
    }

    /**
     * Checks if there are items that will be dropped
     * @return
     */
    public boolean hasItemsToDrop() {
        return getDroppedItemsCount() > 0;
    }
    
}
