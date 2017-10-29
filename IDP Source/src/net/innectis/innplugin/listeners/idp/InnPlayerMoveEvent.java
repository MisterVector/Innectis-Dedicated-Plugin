package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;

/**
 * Called when a player moves
 *
 * @author AlphaBlend
 */
public class InnPlayerMoveEvent extends APlayerEvent implements InnEventCancellable {

    private Location from;
    private Location to;
    private boolean cancel;

    public InnPlayerMoveEvent(IdpPlayer player, Location from, Location to) {
        super(player, InnEventType.PLAYER_MOVE);
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the from location
     * @return
     */
    public Location getFrom() {
        return from;
    }

    /**
     * Gets the to location
     * @return
     */
    public Location getTo() {
        return to;
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
