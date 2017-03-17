package net.innectis.innplugin.inventory;

import net.innectis.innplugin.inventory.payload.InventoryPayload;

/**
 *
 * @author Hret
 *
 * Interface to handle special innectis data inside inventory objects
 */
public interface IdpInventoryDataHolder {

    /**
     * Gets the custom payload this inventory is using
     * @return custom data
     */
    InventoryPayload getPayload();

    /**
     * Sets a custom payload for this inventory
     * @param payload
     */
    void setPayload(InventoryPayload payload);

}
