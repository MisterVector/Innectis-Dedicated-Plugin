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
public class InnPlayerDropItemEvent extends APlayerEvent implements InnEventCancellable {

    private final Item item;
    private boolean cancel;

    public InnPlayerDropItemEvent(IdpPlayer player, Item item) {
        super(player, InnEventType.PLAYER_DROP_ITEM);
        this.item = item;
    }

    public Item getItem() {
        return item;
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
