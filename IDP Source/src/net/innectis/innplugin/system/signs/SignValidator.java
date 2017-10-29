package net.innectis.innplugin.system.signs;

import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.system.signs.ChestShopSign.ChestShopType;

/**
 * A class that checks for complete validity of certain sign types
 *
 * @author AlphaBlend
 */
public class SignValidator {

    /**
     * Returns a MaterialSign with the given sign, if applicable
     * @param text
     * @return
     */
    public static MaterialSign getMaterialSign(String[] text) {
        WallSignType type = WallSignType.fromSignText(text);
        MaterialSign materialSign = null;

        if (type != null) {
            switch (type) {
                case CHEST_SHOP:
                    materialSign = getChestShopSign(text);
                    break;
            }
        }

        return materialSign;
    }

    /**
     * Checks if this is a complete chest shop sign
     * @param text
     * @return
     */
    public static ChestShopSign getChestShopSign(String[] text) {
        ChestShopType type = (text[0].equalsIgnoreCase("[buy]") ? ChestShopType.BUY : ChestShopType.SELL);
        IdpMaterial mat = IdpMaterial.fromString(text[1]);

        if (mat != null) {
            int valutas = 0;
            int amount = mat.getMaxStackSize();
            String valutaLine = text[2];

            try {
                if (valutaLine.contains("@")) {
                    String[] content = valutaLine.split("@");

                    amount = Integer.parseInt(content[0].trim());
                    valutas = Integer.parseInt(content[1].toLowerCase().replace("vt", "").trim());
                }
            } catch (NumberFormatException nfe) {
                return null;
            }

            if (valutas < 1) {
                return null;
            }

            return new ChestShopSign(mat, amount, valutas, type);
        } else {
            return null;
        }
    }

}
