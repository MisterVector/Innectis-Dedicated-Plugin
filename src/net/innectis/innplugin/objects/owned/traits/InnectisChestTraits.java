package net.innectis.innplugin.objects.owned.traits;

import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.objects.owned.InnectisChest;

/**
 * Traits for a chest
 *
 * @author AlphaBlend
 */
public class InnectisChestTraits extends InnectisOwnedObjectTraits {
    private IdpInventory inv;

    public InnectisChestTraits(InnectisChest chest) {
        super(chest);

        this.inv = chest.getInventory();
    }

    /**
     * Applies this chest's traits to the target
     * @param chest
     */
    public void applyTraits(InnectisChest chest) {
        super.applyTraits(chest);

        chest.setInventory(inv);
    }
}
