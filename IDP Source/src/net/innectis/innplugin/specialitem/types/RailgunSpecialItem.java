package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item for a railgun that fires rockets
 *
 * @author AlphaBlend
 */
public class RailgunSpecialItem extends AbstractSpecialItem {

    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore(ChatColor.LIGHT_PURPLE + "Railgun");
    }

    public boolean canApplyTo(IdpItemStack stack) {
        boolean canApplyTo = super.canApplyTo(stack);

        if (canApplyTo) {
            switch (stack.getMaterial()) {
                case WOOD_HOE:
                case STONE_HOE:
                case IRON_HOE:
                case GOLD_HOE:
                case DIAMOND_HOE:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack item, Action action, Block block) {
        return true;
    }

    @Override
    public boolean onInteractEntity(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack stack, LivingEntity entity) {
        return true;
    }

    private void fireRailgun(IdpPlayer player) {
        float yaw = player.getYaw();
        float pitch = player.getPitch();

        World world = player.getWorld().getHandle();
        //player.getHandle().
    }

}
