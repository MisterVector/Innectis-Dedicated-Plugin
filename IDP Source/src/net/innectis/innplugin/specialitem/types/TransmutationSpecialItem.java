package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that allows the changing of
 * one material into another
 *
 * @author Nosliw
 */
public class TransmutationSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Right-click to change selection size");
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Left-click to change blocks");
        itemstack.getItemdata().addLore(ChatColor.YELLOW + "Selection size: 1");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        if (action == Action.PHYSICAL) {
            return false;
        }

        // Check for permission and if the user can use the bonus again
        if (!player.hasPermission(Permission.bonus_misc_transmute)) {
            return true;
        }

        if (action == Action.LEFT_CLICK_BLOCK) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);
            TransmuteBlocks tb = TransmuteBlocks.getTransmuteBlock(mat);

            if (tb == null) {
                player.getHandle().playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 5, 5);
                return true;
            }

            IdpMaterial newMaterial = tb.getNextMaterial(mat);

            // Stairs need to preserve their data value
            boolean preserveDataValue = (tb == TransmuteBlocks.STAIRS_WOOD || tb == TransmuteBlocks.STAIRS_SANDSTONE);

            Location loc = block.getLocation();
            boolean canBuild = (player.hasPermission(Permission.tinywe_override_useanywhere) ? true : BlockHandler.canBuildInArea(player, loc, BlockHandler.ACTION_BLOCK_PLACED, true));

            if (!canBuild) {
                player.getHandle().playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 5, 5);
            } else {
                player.getHandle().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 5);

                int size = getSelectionSize(item.getItemdata());

                for (Block b : BlockHandler.getBlocksNearby(loc, new IdpMaterial[]{mat}, size)) {
                    Location bloc = b.getLocation();
                    byte oldDat = -1;

                    if (preserveDataValue) {
                        oldDat = BlockHandler.getBlockData(b);
                    }

                    if ((player.hasPermission(Permission.tinywe_override_useanywhere) ? true : BlockHandler.canBuildInArea(player, bloc, BlockHandler.ACTION_BLOCK_PLACED, true))) {
                        BlockHandler.setBlock(b, newMaterial);

                        if (oldDat != -1) {
                            BlockHandler.setBlockData(b, oldDat);
                        }

                        b.getWorld().playEffect(bloc, Effect.MOBSPAWNER_FLAMES, 5);
                    }
                }
            }
        } else if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            ItemData data = item.getItemdata();
            int size = getSelectionSize(data);

            if (size > 4) {
                size = 0;
            } else {
                size++;
            }

            setSelectionSize(data, size);
            setSelectionLore(data, size);
            player.setItemInHand(handSlot, item);
            player.getHandle().playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 5, 5);
            player.printInfo("Transmutation Size changed to: " + size);
        }

        return true;

    }

    /**
     * Sets the selection size lore of the transmute tool
     * @param data
     * @param size
     */
    private void setSelectionLore(ItemData data, int size) {
        data.setLore(new String[] {
            ChatColor.GRAY + "Right-click to change selection size",
            ChatColor.GRAY + "Left-click to change blocks",
            ChatColor.YELLOW + "Selection size: " + size
        });
    }

    /**
     * Gets the selection size of the transmute tool
     * @param data
     * @return
     */
    private int getSelectionSize(ItemData data) {
        try {
            return Integer.parseInt(data.getValue("selection_size"));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Sets the selection size of the transmute tool
     * @param data
     * @param size
     */
    private void setSelectionSize(ItemData data, Integer size) {
        data.setValue("selection_size", size.toString());
    }

    public enum TransmuteBlocks {
        STONES(IdpMaterial.STONE, IdpMaterial.GRANITE, IdpMaterial.POLISHED_GRANITE, IdpMaterial.DIORITE,
               IdpMaterial.POLISHED_DIORITE, IdpMaterial.ANDESITE, IdpMaterial.POLISHED_ANDESITE),
        STONES_COBBLE(IdpMaterial.COBBLESTONE, IdpMaterial.MOSSY_COBBLESTONE),
        PLANKS(IdpMaterial.OAK_PLANK, IdpMaterial.SPRUCE_PLANK, IdpMaterial.BIRCH_PLANK,
               IdpMaterial.JUNGLE_PLANK, IdpMaterial.ACACIA_PLANK, IdpMaterial.DARK_OAK_PLANK),
        FENCES(IdpMaterial.OAK_FENCE, IdpMaterial.SPRUCE_FENCE, IdpMaterial.BIRCH_FENCE,
               IdpMaterial.JUNGLE_FENCE, IdpMaterial.ACACIA_FENCE, IdpMaterial.DARK_OAK_FENCE),
        FENCE_GATES(IdpMaterial.OAK_FENCE_GATE, IdpMaterial.SPRUCE_FENCE_GATE, IdpMaterial.BIRCH_FENCE_GATE,
               IdpMaterial.JUNGLE_FENCE_GATE, IdpMaterial.ACACIA_FENCE_GATE, IdpMaterial.DARK_OAK_FENCE_GATE),
        PRISMARINE(IdpMaterial.PRISMARINE, IdpMaterial.PRISMARINE_BRICKS, IdpMaterial.DARK_PRISMARINE),
        GRASS(IdpMaterial.GRASS, IdpMaterial.DIRT, IdpMaterial.PODZOL,
              IdpMaterial.GRASS_PATH),
        SANDSTONE(IdpMaterial.SANDSTONE, IdpMaterial.PRETTY_SANDSTONE, IdpMaterial.SMOOTH_SANDSTONE,
               IdpMaterial.RED_SANDSTONE, IdpMaterial.CHISELED_RED_SANDSTONE, IdpMaterial.SMOOTH_RED_SANDSTONE),
        QUARTZ_BLOCKS(IdpMaterial.QUARTZ_BLOCK, IdpMaterial.QUARTZ_BLOCK_CHISELLED, IdpMaterial.QUARTZ_BLOCK_PILLAR),
        CLAY(IdpMaterial.CLAY, IdpMaterial.BRICK, IdpMaterial.CLAY_HARD),
        SAND(IdpMaterial.SAND, IdpMaterial.RED_SAND),
        STAIRS_WOOD(IdpMaterial.WOODEN_STAIRS, IdpMaterial.SPRUCE_STAIRS, IdpMaterial.BIRCH_STAIRS,
               IdpMaterial.JUNGLE_STAIRS, IdpMaterial.ACACIA_STAIRS, IdpMaterial.DARK_OAK_STAIRS),
        STAIRS_SANDSTONE(IdpMaterial.SANDSTONE_STAIRS, IdpMaterial.RED_SANDSTONE_STAIRS),
        LOGS(IdpMaterial.OAK_LOG, IdpMaterial.SIDE_OAK_LOG_NORTH, IdpMaterial.SIDE_OAK_LOG_EAST,
             IdpMaterial.BARK_OAK_LOG, IdpMaterial.SPRUCE_LOG, IdpMaterial.SIDE_SPRUCE_LOG_NORTH,
             IdpMaterial.SIDE_SPRUCE_LOG_EAST, IdpMaterial.BARK_SPRUCE_LOG, IdpMaterial.BIRCH_LOG,
             IdpMaterial.SIDE_BIRCH_LOG_NORTH, IdpMaterial.SIDE_BIRCH_LOG_EAST, IdpMaterial.BARK_BIRCH_LOG,
             IdpMaterial.JUNGLE_LOG, IdpMaterial.SIDE_JUNGLE_LOG_NORTH, IdpMaterial.SIDE_JUNGLE_LOG_EAST,
             IdpMaterial.BARK_JUNGLE_LOG, IdpMaterial.ACACIA_LOG, IdpMaterial.SIDE_ACACIA_LOG_NORTH,
             IdpMaterial.SIDE_ACACIA_LOG_EAST, IdpMaterial.BARK_ACACIA_LOG, IdpMaterial.DARK_OAK_LOG,
             IdpMaterial.SIDE_DARK_OAK_LOG_NORTH, IdpMaterial.SIDE_DARK_OAK_LOG_EAST, IdpMaterial.BARK_DARK_OAK_LOG),
        LEAVES(IdpMaterial.OAK_LEAVES, IdpMaterial.SPRUCE_LEAVES, IdpMaterial.BIRCH_LEAVES,
               IdpMaterial.JUNGLE_LEAVES, IdpMaterial.ACACIA_LEAVES, IdpMaterial.DARK_OAK_LEAVES),
        DOUBLE_SLABS(IdpMaterial.DOUBLE_OAK_WOOD_SLAB, IdpMaterial.DOUBLE_BIRCH_WOOD_SLAB, IdpMaterial.DOUBLE_SPRUCE_WOOD_SLAB,
             IdpMaterial.DOUBLE_JUNGLE_WOOD_SLAB, IdpMaterial.DOUBLE_ACACIA_WOOD_SLAB, IdpMaterial.DOUBLE_DARKOAK_WOOD_SLAB),
        DOUBLE_SLABS_STONE(IdpMaterial.DOUBLE_STONE_SLAB,  IdpMaterial.DOUBLE_SMOOTH_STONE_SLAB),
        DOUBLE_SLABS_SANDSTONE(IdpMaterial.DOUBLE_SANDSTONE_SLAB, IdpMaterial.DBL_RED_SANDSTONE_SLAB, IdpMaterial.SMTH_DBL_RED_SAND_SLAB),
        SINGLE_SLABS_WOOD(IdpMaterial.OAK_WOOD_SLAB, IdpMaterial.SPRUCE_WOOD_SLAB, IdpMaterial.BIRCH_WOOD_SLAB,
             IdpMaterial.JUNGLE_WOOD_SLAB, IdpMaterial.ACACIA_WOOD_SLAB, IdpMaterial.DARK_OAK_WOOD_SLAB,
             IdpMaterial.UP_OAK_WOOD_SLAB, IdpMaterial.UP_SPRUCE_WOOD_SLAB, IdpMaterial.UP_BIRCH_WOOD_SLAB,
             IdpMaterial.UP_JUNGLE_WOOD_SLAB, IdpMaterial.UP_ACACIA_WOOD_SLAB,
             IdpMaterial.UP_DARK_OAK_WOOD_SLAB),
        SINGLE_SLABS_SANDSTONE(IdpMaterial.SANDSTONE_SLAB, IdpMaterial.RED_SANDSTONE_SLAB,
             IdpMaterial.UP_SANDSTONE_SLAB, IdpMaterial.UP_RED_SANDSTONE_SLAB),
        STONE_BRICKS(IdpMaterial.STONE_BRICKS, IdpMaterial.MOSSY_STONE_BRICKS, IdpMaterial.CRACKED_STONE_BRICKS,
             IdpMaterial.CIRCLE_STONE_BRICKS),
        COBBLESTONE_WALLS(IdpMaterial.COBBLESTONE_WALL, IdpMaterial.COBBLESTONE_MOSSY_WALL),
        GLASS(IdpMaterial.GLASS, IdpMaterial.GLASS_STAINED_WHITE, IdpMaterial.GLASS_STAINED_ORANGE,
             IdpMaterial.GLASS_STAINED_MAGENTA, IdpMaterial.GLASS_STAINED_LIGHT_BLUE, IdpMaterial.GLASS_STAINED_YELLOW,
             IdpMaterial.GLASS_STAINED_LIME, IdpMaterial.GLASS_STAINED_PINK, IdpMaterial.GLASS_STAINED_GRAY,
             IdpMaterial.GLASS_STAINED_LIGHT_GRAY, IdpMaterial.GLASS_STAINED_CYAN, IdpMaterial.GLASS_STAINED_PURPLE,
             IdpMaterial.GLASS_STAINED_BLUE, IdpMaterial.GLASS_STAINED_BROWN, IdpMaterial.GLASS_STAINED_GREEN,
             IdpMaterial.GLASS_STAINED_RED, IdpMaterial.GLASS_STAINED_BLACK),
        GLASS_PANE(IdpMaterial.GLASS_PANE, IdpMaterial.GLASS_PANE_WHITE, IdpMaterial.GLASS_PANE_ORANGE,
             IdpMaterial.GLASS_PANE_MAGENTA, IdpMaterial.GLASS_PANE_LIGHT_BLUE, IdpMaterial.GLASS_PANE_YELLOW,
             IdpMaterial.GLASS_PANE_LIME, IdpMaterial.GLASS_PANE_PINK, IdpMaterial.GLASS_PANE_GRAY,
             IdpMaterial.GLASS_PANE_LIGHT_GRAY, IdpMaterial.GLASS_PANE_CYAN, IdpMaterial.GLASS_PANE_PURPLE,
             IdpMaterial.GLASS_PANE_BLUE, IdpMaterial.GLASS_PANE_BROWN, IdpMaterial.GLASS_PANE_GREEN,
             IdpMaterial.GLASS_PANE_RED, IdpMaterial.GLASS_PANE_BLACK),
        WOOL(IdpMaterial.WOOL_WHITE, IdpMaterial.WOOL_ORANGE, IdpMaterial.WOOL_MAGENTA,
             IdpMaterial.WOOL_LIGHTBLUE, IdpMaterial.WOOL_YELLOW, IdpMaterial.WOOL_LIGHTGREEN,
             IdpMaterial.WOOL_PINK, IdpMaterial.WOOL_GRAY, IdpMaterial.WOOL_LIGHTGRAY,
             IdpMaterial.WOOL_CYAN, IdpMaterial.WOOL_PURPLE, IdpMaterial.WOOL_BLUE,
             IdpMaterial.WOOL_BROWN, IdpMaterial.WOOL_DARKGREEN, IdpMaterial.WOOL_RED,
             IdpMaterial.WOOL_BLACK),
        STAINED_CLAY(IdpMaterial.CLAY_WHITE, IdpMaterial.CLAY_ORANGE, IdpMaterial.CLAY_MAGENTA,
             IdpMaterial.CLAY_LIGHTBLUE, IdpMaterial.CLAY_YELLOW, IdpMaterial.CLAY_LIGHTGREEN,
             IdpMaterial.CLAY_PINK, IdpMaterial.CLAY_GRAY, IdpMaterial.CLAY_LIGHTGRAY,
             IdpMaterial.CLAY_CYAN, IdpMaterial.CLAY_PURPLE, IdpMaterial.CLAY_BLUE,
             IdpMaterial.CLAY_BROWN, IdpMaterial.CLAY_DARKGREEN, IdpMaterial.CLAY_RED,
             IdpMaterial.CLAY_BLACK),
        CARPET(IdpMaterial.CARPET_WHITE, IdpMaterial.CARPET_ORANGE, IdpMaterial.CARPET_MAGENTA,
             IdpMaterial.CARPET_LIGHTBLUE, IdpMaterial.CARPET_YELLOW, IdpMaterial.CARPET_LIGHTGREEN,
             IdpMaterial.CARPET_PINK, IdpMaterial.CARPET_GRAY, IdpMaterial.CARPET_LIGHTGRAY,
             IdpMaterial.CARPET_CYAN, IdpMaterial.CARPET_PURPLE, IdpMaterial.CARPET_BLUE,
             IdpMaterial.CARPET_BROWN, IdpMaterial.CARPET_DARKGREEN, IdpMaterial.CARPET_RED,
             IdpMaterial.CARPET_BLACK),
        QUARTZ(IdpMaterial.QUARTZ_BLOCK, IdpMaterial.QUARTZ_BLOCK_CHISELLED, IdpMaterial.QUARTZ_BLOCK_PILLAR);

        private final IdpMaterial[] materials;

        private TransmuteBlocks(IdpMaterial... materials) {
            this.materials = materials;
        }

        /**
         * Checks if the specified material is valid for
         * these transmute materials
         * @param mat
         * @return
         */
        public boolean isValidMaterial(IdpMaterial mat) {
            for (IdpMaterial m : materials) {
                if (m == mat) {
                    return true;
                }
            }

            return false;
        }

        /**
         * Gets the next material in the series for this material
         * @param mat
         * @return
         */
        public IdpMaterial getNextMaterial(IdpMaterial mat) {
            int idx = 0;

            for (int i = 0; i < materials.length; i++) {
                IdpMaterial m = materials[i];

                if (m == mat) {
                    idx = i + 1;
                    break;
                }
            }

            if (idx == materials.length) {
                return materials[0];
            } else {
                return materials[idx];
            }
        }

        /**
         * Gets the next transmute material for the given block
         * @param mat
         * @return
         */
        public static TransmuteBlocks getTransmuteBlock(IdpMaterial mat) {
            for (TransmuteBlocks tb : values()) {
                if (tb.isValidMaterial(mat)) {
                    return tb;
                }
            }

            return null;
        }
    }

}
