package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.specialitem.SpecialItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.Permission;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Abstract special item which has the events implemented with the default return value.
 *
 * @author Hret
 */
abstract class AbstractSpecialItem implements SpecialItem {

    /**
     * The default setting checks if the itemstack is not null and the material not air.
     * @param itemstack
     * @return
     */
    @Override
    public boolean canApplyTo(IdpItemStack itemstack) {
        return itemstack != null && itemstack.getMaterial() != IdpMaterial.AIR;
    }

    /**
     * @inherit
     */
    @Override
    public boolean onBowShoot(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Entity projectile) {
        return false;
    }

    /**
     * @inherit
     */
    @Override
    public boolean onDamageEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, InnEntityDamageEvent event) {
        return false;
    }

    /**
     * @inherit
     */
    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        return false;
    }

    /**
     * @inherit
     */
    @Override
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, LivingEntity entity) {
        return false;
    }

    /**
     * Checks if the user can use this special item.
     * @param player
     * @return true if the player can use a bonus again.
     */
    protected boolean canUseSpecialItem(IdpPlayer player) {
        // Check if the playre can use the bonus
        if (!player.getSession().canUseBonus()) {
            if (!player.hasPermission(Permission.bonus_no_restrictions)) {
                player.printError("You cannot unleash your power for another "
                        + player.getSession().getNextUseBonus() + " seconds!");
                return false;
            } else {
                player.printError("WARNING: You are using this special item too often!");
                player.printError("This may cause serious lag if used too much!");
            }
        }

        return true;
    }

}
