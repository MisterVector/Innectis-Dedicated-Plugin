package net.innectis.innplugin.system.window;

import net.innectis.innplugin.items.IdpMaterial;

/**
 * An enum listing buttons that perform special behaviors
 * in an inventory
 *
 * @author AlphaBlend
 */
public enum ButtonType {

    /**
     * A button that goes back a page with an inventory
     */
    PAGE_BACK(IdpMaterial.CHEST),

    /**
     * A button that goes forward a page with an inventory
     */
    PAGE_FORWARD(IdpMaterial.CHEST),

    /**
     * A button that specifies item acquisition amount
     * Left-click increases the value, right-click decreases. Minimum of
     * 1 and maximum of 64
     */
    ITEM_ACQUISITION(IdpMaterial.DISPENSER),

    /**
     * Defines a custom button that can allow a window to define
     * custom behavior from this button
     */
    CUSTOM();

    private IdpMaterial material;

    private ButtonType() {
        this(null);
    }

    private ButtonType(IdpMaterial material) {
        this.material = material;
    }

    /**
     * Gets the material representation of this button type
     * @return
     */
    public IdpMaterial getMaterial() {
        return material;
    }
    
}
