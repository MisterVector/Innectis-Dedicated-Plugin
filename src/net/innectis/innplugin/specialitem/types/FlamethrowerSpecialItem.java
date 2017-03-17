package net.innectis.innplugin.specialitem.types;

import java.util.HashSet;
import java.util.List;
import net.innectis.innplugin.handlers.BlockHandler;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.specialitem.SpecialItemConstants;
import net.innectis.innplugin.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A flamethrower item that will create fire in front of the player.
 *
 * @author Hret
 */
public class FlamethrowerSpecialItem extends AbstractSpecialItem {

    public void addLoreName(IdpItemStack itemstack) {
        itemstack.getItemdata().addLore(ChatColor.GRAY + "Unleash a mass of flames!");
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block clickedBlock) {
        boolean effective = Math.random() > 0.8;
        List<Block> blocks = player.getHandle().getLineOfSight((HashSet<Material>) null, effective ? 12 : 8);

        for (Block block : blocks) {
            IdpMaterial mat = IdpMaterial.fromBlock(block);

            if (mat == IdpMaterial.AIR) {
                BlockHandler.setBlock(block, IdpMaterial.FIRE);
            }
        }

        // Check if its super effective
        String effectiveString = null;
        if (effective) {
            effectiveString = " It's super effective!";
        }

        // Display message
        String message = StringUtil.format("{0} {1}used Flamethrower{2}!", player.getColoredDisplayName(), ChatColor.GREEN, effectiveString);
        plugin.broadCastMessageToLocation(player.getLocation(), message, SpecialItemConstants.SPELL_SHOUT_RADIUS);

        return true;
    }

}
