package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;

/**
 * @author AlphaBlend
 * 
 * Event that fires when the player first moves after a respawn
 */
public class InnPlayerPostRespawnEvent extends APlayerEvent {

    public InnPlayerPostRespawnEvent(IdpPlayer player) {
        super(player, InnEventType.PLAYER_POST_RESPAWN);
    }
    
}
