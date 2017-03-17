package net.innectis.innplugin.inventory.payload;

import java.util.UUID;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.inventory.IdpInventory;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.PlayerBackpack;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/**
 * An inventory payload for viewing a player's backpack
 *
 * @author AlphaBlend
 */
public class BackpackInventoryPayload extends InventoryPayload {

    InnPlugin plugin = null;
    private String backpackOwner = null;
    private UUID backpackOwnerId = null;

    public BackpackInventoryPayload(InnPlugin plugin, String backpackOwner, UUID backpackOwnerId) {
        super();

        this.plugin = plugin;
        this.backpackOwner = backpackOwner;
        this.backpackOwnerId = backpackOwnerId;
    }

    @Override
    public void onInventoryClose(IdpPlayer player, InventoryCloseEvent event) {
        boolean self = backpackOwnerId.equals(player.getUniqueId());

        ItemStack[] stack = event.getInventory().getContents();
        IdpItemStack[] newStack = new IdpItemStack[stack.length];

        for (int i = 0; i < stack.length; i++) {
            newStack[i] = IdpItemStack.fromBukkitItemStack(stack[i]);
        }

        PlayerBackpack backpack = null;

        if (self) {
            backpack = player.getSession().getBackpack();
        } else {
            IdpPlayer testPlayer = plugin.getPlayer(backpackOwnerId);

            if (testPlayer != null) {
                backpack = testPlayer.getSession().getBackpack();
            } else {
                backpack = PlayerBackpack.loadBackpackFromDB(backpackOwnerId, backpackOwner);
            }
        }

        backpack.updateItems(newStack);
        backpack.save();

        IdpInventory inv = plugin.getBackpackView(backpackOwnerId);

        if (inv == null) {
            player.printError("Internal server error.");
            plugin.logError("A backpack view was not found!");
            return;
        }

        inv.getViewers().remove(event.getPlayer());

        if (inv.getViewers().isEmpty()) {
            plugin.removeBackpackView(backpackOwnerId);
        }
    }

}
