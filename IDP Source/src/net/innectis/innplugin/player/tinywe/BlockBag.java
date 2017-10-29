package net.innectis.innplugin.player.tinywe;

import net.innectis.innplugin.player.tinywe.blockcounters.MaterialSelector;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpRegion;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.loggers.BlockLogger;
import net.innectis.innplugin.loggers.BlockLogger.BlockAction;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory.CountType;
import net.innectis.innplugin.player.tinywe.IdpEditSession.SpecialItemHandleResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Hret
 */
public class BlockBag {

    private IdpRegion region;

    public BlockBag(IdpRegion region) {
        this.region = region;
    }

    /**
     * This will check the region for the size that is allowed
     *
     * @param session
     * @throws RegionSizeException
     */
    public void checkRegion(IdpEditSession session) throws RegionSizeException {
        if (region.getArea() > session.getMaxSelectionSize()) {
            throw new RegionSizeException("The total size of your selection is too big, max size: " + session.getMaxSelectionSize() + "!");
        }
    }

    /**
     * This will count all blocks of the selected material
     *
     * @param session
     * @param world
     * @param material
     * @return
     * @throws RegionSizeException
     */
    public int countBlocks(IdpEditSession session, IdpWorld world, MaterialSelector selector) throws RegionSizeException {
        checkRegion(session);
        int counter = 0;
        for (int x = region.getLowestX(); x <= region.getHighestX(); x++) {
            for (int z = region.getLowestZ(); z <= region.getHighestZ(); z++) {
                for (int y = region.getLowestY(); y <= region.getHighestY(); y++) {
                    if (selector == null || selector.materialSelected(IdpMaterial.fromBlock(world.getBlockAt(new Vector(x, y, z))))) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    /**
     * *
     * Gets the selection of blocks
     *
     * @param session
     * @param world
     * @param Selector
     * @param Counttype
     * @return List of blocks
     * @throws RegionSizeException
     */
    public List<Block> getBlockList(IdpEditSession session, World world, MaterialSelector selector, CountType countype) throws RegionSizeException {
        checkRegion(session);
        BlockCounter counter = BlockCounterFactory.getCounter(countype);
        // Check for the counter
        if (counter == null) {
            throw new UnsupportedOperationException("The counter needed is not implemented yet");
        }

        return counter.getBlockList(region, world, selector);
    }

    /**
     * Sets the list of blocks to the given material. Its not possible for this
     * method to select what blocks you want to replace. The blocks that are
     * given to this method should be selected with a class that implements the
     * BlockCounter class.
     *
     * @param session
     * @param editsession
     * @param blocks
     * @param force
     * @param toMaterial
     * @param virtual
     * @throws TWEActionNotFinishedException
     * @see BlockCounter
     * @see BlockCounterFactory
     * @return amount of blocks changed
     */
    public TinyWEBlockChanges setBlocks(IdpEditSession session, List<Block> blocks, IdpMaterial toMaterial, boolean force, boolean virtual) throws TWEActionNotFinishedException {
        int maxBlockChanges = session.getMaxBlockChanges(); // Keeps track of the amount of block that are allowed to be changed.

        // Counts the blocks that were processed
        int processedBlocks = 0;

        // Counts the blocks that have actually changed
        int changedBlockCounter = 0;

        // Counts the blocks that were ignored
        int ignoredBlocks = 0;

        // Counts the blocks that are explicitly ignored
        int explicitIgnored = 0;

        // Sort them
        sortBlocks(blocks);

        IdpPlayer player = session.getPlayer();

        for (Block block : blocks) {
            // Max blocks checked
            if (maxBlockChanges-- < 0) {
                throw new TWEActionNotFinishedException("Your selection is too large, can't edit all blocks. "
                        + " (" + changedBlockCounter + " of max " + session.getMaxBlockChanges() + " blocks changed");
            }

            IdpMaterial blockMaterial = IdpMaterial.fromBlock(block);

            // Same type, skip block
            if (toMaterial == blockMaterial) {
                continue;
            }

            processedBlocks++;

            // Indicates if the block change counter should be ignored
            boolean ignored = false;

            // Check access (use placed as that also checks for abnormal blocks like bedrock)
            if (session.canOverwriteBlock(block)) {
                try {
                    // Check if the block change had failed
                    switch (changeBlock(session, toMaterial, block, force, virtual)) {
                        case -1:
                            throw new TWEActionNotFinishedException("You don't have enough blocks to set this region. "
                                    + "(" + changedBlockCounter + " of " + blocks.size() + " blocks changed)");
                        case -2:
                            ignoredBlocks++;
                            // Drop to case 0 to ignore
                        case 0:
                            ignored = true;

                            continue;
                        case 1:
                            IdpMaterial mat = IdpMaterial.fromBlock(block);
                            Location loc = block.getLocation();

                            // Log the action when success!
                            BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
                            blockLogger.logBlockAction(player.getUniqueId(), mat, loc, BlockAction.WE_ACTION);
                            break;
                    }
                } catch (TWEActionNotFinishedException twex) {
                    throw twex;
                } catch (Exception ex) {
                    InnPlugin.logError("Exception in setting block! (TWE)", ex);
                }

                if (!ignored) {
                    // Block has changed to increase counter
                    changedBlockCounter++;
                }
            } else {
                explicitIgnored++;
            }
        }

        return new TinyWEBlockChanges(processedBlocks, ignoredBlocks, changedBlockCounter, explicitIgnored);
    }

    /**
     * Handles the updating of the block aswell as taking and giving the items
     * to/from the player.
     *
     * @param session
     * @param toMaterial
     * @param block
     * @return 1 if success, 0 if the item couldn't be given to the
     * player, -1 if player doesn't have the required item, -2 if
     * the item could not be given to the player either because
     * of a full inventory or the item was explicitly denied, or
     * because the player did not have an item required for
     * the transaction
     * @throws TWEActionNotFinishedException
     */
    public int changeBlock(IdpEditSession session, IdpMaterial toMaterial, Block block, boolean force, boolean virtual) throws TWEActionNotFinishedException {
        Location loc = block.getLocation();
        IdpBlockData blockData = null;

        switch (toMaterial) {
            case LOCKED: // Lock the block.
                blockData = BlockHandler.getIdpBlockData(loc);
                boolean locked = blockData.isUnbreakable();

                if (!locked) {
                    blockData.setUnbreakable(true);
                    return 1;
                } else {
                    return 0;
                }
            case UN_LOCKED: // Unlock the block.
                blockData = BlockHandler.getIdpBlockData(loc);
                boolean unlocked = !blockData.isUnbreakable();

                if (!unlocked) {
                    blockData.setUnbreakable(false);
                    return 1;
                } else {
                    return 0;
                }
            case VIRTUAL: // Make the block virtual.
                blockData = BlockHandler.getIdpBlockData(loc);

                if (!blockData.isVirtualBlock()) {
                    return 0;
                }

                blockData.setVirtualBlockStatus(false);
                return 1;
            case NON_VIRTUAL: // Make the block non-virtual.
                blockData = BlockHandler.getIdpBlockData(loc);

                if (blockData.isVirtualBlock()) {
                    return 0;
                }

                blockData.setVirtualBlockStatus(true);
                return 1;
            }

        // Cannot place material, so don't do anything
        if (!session.canPlaceMaterial(toMaterial)) {
            return 0;
        }

        // Get the inventory item equivalent of what the player is trying to set
        // to remove the required item
        IdpMaterial convertMaterial = session.getInventoryMaterial(toMaterial);

        // Check if the inventory should be used
        if (session.useInventory()) {
            // A flag to indicate if an item was given to the player
            boolean handledItemTransaction = false;

            // Used to check if the inventory should be checked
            boolean checkInventory = false;

            // Check if the player can acquire this block
            if (session.canProcessBlock(block)) {
                // Don't attempt to remove air from inventory
                if (convertMaterial != IdpMaterial.AIR) {
                    // If setting the block to a specified material, make sure the player has the item
                    if (!session.hasItemInInventory(convertMaterial)) {
                        return -1;
                    }
                }

                SpecialItemHandleResult handleResult = session.handleSpecialBlock(block);

                if (handleResult == SpecialItemHandleResult.NOT_SPECIAL) {
                    if (IdpMaterial.fromBlock(block) != IdpMaterial.AIR) {
                        IdpItemStack returnStack = session.getReturnStack(block.getState());

                        if (returnStack != null) {
                            int remain = session.addItemToPlayer(returnStack);

                            // We have successfully handled this
                            if (remain == 0) {
                                handledItemTransaction = true;
                            }
                        }
                    } else {
                        // Air is always handled
                        handledItemTransaction = true;
                    }
                } else {
                    // Check for special item being handled successfully
                    handledItemTransaction = (handleResult == SpecialItemHandleResult.SPECIAL_HANDLED);
                }

                // Remove the item the player is setting the block to if
                // all is successful
                if (handledItemTransaction && convertMaterial != IdpMaterial.AIR) {
                    session.removeItemFromPlayer(convertMaterial);

                    switch (convertMaterial) {
                        case WATER_BUCKET:
                        case LAVA_BUCKET:
                            session.addItemToPlayer(new IdpItemStack(IdpMaterial.BUCKET, 1));
                    }
                }

                // If the item couldn't go into the inventory, let's check the
                // inventory one last time to make sure they can set the target block
                // (the inventory is only checked after this point if force is true)
                if (!handledItemTransaction) {
                    checkInventory = true;
                }
            } else {
                // We will need to check the inventory after this point
                checkInventory = true;
            }

            // If the item did not go into the player's inventory then it cannot
            // be removed from the world unless being forced
            if (!handledItemTransaction) {
                if (!force) {
                    return -2;
                }

                // Only check inventory if we can (removes duplicate check)
                if (checkInventory) {
                    // Don't attempt to remove air from inventory
                    if (convertMaterial != IdpMaterial.AIR) {
                        // If unable to remove any items, we cannot continue
                        if (!session.removeItemFromPlayer(convertMaterial)) {
                            return -1;
                        }
                    }
                }
            }
        }

        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat == IdpMaterial.BOOKCASE) {
            InnectisBookcase bookcase = InnectisBookcase.getBookcase(loc);
            InnectisLot lot = LotHandler.getLot(loc);
            if (bookcase != null) {
                if (!bookcase.getOwner().equalsIgnoreCase(session.getPlayer().getName())
                        && !session.getPlayer().hasPermission(Permission.owned_object_override)
                        && !(lot != null && lot.getOwner().equalsIgnoreCase(session.getPlayer().getName()))) {
                    return 0;
                }

                IdpItemStack[] items = bookcase.getItems();
                World world = block.getWorld();

                for (int i = 0; i < items.length; i++) {
                    if (items[i] != null && items[i].getMaterial() != IdpMaterial.AIR) {
                        world.dropItem(loc, items[i].toBukkitItemstack());
                    }
                    items[i] = null;
                }
                bookcase.delete();
            }
        }

        blockData = BlockHandler.getIdpBlockData(loc);

        // Clear the existing block data if it exists
        if (blockData.hasData()) {
            blockData.clear();
        }

        if (virtual) {
            blockData.setVirtualBlockStatus(true);
        }

        byte data = (byte) toMaterial.getData();

        // Special case pistons, set data to 1
        if (toMaterial == IdpMaterial.PISTON || toMaterial == IdpMaterial.STICKY_PISTON) {
            data = 1;
        }

        // Make sure leaves do not decay when TinyWE'd
        if (toMaterial.getBukkitMaterial() == Material.LEAVES
                || toMaterial.getBukkitMaterial() == Material.LEAVES_2) {
            data = (byte) (data | 4);
        }

        // Orient portal blocks correctly based on current selection size
        if (toMaterial == IdpMaterial.PORTAL) {
            int diffX = Math.abs(region.getHighestX() - region.getLowestX());
            int diffZ = Math.abs(region.getHighestZ() - region.getLowestZ());

            data = (byte) (diffX > diffZ ? 1 : 2);
        }

        BlockHandler.setBlock(block, toMaterial, data);

        // Log this as a block placement
        session.placedBlock(block);

        return 1;

    }

    /**
     * Sort the blocks to make sure the non-solid ones are done first and before
     * the solid ones.
     *
     * @param blocks
     */
    private void sortBlocks(List<Block> blocks) {
        Collections.sort(blocks, new ComparatorImpl());
    }

    /**
     * Comparator to sort blocks. Nonsolid will be first in the array (uncluding
     * liquids)
     */
    private static class ComparatorImpl implements Comparator<Block> {

        public ComparatorImpl() {
        }

        public int compare(Block blk1, Block blk2) {
            IdpMaterial mat1 = IdpMaterial.fromBlock(blk1);
            IdpMaterial mat2 = IdpMaterial.fromBlock(blk2);

            boolean nonsol1 = mat1.isWeakBlock();
            boolean nonsol2 = mat2.isWeakBlock();

            if (nonsol1 == nonsol2) {
                return 0;
            } else {
                return nonsol1 ? -1 : 1;
            }
        }
    }

    /**
    * An object that keeps track of block changes in a TinyWE edit session
    *
    * @author AlphaBlend
    */
    public class TinyWEBlockChanges {
        private int processedBlocks;
        private int blocksChanged;
        private int ignoredBlocks;
        private int explicitIgnored;

        public TinyWEBlockChanges(int processedBlocks, int ignoredBlocks, int blocksChanged, int explicitIgnored) {
            this.processedBlocks = processedBlocks;
            this.ignoredBlocks = ignoredBlocks;
            this.blocksChanged = blocksChanged;
            this.explicitIgnored = explicitIgnored;
        }

        /**
         * Gets how many blocks were processed
         * @return
         */
        public int getBlocksProcessed() {
            return processedBlocks;
        }

        /**
         * Gets how many blocks were changed by TinyWE
         *
         * @return
         */
        public int getBlocksChanged() {
            return blocksChanged;
        }

        /**
         * Gets how many blocks were ignored because they could not go into
         * the player's inventory
         * @return
         */
        public int getIgnoredBlocks() {
            return ignoredBlocks;
        }

        /**
         * Gets how many blocks were explicitly ignored (can't remove by -force)
         * @return
         */
        public int getExplicitlyIgnored() {
            return explicitIgnored;
        }
    }

}