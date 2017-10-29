package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.specialitem.SpecialItemConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.tinywe.blockcounters.BlockCounterFactory;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that allows its user to see
 * through blocks for a certain amount of time
 *
 * @author Nosliw
 */
public class XRaySpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "No wall will block your vision!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block clickBlock) {
        if (action == Action.PHYSICAL) {
            return false;
        }

        int size = 10;
        IdpItemStack handStack = player.getItemInHand(handSlot);

        // Check for permission and if not, subtract item
        if (!player.hasPermission(Permission.bonus_misc_xray)) {
            if (handStack.getAmount() > 1) {
                handStack.setAmount(handStack.getAmount() - 1);
                player.setItemInHand(handSlot, handStack);
            } else {
                player.setItemInHand(handSlot, new IdpItemStack(IdpMaterial.AIR, 1));
            }
        }

        if (BlockHandler.canBuildInArea(player, player.getLocation(), 2, false)) {
            PlayerSession session = player.getSession();

            if (session.getSpecialItemInUse()) {
                player.printError("You have a special item in use. Cannot use this tool!");
                return true;
            }

            // Display message
            String message = StringUtil.format("{0} {1}used their xray vision!", player.getColoredDisplayName(), ChatColor.GREEN);
            plugin.broadCastMessageToLocation(player.getLocation(), message, SpecialItemConstants.SPELL_SHOUT_RADIUS);

            Location loc = player.getLocation();
            World world = loc.getWorld();
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            Location loc1 = new Location(world, x - size, y - size, z - size);
            Location loc2 = new Location(world, x + size, y + size, z + size);

            // Apply the effect.
            IdpWorldRegion region = new IdpWorldRegion(world, loc1, loc2);
            List<Block> originalBlocks = new ArrayList<Block>();

            for (Block block : BlockCounterFactory.getCounter(BlockCounterFactory.CountType.SPHERE).getBlockList(region, player.getLocation().getWorld(), null)) {
                IdpMaterial mat = IdpMaterial.fromBlock(block);

                if (mat != IdpMaterial.AIR) {
                    player.getHandle().sendBlockChange(block.getLocation(), Material.BARRIER, (byte) 0);
                    originalBlocks.add(block);
                }
            }

            InnPlugin.getPlugin().getTaskManager().addTask(new XRayCleanupTask(player, originalBlocks));
            player.getSession().setSpecialItemInUse(true);
        } else {
            player.printError("You cannot unleash this power here!");
        }
        return true;
    }

    class XRayCleanupTask extends LimitedTask {
        private UUID playerId;
        private List<Block> originalBlocks = new ArrayList<Block>();

        public XRayCleanupTask(IdpPlayer player, List<Block> originalBlocks) {
            super(RunBehaviour.ASYNC, 15000, 1);

            this.playerId = player.getUniqueId();
            this.originalBlocks = originalBlocks;
        }

        @Override
        public void run() {
            IdpPlayer player = InnPlugin.getPlugin().getPlayer(playerId);
            PlayerSession session = null;

            if (player != null) {
                for (Block block : originalBlocks) {
                    player.sendBlockChange(block);
                }

                session = player.getSession();
                player.print(ChatColor.GREEN, "Your X-ray vision wears off!");
            } else {
                session = PlayerSession.getActiveSession(playerId);
            }

            if (session != null) {
                session.setSpecialItemInUse(false);
            }
        }
    }

}
