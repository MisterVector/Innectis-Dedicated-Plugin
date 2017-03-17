package net.innectis.innplugin.specialitem.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.PresentContent;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayerInventory;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A special item that contains other items
 * inside of it
 *
 * @author AlphaBlend
 */
public class PresentSpecialItem extends AbstractSpecialItem {

    public void addLoreName(IdpItemStack stack) {
        stack.getItemdata().addLore("Full of goodies!");
    }

    @Override
    public boolean canApplyTo(IdpItemStack stack) {
        switch (stack.getMaterial()) {
            case REDSTONE_LAMP_OFF:
                return true;
        }

        return false;
    }

    @Override
    public boolean onInteract(InnPlugin plugin, IdpPlayer player, EquipmentSlot handSlot, IdpItemStack stack, Action action, Block block) {
            // Only on right click air.
            if (action == Action.RIGHT_CLICK_AIR) {
                player.setItemInHand(handSlot, IdpItemStack.EMPTY_ITEM);

                PresentContent present = PresentContent.getPresent(stack);

                if (present == null) {
                    player.printError("Invalid item!");
                    InnPlugin.logError("Invalid present! (" + stack.getItemdata().getValue(PresentContent.PRESENT_TAG) + ")");
                    return true;
                }

                IdpPlayerInventory inv = player.getInventory();
                IdpContainer cont = new IdpContainer(inv);
                IdpItemStack[] presentItems = present.getItems();
                List<IdpItemStack> extraitems = cont.addMaterialsToStack(presentItems);
                Location loc = player.getLocation();

                // Drop items that dont fit
                for (IdpItemStack dropitem : extraitems) {
                    player.getLocation().getWorld().dropItem(loc, dropitem.toBukkitItemstack());
                }

                inv.setItems(cont.getItems());
                inv.updateBukkitInventory();

                present.setOpened(true);
                present.save();

                player.printInfo("You opened your present!");

                HashMap<IdpMaterial, Integer> materialCount = new HashMap<IdpMaterial, Integer>();

                // First, get the count of materials in this present
                for (IdpItemStack presentItem : presentItems) {
                    if (presentItem == null) {
                        continue;
                    }

                    IdpMaterial mat = presentItem.getMaterial();
                    int amt = presentItem.getAmount();

                    if (!materialCount.containsKey(mat)) {
                        materialCount.put(mat, amt);
                    } else {
                        int previousAmount = materialCount.get(mat);
                        materialCount.put(mat, amt + previousAmount);
                    }
                }

                StringBuilder sb = new StringBuilder();
                int count = 0;

                // Second, list the contents of the present, now sorted by material count
                for (Map.Entry<IdpMaterial, Integer> entry : materialCount.entrySet()) {
                    IdpMaterial mat = entry.getKey();
                    int amt = entry.getValue();

                    if (count > 0) {
                        sb.append(", ");
                    }

                    sb.append(amt).append(" ").append(mat);
                    count++;
                }

                player.printInfo("Contents: " + sb.toString());

                return true;
            }

            return false;
    }

}
