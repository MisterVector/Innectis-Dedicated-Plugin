package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.items.IdpItemStack;

/**
 * A special item applied to a sword that kills
 * everything it touches in one hit
 *
 * @author AlphaBlend
 */
public class FrostmourneSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore("Slay foes in one hit");
    }

    @Override
    public boolean canApplyTo(IdpItemStack stack) {
        switch (stack.getMaterial()) {
            case IRON_SWORD:
            case GOLD_SWORD:
            case DIAMOND_SWORD:
                return true;
        }

        return false;
    }

}
