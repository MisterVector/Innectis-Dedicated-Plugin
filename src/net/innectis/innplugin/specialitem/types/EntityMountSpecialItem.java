package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import net.innectis.innplugin.player.chat.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author AlphaBlend
 */
public class EntityMountSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore(ChatColor.GREEN + "Right-click entity to capture it");
        stack.getItemdata().addLore(ChatColor.GREEN + "Right-click new entity to mount captured entity");
        stack.getItemdata().addLore(ChatColor.GREEN + "Right-click captured entity to remove capture");
        stack.getItemdata().addLore(ChatColor.GREEN + "Right-click mounted entity to unmount it");
    }

    @Override
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handslot, IdpItemStack stack, LivingEntity entity) {
        // Only allow those with permission to set players on mobs
        if (entity instanceof Player && !player.hasPermission(Permission.entity_mount_manipulate_player)) {
            return true;
        }

        Entity passenger = player.getSession().getPassengerEntity();

        if (passenger == null || !passenger.isValid()) {
            if (entity.isInsideVehicle()) {
                entity.leaveVehicle();
                player.printInfo("Ejected this entity from its vehicle!");
            } else {
                player.getSession().setPassengerEntity(entity);
                player.printInfo("Passenger entity set!");
            }
        } else {
            if (entity.equals(passenger)) {
                player.getSession().setPassengerEntity(null);
                player.printInfo("This entity is no longer a passenger entity.");
            } else {
                entity.setPassenger(passenger);
                player.getSession().clearPassengerEntity();
                player.printInfo("Set passenger on target entity!");
            }
        }

        return true;
    }

}
