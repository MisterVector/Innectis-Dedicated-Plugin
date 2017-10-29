package net.innectis.innplugin.specialitem;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * The interface for a class handling a special item
 *
 * @author Hret
 */
public interface SpecialItem {

    /**
     * Checks if this special item can be applied to the given itemstack
     * @param itemstack
     * @return
     */
    boolean canApplyTo(IdpItemStack itemstack);

    /**
     * This will add the name of the lore that belongs with this special item
     * @param itemstack
     */
    void addLoreName(IdpItemStack itemstack);

    /**
     * This will handle the event when a player shoots its bow.
     * @param plugin
     * @param player
     * @param handSlot
     * @param item
     * @param projectile
     * @return true if event handled and should be stopped
     */
    public boolean onBowShoot(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Entity projectile);

    /**
     * This will handle the event when a player interacts with air or a block (or steps on something)
     * @param plugin
     * @param player
     * @param handSlot
     * @param item
     * @param action
     * @param block
     * @return true if event handled and should be stopped
     */
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block);

    /**
     * This will handle the event when a player interacts with an entity
     * @param plugin
     * @param player
     * @param handSlot
     * @param item
     * @param entity
     * @return true if event handled and should be stopped
     */
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, LivingEntity entity);

    /**
     * This will handle the event when a player damages an entity (will also trigger for bows)
     * @param plugin
     * @param player
     * @param handSlot
     * @param item
     * @param event
     * @return true if event handled and should be stopped
     */
    public boolean onDamageEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, InnEntityDamageEvent event);

}
