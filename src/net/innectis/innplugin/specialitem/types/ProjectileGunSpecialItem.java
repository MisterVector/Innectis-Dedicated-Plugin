package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.EntityConstants;
import net.innectis.innplugin.objects.ProjectileType;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * A special item that allows several different projectiles
 * to be fired from this item
 *
 * @author AlphaBlend
 */
public class ProjectileGunSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore(ChatColor.GREEN + "Right-click to cycle projectiles");
        stack.getItemdata().addLore(ChatColor.GREEN + "Left-click to fire projectiles");
        stack.getItemdata().addLore(ChatColor.YELLOW + "Projectile: " + ProjectileType.fromCycleNumber(0).getName());
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack stack, Action action, Block block) {
        ItemData data = stack.getItemdata();

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            int cycleNumber = getCycleNumber(data);
            ProjectileType type = ProjectileType.fromCycleNumber(cycleNumber);

            Player bukkitPlayer = player.getHandle();
            Projectile projectile = bukkitPlayer.launchProjectile(type.getBukkitClass());

            // If an arrow is spawned make sure it's marked custom
            if (projectile instanceof Arrow) {
                projectile.setMetadata(EntityConstants.METAKEY_CUSTOM_ARROW, new FixedMetadataValue(plugin, true));
            }
        } else if (action == Action.LEFT_CLICK_AIR
                || action == Action.LEFT_CLICK_BLOCK) {
            ProjectileType type = ProjectileType.SMALL_FIREBALL;
            int cycleNumber = getCycleNumber(data);

            // If lore exists, get the projectile type, then find the next one
            if (cycleNumber > -1) {
                type = ProjectileType.fromCycleNumber(cycleNumber);
                type = type.nextType();
            }

            setCycleNumber(data, type.getCycleNumber());
            setProjectileLore(data, type);
            player.setItemInHand(handSlot, stack);

            player.printInfo("Set projectile type to: " + type.getName());
        }

        return true;
    }

    /**
     * Sets the projectile name currently used on the projectile gun
     * @param data
     * @param cycleNumber
     */
    private void setProjectileLore(ItemData data, ProjectileType type) {
        data.setLore(new String[] {
            ChatColor.GREEN + "Right-click to fire projectiles",
            ChatColor.GREEN + "Left-click to cycle projectiles",
            ChatColor.YELLOW + "Projectile: " + type.getName()}
        );
    }

    /**
     * Sets the cycle number of the projectile gun
     * @param data
     * @param cycleNumber
     */
    private void setCycleNumber(ItemData data, Integer cycleNumber) {
        data.setValue("cycle_number", cycleNumber.toString());
    }

    /**
     * Gets the cycle number of the projectile gun
     * @param data
     * @return
     */
    private int getCycleNumber(ItemData data) {
        try {
            return Integer.parseInt(data.getValue("cycle_number"));
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

}
