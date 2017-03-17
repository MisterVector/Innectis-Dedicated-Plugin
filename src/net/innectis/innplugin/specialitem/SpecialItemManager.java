package net.innectis.innplugin.specialitem;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.listeners.idp.InnEntityDamageEvent;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Manager to manage special items and sending events to the correct special item
 *
 * @author Hret
 */
public class SpecialItemManager {

    private InnPlugin plugin;

    public SpecialItemManager(InnPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a new special item from an item stack
     * and a special item type
     * item stack
     * @param stack
     * @param type
     * @return true if the item was created successfully, false otherwise
     */
    public static boolean createSpecialItem(IdpItemStack stack, SpecialItemType type) {
        SpecialItem specialItem = type.getSpecialItem();

        if (!specialItem.canApplyTo(stack)) {
            return false;
        }

        ItemData itemData = stack.getItemdata();
        itemData.setItemName(ChatColor.GOLD + type.getName());
        itemData.setSpecialItem(type);
        itemData.setLore(new String[0]);

        specialItem.addLoreName(stack);

        return true;
    }

    /**
     * Checks if the special item has a bow effect and fires it.
     *
     * @param player
     * @param item
     * @param projectile
     * @return True when the event has been handled and should be cancelled and stopped.
     */
    public boolean onBowShoot(IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Entity projectile) {
        // No item in hand, so cancel
        if (handSlot == null) {
            return false;
        }

        SpecialItemType type = getSpecialItem(item, handSlot, player);

        if (type == null) {
            return false;
        }

        return type.getSpecialItem().onBowShoot(plugin, player, handSlot, item, projectile);
    }

    /**
     * Checks if the item is special and if it should do anything on interact
     *
     * @param player
     * @param handSlot
     * @param item
     * @param action
     * @param block
     * @return True when the event has been handled and should be cancelled and stopped.
     */
     public boolean onInteract(IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        // No item in hand, so cancel
        if (handSlot == null) {
            return false;
        }

        SpecialItemType type = getSpecialItem(item, handSlot, player);

        if (type == null) {
            return false;
        }

        return type.getSpecialItem().onInteract(plugin, player, handSlot, item, action, block);
    }

    /**
     * Checks if the item is special and if it should do anything on interact
     *
     * @param player
     * @param handSlot
     * @param item
     * @param entity
     * @return True when the event has been handled and should be canceled and stopped.
     */
    public boolean onInteractEntity(IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, LivingEntity entity) {
        // No item in hand, so cancel
        if (handSlot == null) {
            return false;
        }

        SpecialItemType type = getSpecialItem(item, handSlot, player);

        if (type == null) {
            return false;
        }

        return type.getSpecialItem().onInteractEntity(plugin, player, handSlot, item, entity);
    }

    /**
     * Checks if the item is special and if it alters anything related to damage
     *
     * @param player
     * @param handSlot
     * @param item
     * @param event
     * @return True when the event has been handled and should be cancelled and stopped.
     */
    public boolean onDamageEntity(IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, InnEntityDamageEvent event) {
        // No item in hand, so cancel
        if (handSlot == null) {
            return false;
        }

        SpecialItemType type = getSpecialItem(item, handSlot, player);

        if (type == null) {
            return false;
        }

        return type.getSpecialItem().onDamageEntity(plugin, player, handSlot, item, event);
    }

    /**
     * Returns a special item type, if applicable, for the itemstack
     * @param item
     * @return The effect or null
     */
    private SpecialItemType getSpecialItem(IdpItemStack item, EquipmentSlot handSlot, IdpPlayer player) {
        if (item == null || item.getMaterial() == IdpMaterial.AIR || item.getItemdata().isEmpty()) {
            return null;
        }

        return item.getItemdata().getSpecialItem();
    }

}
