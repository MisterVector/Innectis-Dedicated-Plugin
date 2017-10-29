package net.innectis.innplugin.specialitem.types;

import net.innectis.innplugin.items.IdpItemStack;

/**
 * A special item that may be redeemed
 * for valutas of a certain amount
 *
 * @author AlphaBlend
 */
public class BankNoteSpecialItem extends AbstractSpecialItem {

    @Override
    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore("Redeem at a bank to");
        stack.getItemdata().addLore("receive valutas!");
    }

    @Override
    public boolean canApplyTo(IdpItemStack stack) {
        switch (stack.getMaterial()) {
            case PAPER:
                return true;
        }

        return false;
    }

}
