package net.innectis.innplugin.handlers;

import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.data.IdpBlockData.SaveStrategy;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hret
 */
public final class GateHandler {

    private static final int MAX_GATE_WIDTH = 12;
    private static final int MAX_BIGGATE_WIDTH = 24;
    private static final int MAX_GATE_HEIGHT = 12;
    private static final int MAX_BIGGATE_HEIGHT = 24;

    public static void closeGate(Block block, BlockFace direction, boolean useBig) {
        int maxWidth = (useBig ? MAX_BIGGATE_WIDTH : MAX_GATE_WIDTH);
        int maxHeight = (useBig ? MAX_BIGGATE_HEIGHT : MAX_GATE_HEIGHT);
        int gateWidth = 0, gateHeight = 0;

        IdpMaterial checkMaterial = IdpMaterial.fromBlock(block);

        while (isValidGateMaterial(checkMaterial) && gateWidth < maxWidth) {
            gateWidth++;
            gateHeight = 0;

            Block block2 = block.getRelative(BlockFace.DOWN);
            IdpMaterial freeSpotMaterial = IdpMaterial.fromBlock(block2);

            while (freeSpotMaterial == IdpMaterial.AIR && gateHeight < maxHeight) {
                gateHeight++;

                BlockHandler.setBlock(block2, checkMaterial);
                BlockHandler.getIdpBlockData(block2.getLocation(), SaveStrategy.EAGER).setVirtualBlockStatus(true);

                block2 = block2.getRelative(BlockFace.DOWN);
                freeSpotMaterial = IdpMaterial.fromBlock(block2);
            }

            block = block.getRelative(direction);
            checkMaterial = IdpMaterial.fromBlock(block);
        }
    }

    public static void openGate(Block block, BlockFace direction) {
        IdpMaterial checkMaterial = IdpMaterial.fromBlock(block);

        while (isValidGateMaterial(checkMaterial)) {
            Block block2 = block.getRelative(BlockFace.DOWN);
            IdpMaterial sameMaterialCheck = IdpMaterial.fromBlock(block2);

            while (sameMaterialCheck == checkMaterial) {
                Location loc = block2.getLocation();

                if (!BlockHandler.getIdpBlockData(loc).isVirtualBlock()) {
                    loc.getWorld().dropItem(loc, new ItemStack(checkMaterial.getBukkitMaterial(), 1));
                }

                BlockHandler.setBlock(block2, IdpMaterial.AIR);

                IdpBlockData blockData = BlockHandler.getIdpBlockData(loc, SaveStrategy.EAGER);

                if (blockData.hasData()) {
                    blockData.clear();
                }

                block2 = block2.getRelative(BlockFace.DOWN);
                sameMaterialCheck = IdpMaterial.fromBlock(block2);
            }

            block = block.getRelative(direction);
            checkMaterial = IdpMaterial.fromBlock(block);
        }
    }

    /**
     * Checks if the given material is a valid fence material
     * @param mat
     * @return
     */
    public static boolean isValidGateMaterial(IdpMaterial mat) {
        switch (mat) {
            case OAK_FENCE:
            case SPRUCE_FENCE:
            case BIRCH_FENCE:
            case JUNGLE_FENCE:
            case DARK_OAK_FENCE:
            case ACACIA_FENCE:
            case NETHER_BRICK_FENCE:
            case IRON_BARS:
            case GLASS_PANE:
            case GLASS_PANE_WHITE:
            case GLASS_PANE_ORANGE:
            case GLASS_PANE_MAGENTA:
            case GLASS_PANE_LIGHT_BLUE:
            case GLASS_PANE_YELLOW:
            case GLASS_PANE_LIME:
            case GLASS_PANE_PINK:
            case GLASS_PANE_GRAY:
            case GLASS_PANE_LIGHT_GRAY:
            case GLASS_PANE_CYAN:
            case GLASS_PANE_PURPLE:
            case GLASS_PANE_BLUE:
            case GLASS_PANE_BROWN:
            case GLASS_PANE_GREEN:
            case GLASS_PANE_RED:
            case GLASS_PANE_BLACK:
                return true;
        }

        return false;
    }

}
