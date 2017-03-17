package net.innectis.innplugin.listeners.bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.system.economy.BlockEconomy;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.handlers.BridgeHandler;
import net.innectis.innplugin.handlers.datasource.DBManager;
import net.innectis.innplugin.handlers.GateHandler;
import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EnchantmentType;
import net.innectis.innplugin.objects.PresentContent;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockBreakEvent;
import net.innectis.innplugin.listeners.idp.InnPlayerBlockPlaceEvent;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpDynamicWorldSettings;
import net.innectis.innplugin.location.IdpWorld;
import net.innectis.innplugin.location.IdpWorldFactory;
import net.innectis.innplugin.location.IdpWorldType;
import net.innectis.innplugin.loggers.BlockLogger.BlockAction;
import net.innectis.innplugin.NotchcodeUsage;
import net.innectis.innplugin.loggers.BlockLogger;
import net.innectis.innplugin.loggers.LogType;
import net.innectis.innplugin.objects.EntityConstants;
import net.innectis.innplugin.objects.owned.handlers.ChestHandler;
import net.innectis.innplugin.objects.owned.handlers.DoorHandler;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.handlers.TrapdoorHandler;
import net.innectis.innplugin.objects.owned.handlers.WaypointHandler;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.objects.owned.InnectisChest;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.InnectisSwitch;
import net.innectis.innplugin.objects.owned.InnectisTrapdoor;
import net.innectis.innplugin.objects.owned.InnectisWaypoint;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.objects.owned.SwitchFlagType;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerCredentials;
import net.innectis.innplugin.player.PlayerCredentialsManager;
import net.innectis.innplugin.player.PlayerEffect;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSettings;
import net.innectis.innplugin.system.signs.ChestShopSign;
import net.innectis.innplugin.system.signs.SignValidator;
import net.innectis.innplugin.system.signs.WallSignType;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.system.economy.DroppedValutaOrb;
import net.innectis.innplugin.tasks.sync.WaterRemoveTask;
import net.innectis.innplugin.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Diode;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Hret
 */
public class IdpBlockListener implements Listener {

    private Random random = new Random(System.currentTimeMillis());
    private InnPlugin plugin;

