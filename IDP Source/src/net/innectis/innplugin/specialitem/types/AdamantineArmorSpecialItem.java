package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.items.IdpItemStack;
import org.bukkit.ChatColor;

/**
 * A simple class that allows an item to become
 * an adamantine armor item
 *
 * @author AlphaBlend
 */
public class AdamantineArmorSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore(ChatColor.YELLOW + "Provides Great Protection");
    }

    @Override
    public boolean canApplyTo(IdpItemStack stack) {
        switch (stack.getMaterial()) {
            case DIAMOND_HELMET:
            case DIAMOND_CHEST:
            case DIAMOND_LEGGINS:
            case DIAMOND_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHEST:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
                return true;
        }

        return false;
    }

}
