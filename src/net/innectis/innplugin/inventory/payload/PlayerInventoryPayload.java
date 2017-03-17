package net.innectis.innplugin.inventory.payload;

import java.util.UUID;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.objects.ViewedPlayerInventoryData;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.player.IdpPlayer;
import net.innectis.innplugin.player.IdpPlayerInventory;
import net.innectis.innplugin.player.InventoryType;
import net.innectis.innplugin.player.PlayerSession;
import net.innectis.innplugin.player.PlayerSession.PlayerStatus;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * A payload used when viewing a player's inventory
 *
 * @author AlphaBlend
 */
public class PlayerInventoryPayload extends InventoryPayload {

    private InnPlugin plugin = null;

    public PlayerInventoryPayload(InnPlugin plugin) {
        super();

        this.plugin = plugin;
    }

    @Override
    public void onInventoryClose(IdpPlayer player, InventoryCloseEvent event) {
        PlayerSession session = player.getSession();
        IdpContainer container = new IdpContainer(event.getInventory());

        ViewedPlayerInventoryData vpid = session.getViewedPlayerInventoryData();
        UUID inventoryPlayerId = vpid.getPlayerId();
        String inventoryPlayerName = vpid.getPlayerName();
        InventoryType inventoryType = vpid.getViewedInventoryType();

        IdpPlayer testPlayer = plugin.getPlayer(inventoryPlayerId);
        boolean updated = false;

        // They came online after we were viewing their inventory
        if (testPlayer != null) {
            if (testPlayer.getSession().getPlayerStatus() == PlayerStatus.DEAD_PLAYER) {
                player.printError("Player is dead. Unable to update inventory!");
                return;
            }

            IdpPlayerInventory playerInventory = testPlayer.getInventory();
            InventoryType playerInventoryType = playerInventory.getType();

            // The player's inventory is currently set as what we viewed, so we only
            // need to update their bukkit inventory
            if (inventoryType == playerInventoryType) {
                playerInventory.setItems(container.getNonArmorItems());
                playerInventory.setArmorItems(container.getArmorItems());
                playerInventory.setOffHandItem(container.getOffHandItem());
                playerInventory.updateBukkitInventory();
                updated = true;
            }
        }

        // If the inventory hasn't yet been updated, let's save it
        if (!updated && inventoryType != InventoryType.NO_SAVE) {
            IdpPlayerInventory inv = IdpPlayerInventory.load(inventoryPlayerId, inventoryPlayerName, inventoryType, plugin);
            inv.setItems(container.getNonArmorItems());
            inv.setArmorItems(container.getArmorItems());
            inv.setOffHandItem(container.getOffHandItem());
            inv.store();
        }

        session.setViewedPlayerInventoryData(null);
    }

}