    public IdpBlockListener(InnPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityBlockForm(EntityBlockFormEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Snowman) {
            InnectisLot lot = LotHandler.getLot(event.getBlock().getLocation(), true);

            // Snowmen can only create snow on golemtrail lots
            if (lot != null && lot.isFlagSet(LotFlagType.GOLEMTRAIL)) {
                return;
            }
        } else if (entity instanceof Player) {
            // Only allow player to use frost walker if there is no lot
            // or the player has access to the lot
            IdpPlayer player = plugin.getPlayer((Player) entity);
            Location loc = event.getBlock().getLocation();
            InnectisLot lot = LotHandler.getLot(loc, true);

            if (lot == null || lot.canPlayerAccess(player.getName())
                    || player.hasPermission(Permission.world_build_unrestricted)) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // Only apply super growth if this block is a seed
        if (mat.isSeed()) {
            IdpMaterial matBelowFarmland = IdpMaterial.fromBlock(block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN));
            boolean hasSuperGrowth = (matBelowFarmland == IdpMaterial.PODZOL);

            if (hasSuperGrowth) {
                BlockState state = event.getNewState();
                MaterialData data = state.getData();
                data.setData((byte) 7);

                state.setData(data);
                state.update();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onStructureGrow(StructureGrowEvent event) {

        InnectisLot lot = LotHandler.getLot(event.getLocation());
        Random random = new Random();

        for (Iterator<BlockState> it = event.getBlocks().iterator(); it.hasNext();) {
            BlockState block = it.next();
            InnectisLot lot2 = LotHandler.getLot(block.getLocation());
            if (lot2 != null && lot2 != lot) {
                if (lot != null) {
                    lot = lot.getParentTop();
                }

                lot2 = lot2.getParentTop();
            }
            if (lot != lot2) {

                // Lets drop what we are now missing.
                IdpMaterial material = IdpMaterial.fromItemStack(new ItemStack(block.getType(), 1, block.getRawData()));
                if (material.getDropChance() >= random.nextDouble()) {
                    block.getWorld().dropItem(block.getLocation(), material.getDrops().toBukkitItemstack());
                }
                it.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();
        IdpBlockData blockData = BlockHandler.getIdpBlockData(block.getLocation());

        if (blockData.isVirtualBlock()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat == IdpMaterial.DISPENSER || mat == IdpMaterial.DROPPER) {
            if (!(block.getState() instanceof Dispenser)) {
                return;
            }

            InnectisLot lot = LotHandler.getLot(block.getLocation(), true);
            IdpMaterial itemMaterial = IdpMaterial.fromItemStack(event.getItem());

            // Don't allow dispensers to place some items outside of lots
            switch (itemMaterial) {
                case WATER_BUCKET:
                case LAVA_BUCKET:
                case FIRE_CHARGE:
                case FLINT_AND_TINDER:
                case OAK_BOAT:
                case MINECART:
                case STORAGE_MINECART:
                case POWERED_MINECART:
                case TNT_MINECART:
                case HOPPER_MINECART:
                    BlockFace face = BlockFace.SELF;
                    byte dat = BlockHandler.getBlockData(block);

                    switch (dat) {
                        case 0:
                            face = BlockFace.DOWN;
                            break;
                        case 1:
                            face = BlockFace.UP;
                            break;
                        case 2:
                            face = BlockFace.NORTH;
                            break;
                        case 3:
                            face = BlockFace.SOUTH;
                            break;
                        case 4:
                            face = BlockFace.WEST;
                            break;
                        case 5:
                            face = BlockFace.EAST;
                            break;
                    }

                    InnectisLot lot2 = LotHandler.getLot(block.getRelative(face).getLocation(), true);

                    if (lot != lot2 && !(lot == null || lot2 == null)
                            && !lot2.isFlagSet(LotFlagType.FREEFLOW)) {
                        event.setCancelled(true);
                        return;
                    }
            }

            if (lot != null) {
                // This must be checked first, otherwise InfiniteDispenser behavior could dupe the item
                if (lot.isFlagSet(LotFlagType.INFINITEWATER) && itemMaterial == IdpMaterial.WATER_BUCKET) {
                    // Always cancel here
                    event.setCancelled(true);

                    // If infinite water, do not create a water source block, so instead
                    // go one data value up. Also, since we don't have access to the block
                    // dispensed, we need to create the block ourselves
                    Dispenser disp = (Dispenser) block.getState();
                    org.bukkit.material.Dispenser dispenserData = (org.bukkit.material.Dispenser) disp.getData();
                    Block dispenseBlock = block.getRelative(dispenserData.getFacing());
                    IdpMaterial dispenseBlockMaterial = IdpMaterial.fromBlock(dispenseBlock);

                    // Only dispense for air and weak blocks
                    if (dispenseBlockMaterial == IdpMaterial.AIR || dispenseBlockMaterial.isWeakBlock()) {
                        BlockHandler.setBlock(dispenseBlock, IdpMaterial.WATER);

                        // Add a task that will automatically remove this block after a time
                        plugin.getTaskManager().addTask(new WaterRemoveTask(dispenseBlock, 3000));
                    }

                    return;
                }

                if (lot.isFlagSet(LotFlagType.INFINITEDISPENSER)) {
                    IdpItemStack item = IdpItemStack.fromBukkitItemStack(event.getItem());

                    // Duplicate presentcontent to avoid stacking (and invalid DB entries).
                    if (item.getItemdata().getSpecialItem() == SpecialItemType.PRESENT) {
                        PresentContent present = PresentContent.getPresent(item).clone();
                        present.save();
                        present.setPresent(item);
                    }

                    Dispenser disp = (Dispenser) block.getState();
                    IdpInventory inv = new IdpInventory(disp.getInventory());

                    // We dunno what itemstack is changed, and the itemstack will be updated AFTER this event.
                    // Due to this, we can only know for sure an empty place will not be used by this event
                    // So that means this will act wierd with stacks
                    if (inv.firstEmpty() >= 0) {
                        inv.setItem(inv.firstEmpty(), item);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockSpread(BlockSpreadEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

        if (blockData.isVirtualBlock()) {
            event.setCancelled(true);
            return;
        }

        InnectisLot lotFrom = LotHandler.getLot(event.getSource().getLocation());
        InnectisLot lotTo = LotHandler.getLot(loc, true);
        if (lotFrom == null && lotTo == null) {
            //let it happen
        } else if (lotFrom == lotTo) {
            //let it happen
        } else if (lotTo != null && lotTo.isFlagSet(LotFlagType.FREEFLOW)) {
            //let it happen
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        List<Block> blocklist = new ArrayList<Block>(event.getBlocks());
        blocklist.add(event.getBlock());

        // Add the air block that is going to be 'pushed'
        blocklist.add(blocklist.get(blocklist.size() - 1).getRelative(event.getDirection()));

        for (Block block : blocklist) {
            if (block == null) {
                continue;
            }

            IdpMaterial mat = IdpMaterial.fromBlock(block);
            Location loc = block.getLocation();

            // Stop bookcases
            if (mat == IdpMaterial.BOOKCASE) {
                InnectisBookcase bookcase = InnectisBookcase.getBookcase(loc);
                // Only bookcases that are not empty
                if (bookcase != null && !bookcase.isEmpty()) {
                    event.setCancelled(true);
                    return;
                }
            }

            //Stop pistons from moving locked blocks
            if (BlockHandler.getIdpBlockData(loc).isUnbreakable()) {
                event.setCancelled(true);
                return;
            }

            IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

            //Stop pistons from moving virtual blocks:
            if (blockData.isVirtualBlock()) {
                event.setCancelled(true);
                return;
            }

            //Stop pistons from moving unbreak blocks:
            if (blockData.isUnbreakable()) {
                event.setCancelled(true);
                return;
            }

            //Stop pistons from moving into other lots:
            InnectisLot lotFrom = LotHandler.getLot(loc, true);
            InnectisLot lotTo = LotHandler.getLot(block.getRelative(event.getDirection()).getLocation(), true);

            if (lotFrom == null && lotTo == null) {
                //let it happen
            } else if (lotFrom == lotTo) {
                //let it happen
            } else if (lotFrom != null && lotTo != null && lotFrom.getParentTop() == lotTo.getParentTop()) {
                //let it happen
            } else {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        Block block = event.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        if (mat == IdpMaterial.STICKY_PISTON) {
            Block blockFrom = event.getRetractLocation().getBlock();
            IdpMaterial fromMaterial = IdpMaterial.fromBlock(blockFrom);

            if (fromMaterial == IdpMaterial.AIR) {
                return;
            }

            if (fromMaterial == IdpMaterial.SAND) {
                event.setCancelled(true);
                return;
            }

            Location locFrom = blockFrom.getLocation();

            // Stop bookcases
            if (fromMaterial == IdpMaterial.BOOKCASE) {
                InnectisBookcase bookcase = InnectisBookcase.getBookcase(locFrom);
                // Only bookcases that are not empty
                if (bookcase != null && !bookcase.isEmpty()) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Stop pistons from moving locked blocks
            if (BlockHandler.getIdpBlockData(locFrom).isUnbreakable()) {
                event.setCancelled(true);
                return;
            }

            IdpBlockData blockData = BlockHandler.getIdpBlockData(locFrom);

            // Stop pistons from moving virtual blocks or unbreak blocks
            if (blockData.isVirtualBlock() || blockData.isUnbreakable()) {
                event.setCancelled(true);
                return;
            }

            //Stop pistons from moving into other lots:
            InnectisLot lotFrom = LotHandler.getLot(locFrom, true);
            InnectisLot lotTo = LotHandler.getLot(blockFrom.getRelative(event.getDirection().getOppositeFace()).getLocation(), true);
            if (lotFrom == null && lotTo == null) {
                //let it happen
            } else if (lotFrom == lotTo) {
                //let it happen
            } else {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();
        Location loc = block.getLocation();
        IdpBlockData data = BlockHandler.getIdpBlockData(loc);

        // Virtual leaves cannot decay
        if (data.isVirtualBlock() || data.isUnbreakable()) {
            event.setCancelled(true);
            return;
        }

        // Red and Golden apples have a chance of dropping in all worlds
        if (random.nextInt(900) == 8) {
            event.getBlock().getWorld().dropItem(loc, new ItemStack(IdpMaterial.RED_APPLE.getBukkitMaterial(), 1));
        }

        // Give a very slim chance of golden apples dropping alongside red apples
        if (random.nextInt(15000) == 88) {
            event.getBlock().getWorld().dropItem(loc, new ItemStack(IdpMaterial.GOLD_APPLE.getBukkitMaterial(), 1));
        }
    }

    /**
     * Water flow
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block blockFrom = event.getBlock();
        Block blockTo = event.getToBlock();

        boolean creativewater = false;

        Location locTo = blockTo.getLocation();

        // Check if there's any movement
        if (blockFrom.getX() != blockTo.getX() || blockFrom.getY() != blockTo.getY() || blockFrom.getZ() != blockTo.getZ()) {
            //Stop water AND lava from flowing into other lots:
            InnectisLot lotFrom = LotHandler.getLot(blockFrom.getLocation(), true);
            InnectisLot lotTo = LotHandler.getLot(locTo, true);
            if (lotFrom == null && lotTo == null) {
                //let it happen
            } else if (lotFrom == lotTo) {
                //let it happen
                IdpMaterial matTo = IdpMaterial.fromBlock(blockTo);

                // Do this only if it stays on the same lot and the flag is set
                if ((matTo == IdpMaterial.AIR
                        || matTo == IdpMaterial.WATER)
                        && lotFrom.isFlagSet(LotFlagType.CREATIVEWATER)) {
                    creativewater = true;
                }
            } else if (lotTo != null && lotTo.isFlagSet(LotFlagType.FREEFLOW)) {
                //let it happen
            } else {
                event.setCancelled(true);
                return;
            }
        }

        IdpMaterial matFrom = IdpMaterial.fromBlock(blockFrom);

        if (matFrom.isWater()) {
            if (BlockHandler.getBlocksNearby(locTo, new IdpMaterial[]{IdpMaterial.SPONGE, IdpMaterial.WET_SPONGE}, Configuration.SPONGE_RADIUS).size() > 0) {
                event.setCancelled(true);
                return;
            }

            // Water is in creative mode
            // Check after sponges!
            if (creativewater) {
                BlockHandler.setBlock(blockTo, IdpMaterial.WATER);

                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * IdpBlock is powered by a diode
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPower(BlockRedstoneEvent event) {
        Block sourceBlock = event.getBlock();
        IdpMaterial sourceMaterial = IdpMaterial.fromBlock(sourceBlock);

        // Only process if the source block is a repeater
        if (sourceMaterial != IdpMaterial.REDSTONE_REPEATER_OFF && sourceMaterial != IdpMaterial.REDSTONE_REPEATER_ON) {
            return;
        }

        boolean isPowered = (sourceMaterial == IdpMaterial.REDSTONE_REPEATER_OFF);

        Diode diode = (Diode) sourceBlock.getState().getData();
        Block poweredBlock = sourceBlock.getRelative(diode.getFacing());
        IdpMaterial poweredMaterial = IdpMaterial.fromBlock(poweredBlock);

        // Don't bother processing if air or redstone wire is being powered
        if (poweredMaterial == IdpMaterial.AIR || poweredMaterial == IdpMaterial.REDSTONE_WIRE) {
            return;
        }

        // Innectis switches
        if (poweredMaterial == IdpMaterial.LEVER) {
            InnectisSwitch st = InnectisSwitch.getSwitch(sourceBlock.getLocation());

            // Check if it is an redstone triggered switch
            if (st != null && st.isFlagSet(SwitchFlagType.REDSTONE_TRIGGER)) {
                // Check if the switch needs to be powered on or off
                if ((st.getOnState() && !isPowered) || (!st.getOnState() && isPowered)) {

                    // There is a bug where the updated lever triggers the redstone event again.
                    // Hence why we are forcing the repeater off first
                    if (sourceMaterial == IdpMaterial.REDSTONE_REPEATER_ON) {
                        byte dat = BlockHandler.getBlockData(sourceBlock);
                        BlockHandler.setBlock(sourceBlock, IdpMaterial.REDSTONE_REPEATER_OFF, dat);
                    }

                    st.toggleAll();
                }
            }

            return;
        }

        if (poweredMaterial == IdpMaterial.OBSIDIAN) {
            try {
                // Check if its powered off and if the block is a controller
                // if block is no controller, no need for the openbridge method
                IdpBlockData blockData = BlockHandler.getIdpBlockData(poweredBlock.getLocation());

                if (!isPowered && blockData.isBridgeController()) {
                    BridgeHandler.openBridge(poweredBlock);
                } else {
                    BridgeHandler.closeBridge(poweredBlock);
                }
            } catch (Exception ex) {
                InnPlugin.logError("A bridge has crashed.", ex);
            }

            // Blockfaces
            BlockFace[] blockFaces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};
            // The control block is also used for gates
            for (int i = 0; i < 10; i++) {
                poweredBlock = poweredBlock.getRelative(BlockFace.UP);

                // around the block
                for (BlockFace face : blockFaces) {
                    // Get the surrounding block
                    Block block2 = poweredBlock.getRelative(face);
                    Location loc = block2.getLocation();

                    // Skip virtual blocks
                    if (BlockHandler.getIdpBlockData(loc).isVirtualBlock()) {
                        continue;
                    }

                    // Check if the specified block is valid
                    IdpMaterial blockMat = IdpMaterial.fromBlock(block2);
                    boolean valid = GateHandler.isValidGateMaterial(blockMat);

                    // Valid gate material, continue checking
                    if (valid) {
                        boolean blockbelowFence = false;

                        // Check if block below is a gate
                        IdpMaterial mat2 = IdpMaterial.fromBlock(block2.getRelative(BlockFace.DOWN));
                        blockbelowFence = (mat2 == blockMat);

                        // Open/Close gate
                        if (!isPowered) {
                            if (blockbelowFence) {
                                GateHandler.openGate(block2, face);
                            }
                        } else {
                            if (!blockbelowFence) {
                                InnectisLot lot = LotHandler.getLot(loc, true);
                                boolean useBig = false;

                                if (lot != null) {
                                    // Do not form gate if NoStructure flag is set
                                    if (lot.isFlagSet(LotFlagType.NOSTRUCTURE)) {
                                        return;
                                    }

                                    useBig = lot.isFlagSet(LotFlagType.BIGSTRUCTURE);
                                }

                                GateHandler.closeGate(block2, face, useBig);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBurn(BlockBurnEvent event) {
        Location loc = event.getBlock().getLocation();
        IdpWorldType worldType = IdpWorldType.getIdpWorldType(loc.getWorld().getName());

        InnectisLot lot = LotHandler.getLot(loc, true);

        // Allow blocks to burn in the reszone if not on a lot
        boolean allowDestruction = (lot == null ? worldType == IdpWorldType.RESWORLD : lot.isFlagSet(LotFlagType.DESTRUCTION));

        /* Do not let blocks get destroyed via burning if flag is not set
         * They still have fire though, and the fire never goes away */
        if (!allowDestruction) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();
        Location loc = block.getLocation();
        IdpWorldType worldType = IdpWorldType.getIdpWorldType(loc.getWorld().getName());

        //IdpPlayer player = new IdpPlayer(plugin, event.getPlayer());
        IgniteCause cause = event.getCause();

        InnectisLot lot = LotHandler.getLot(loc, true);

        // Allow blocks to ignite in the reszone if not on a lot
        boolean allowDestruction = (lot == null ? worldType == IdpWorldType.RESWORLD : lot.isFlagSet(LotFlagType.DESTRUCTION));

        if (!allowDestruction && (cause == IgniteCause.LAVA
                || cause == IgniteCause.SPREAD || cause == IgniteCause.LIGHTNING)) {
            event.setCancelled(true);
            return;
        }
    }

    /**
     * This event is called when using flint and steel! block = 51
     *
     * @param event
     */
    @NotchcodeUsage()
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // --------------------------------------------------------------------
        // Player state cehcks
        // --------------------------------------------------------------------
        //MUST BE FIRST!
        //Don't allow player to break blocks if they are frozen
        if (player.getSession().isFrozen()) {
            player.printError("You are frozen and cannot break blocks!");
            event.setCancelled(true);
            player.getInventory().updateBukkitInventory();
            return;
        }

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            player.getInventory().updateBukkitInventory();
            return;
        }

        Block block = event.getBlock();
        IdpMaterial mat = IdpMaterial.fromBlock(block);
        EquipmentSlot handSlot = event.getHand();
        IdpItemStack handItem = player.getItemInHand(handSlot);
        IdpMaterial handMaterial = handItem.getMaterial();

        // --------------------------------------------------------------------
        // Check for secondairy listeners
        // --------------------------------------------------------------------
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_BLOCK_PLACE)) {
            InnPlayerBlockPlaceEvent idpevent = new InnPlayerBlockPlaceEvent(player, block, event.getBlockAgainst(), event.canBuild());
            plugin.getListenerManager().fireEvent(idpevent);
            if (idpevent.isCancelled()) {
                event.setCancelled(true);
                player.getInventory().updateBukkitInventory();
            }
            if (idpevent.shouldTerminate()) {
                return;
            } else {
                event.setBuild(idpevent.canBuild());
            }
        }

        // --------------------------------------------------------------------
        // Check worlds
        // --------------------------------------------------------------------
        IdpWorld world = IdpWorldFactory.getWorld(block.getWorld().getName());
        Location loc = block.getLocation();

        // Dynamic world check first
        if (world.getWorldType() == IdpWorldType.DYNAMIC) {
            IdpDynamicWorldSettings settings = (IdpDynamicWorldSettings) world.getSettings();

            // Check if active TNT
            if (handMaterial == IdpMaterial.TNT && settings.isTntAllowed()) {
                event.setCancelled(true);

                if (handItem.getAmount() == 1) {
                    handItem = null;
                } else {
                    handItem.setAmount(handItem.getAmount() - 1);
                }

                player.setItemInHand(handSlot, handItem);

                BlockHandler.spawnTNT(loc, 3.0, 100);
            }

            // Check if buildable
            if (!settings.isBuildable()) {
                event.setCancelled(true);
                player.getInventory().updateBukkitInventory();
                return;
            }
        }

        // --------------------------------------------------------------------
        // Check block specific
        // --------------------------------------------------------------------
        boolean placeOverride = (mat == IdpMaterial.FIRE && player.getSession().isUsingFlintSteel());
        if (placeOverride) {
            player.getSession().setUsingFintSteel(false);
        }

        // Build restriction
        // This event is also fired when flint and steel creates
        // a portal, and the block passed is PORTAL (for some reason, the block in hand is FIRE...)
        String errMsg = BlockHandler.canPlaceBlock(player, loc, handMaterial, false);

        if (errMsg != null && !placeOverride) {
            event.setCancelled(true);
            player.getInventory().updateBukkitInventory();
            player.printError(errMsg);

            Block testBlock = player.getLocation().getBlock();
            IdpMaterial testMaterial = IdpMaterial.fromBlock(testBlock);
            Block standingBlock = null;

            // If player is standing on a half slab (but not a top slab) with
            // water below, IDP needs to know this is the standing block, as it
            // will incorrectly think the player is block jumping when in fact
            // they are not
            switch (testMaterial) {
                case STONE_SLAB:
                case SANDSTONE_SLAB:
                case WOOD_SLAB:
                case COBBLE_SLAB:
                case BRICK_SLAB:
                case STONE_BRICK_SLAB:
                case NETHER_BRICK_SLAB:
                case QUARTZ_SLAB:
                case OAK_WOOD_SLAB:
                case SPRUCE_WOOD_SLAB:
                case BIRCH_WOOD_SLAB:
                case JUNGLE_WOOD_SLAB:
                case ACACIA_WOOD_SLAB:
                case DARK_OAK_WOOD_SLAB:
                case RED_SANDSTONE_SLAB:
                    standingBlock = testBlock;
                    break;
                default:
                    standingBlock = testBlock.getRelative(BlockFace.DOWN);
                    break;
            }

            // Check if the player is jumping. This would be true if the player
            // is falling as well, but that would also mamke it possible to jump over
            // a tall block or 2-high wall in some situations
            boolean isJumping = (standingBlock.equals(block) || IdpMaterial.fromBlock(standingBlock).isNonSolid(false));

            if (isJumping) {
                int scanRange = 1;

                // Scan the blocks around the block placed for any surrounding blocks
                // and if any blocks above the surrounding blocks
                for (int x = -scanRange; x <= scanRange; x++) {
                    for (int z = -scanRange; z <= scanRange; z++) {
                        Block newBlock = standingBlock.getRelative(x, 0, z);
                        IdpMaterial newMaterial = IdpMaterial.fromBlock(newBlock);

                        // Only check solid blocks
                        if (newMaterial.isSolid()) {
                            // First check if this is a tall block
                            boolean blockJump = newMaterial.isTall();

                            // If not a tall block, check for a block on top
                            if (!blockJump) {
                                IdpMaterial materialAbove = IdpMaterial.fromBlock(newBlock.getRelative(BlockFace.UP));

                                // A solid block is on top
                                if (materialAbove.isSolid()) {
                                    blockJump = true;
                                }
                            }

                            if (blockJump) {
                                //InnPlugin.getPlugin().broadCastStaffMessage(ChatColor.DARK_GREEN + "[IDP] " + player.getName() + " may be attempting to block jump.", true);
                                InnPlugin.logInfo(player.getName() + " may be attempting to block jump.");
                                Location teleportLocation = LocationUtil.transferOffsetLocation(player.getLocation(), standingBlock.getLocation());
                                player.teleport(teleportLocation, TeleportType.RAW_COORDINATES);
                                return;
                            }
                        }
                    }
                }
            }

            return;
        }

        // Don't allow placing a slab onto another if the first slab is virtual
        if (mat.isSingleSlab()) {
            IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

            if (blockData.isVirtualBlock() && !player.hasPermission(Permission.world_build_unrestricted)) {
                event.setCancelled(true);
                return;
            }
        }

        InnectisLot lot = LotHandler.getLot(player.getLocation());

        // Check for appropriate block placement in the pixel build area
        if (lot != null && lot.isFlagSet(LotFlagType.PIXELBUILD)) {
            if (!isPixelBuildBlock(IdpMaterial.fromBlock(block))
                    && !player.hasPermission(Permission.world_build_unrestricted)) {
                player.printError("This block is not a pixel build block!");
                event.setCancelled(true);
                return;
            }
        }

        // Turn off the player's light if they place the only torch in hand
        // and ignore creative mode
        if (player.getSession().hasLightsEnabled() && mat == IdpMaterial.TORCH
                && player.getHandle().getGameMode() != GameMode.CREATIVE) {
            if (handItem.getAmount() == 1) {
                IdpItemStack headStack = player.getHelmet();

                // Make sure the player doesn't have a glowstone helmet on
                if (headStack == null || headStack.getMaterial() != IdpMaterial.GLOWSTONE) {
                    PlayerEffect.NIGHT_VISION.removeSpecial(player);
                }
            }
        }

        // Placing a sign with copied text?
        if (mat == IdpMaterial.WALL_SIGN || mat == IdpMaterial.SIGN_POST) {
            String[] tempSignText = player.getSession().getSignLines();
            boolean isUsingCopiedSignText = player.getSession().hasSignLines();

            if (tempSignText != null && isUsingCopiedSignText && block.getState() instanceof Sign) {
                Sign newSign = (Sign) block.getState();
                if (newSign != null) {
                    for (int count = 0; count < 4; count++) {
                        if (tempSignText[count] != null) {
                            newSign.setLine(count, tempSignText[count].replace('\u00A7', '&'));
                        }
                    }
                    newSign.update();

                    player.getSession().clearSignLines();
                    player.printInfo("Pasted sign text! Left-click air with a sign to paste again.");
                }
            }
        }

        // Sponges
        if (mat == IdpMaterial.SPONGE || mat == IdpMaterial.WET_SPONGE) {
            int ox = loc.getBlockX();
            int oy = loc.getBlockY();
            int oz = loc.getBlockZ();

            int spongeradius = Configuration.SPONGE_RADIUS;

            for (int cx = -spongeradius; cx <= Configuration.SPONGE_RADIUS; cx++) {
                for (int cy = -spongeradius; cy <= Configuration.SPONGE_RADIUS; cy++) {
                    for (int cz = -spongeradius; cz <= Configuration.SPONGE_RADIUS; cz++) {
                        Block aroundsponge = block.getWorld().getBlockAt(ox + cx, oy + cy, oz + cz);
                        IdpMaterial checkMat = IdpMaterial.fromBlock(aroundsponge);

                        if (checkMat == IdpMaterial.WATER || checkMat == IdpMaterial.STATIONARY_WATER) {
                            BlockHandler.setBlock(aroundsponge, IdpMaterial.AIR);
                        }
                    }
                }
            }
        }

        // Hopper & Droppers
        if (mat == IdpMaterial.HOPPER || mat == IdpMaterial.DROPPER) {
            for (Block chestBlock : BlockHandler.getBlocksAttached(block, new IdpMaterial[]{IdpMaterial.CHEST, IdpMaterial.TRAPPED_CHEST}, true)) {
                InnectisChest chestObject = ChestHandler.getChest(chestBlock.getLocation());
                if (chestObject != null && !chestObject.getOwner().equalsIgnoreCase(player.getName())
                        && !player.hasPermission(Permission.world_build_unrestricted)) {
                    event.setCancelled(true);
                    player.getInventory().updateBukkitInventory();
                    player.printError("You cannot connect a " + mat.getName() + " to a chest you do not own!");
                    return;
                }
            }
        }

        // Chest
        if (mat == IdpMaterial.CHEST || mat == IdpMaterial.TRAPPED_CHEST) {
            try {
                int chest = 0;

                for (BlockFace face : BlockHandler.getAllSideFaces()) {
                    IdpMaterial relativeMaterial = IdpMaterial.fromBlock(block.getRelative(face));

                    if (relativeMaterial == mat) {
                        chest++;
                    }
                }

                if (chest > 1) {
                    event.setCancelled(true);
                    player.getInventory().updateBukkitInventory();
                    return;
                }

                PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName());

                if (ChestHandler.createChest(credentials, block) != null) {
                    PlayerCredentialsManager.addCredentialsToCache(credentials);
                    player.printInfo("You lock the chest. Only you can access this chest.");
                } else {
                    event.setCancelled(true);
                    player.getInventory().updateBukkitInventory();
                    player.printError("Unable to create chest!");
                }
            } catch (SQLException ex) {
                event.setCancelled(true);
                player.getInventory().updateBukkitInventory();
                player.printError("An internal error has occured!");
                InnPlugin.logError(player.getColoredName() + ChatColor.RED + " tried to place a chest, but the sql failed", ex);
            }
        }

        // Iron Door
        if (mat == IdpMaterial.IRON_DOOR_BLOCK) {
            try {
                PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName());

                if (DoorHandler.createDoor(credentials, block) == null) {
                    PlayerCredentialsManager.addCredentialsToCache(credentials);
                    event.setCancelled(true);
                    player.getInventory().updateBukkitInventory();
                    return;
                }
            } catch (SQLException ex) {
                event.setCancelled(true);
                player.getInventory().updateBukkitInventory();
                player.printError("An internal error has occured!");
                InnPlugin.logError(player.getColoredName() + ChatColor.RED + " tried to place an iron door, but the sql failed", ex);
            }
        }

        // Trap Door
        if (mat == IdpMaterial.IRON_TRAP_DOOR) {
            try {
                PlayerCredentials credentials = PlayerCredentialsManager.getByName(player.getName());
                TrapdoorHandler.createTrapdoor(world.getHandle(), loc, credentials);
                PlayerCredentialsManager.addCredentialsToCache(credentials);
            } catch (SQLException ex) {
                event.setCancelled(true);
                player.getInventory().updateBukkitInventory();
                player.printError("An internal error has occured!");
                InnPlugin.logError(player.getColoredName() + " tried to place a trap door, but there was an error.", ex);
            }
        }

        // Only if in survival mode.
        if (player.getHandle().getGameMode() != GameMode.CREATIVE) {
            try {
                // Check if the item is a block and if its the last block
                if (handMaterial.isBlock() && handItem.getAmount() == 1) {

                    // First remove the item that will be placed.
                    handItem.setAmount(0);
                    player.setItemInHand(handSlot, handItem);

                    // Find the first stack that is in the inventory and move it to the iteminhand slot
                    IdpItemStack fstack = player.getFirstItemstack(handMaterial);
                    player.setItemInHand(handSlot, fstack);
                }
            } catch (Exception ex) {
                // Just to be sure, but this can be ignored.
            }
        }

        BlockEconomy blockEconomy = BlockEconomy.fromMaterial(mat);

        if (blockEconomy != null) {
            // Do not allow placement of blocks that give valutas when broken
            if (blockEconomy.isValidOnWorld(player.getWorld().getActingWorldType())
                    && !player.hasPermission(Permission.world_build_unrestricted)) {
                player.printError("You cannot place a " + mat + " on this world!");
                event.setCancelled(true);
                player.getInventory().updateBukkitInventory();
                return;
            }
        }

        // Clear the block data if it exists
        IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

        if (blockData.hasData()) {
            blockData.clear();
        }

        //update last edit based on who placed the block (owner, or member?)
        if (lot != null) {
            if (lot.getOwner().equalsIgnoreCase(player.getName())) {
                lot.setLastOwnerEdit(System.currentTimeMillis());
            } else if (lot.canPlayerAccess(player.getName())) {
                lot.setLastMemberEdit(System.currentTimeMillis());
            }
        }

        // Log the action
        BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
        blockLogger.logBlockAction(player.getUniqueId(), mat, loc, BlockAction.BLOCK_PLACE);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockDamage(BlockDamageEvent event) {
        IdpItemStack handStack = IdpItemStack.fromBukkitItemStack(event.getItemInHand());

        // Watch damage (for staff)
        if (handStack.getMaterial() == IdpMaterial.WATCH) {
            return;
        }
    }

    /**
     * Check is block can be insta mined in pixel world
     *
     * @param id
     * @return true if it is allowed to be mined
     */
    private boolean isPixelBuildBlock(IdpMaterial mat) {
        switch (mat) {
            case STONE:
            case GRASS:
            case PODZOL:
            case DIRT:
            case COBBLESTONE:
            case OAK_PLANK:
            case SPRUCE_PLANK:
            case BIRCH_PLANK:
            case JUNGLE_PLANK:
            case ACACIA_PLANK:
            case DARK_OAK_PLANK:
            case SAND:
            case RED_SAND:
            case GRAVEL:
            case GLASS:
            case LAPIS_LAZULI:
            case SANDSTONE:
            case PRETTY_SANDSTONE:
            case SMOOTH_SANDSTONE:
            case WOOL_WHITE:
            case WOOL_ORANGE:
            case WOOL_MAGENTA:
            case WOOL_LIGHTBLUE:
            case WOOL_YELLOW:
            case WOOL_LIGHTGREEN:
            case WOOL_PINK:
            case WOOL_GRAY:
            case WOOL_LIGHTGRAY:
            case WOOL_CYAN:
            case WOOL_PURPLE:
            case WOOL_BLUE:
            case WOOL_BROWN:
            case WOOL_DARKGREEN:
            case WOOL_RED:
            case WOOL_BLACK:
            case GOLD_BLOCK:
            case EMERALD_BLOCK:
            case IRON_BLOCK:
            case DIAMOND_BLOCK:
            case SIGN_POST:
            case WALL_SIGN:
            case GLASS_STAINED_WHITE:
            case GLASS_STAINED_ORANGE:
            case GLASS_STAINED_MAGENTA:
            case GLASS_STAINED_LIGHT_BLUE:
            case GLASS_STAINED_YELLOW:
            case GLASS_STAINED_LIME:
            case GLASS_STAINED_PINK:
            case GLASS_STAINED_GRAY:
            case GLASS_STAINED_LIGHT_GRAY:
            case GLASS_STAINED_CYAN:
            case GLASS_STAINED_PURPLE:
            case GLASS_STAINED_BLUE:
            case GLASS_STAINED_BROWN:
            case GLASS_STAINED_GREEN:
            case GLASS_STAINED_RED:
            case GLASS_STAINED_BLACK:
            case CLAY_WHITE:
            case CLAY_ORANGE:
            case CLAY_MAGENTA:
            case CLAY_LIGHTBLUE:
            case CLAY_YELLOW:
            case CLAY_LIGHTGREEN:
            case CLAY_PINK:
            case CLAY_GRAY:
            case CLAY_LIGHTGRAY:
            case CLAY_CYAN:
            case CLAY_PURPLE:
            case CLAY_BLUE:
            case CLAY_BROWN:
            case CLAY_DARKGREEN:
            case CLAY_RED:
            case CLAY_BLACK:
                return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Block block = event.getBlock();
        IdpPlayer player = plugin.getPlayer(event.getPlayer());

        // --------------------------------------------------------------------
        // Check player status
        // --------------------------------------------------------------------
        //MUST BE FIRST!
        //Don't allow player to break blocks if they are frozen
        if (player.getSession().isFrozen()) {
            player.printError("You are frozen and cannot break blocks!");
            event.setCancelled(true);
            return;
        }

        // If not logged in
        if (!player.getSession().isLoggedIn()) {
            player.printError("You are not logged in!");
            event.setCancelled(true);
            return;
        }

        Location loc = block.getLocation();

        // --------------------------------------------------------------------
        // Check for secondairy listeners
        // --------------------------------------------------------------------
        if (plugin.getListenerManager().hasListeners(InnEventType.PLAYER_BLOCK_BREAK)) {
            InnPlayerBlockBreakEvent idpevent = new InnPlayerBlockBreakEvent(player, block);
            plugin.getListenerManager().fireEvent(idpevent);
            if (idpevent.isCancelled()) {
                event.setCancelled(true);
            }
            if (idpevent.shouldTerminate()) {
                return;
            }
        }

        // --------------------------------------------------------------------
        // Check for dynamic worlds.
        // --------------------------------------------------------------------
        IdpWorld world = IdpWorldFactory.getWorld(block.getWorld().getName());

        // Dynamic world check first
        if (world.getWorldType() == IdpWorldType.DYNAMIC) {
            IdpDynamicWorldSettings settings = (IdpDynamicWorldSettings) world.getSettings();

            // Check if buildable
            if (!settings.isBuildable()) {
                event.setCancelled(true);
                return;
            }
        }

        IdpMaterial mat = IdpMaterial.fromBlock(block);
        InnectisLot lot = LotHandler.getLot(loc, true);

        // --------------------------------------------------------------------
        // World is OK, and player status is OK, check rights
        // --------------------------------------------------------------------
        // Block building (NEEDS TO BE FIRST)
        String errMsg = BlockHandler.canBreakBlock(player, loc, false);
        if (errMsg != null) {
            event.setCancelled(true);
            player.sendBlockChange(block);
            player.printError(errMsg);

            // If it is ok to check for block clipping
            boolean checkBlockClip = !(player.isFlying() || player.getHandle().getGameMode() == GameMode.CREATIVE);

            if (checkBlockClip) {
                // This will flag if the player is falling too, but since we're only
                // checing once they break a block, they are more likely to have broken
                // it if they were jumping
                Location lastLocation = player.getSession().getLastLocation();
                Block blockUnder = lastLocation.getBlock().getRelative(BlockFace.DOWN);
                boolean isJumping = IdpMaterial.fromBlock(blockUnder).isNonSolid(false);

                if (isJumping) {
                    // Check all sides of the block broken for the player
                    BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST,
                        BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST,
                        BlockFace.WEST, BlockFace.NORTH_WEST};

                    for (BlockFace face : faces) {
                        Block newBlock = block.getRelative(face);

                        // This player's feet level comes in contact with this block
                        Location location = newBlock.getLocation();

                        if (location.equals(player.getLocation())) {
                            InnPlugin.getPlugin().broadCastStaffMessage(ChatColor.DARK_GREEN + "[IDP] " + player.getName() + " may be attempting to block clip.", true);
                            InnPlugin.logInfo(player.getName() + " may be attempting to block clip.");
                            player.teleport(blockUnder.getLocation(), TeleportType.PVP_IMMUNITY);
                            return;
                        }
                    }
                }
            }

            return;
        }

        IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

        // Virtual block breaks (NEW)
        // They are allowed to be destoryed but they wont drop anything
        if (blockData.isVirtualBlock()) {
            BlockHandler.setBlock(block, IdpMaterial.AIR);
            blockData.clear();

            event.setCancelled(true);
            return;
        }

        // Do not drop the block if broken in a spleef lot
        if (lot != null && lot.isFlagSet(LotFlagType.SPLEEF)) {
            BlockHandler.setBlock(block, IdpMaterial.AIR);

            event.setCancelled(true);
            return;
        }

        // Lever destruction
        if (mat == IdpMaterial.LEVER) {
            InnectisSwitch sw = InnectisSwitch.getSwitch(loc);
            if (sw != null) {
                sw.delete();
            }
        }

        // Gate destruction
        if (GateHandler.isValidGateMaterial(mat)) {
            Block block2 = block.getRelative(BlockFace.DOWN);
            IdpMaterial mat2 = IdpMaterial.fromBlock(block2);
            IdpBlockData blockData2 = BlockHandler.getIdpBlockData(block2.getLocation());

            while (mat2 == mat && blockData2.isVirtualBlock()) {
                BlockHandler.setBlock(block2, IdpMaterial.AIR);
                blockData2.clear();

                block2 = block2.getRelative(BlockFace.DOWN);
                blockData2 = BlockHandler.getIdpBlockData(block2.getLocation());
                mat2 = IdpMaterial.fromBlock(block2);
            }
        }

        // Sponge removal
        if (mat == IdpMaterial.SPONGE || mat == IdpMaterial.WET_SPONGE) {
            int distance = Configuration.SPONGE_RADIUS + 1;

            // update surrounding water
            for (int y = -distance; y <= distance; y++) {
                for (int x = -distance; x <= distance; x++) {
                    for (int z = -distance; z <= distance; z++) {

                        // Only on outer walls
                        if (Math.abs(x) == distance
                                || Math.abs(y) == distance
                                || Math.abs(z) == distance) {

                            Block relative = block.getRelative(x, y, z);
                            IdpMaterial mat2 = IdpMaterial.fromBlock(relative);

                            if (mat2.isWater()) {
                                BlockHandler.setBlock(relative, IdpMaterial.WATER);
                            }
                        }
                    }
                }
            }
        }

        // Bridge destruction
        if (mat == BridgeHandler.CONTROLBLOCK_MATERIAL && BlockHandler.getIdpBlockData(loc).isBridgeController()) {
            // Only open itself, dont power surrounding
            BridgeHandler.openBridge(block, 1);
        }

        if (!removeOwnedObject(player, lot, block, false)) {
            event.setCancelled(true);
            return;
        }

        // Chop down the whole tree, if needed.
        BlockHandler.calcTreeFell(player, block);

        // Stop water from ice
        switch (world.getActingWorldType()) {
            case NETHER: // Why bother checking? Nether will handle this itself.
                break;
            default:
                if (lot != null && lot.isFlagSet(LotFlagType.NOMELT)
                        && mat == IdpMaterial.ICE) {
                    IdpItemStack handStack = player.getItemInHand(EquipmentSlot.HAND);
                    Map<EnchantmentType, Integer> enchants = handStack.getItemdata().getEnchantments();
                    boolean isSilkTouch = false;

                    // Make sure to special case silk touch, as this will prevent the ice from turning to water
                    for (EnchantmentType type : enchants.keySet()) {
                        if (type == EnchantmentType.SILK_TOUCH) {
                            isSilkTouch = true;
                            break;
                        }
                    }

                    // if not broken by a silk touch enchanted tool, set to anything but
                    // air first, so this does not turn to water
                    if (!isSilkTouch) {
                        BlockHandler.setBlock(block, IdpMaterial.DIRT);
                        BlockHandler.setBlock(block, IdpMaterial.AIR);

                        event.setCancelled(true);
                    }
                }
        }

        // Only check block quota and x-ray detection in all main inventory worlds
        if (world.getSettings().getInventoryType() == InventoryType.MAIN) {
            boolean checkExceedingQuota = true;

            // Log diamond and lapis ore
            if (mat == IdpMaterial.DIAMOND_ORE || mat == IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
                if (mat == IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
                    InnectisWaypoint waypoint = WaypointHandler.getWaypoint(block.getLocation());

                    // Do not check or add to quota if the player broke a waypoint
                    if (waypoint != null) {
                        checkExceedingQuota = false;
                    }
                }

                // Only update quota if player did not break a waypoint
                if (checkExceedingQuota) {
                    PreparedStatement statement = null;

                    try {
                        statement = DBManager.prepareStatement("INSERT DELAYED INTO block_breaks (time, player_id, blockid, world, x, y, z) VALUES (NOW(), ?, ?, ?, ?, ?, ?);");
                        statement.setString(1, player.getUniqueId().toString());
                        statement.setInt(2, BlockHandler.getBlockTypeId(block));
                        statement.setString(3, loc.getWorld().getName());
                        statement.setInt(4, loc.getBlockX());
                        statement.setInt(5, loc.getBlockY());
                        statement.setInt(6, loc.getBlockZ());
                        statement.executeUpdate();
                    } catch (SQLException ex) {
                        InnPlugin.logError("Cannot insert diamond and lapis ore breaks for player '" + player.getName() + "'.", ex);
                    } finally {
                        DBManager.closePreparedStatement(statement);
                    }
                }
            }

            // --------------------------------------------------------------------
            // Normally handled, Log stuff
            // --------------------------------------------------------------------
            // Check if x-ray
            if (player.getSession().checkBlockUse(block, true)) {
                InnPlugin.getPlugin().broadCastStaffMessage(ChatColor.DARK_GREEN + "[IDP] " + player.getName() + " may be using x-ray! (" + mat.getName() + ")", false);
                InnPlugin.logInfo(player.getName() + " may be using x-ray! (" + mat.getName() + ")");
            }

            // Only check quota if allowed
            if (checkExceedingQuota) {
                // If passing quota boundary, notify Staff.
                boolean exceededQuota = BlockHandler.isExceedingQuota(player, block);
                BlockHandler.appendQuota(player, block);

                if (!exceededQuota && BlockHandler.isExceedingQuota(player, block)) {
                    InnPlugin.getPlugin().broadCastStaffMessage(ChatColor.DARK_GREEN + "[IDP] " + player.getName() + " is now exceeding their quota for " + mat.getName() + ".", false);
                    InnPlugin.logInfo(player.getName() + " is now exceeding their quota for " + mat.getName() + ".");
                }
            }
        }

        //update last edit based on who broke the block (owner, or member?)
        if (lot != null) {
            if (lot.getOwner().equalsIgnoreCase(player.getName())) {
                lot.setLastOwnerEdit(System.currentTimeMillis());
            } else if (lot.canPlayerAccess(player.getName())) {
                lot.setLastMemberEdit(System.currentTimeMillis());
            }
        }

        PlayerSession session = player.getSession();
        BlockEconomy blockEconomy = BlockEconomy.fromMaterial(mat);

        if (blockEconomy != null) {
            if (blockEconomy.isValidOnWorld(player.getWorld().getActingWorldType())) {
                int countToValue = blockEconomy.getCountToValue();
                int valutas = blockEconomy.getValue();
                boolean giveValue = false;

                // If this block takes more than a single one to get valutas, then keep
                // track of how many blocks were mined
                if (countToValue > 1) {
                    session.addMaterialMined(mat);
                    int count = session.getMaterialMinedCount(mat);

                    if (count == countToValue) {
                        session.clearMaterialMined(mat);
                        giveValue = true;
                    }
                } else {
                    giveValue = true;
                }

                if (giveValue) {
                    TransactionObject transaction = TransactionHandler.getTransactionObject(player);
                    transaction.addValue(valutas, TransactionType.VALUTAS);

                    if (player.getSession().hasSetting(PlayerSettings.VALUTA_MESSAGE)) {
                        player.printInfo("You have gained " + valutas + " valuta" + (valutas != 1 ? "s" : "") + " from this " + mat.getName() + "!");
                    }
                }
            }
        }

        // Log the action
        BlockLogger blockLogger = (BlockLogger) LogType.getLoggerFromType(LogType.BLOCK);
        blockLogger.logBlockAction(player.getUniqueId(), mat, loc, BlockAction.BLOCK_BREAK);
    }

    /**
     * Attempts to remove any owned objects of the player
     *
     * @param player
     * @param lot
     * @param block
     * @return false if an error occurred, cancel further events if so
     */
    private boolean removeOwnedObject(IdpPlayer player, InnectisLot lot, Block block, boolean custom) {
        IdpMaterial mat = IdpMaterial.fromBlock(block);
        Location loc = block.getLocation();

        // Waypoints
        if (mat == IdpMaterial.LAPIS_LAZULI_OREBLOCK) {
            try {
                InnectisWaypoint waypoint = WaypointHandler.removeWaypoint(player, block);

                if (waypoint != null) {
                    BlockHandler.setBlock(block, IdpMaterial.AIR);

                    IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);

                    if (blockData.hasData()) {
                        blockData.clear();
                    }

                    // Do not return resources on a custom inventory lot
                    if (!custom) {
                        switch (waypoint.getCostType()) {
                            case VALUTA_COST:
                                ExperienceOrb orb = (ExperienceOrb) loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
                                orb.setExperience(1);
                                DroppedValutaOrb vorb = new DroppedValutaOrb(ChatColor.AQUA + "Waypoint", 20);
                                orb.setMetadata(EntityConstants.METAKEY_DROPPED_VALUTAS, new FixedMetadataValue(plugin, vorb));
                                break;
                            case LAPIS_COST:
                                loc.getWorld().dropItem(loc, new IdpItemStack(IdpMaterial.LAPIS_LAZULI_BLOCK, 2).toBukkitItemstack());
                                break;
                            default:
                                // Waypoint did not require a cost, do nothing
                        }
                    }

                }

                return true;
            } catch (SQLException ex) {
                player.printError("An internal error has occured!");
                player.sendBlockChange(block);
                InnPlugin.logError(ChatColor.RED + player.getColoredName() + ChatColor.RED + " tried to remove a waypoint, but the sql failed", ex);
                return false;
            }
        }

        // Chest protection
        if (mat == IdpMaterial.CHEST || mat == IdpMaterial.TRAPPED_CHEST) {
            InnectisChest chest = ChestHandler.getChest(loc);

            if (chest != null) {
                if (!(chest.getOwner().equalsIgnoreCase(player.getName())
                        || (lot != null && lot.getOwner().equalsIgnoreCase(player.getName())))
                        && !player.hasPermission(Permission.owned_object_override)) {
                    player.printError("You don't own this chest. Unable to break!");
                    return false;
                }
            }

            try {
                if (!ChestHandler.removeChest(player, block)) {
                    player.printError("The chest could not be broken!");
                    return false;
                }
            } catch (SQLException ex) {
                player.printError("An internal error has occured!");
                player.sendBlockChange(block);
                InnPlugin.logError(player.getColoredName() + ChatColor.RED + " tried to remove a chest, but the sql failed", ex);
                return false;
            }
        }

        // Iron door
        if (mat == IdpMaterial.IRON_DOOR_BLOCK) {
            try {
                if (!DoorHandler.removeDoor(player, block)) {
                    return false;
                }
            } catch (SQLException ex) {
                player.printError("An internal error has occured!");
                player.sendBlockChange(block);
                InnPlugin.logError(ChatColor.RED + player.getColoredName() + ChatColor.RED + " tried to remove an iron door, but the sql failed", ex);
                return false;
            }
        }

        // Iron door if block below is removed
        if (IdpMaterial.fromBlock(block.getRelative(BlockFace.UP)) == IdpMaterial.IRON_DOOR_BLOCK && mat != IdpMaterial.IRON_DOOR_BLOCK) {
            try {
                if (!DoorHandler.removeDoor(player, block.getRelative(BlockFace.UP))) {
                    return false;
                }
            } catch (SQLException ex) {
                player.printError("An internal error has occured!");
                player.sendBlockChange(block.getRelative(BlockFace.UP).getLocation(), block);
                InnPlugin.logError(ChatColor.RED + player.getColoredName() + ChatColor.RED + " tried to remove an iron door, but the sql failed", ex);
                return false;
            }
        }

        // Bookcases
        if (mat == IdpMaterial.BOOKCASE) {
            InnectisBookcase bcase = InnectisBookcase.getBookcase(loc);
            if (bcase != null) {
                if (!(bcase.getOwner().equalsIgnoreCase(player.getName())
                        || (lot != null && lot.getOwner().equalsIgnoreCase(player.getName())))
                        && !player.hasPermission(Permission.owned_object_override)) {
                    player.printError("You don't own this bookcase. Unable to break!");
                    return false;
                } else {
                    // Drop all contents
                    IdpItemStack[] items = bcase.getItems();
                    World world = block.getWorld();

                    for (int i = 0; i < items.length; i++) {
                        if (items[i] != null && items[i].getMaterial() != IdpMaterial.AIR) {
                            world.dropItem(loc, items[i].toBukkitItemstack());
                        }
                        items[i] = null;
                    }

                    // Delete the bookcase
                    bcase.delete();
                }
            }
        }

        // Trap Door removal
        if (mat == IdpMaterial.IRON_TRAP_DOOR) {
            InnectisTrapdoor trapdoor = TrapdoorHandler.getTrapdoor(loc);

            // This trap door is owned, check for break rights
            if (trapdoor != null) {
                InnectisLot traplot = LotHandler.getLot(trapdoor.getLocation());

                if (!(trapdoor.getOwner().equalsIgnoreCase(player.getName())
                        || (traplot != null && traplot.getOwner().equalsIgnoreCase(player.getName()))
                        || player.hasPermission(Permission.owned_object_override))) {
                    player.printError("You do not own this trap door. Unable to break!");
                    return false;
                }

                try {
                    TrapdoorHandler.removeTrapdoor(loc);
                } catch (SQLException ex) {
                    player.printError("Error removing trap door!");
                    return false;
                }
            }
        }

        // Check for any trapdoors around this block to see if the player
        // has rights on any of the trapdoors to allow the block to break
        BlockFace[] faces = BlockHandler.getAllSideFaces();

        for (BlockFace face : faces) {
            Block bblock = block.getRelative(face);
            MaterialData md = bblock.getState().getData();

            // Check to see if a trapdoor is next to this block
            if (md instanceof TrapDoor) {
                TrapDoor trapdoor = (TrapDoor) md;
                Block attachedBlock = bblock.getRelative(trapdoor.getAttachedFace());

                // Check if this trapdoor is attached to the block broken
                if (attachedBlock.equals(block)) {
                    InnectisTrapdoor innTrapdoor = TrapdoorHandler.getTrapdoor(bblock.getLocation());

                    // If a player does not own any of the attached trapdoors
                    // or does not own any lot the trapdoor is on, cancel the break
                    if (innTrapdoor != null) {
                        if (!innTrapdoor.getOwner().equalsIgnoreCase(player.getName())
                                || (lot != null && !lot.getOwner().equalsIgnoreCase(player.getName()))
                                || !player.hasPermission(Permission.owned_object_override)) {
                            player.printError("You can't break this block.");
                            return false;
                        } else {
                            try {
                                // Remove reference to this trapdoor since the
                                // foundation will break anyway
                                TrapdoorHandler.removeTrapdoor(innTrapdoor.getId());
                            } catch (SQLException ex) {
                                plugin.logError("Error while breaking trapdoor!", ex);
                                player.printError("Internal server error. Notify an admin!");
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSignChange(SignChangeEvent event) {
        IdpPlayer player = plugin.getPlayer(event.getPlayer());
        String[] newLines = event.getLines();
        Block block = event.getBlock();

        WallSignType signType = WallSignType.fromSignText(newLines);

        switch (signType) {
            case STASH_SIGN:
                IdpMaterial mat = IdpMaterial.fromString(newLines[1]);

                // Keep the ID:data text on the sign if it's a potion
                if (mat != null && mat != IdpMaterial.POTIONS) {
                    newLines[1] = mat.getName();
                }

                break;
            case CHEST_SHOP:
                Block blockBelow = block.getRelative(BlockFace.DOWN);

                if (blockBelow.getState() instanceof Chest
                        || blockBelow.getState() instanceof DoubleChest) {
                    InnectisChest chest = ChestHandler.getChest(blockBelow.getLocation());

                    if (chest != null) {
                        if (!chest.canPlayerManage(player.getName())
                                && !player.hasPermission(Permission.owned_object_override)) {
                            player.printError("You do not own or operate the chest under this sign!");
                            event.setCancelled(true);
                            return;
                        }

                        ChestShopSign chestSign = SignValidator.getChestShopSign(newLines);

                        if (chestSign != null) {
                            IdpContainer container = new IdpContainer(chest.getInventory());
                            int count = container.countMaterial(chestSign.getMaterial());
                            newLines[2] = chestSign.getAmount() + " @ " + chestSign.getCost() + " vT";
                            newLines[3] = "Count: " + count;
                        } else {
                            player.printError("This chest sign is not formatted correctly.");
                        }
                    } else {
                        player.printError("The sign below this chest is not valid!");
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    player.printError("No chest below this chest shop sign.");
                }

                break;
        }

        // Parse colours
        for (int i = 0; i < 4; i++) {
            newLines[i] = ChatColor.convertLongColors(newLines[i]);
            event.setLine(i, ChatColor.parseSignColor(newLines[i], player));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        /* Called when a block is undergoing a universe physics check
         * on whether it can be built For example, cacti cannot be built
         * on grass unless overridden here
         */
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        IdpBlockData blockData = BlockHandler.getIdpBlockData(loc);
        IdpMaterial mat = IdpMaterial.fromBlock(block);

        // Dont let virtual blocks fade with the exception to fire
        if (blockData.isVirtualBlock() && mat != IdpMaterial.FIRE) {
            event.setCancelled(true);
        }

        // Dont let virtual blocks fade with an exception to fire
        if (blockData.isUnbreakable()) {
            event.setCancelled(true);
        }

        if (mat == IdpMaterial.ICE || mat == IdpMaterial.SNOW_LAYER) {
            InnectisLot lot = LotHandler.getLot(loc, true);

            if (lot != null && lot.isFlagSet(LotFlagType.NOMELT)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockForm(BlockFormEvent event) {
        Location loc = event.getBlock().getLocation();
        InnectisLot lot = LotHandler.getLot(loc);
        BlockState newState = event.getNewState();

        if (lot != null && lot.isFlagSet(LotFlagType.PIXELBUILD)) {
            event.setCancelled(true);
            return;
        }

        if (newState.getType() == org.bukkit.Material.SNOW
                || newState.getType() == org.bukkit.Material.ICE) {
            List<InnectisLot> lots = LotHandler.getLots(loc, true);

            if (lots != null) {
                for (InnectisLot l : lots) {
                    // Snow/Ice won't form on a lot if flag NoFreeze is set
                    if (l != null && l.isFlagSet(LotFlagType.NOFREEZE)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

}
