package net.innectis.innplugin.system.economy;

import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpWorldType;

/**
 * An enum providing blocks with a valuta value when broken
 *
 * @author AlphaBlend
 */
public enum BlockEconomy {

    COAL_ORE(IdpMaterial.COAL_ORE, 1, 10, IdpWorldType.RESWORLD),
    NETHER_QUARTZ_ORE(IdpMaterial.NETHER_QUARTZ_ORE, 1, 6, IdpWorldType.NETHER),
    IRON_ORE(IdpMaterial.IRON_ORE, 1, 1, IdpWorldType.RESWORLD),
    GOLD_ORE(IdpMaterial.GOLD_ORE, 2, 1, IdpWorldType.RESWORLD),
    LAPIZ_LAZULI(IdpMaterial.LAPIS_LAZULI_OREBLOCK, 2, 1, IdpWorldType.RESWORLD),
    REDSTONE_ORE(IdpMaterial.REDSTONE_ORE, 2, 1, IdpWorldType.RESWORLD),
    GLOWING_REDSTONE_ORE(IdpMaterial.GLOWING_REDSTONE_ORE, 2, 1, IdpWorldType.RESWORLD),
    DIAMOND_ORE(IdpMaterial.DIAMOND_ORE, 3, 1, IdpWorldType.RESWORLD),
    EMERALD_ORE(IdpMaterial.EMERALD_ORE, 8, 1, IdpWorldType.RESWORLD),
    MOB_SPAWNER(IdpMaterial.MOB_SPAWNER, 10, 1, IdpWorldType.RESWORLD, IdpWorldType.NETHER);

    private IdpMaterial material;
    private int value;
    private int countToValue;
    private IdpWorldType[] worldTypes;

    private BlockEconomy(IdpMaterial material, int value, int countToValue, IdpWorldType... worldTypes) {
        this.material = material;
        this.value = value;
        this.countToValue = countToValue;
        this.worldTypes = worldTypes;
    }

    /**
     * Gets the material of this block
     * @return
     */
    public IdpMaterial getMaterial() {
        return material;
    }

    /**
     * Gets the value of this block
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the amount of blocks to mine of this type to get
     * the specified value
     * @return
     */
    public int getCountToValue() {
        return countToValue;
    }

    /**
     * Checks if this block is valid on the specified world
     * @param type
     * @return
     */
    public boolean isValidOnWorld(IdpWorldType type) {
        for (IdpWorldType wt : worldTypes) {
            if (wt == type) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a block economy value from the specified material
     * @param mat
     * @return
     */
    public static BlockEconomy fromMaterial(IdpMaterial mat) {
        for (BlockEconomy be : values()) {
            if (be.getMaterial() == mat) {
                return be;
            }
        }

        return null;
    }

}
