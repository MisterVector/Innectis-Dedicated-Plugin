package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.entity.Hanging;

public class InnHangingBreakEvent extends APlayerEvent implements InnEventCancellable {

    private Hanging entity;
    private boolean cancel;

    public InnHangingBreakEvent(IdpPlayer player, Hanging entity) {
        super(player, InnEventType.HANGING_BREAK);
        this.entity = entity;
        cancel = false;
    }

    /**
     * Checks if the event was cancelled.
     * @return
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the event to cancelled.
     * @param cancelled
     */
    @Override
    public void setCancelled(boolean cancelled) {
        cancel = cancelled;
    }

    /**
     * The entity that was broken.
     * @return
     */
    public Hanging getHangingEntity() {
        return entity;
    }
    
}
