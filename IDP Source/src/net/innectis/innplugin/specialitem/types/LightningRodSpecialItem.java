package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that allows an item to strike
 * lightning at a location it interacts with
 *
 * @author AlphaBlend
 */
public class LightningRodSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore(ChatColor.GREEN + "Lightning Rod");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack stack, Action action, Block block) {
        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            int range = getRange(stack);

            if (range > 0) {
                range += 5;
            } else {
                range = 15;
            }

            if (range > 50) {
                range = 15;
            }

            setRange(stack, range);
            player.setItemInHand(handSlot, stack);
            player.printInfo("Lightning range set to: " + range);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            int range = getRange(stack);
            if (range == 0) {
                range = 15;
            }

            Block targetBlock = player.getTargetBlock(range);
            Location loc = (targetBlock != null ? targetBlock.getLocation() : player.getLocation());
            loc.getWorld().strikeLightning(loc);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack stack, LivingEntity entity) {
        World world = entity.getWorld();
        world.strikeLightning(entity.getLocation());

        return true;
    }

    private int getRange(IdpItemStack stack) {
        ItemData itemData = stack.getItemdata();

        try {
            return Integer.parseInt(itemData.getValue("range"));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    private void setRange(IdpItemStack stack, Integer range) {
        ItemData itemData = stack.getItemdata();
        itemData.setValue("range", range.toString());
    }

}
