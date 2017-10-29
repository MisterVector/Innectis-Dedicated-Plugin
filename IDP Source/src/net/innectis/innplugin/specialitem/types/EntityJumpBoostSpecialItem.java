package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that jumps entities in
 * the air when used on them
 *
 * @author AlphaBlend
 */
public class EntityJumpBoostSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore(ChatColor.GREEN + "Right-click an entity to cause");
        stack.getItemdata().addLore(ChatColor.GREEN + "it to fly high in the air");
    }

    @Override
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handslot, IdpItemStack stack, LivingEntity entity) {
        Entity velocityEntity = (entity.getVehicle() != null ? entity.getVehicle() : entity);

        org.bukkit.util.Vector speed = velocityEntity.getVelocity();
        speed.setY(4);
        velocityEntity.setVelocity(speed);

        // make sure to ignore fall damage if the entity whose velocity is being modified is a player
        if (velocityEntity instanceof Player) {
            IdpPlayer velocityPlayer = plugin.getPlayer((Player) velocityEntity);
            velocityPlayer.getSession().setJumped(true);
        }

        return true;
    }

}
