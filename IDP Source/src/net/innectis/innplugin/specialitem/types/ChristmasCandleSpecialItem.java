package net.innectis.innplugin.specialitem.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.system.command.commands.MiscCommands;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that provides a nice effect to the
 * player and replaces entities around the player with
 * other entities
 *
 * @author Hret
 */
public class ChristmasCandleSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Shine brightly in the dark!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        if (action == Action.PHYSICAL) {
            return false;
        }

        // Check for permission and if the user can use the bonus again
        if (!player.hasPermission(Permission.bonus_christmas_candle) || !super.canUseSpecialItem(player)) {
            return true;
        }

        if (BlockHandler.canBuildInArea(player, player.getLocation(), 2, false)) {
            player.getSession().useBonus();

            if (player.getSession().getDisplayBonusMessage()) {
                InnPlugin.getPlugin().broadCastMessage(ChatColor.AQUA, "[Bonus] " + player.getColoredDisplayName() + ChatColor.AQUA + " released their " + ChatColor.GOLD + "Christmas Candle" + ChatColor.AQUA + "!");
            } else {
                player.print(ChatColor.AQUA, "[Bonus] You" + ChatColor.AQUA + " release your " + ChatColor.GOLD + "Christmas Candle" + ChatColor.AQUA + "!");
            }

            plugin.getTaskManager().addTask(new ChristmasCandleBehaviour(player));
        } else {
            player.printError("You cannot unleash this power here!");
        }
        return true;
    }
}

class ChristmasCandleBehaviour extends LimitedTask {

    private final IdpPlayer player;
    private final Location playerLoc;

    public ChristmasCandleBehaviour(IdpPlayer player) {
        super(RunBehaviour.ASYNC, 1, 1);
        this.player = player;
        this.playerLoc = player.getLocation();
    }

    public void run() {
        int ox = playerLoc.getBlockX();
        int oy = playerLoc.getBlockY();
        int oz = playerLoc.getBlockZ();
        final World world = playerLoc.getWorld();

        List<Location> blocks = new ArrayList<Location>();
        final Random rand = new Random();
        final boolean sendMessage = player.getSession().getDisplayBonusMessage();
        int i = 0;

        Location temploc;
        for (int range = 3; range < 8; range++) {
            for (double cx = -range; cx <= range;) {
                for (double cy = -range; cy <= range;) {
                    for (double cz = -range; cz <= range;) {
                        temploc = new Location(world, ox + cx, oy + cy, oz + cz);
                        double d = temploc.distance(playerLoc);

                        if (d < range + 0.5 && d > range - 0.5) {
                            Block block = world.getBlockAt(temploc);
                            Location loc = block.getLocation();
                            IdpMaterial mat = IdpMaterial.fromBlock(block);

                            if (mat == IdpMaterial.AIR && BlockHandler.canBuildInArea(player, loc, 2, false)) {
                                if (rand.nextInt(2) == 1) {
                                    BlockHandler.setBlock(block, IdpMaterial.GLASS);
                                } else {
                                    BlockHandler.setBlock(block, IdpMaterial.GLOWSTONE);
                                }

                                BlockHandler.getIdpBlockData(loc).setVirtualBlockStatus(true);
                                blocks.add(loc);
                                i++;
                            }
                        }
                        cz += 0.25;
                    }
                    cy += 0.25;
                }
                cx += 0.25;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (Location loc : blocks) {
                Block block = world.getBlockAt(loc);
                BlockHandler.setBlock(block, IdpMaterial.AIR);

                BlockHandler.getIdpBlockData(loc).clear();
            }
            blocks.clear();

            final int tempRange = range;
            InnPlugin.getPlugin().getTaskManager().addTask(new LimitedTask(RunBehaviour.SYNCED, 0, 1) {
                public void run() {
                    for (LivingEntity entity : world.getLivingEntities()) {
                        if (playerLoc.distance(entity.getLocation()) <= tempRange + 1) {
                            if (entity instanceof Monster) {
                                String mobName = "thing";

                                if (entity.getType() != null) {
                                    mobName = entity.getType().toString().toLowerCase();
                                }

                                String actionTaken = " was turned to dust by " + player.getName() + "'s light.";
                                if (rand.nextInt(5) == 1) {
                                    switch (rand.nextInt(4)) {
                                        case 0:
                                            world.spawnEntity(entity.getLocation(), EntityType.PIG);
                                            actionTaken = " was converted to a pig by " + player.getName() + "'s light.";
                                            entity.remove();
                                            break;
                                        case 1:
                                            world.spawnEntity(entity.getLocation(), EntityType.COW);
                                            actionTaken = " was converted to a cow by " + player.getName() + "'s light.";
                                            entity.remove();
                                            break;
                                        case 2:
                                            world.spawnEntity(entity.getLocation(), EntityType.CHICKEN);
                                            actionTaken = " was converted to a chicken by " + player.getName() + "'s light.";
                                            entity.remove();
                                            break;
                                        case 3:
                                            world.spawnEntity(entity.getLocation(), EntityType.SHEEP);
                                            actionTaken = " was converted to a sheep by " + player.getName() + "'s light.";
                                            entity.remove();
                                            break;
                                        default:
                                            entity.remove();
                                            break;
                                    }
                                } else {
                                    entity.damage(1000.0D, player.getHandle());
                                    entity.remove();
                                }

                                if (sendMessage) {
                                    for (IdpPlayer nearPlayer : InnPlugin.getPlugin().getOnlinePlayers()) {
                                        if (nearPlayer.getLocation().getWorld().equals(playerLoc.getWorld())) {
                                            if (nearPlayer.getLocation().distance(playerLoc) <= 30) {
                                                nearPlayer.printRaw("A " + mobName + actionTaken);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }

        try {
            Thread.sleep(8000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MiscCommands.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}