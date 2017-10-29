package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.objects.PresentContent;
import net.innectis.innplugin.specialitem.SpecialItemType;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * An implementation of inventory payload
 * dealing with presents
 *
 * @author AlphaBlend
 */
public class PresentInventoryPayload extends InventoryPayload {

    PresentContent present = null;

    public PresentInventoryPayload(PresentContent present) {
        super();

        this.present = present;
    }

    @Override
    public void onInventoryClose(IdpPlayer player, InventoryCloseEvent event) {
        ItemStack[] stack = event.getInventory().getContents();
        IdpItemStack[] contents = new IdpItemStack[stack.length];

        int itemcount = 0;
        IdpItemStack item;

        for (int i = 0; i < stack.length; i++) {
            item = IdpItemStack.fromBukkitItemStack(stack[i]);

            if (item == null || item.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            /** Dont allow present recursion */
            if (item.getItemdata().getSpecialItem() == SpecialItemType.PRESENT) {
                player.addItemToInventory(item);
            } else {
                contents[i] = item;
                itemcount++;
            }
        }

        if (itemcount == 0) {
            player.printError("No items in present!");
            return;
        }

        // Set contents and save
        present.setItems(contents);

        if (present.save()) {
            // Make the presentitem
            IdpItemStack presentitem = present.createPresent();

            // Give to player
            player.addItemToInventory(presentitem);
            player.getInventory().updateBukkitInventory();

            player.printInfo("Your present has been created!");
        } else {
            player.printError("Could not create present!");
        }
    }

}
