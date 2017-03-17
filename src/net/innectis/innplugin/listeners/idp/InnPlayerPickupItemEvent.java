package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.entity.Item;

/**
 *
 * @author Nosliw
 */
public class InnPlayerPickupItemEvent extends APlayerEvent implements InnEventCancellable {

    private final Item item;
    private final int remaining;
    private boolean cancel;

    public InnPlayerPickupItemEvent(IdpPlayer player, Item item, int remaining) {
        super(player, InnEventType.PLAYER_PICKUP_ITEM);

        this.item = item;
        this.remaining = remaining;
        this.cancel = false;
    }

    public Item getItem() {
        return item;
    }

    public int getRemaining() {
        return remaining;
    }

    public IdpItemStack getItemStack() {
        return IdpItemStack.fromBukkitItemStack(item.getItemStack());
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
}
