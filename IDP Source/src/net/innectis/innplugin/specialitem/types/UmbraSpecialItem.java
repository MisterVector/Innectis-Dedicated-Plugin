package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.items.IdpItemStack;
import org.bukkit.ChatColor;

/**
 * A special sword that when its foes are killed by it
 * their head will drop
 *
 * @author AlphaBlend
 */
public class UmbraSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore(ChatColor.GRAY + "Slain mobs drop their head");
    }

    @Override
    public boolean canApplyTo(IdpItemStack stack) {
        switch (stack.getMaterial()) {
            case WOOD_SWORD:
            case IRON_SWORD:
            case GOLD_SWORD:
            case DIAMOND_SWORD:
                return true;
        }

        return false;
    }

}
