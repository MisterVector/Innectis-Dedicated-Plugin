package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.listeners.InnPlayerEvent;
import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Hret
 *
 * The base for all player events in the IDP.
 */
abstract class APlayerEvent extends AbstractInnEvent implements InnPlayerEvent {

    private final IdpPlayer player;

    public APlayerEvent(final IdpPlayer player, final InnEventType type) {
        super(type);
        this.player = player;
    }

    /**
     * The player that has triggered the event
     * @return
     */
    @Override
    public IdpPlayer getPlayer() {
        return player;
    }
    
}
