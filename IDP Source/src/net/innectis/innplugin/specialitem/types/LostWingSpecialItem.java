package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.specialitem.SpecialItemConstants;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayer.TeleportType;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

public class LostWingSpecialItem extends AbstractSpecialItem {

    private final int MAX_SECONDS = 300;

    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Release those hidden wings!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        if (action == Action.PHYSICAL) {
            return false;
        }

        if (player.getSession().isCanFly()) {
            player.printError("This is not the time to use this..");
        } else {

            IdpItemStack handStack = player.getItemInHand(handSlot);

            // Check for permission and if not, subtract item
            if (!player.hasPermission(Permission.bonus_misc_fly)) {
                if (handStack.getAmount() > 1) {
                    handStack.setAmount(handStack.getAmount() - 1);
                    player.setItemInHand(handSlot, handStack);
                } else {
                    player.setItemInHand(handSlot, new IdpItemStack(IdpMaterial.AIR, 1));
                }
            }

            if (BlockHandler.canBuildInArea(player, player.getLocation(), 2, false)) {
                // Display message
                String message = StringUtil.format("{0} {1}found their lost wings!", player.getColoredDisplayName(), ChatColor.GREEN);
                plugin.broadCastMessageToLocation(player.getLocation(), message, SpecialItemConstants.SPELL_SHOUT_RADIUS);

                // Apply the effect.
                player.setAllowFlight(true);
                player.getSession().setCanFly(true);
                player.printInfo("You feel as light as a feather..");

                // Spawn cleanup
                plugin.getTaskManager().addTask(new LostWingTimeOut(player, MAX_SECONDS));

            } else {
                player.printError("You cannot unleash this power here!");
            }
        }
        return true;
    }

    private class LostWingTimeOut extends LimitedTask {
        private IdpPlayer player;
        private int maxSeconds;

        public LostWingTimeOut(IdpPlayer player, int maxSeconds) {
            super(RunBehaviour.SYNCED, 1000, maxSeconds);
            this.player = player;
        }

        public void run() {
            if (player != null) {
                boolean expired = (super.executecount == maxSeconds);

                if (!expired) {
                    int timeLeft = (super.executecount - maxSeconds);

                    // Announce when the lost wings will expire
                    if (timeLeft == 30 || timeLeft == 15 || timeLeft == 10
                            || timeLeft <= 5) {
                        player.printError("Your lost wings will wither away in " + timeLeft + " second" + (timeLeft != 1 ? "s" : ""));
                    }
                } else {
                    player.getHandle().setFallDistance(0);

                    // Teleport to same location with spawn finder taking effect
                    player.teleport(player.getLocation(), TeleportType.USE_SPAWN_FINDER, TeleportType.PVP_IMMUNITY);

                    player.setAllowFlight(false);
                    player.getSession().setCanFly(false);
                    player.printError("You feel your wings wither away..");
                }
            }
        }
    }

}
