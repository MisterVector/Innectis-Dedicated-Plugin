package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Hret
 *
 * Event to handle the movement of a player
 */
public class InnPlayerLotLeaveEvent extends APlayerEvent implements InnEventCancellable {

    private boolean cancel;
    private final InnectisLot to;
    private final InnectisLot from;

    public InnPlayerLotLeaveEvent(IdpPlayer player, InnectisLot to, InnectisLot from) {
        super(player, InnEventType.PLAYER_LOT_LEAVE);
        this.to = to;
        this.from = from;
        this.cancel = false;
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
     * The lot that is on the location where the player moves to.
     * @return
     */
    public InnectisLot getTo() {
        return to;
    }

    /**
     * The lot on the location where the player came from
     * @return
     */
    public InnectisLot getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "IdpPlayerMoveEvent " + getPlayer().getName() + " (From: " + getFrom() + ") - (To: " + getTo() + ")";
    }
    
}
