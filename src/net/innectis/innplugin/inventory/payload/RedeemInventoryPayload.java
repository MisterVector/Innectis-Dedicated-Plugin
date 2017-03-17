package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.handlers.TransactionHandler;
import net.innectis.innplugin.handlers.TransactionHandler.TransactionType;
import net.innectis.innplugin.objects.TransactionObject;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.items.ItemData;
import net.innectis.innplugin.items.RewardItem;
import net.innectis.innplugin.player.chat.ChatColor;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.specialitem.SpecialItem;
import net.innectis.innplugin.specialitem.SpecialItemType;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * An implementation of inventory payload that
 * handles the redeem chest
 *
 * @author AlphaBlend
 */
public class RedeemInventoryPayload extends InventoryPayload {

    public RedeemInventoryPayload() {
        super(InventoryAction.CLOSE_PLAYER_INVENTORY_LATER);
    }

    @Override
    public void onInventoryClick(IdpPlayer player, InventoryClickEvent event) {
        event.setCancelled(true);

        boolean error = false;
        String msg = "";
        IdpItemStack cursorItem = IdpItemStack.fromBukkitItemStack(event.getCursor());
        int rawSlot = event.getRawSlot();

        // Placing item in the anvil inventory
        if (cursorItem != null && cursorItem.getMaterial() != IdpMaterial.AIR) {
            error = true;
            msg = "You may not place items in here!";
        }

        if (!error && rawSlot < 54 && event.isLeftClick()) {
            RewardItem reward = RewardItem.fromSlot(rawSlot);

            if (reward != null) {
                TransactionObject transaction = TransactionHandler.getTransactionObject(player);
                int votePoints = transaction.getValue(TransactionType.VOTE_POINTS);

                if (votePoints >= reward.getCost()) {
                    IdpItemStack rewardStack = new IdpItemStack(reward.getItemStack());
                    ItemData itemData = rewardStack.getItemdata();
                    itemData.setLore(new String[0]);

                    SpecialItemType specialItemType = itemData.getSpecialItem();

                    // Restore the special item lore when giving this item to the player
                    if (specialItemType != null) {
                        SpecialItem specialItem = specialItemType.getSpecialItem();
                        specialItem.addLoreName(rewardStack);
                    }

                    int remain = player.addItemToInventory(rewardStack, true);

                    if (remain == 0) {
                        transaction.subtractValue(reward.getCost(), TransactionType.VOTE_POINTS);
                        msg = "You have exchanged " + reward.getCost() + " vote point(s) for "
                                + reward.getName() + ChatColor.DARK_GREEN + "!";
                    } else {
                        error = true;
                        msg = "Your inventory is too full to accept this reward item!";
                    }
                } else {
                    error = true;
                    msg = "You do not have enough vote points for that item! (" + reward.getCost() + ")";
                }
            }
        }

        if (!msg.isEmpty()) {
            if (error) {
                player.printError(msg);
            } else {
                player.printInfo(msg);
            }
        }
    }

}
