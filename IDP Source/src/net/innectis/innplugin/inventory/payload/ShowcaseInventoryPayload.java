package net.innectis.innplugin.inventory.payload;

import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * An inventory payload implementation handling a
 * showcase inventory
 *
 * @author AlphaBlend
 */
public class ShowcaseInventoryPayload extends InventoryPayload {

    public ShowcaseInventoryPayload() {
        super();
    }

    @Override
    public void onInventoryClick(IdpPlayer player, InventoryClickEvent event) {
        event.setCancelled(true);
    }
    
}
