package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.specialitem.SpecialItem;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * This class creates a special item that is combined from 2 different special items.
 * This special item itself doesn't do anything on its own. Instead it will send the events to the
 * special items that are composed in this special item.
 * <p/>
 * By design does this class not extend the abstract effect.
 * So that it wont have any default implementations on its own and sends all to the subitems.
 *
 * @author Hret
 */
public class CombinedSpecialItem implements SpecialItem {

    private final SpecialItem item1;
    private final SpecialItem item2;

    public CombinedSpecialItem(SpecialItem item1, SpecialItem item2) {
        if (item1 == null || item2 == null) {
            throw new IllegalArgumentException("The combinedspecial item cannot be composed of null items.");
        }

        this.item1 = item1;
        this.item2 = item2;
    }

    public void addLoreName(IdpItemStack itemstack) {
        item1.addLoreName(itemstack);
        item2.addLoreName(itemstack);
    }

    public boolean canApplyTo(IdpItemStack itemstack) {
        return item1.canApplyTo(itemstack) && item2.canApplyTo(itemstack);
    }

    @Override
    public boolean onBowShoot(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Entity projectile) {
        return item1.onBowShoot(plugin, player, handSlot, item, projectile) | item2.onBowShoot(plugin, player, handSlot, item, projectile);
    }

    @Override
    public boolean onDamageEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, InnEntityDamageEvent event) {
        return item1.onDamageEntity(plugin, player, handSlot, item, event) | item2.onDamageEntity(plugin, player, handSlot, item, event);
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        return item1.onInteract(plugin, player, handSlot, item, action, block) | item2.onInteract(plugin, player, handSlot, item, action, block);
    }

    @Override
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, LivingEntity entity) {
        return item1.onInteractEntity(plugin, player, handSlot, item, entity) | item2.onInteractEntity(plugin, player, handSlot, item, entity);
    }

}
