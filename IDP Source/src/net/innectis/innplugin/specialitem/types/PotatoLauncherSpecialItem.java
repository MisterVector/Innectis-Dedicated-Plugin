package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.tasks.LimitedTask;
import net.innectis.innplugin.tasks.RunBehaviour;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFirework;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that fires poisoned potatoes
 *
 * @author Nosliw
 */
public class PotatoLauncherSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Fire those useless poison potatoes!");
        itemstack.getItemdata().addLore(ChatColor.RED + "Left Click" + ChatColor.GRAY + ": Fire potato!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block clickedBlock) {
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (player.removeItemFromInventory(IdpMaterial.POISON_POTATO, 1)) {
                Item itemEntity = player.getLocation().getWorld().dropItem(player.getLocation().add(0, 1, 0), new IdpItemStack(IdpMaterial.POISON_POTATO, 1).toBukkitItemstack());
                itemEntity.setVelocity(player.getLocation().getDirection().multiply(1.6));

                itemEntity.setPickupDelay(Integer.MAX_VALUE);
                player.getHandle().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 25, 5);
                plugin.getTaskManager().addTask(new PotatoSplatTask(itemEntity));
            } else {
                player.printError("You do not have any ammo for this!");
            }
        }

        return true;
    }

    private class PotatoSplatTask extends LimitedTask {
        private Item entity;

        public PotatoSplatTask(Item entity) {
            super(RunBehaviour.SYNCED, 250, 50);
            this.entity = entity;
        }

        public void run() {
            if (entity == null || entity.isDead()) {
                executecount = 0;
            } else if (executecount == 0 || entity.isOnGround() || !entity.getNearbyEntities(1, 1, 1).isEmpty()) {
                Firework firework = BlockHandler.launchFirework(entity.getLocation(),
                        FireworkEffect.Type.BALL, true, Color.YELLOW, Color.GREEN, true);
                ((CraftWorld) entity.getWorld()).getHandle().broadcastEntityEffect(((CraftFirework) firework).getHandle(), (byte) 17);
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_SLIME_ATTACK, 10, 5);

                firework.remove();
                entity.remove();
                executecount = 0;
            }
        }
    }

}
