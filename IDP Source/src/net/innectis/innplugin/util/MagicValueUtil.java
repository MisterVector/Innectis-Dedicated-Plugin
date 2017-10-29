package net.innectis.innplugin.util;

import net.innectis.innplugin.items.IdpMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Utilities that deal with magic values
 *
 * @author AlphaBlend
 */
public class MagicValueUtil {

    /**
     * Gets the type ID from material data
     * @param data
     * @return
     */
    public static int getIdFromMaterialData(MaterialData data) {
        return data.getItemTypeId();
    }

    /**
     * Gets the data value from material data
     * @param data
     * @return
     */
    public static byte getDataFromMaterialData(MaterialData data) {
        return data.getData();
    }

    /**
     * Takes a material and amount and makes a bukkit item stack
     * @param mat
     * @param amount
     * @return
     */
    public static ItemStack materialAmountToItemStack(IdpMaterial mat, int amount) {
        return new ItemStack(mat.getBukkitMaterial(), amount, (short) 0, (byte) mat.getData());
    }

    /**
     * Gets the type ID from the bukkit material
     * @param mat
     * @return
     */
    public static int getIdFromBukkitMaterial(Material mat) {
        return mat.getId();
    }

}
