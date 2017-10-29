package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that will cause a block to fall
 * when used on it
 *
 * @author AlphaBlend
 */
public class FallingBlockSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore("Click a block to cause it to fall!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot slot, IdpItemStack item, Action action, Block block) {
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return false;
        }

        BlockHandler.dropBlock(block);

        return true;
    }

}
