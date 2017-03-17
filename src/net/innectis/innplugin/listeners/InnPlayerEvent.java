package net.innectis.innplugin.listeners;

import net.innectis.innplugin.player.IdpPlayer;

/**
 *
 * @author Hret
 *
 * Interface for events that are triggered or related to a player
 */
public interface InnPlayerEvent {

    /**
     * The player that has triggered the event
     * @return
     */
    IdpPlayer getPlayer();
    
}
