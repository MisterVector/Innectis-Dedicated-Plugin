package net.innectis.innplugin.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.data.IdpBlockData.SaveStrategy;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.util.Queue;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Ladder;

/**
 * @author Hret
 */
public final class BridgeHandler {

    private static final Object _syncLock = new Object();
    private static Map<String, Long> bridgeActions = new HashMap<String, Long>();
    /** The block that is the ID of the controlblock for bridges */
    public static final IdpMaterial CONTROLBLOCK_MATERIAL = IdpMaterial.OBSIDIAN;
    /** Blockfaces; North, East, South, West */
    private static final BlockFace[] bf_NESW = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};
    /** Blockfaces; North, East, South, West, Up, Down */
    private static final BlockFace[] bf_NESWUD = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN};

    /**
     * Checks if its allowed to do an action with a bridge
     * @param block
     * @return
     */
    private static boolean canDoBridgeAction(Block block) {
        synchronized (_syncLock) {
            String key = blockToKey(block);

            if (bridgeActions.containsKey(key)) {
                long time = bridgeActions.get(key);

                if (System.currentTimeMillis() - time < getBridge_Timeout()) {
                    return false;
                }
            }
            bridgeActions.put(key, System.currentTimeMillis());
        }

        clearList();
        return true;
    }

    /**
     * Removes the old values from the list
     */
    private static void clearList() {
        synchronized (_syncLock) {
            String key;
            Set<String> keyset = new HashSet<String>(bridgeActions.keySet());
            for (Iterator<String> it = keyset.iterator(); it.hasNext();) {
                key = it.next();
                if (System.currentTimeMillis() - bridgeActions.get(key) > getBridge_Timeout()) {
                    bridgeActions.remove(key);
                }
            }
        }
    }

    /**
     * Makes a key from a block
     * @param block
     * @return
     */
    private static String blockToKey(Block block) {
        Location loc = block.getLocation();
        return String.format("{%s.%s.%s.%s}", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
    }

    /**
     * Closes a bridge
     * @param baseBlock
     */
    public static void closeBridge(Block baseBlock) {
        InnectisLot lot = LotHandler.getLot(baseBlock.getLocation());
        int bridgePower = (lot != null && lot.isFlagSet(LotFlagType.BIGSTRUCTURE) ? getBigBridgePower() : getBridgePower());
        closeBridge(baseBlock, bridgePower);
    }

    /**
     * Closes a bridge, where power is the range the power is supported for control blocks
     * @param baseBlock
     * @param power
     */
    private static void closeBridge(Block baseBlock, int power) {
        if (!canDoBridgeAction(baseBlock)) {
            return;
        }

        // Determine the max length of the bridge
        InnectisLot lot = LotHandler.getLot(baseBlock.getLocation());
        int bridgeLength = (lot != null && lot.isFlagSet(LotFlagType.BIGSTRUCTURE) ? getMax_BigBridgeLength() : getMax_BridgeLength());

        BlockFace direction;
        Block currentBlock;
        List<Block> blocklist;
        BridgeBlockObject bbo;
        Queue<BridgeBlockObject> queue = new Queue<BridgeBlockObject>(4);
        queue.put(new BridgeBlockObject(baseBlock, power, BlockFace.SELF));

        Block materialBlock = baseBlock.getRelative(BlockFace.DOWN);
        IdpMaterial bridgeMaterial = IdpMaterial.fromBlock(materialBlock);

        // Ladders have a special blockface!
        BlockFace specialMaterialFace = BlockFace.SELF;
        if (bridgeMaterial == IdpMaterial.LADDER) {
            Ladder ldr = (Ladder) materialBlock.getState().getData();
            specialMaterialFace = ldr.getAttachedFace();
        }


        if (!bridgeItemAllowed(bridgeMaterial)) {
            // Get default if item not allowed!
            bridgeMaterial = IdpMaterial.COBBLESTONE;
        }

        while (!queue.isEmpty()) {
            bbo = queue.pop();
            baseBlock = bbo.getBlock();
            power = bbo.getPower();
            direction = bbo.getDirection();

            if (power <= 0) {
                continue;
            }

            power--;

            // Set data
            //baseBlock.setData(CONTROL_DATA_CONTROLLER);
            BlockHandler.getIdpBlockData(baseBlock.getLocation(), IdpBlockData.SaveStrategy.EAGER).setBridgeController(true);

            for (BlockFace face : bf_NESW) {
                // List to store the bridge
                blocklist = new ArrayList<Block>();
                // Reset block
                currentBlock = baseBlock;
                // Check is length is still allowed
                if (getBridge(currentBlock, face, blocklist) == bridgeLength + 1) {
                    continue;
                }
                // Set blocks in list
                for (Block block : blocklist) {
                    // Check if the block can be replaced
                    if (canReplaceForBridge(block)) {

                        boolean allow = false;
                        int handlingType = isSpecialBlock(bridgeMaterial);

                        if (handlingType == 0) {
                            allow = true;
                        } else {
                            // 1 = block below
                            if ((handlingType & 1) != 0) {
                                allow = IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN)).isSolid();

                                if (!allow) {
                                    break;
                                }
                            }

                            // 2 = block on side
                            if ((handlingType & 2) != 0) {
                                // When special face is set, only look for the given direction
                                if (specialMaterialFace == BlockFace.SELF) {
                                    for (BlockFace tmpface : bf_NESW) {
                                        allow = IdpMaterial.fromBlock(block.getRelative(tmpface)).isSolid();

                                        if (allow) {
                                            break;
                                        }
                                    }
                                } else {
                                    allow = IdpMaterial.fromBlock(block.getRelative(specialMaterialFace)).isSolid();
                                }

                                if (!allow) {
                                    break;
                                }
                            }
                            // 4 = netherrack below
                            if ((handlingType & 4) != 0) {
                                allow = (IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN)) == IdpMaterial.NETHERRACK);

                                if (!allow) {
                                    break;
                                }
                            }

                            // 8 = no snow below
                            if ((handlingType & 8) != 0) {
                                allow = (IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN)) != IdpMaterial.SNOW_LAYER);

                                if (!allow) {
                                    break;
                                }
                            }
                        }

                        if (allow) {
                            IdpMaterial mat = IdpMaterial.fromBlock(block);

                            // If the material is not solid, then break it if it is not air
                            if (!mat.isSolid() && mat != IdpMaterial.AIR) {
                                block.breakNaturally();
                            }

                            // Set block
                            BlockHandler.setBlock(block, bridgeMaterial);

                            // Set virtual data
                            BlockHandler.getIdpBlockData(block.getLocation(), IdpBlockData.SaveStrategy.LAZY).setVirtualBlockStatus(true);
                        }
                    }
                }
            }

            direction = direction.getOppositeFace();

            // Power surrounding control blocks
            for (BlockFace face : bf_NESWUD) {
                if (face == direction) {
                    continue;
                }
                // Check if surrounding block is a powered off controlblock
                if (IdpMaterial.fromBlock(baseBlock.getRelative(face)) == CONTROLBLOCK_MATERIAL
                        && !BlockHandler.getIdpBlockData(baseBlock.getRelative(face).getLocation()).isBridgeController()) {
                    queue.put(new BridgeBlockObject(baseBlock.getRelative(face), power, face));
//                    closeBridge(baseBlock.getRelative(face), power, typeid);
                }
            }
        }
    }

    /**
     * Opens a bridge
     * @param baseBlock
     */
    public static void openBridge(Block baseBlock) {
        InnectisLot lot = LotHandler.getLot(baseBlock.getLocation());
        int bridgePower = (lot != null && lot.isFlagSet(LotFlagType.BIGSTRUCTURE) ? getBigBridgePower() : getBridgePower());
        openBridge(baseBlock, bridgePower);
    }

    /**
     * Opens a bridge and opens surrounding blocks
     *
     * @param baseBlock
     * @param power
     */
    public static void openBridge(Block baseBlock, int power) {
        if (!canDoBridgeAction(baseBlock)) {
            return;
        }

        // Determine the max length of the bridge
        InnectisLot lot = LotHandler.getLot(baseBlock.getLocation());
        int bridgeLength = (lot != null && lot.isFlagSet(LotFlagType.BIGSTRUCTURE) ? getMax_BigBridgeLength() : getMax_BridgeLength());

        // init vars
        BlockFace direction;
        Block currentBlock;
        List<Block> blocklist;
        BridgeBlockObject bbo;
        Queue<BridgeBlockObject> queue = new Queue<BridgeBlockObject>(4);
        queue.put(new BridgeBlockObject(baseBlock, power, BlockFace.SELF));

        while (!queue.isEmpty()) {
            bbo = queue.pop();
            baseBlock = bbo.getBlock();
            power = bbo.getPower();
            direction = bbo.getDirection();

            if (power <= 0) {
                continue;
            }

            // Reduce power for later
            power--;

            // Reset data
            //baseBlock.setData((byte) 0);
            BlockHandler.getIdpBlockData(baseBlock.getLocation(), IdpBlockData.SaveStrategy.EAGER).setBridgeController(false);

            for (BlockFace face : bf_NESW) {
                // Reset vars
                blocklist = new ArrayList<Block>();
                currentBlock = baseBlock;
                // Get bridge and check if length allowed
                if (getBridge(currentBlock, face, blocklist) == bridgeLength + 1) {
                    continue;
                }

                // Get blocks
                for (Block block : blocklist) {
                    IdpBlockData blockData = BlockHandler.getIdpBlockData(block.getLocation());

                    // Check if virtual block
                    if (blockData.isVirtualBlock()) {
                        // Remove block
                        BlockHandler.setBlock(block, IdpMaterial.AIR);

                        blockData.setSaveStrategy(SaveStrategy.LAZY);

                        // Clear data!
                        blockData.clear();
                    }
                }
            }

            direction = direction.getOppositeFace();
            // Power surrounding control blocks
            for (BlockFace face : bf_NESWUD) {
                if (face == direction) {
                    continue;
                }

                // Check if powered on control block
                if (IdpMaterial.fromBlock(baseBlock.getRelative(face)) == CONTROLBLOCK_MATERIAL && BlockHandler.getIdpBlockData(baseBlock.getRelative(face).getLocation()).isBridgeController()) {
                    queue.put(new BridgeBlockObject(baseBlock.getRelative(face), power, face));
//                    openBridge(baseBlock.getRelative(face), power);
                }
            }
        }
    }

    private static int getBridge(Block currentBlock, BlockFace face, List<Block> blocklist) {
        InnectisLot lot = LotHandler.getLot(currentBlock.getLocation());
        int bridgeLength = (lot != null && lot.isFlagSet(LotFlagType.BIGSTRUCTURE) ? getMax_BigBridgeLength() : getMax_BridgeLength());

        int size = 0;
        for (; size <= bridgeLength; size++) {
            currentBlock = currentBlock.getRelative(face);
            if (IdpMaterial.fromBlock(currentBlock) == CONTROLBLOCK_MATERIAL) {
                break;
            } else {
                blocklist.add(currentBlock.getRelative(BlockFace.UP));
            }
        }
        return size;
    }

    /**
     * Checks if the material is allowed to be used for a bridge
     * @param id
     * @return <br/>
     * 1 = block below <br/>
     * 2 = block on side <br/>
     * 4 = netherrack below <br/>
     * 8 = no snow below <br/>
     * Bitwise! <br/>
     * 5 = block below and netherrack below! <br/>
     */
    public static int isSpecialBlock(IdpMaterial mat) {
        switch (mat) {
            case POWERED_RAIL:
            case DETECTOR_RAIL:
            case ACTIVATOR_RAIL:
            case RAILS:
                return 1;
            case LADDER:
                return 2;
            case FIRE:
                return 1 + 4;
            case PUMPKIN:
            case JACK_O_LANTERN:
                return 8;
        }

        return 0;
    }

    /**
     * Checks if the given block can be replaced for an bridge block
     * @param block
     * @return
     */
    private static boolean canReplaceForBridge(Block block) {

        // Don't create blocks on NoStructure lot
        InnectisLot lot = LotHandler.getLot(block.getLocation());
        if (lot != null && lot.isFlagSet(LotFlagType.NOSTRUCTURE)) {
            return false;
        }

        IdpMaterial mat = IdpMaterial.fromBlock(block);

        switch (mat) {
            case AIR:
            case WHEAT_BLOCK:
            case POTATO_BLOCK:
            case CARROT_BLOCK:
            case SUGAR_CANE:
            case COCOA_PLANT:
            case NETHER_WART:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if the material is allowed to be used for a bridge
     * @param id
     * @return
     */
    public static boolean bridgeItemAllowed(IdpMaterial mat) {
        // Only allow solid blocks
        if (!mat.isSolid()) {
            // These blocks are exceptions as they are handled in a special way
            switch (mat) {
                case POWERED_RAIL:
                case DETECTOR_RAIL:
                case ACTIVATOR_RAIL:
                case RAILS:
                case LADDER:
                case FIRE:
                    return true;
                default:
                    return false;
            }
        }

        // Leave ores alone
        if (mat.isOre()) {
            return false;
        }

        // Don't allow glass panes
        if (mat == IdpMaterial.GLASS_PANE || mat.isTypeOf(IdpMaterial.GLASS_PANE_WHITE)) {
            return false;
        }

        // Don't allow monster blocks
        if (mat.isTypeOf(IdpMaterial.MONSTER_BLOCK_STONE)) {
            return false;
        }

        // Don't allow structure type blocks
        if (mat == IdpMaterial.STRUCTURE_VOID || mat.isTypeOf(IdpMaterial.STRUCTURE_BLOCK_SAVE)) {
            return false;
        }

        // These materials CANNOT be used for bridges
        switch (mat) {
            case AIR:
            case PODZOL:
            case SAND:
            case RED_SAND:
            case GRAVEL:
            case BEDROCK:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
            case BIRCH_LEAVES:
            case JUNGLE_LEAVES:
            case DARK_OAK_LEAVES:
            case ACACIA_LEAVES:
            case SPONGE:
            case WET_SPONGE:
            case DISPENSER:
            case NOTE_BLOCK:
            case BED_BLOCK:
            case PISTON:
            case STICKY_PISTON:
            case PISTON_EXTENSION:
            case PISTON_MOVING_PIECE:
            case BROWN_MUSHROOM:
            case RED_MUSHROOM:
            case TNT:
            case WORKBENCH:
            case BOOKCASE:
            case MOB_SPAWNER:
            case CHEST:
            case TRAPPED_CHEST:
            case FARMLAND:
            case FURNACE:
            case BURNING_FURNACE:
            case OAK_DOOR_BLOCK:
            case SPRUCE_DOOR_BLOCK:
            case BIRCH_DOOR_BLOCK:
            case JUNGLE_DOOR_BLOCK:
            case ACACIA_DOOR_BLOCK:
            case DARK_OAK_DOOR_BLOCK:
            case IRON_DOOR_BLOCK:
            case CACTUS:
            case JUKEBOX:
            case OAK_FENCE:
            case BIRCH_FENCE:
            case SPRUCE_FENCE:
            case JUNGLE_FENCE:
            case ACACIA_FENCE:
            case DARK_OAK_FENCE:
            case NETHER_BRICK_FENCE:
            case CAKE:
            case TRAP_DOOR:
            case IRON_BARS:
            case IRON_TRAP_DOOR:
            case OAK_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case ENCHANTMENT_TABLE:
            case CAULDRON_BLOCK:
            case END_PORTAL_FRAME:
            case ENDER_CHEST:
            case COMMAND_BLOCK:
            case REPEAT_COMMAND_BLOCK:
            case CHAIN_COMMAND_BLOCK:
            case BEACON:
            case COBBLESTONE_WALL:
            case COBBLESTONE_MOSSY_WALL:
            case SKULL_BLOCK:
            case ANVIL:
            case SLIGHTLY_DAMAGED_ANVIL:
            case VERY_DAMAGED_ANVIL:
            case DAYLIGHT_DETECTOR:
            case INVERTED_DAY_DETECTOR:
            case REDSTONE_BLOCK:
            case GLOWSTONE:
            case HOPPER:
            case DROPPER:
            case IRON_BLOCK:
            case COAL_BLOCK:
            case GOLD_BLOCK:
            case DIAMOND_BLOCK:
            case EMERALD_BLOCK:
            case LAPIS_LAZULI_BLOCK:
            case REDSTONE_LAMP_OFF:
            case REDSTONE_LAMP_ON:
            case DRAGON_EGG:
            case SEA_LANTERN:
            case HAY:
            case FREE_STAND_BANNER:
            case WALL_MOUNT_BANNER:
            case FROSTED_ICE:
            case MAGMA_BLOCK:
                return false;
        }

        return true;
    }

    /**
     * @return the Bridge_Timeout
     */
    public static int getBridge_Timeout() {
        return Configuration.BRIDGE_TIMEOUT;
    }

    /**
     * @return the MAX_BRIDGE_LENGTH
     */
    public static int getMax_BridgeLength() {
        return Configuration.MAX_BRIDGE_LENTH;
    }

    /**
     * @return the MAX_BIGBRIDGE_LENGTH
     */
    public static int getMax_BigBridgeLength() {
        return Configuration.MAX_BIGBRIDGE_LENTH;
    }

    /**
     * @return the MAX_BRIDGE_POWER
     */
    public static int getBridgePower() {
        return Configuration.BRIDGE_POWER;
    }

    /**
     * @return the MAX_BIGBRIDGE_POWER
     */
    public static int getBigBridgePower() {
        return Configuration.BIG_BRIGE_POWER;
    }
}

class BridgeBlockObject {

    private Block block;
    private int power;
    private BlockFace direction;

    public BridgeBlockObject(Block block, int power, BlockFace direction) {
        this.block = block;
        this.power = power;
        this.direction = direction;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public BlockFace getDirection() {
        return direction;
    }

}
