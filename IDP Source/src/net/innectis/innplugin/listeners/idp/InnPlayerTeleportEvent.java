package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 *
 * @author Hret
 *
 * Event to handle the movement of a player
 */
public class InnPlayerTeleportEvent extends APlayerEvent implements InnEventCancellable {

    private boolean cancel = false;
    private final TeleportCause cause;
    private final Location locTo;
    private final Location locFrom;
    private InnectisLot lotTo;
    private InnectisLot lotFrom;

    public InnPlayerTeleportEvent(final IdpPlayer player, final TeleportCause cause, final Location locTo, final Location locFrom) {
        super(player, InnEventType.PLAYER_TELEPORT);

        this.locTo = locTo;
        this.locFrom = locFrom;
        this.cause = cause;
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
     * The location where the player is going to.
     *
     * @return
     */
    public Location getLocTo() {
        return locTo;
    }

    /**
     * The location where the player came from.
     *
     * @return
     */
    public Location getLocFrom() {
        return locFrom;
    }

    /**
     * The lot that is on the location where the player moves to.
     *
     * @return
     */
    public InnectisLot getLotTo() {
        if (lotTo == null) {
            lotTo = LotHandler.getLot(locTo);
        }
        return lotTo;
    }

    /**
     * The lot on the location where the player came from
     *
     * @return
     */
    public InnectisLot getLotFrom() {
        if (lotFrom == null) {
            lotFrom = LotHandler.getLot(locFrom);
        }
        return lotFrom;
    }

    /**
     * The cause of player teleporting.
     *
     * @return
     */
    public TeleportCause getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "IdpPlayerMoveEvent " + getPlayer().getName() + " (From: " + getLocFrom() + ") - (To: " + getLocTo() + ")";
    }
    
}
