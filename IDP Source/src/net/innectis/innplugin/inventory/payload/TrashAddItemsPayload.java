package net.innectis.innplugin.inventory.payload;

import java.util.List;
import net.innectis.innplugin.handlers.TrashHandler;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * A payload that handles items being added to the trash
 *
 * @author AlphaBlend
 */
public class TrashAddItemsPayload extends InventoryPayload {

    public TrashAddItemsPayload() {
        super();
    }

    @Override
    public void onInventoryClose(IdpPlayer player, InventoryCloseEvent event) {
        List<String> errorMessages = TrashHandler.addTrashFromInventory(event.getInventory());

        if (errorMessages.size() > 0) {
            for (String message : errorMessages) {
                player.printError(message);
            }
        }
    }
    
}
