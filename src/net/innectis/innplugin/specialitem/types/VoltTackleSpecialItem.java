package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.specialitem.SpecialItemConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.data.IdpBlockData;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession.PlayerStatus;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

/**
 * A special item that mimics the effect of
 * the pokemon pikachu attack "volt tackle"
 *
 * @author Nosliw
 */
public class VoltTackleSpecialItem extends AbstractSpecialItem {

    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Preforms a Charge Attack!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block clickedBlock) {
        if (BlockHandler.canBuildInArea(player, player.getLocation(), 2, false)) {

            if (!player.getSession().canUseBonus()) {
                if (player.hasPermission(Permission.bonus_no_restrictions)) {
                    player.printError("WARNING: You are using your abilities too often.");
                    player.printError("This may cause serious lag if you continue!");
                } else {
                    player.printError("You fail to build up enough charge.");
                    player.printError("You will be able to release this power in " + player.getSession().getNextUseBonus() + " seconds!");
                    return true;
                }
            }

            player.getSession().useBonus();


            if (!player.hasPermission(Permission.bonus_attack_pikachu)) {
                player.setItemInHand(handSlot, new IdpItemStack(IdpMaterial.AIR, 1));
            }

            String message = StringUtil.format("{0} {1}uses {2}Volt Tackle{1}!", player.getColoredDisplayName(), ChatColor.GREEN, ChatColor.GOLD);
            plugin.broadCastMessageToLocation(player.getLocation(), message, SpecialItemConstants.SPELL_SHOUT_RADIUS);

            if (!generateVoltTackle(player)) {
                player.printError("Your ability does not release correctly!");
            }

        } else {
            player.printError("You are not comfortable preforming this action here!");
            return true;
        }

        // Make sure the volt tackle moves.
        plugin.getTaskManager().addTask(new VoltTackleMoveTask(player.getLocation(), player));
        return true;
    }

    public boolean generateVoltTackle(IdpPlayer player) {

        // Lets kill what we ran over..
        for (Entity ent : player.getNearbyEntities(3, LivingEntity.class, false, false, 0)) {
            ((LivingEntity) ent).damage(100.0D, player.getHandle());
        }
        for (IdpPlayer hitPlayer : player.getNearByPlayers(3)) {
            if (hitPlayer.getSession().getPlayerStatus() == PlayerStatus.ALIVE_PLAYER) {
                hitPlayer.getSession().setDeathMessage(hitPlayer.getName() + " was hit by " + player.getName() + "'s Volt Tackle");
                hitPlayer.setHealth(0);
            }
        }

        // Now lets generate the effect
        List<Block> blocks = new ArrayList<Block>();
        BlockFace direction = player.getFacingDirection();
        BlockFace side = BlockFace.EAST;
        Block keyBlock = player.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(direction);
        Random random = new Random();

        // This block MUST be first.
        blocks.add(keyBlock);
        blocks.add(keyBlock.getRelative(BlockFace.DOWN));

        // Now lets generate the behind..
        direction = direction.getOppositeFace();
        switch (direction) {
            case EAST:
            case SOUTH_EAST:
            case WEST:
            case NORTH_WEST:
                side = BlockFace.NORTH;
        }

        // Top
        blocks.add(keyBlock.getRelative(BlockFace.UP).getRelative(direction));
        blocks.add(keyBlock.getRelative(BlockFace.UP).getRelative(direction).getRelative(direction));
        blocks.add(keyBlock.getRelative(BlockFace.UP).getRelative(direction).getRelative(direction).getRelative(direction));
        blocks.add(keyBlock.getRelative(BlockFace.UP).getRelative(direction).getRelative(direction).getRelative(direction).getRelative(direction).getRelative(direction));

        // Bottom
        blocks.add(keyBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(direction));
        blocks.add(keyBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(direction).getRelative(direction));
        blocks.add(keyBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(direction).getRelative(direction).getRelative(direction));
        blocks.add(keyBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getRelative(direction).getRelative(direction).getRelative(direction).getRelative(direction));

        // Side
        blocks.add(keyBlock.getRelative(direction).getRelative(side));
        blocks.add(keyBlock.getRelative(direction).getRelative(side).getRelative(direction));
        blocks.add(keyBlock.getRelative(direction).getRelative(side).getRelative(direction).getRelative(direction));
        blocks.add(keyBlock.getRelative(direction).getRelative(side).getRelative(direction).getRelative(direction).getRelative(direction));

        blocks.add(keyBlock.getRelative(direction).getRelative(BlockFace.DOWN).getRelative(side));
        blocks.add(keyBlock.getRelative(direction).getRelative(BlockFace.DOWN).getRelative(side).getRelative(direction));
        blocks.add(keyBlock.getRelative(direction).getRelative(BlockFace.DOWN).getRelative(side).getRelative(direction).getRelative(direction));

        // Other side
        side = side.getOppositeFace();

        blocks.add(keyBlock.getRelative(direction).getRelative(side));
        blocks.add(keyBlock.getRelative(direction).getRelative(side).getRelative(direction));

        blocks.add(keyBlock.getRelative(direction).getRelative(BlockFace.DOWN).getRelative(side));
        blocks.add(keyBlock.getRelative(direction).getRelative(BlockFace.DOWN).getRelative(side).getRelative(direction));
        blocks.add(keyBlock.getRelative(direction).getRelative(BlockFace.DOWN).getRelative(side).getRelative(direction).getRelative(direction));
        blocks.add(keyBlock.getRelative(direction).getRelative(BlockFace.DOWN).getRelative(side).getRelative(direction).getRelative(direction).getRelative(direction).getRelative(direction));

        for (Block block : blocks) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat == IdpMaterial.AIR) {
                Location loc = block.getLocation();

                if (BlockHandler.canBuildInArea(player, loc, 2, false)) {
                    if (blocks.get(0) == block) {
                        BlockHandler.setBlock(block, IdpMaterial.GLASS);
                    } else {
                        IdpMaterial randomMaterial = (random.nextInt(3) == 1 ? IdpMaterial.GLASS : IdpMaterial.WOOL_YELLOW);
                        BlockHandler.setBlock(block, randomMaterial);
                    }

                    BlockHandler.getIdpBlockData(loc).setVirtualBlockStatus(true);
                }
            } else if (blocks.get(0) == block) {
                return false;
            }
        }
        return true;
    }

    /**
     * @author Nosliw
     *
     * Task that will cleanup the Volt Tackle and generate next step, if possible.
     */
    private class VoltTackleMoveTask extends LimitedTask {

        final int size = 4;
        private int i = 0;
        private IdpPlayer player;
        private Location location;
        private Boolean canContinue = true;

        public VoltTackleMoveTask(Location location, IdpPlayer player) {
            super(RunBehaviour.SYNCED, 200, 50);
            this.location = location;
            this.player = player;

        }

        public void run() {

            if (!canContinue) {
                return;
            }

            World world = location.getWorld();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            Vector vec1 = new Vector(x - size, y - size, z - size);
            Vector vec2 = new Vector(x + size, y + size, z + size);

            IdpWorldRegion region = new IdpWorldRegion(world, vec1, vec2);

            for (Block block : BlockCounterFactory.getCounter(BlockCounterFactory.CountType.SPHERE).getBlockList(region, location.getWorld(), null)) {
                IdpBlockData blockData = BlockHandler.getIdpBlockData(block.getLocation());

                if (blockData.isVirtualBlock()) {
                    BlockHandler.setBlock(block, IdpMaterial.AIR);
                    blockData.clear();
                }
            }

            i++;
            if (i == 50
                    || InnPlugin.isShuttingDown()) {
                return;
            }

            if (player != null
                    && player.isOnline()) {
                Location loc = player.getLocation().getBlock().getRelative(player.getFacingDirection()).getRelative(BlockFace.DOWN).getLocation();
                player.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), player.getLocation().getYaw(), player.getPitch()), TeleportType.IGNORE_RESTRICTION, TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);
                location = player.getLocation();
                if (!generateVoltTackle(player)) {
                    player.printError("You cannot go any further.");
                    canContinue = false;
                }
            }

        }
    }

}
