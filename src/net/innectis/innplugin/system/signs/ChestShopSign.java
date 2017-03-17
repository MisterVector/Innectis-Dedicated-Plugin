package net.innectis.innplugin.system.signs;

import net.innectis.innplugin.items.IdpMaterial;

/**
 * A class describing a chest shop sign
 *
 * @author AlphaBlend
 */
public class ChestShopSign extends MaterialSign {

    private int amount;
    private int valutas;
    private ChestShopType type;

    public ChestShopSign(IdpMaterial material, int amount, int valutas, ChestShopType type) {
        super(material);

        this.amount = amount;
        this.valutas = valutas;
        this.type = type;
    }

    /**
     * Gets the amount of the item involved with this chest shop
     * @return
     */
    public int getAmount() {
        return amount;
    }
    /**
     * Gets the valuta amount of this chest shop
     * @return
     */
    public int getCost() {
        return valutas;
    }

    /**
     * Gets the type of chest shop
     * @return
     */
    public ChestShopType getShopType() {
        return type;
    }

    @Override
    public WallSignType getType() {
        return WallSignType.CHEST_SHOP;
    }

    // Lists all possible chest shop types
    public enum ChestShopType {
        BUY,
        SELL;
    }
    
}
