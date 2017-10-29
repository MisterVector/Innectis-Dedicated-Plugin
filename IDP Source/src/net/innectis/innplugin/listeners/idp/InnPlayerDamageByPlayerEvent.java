package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;

/**
 * @author AlphaBlend
 *
 * Event for handling player damage by another player
 */
public class InnPlayerDamageByPlayerEvent extends APlayerEvent implements InnEventCancellable {

    private final IdpPlayer damager;
    private boolean cancel;

    public InnPlayerDamageByPlayerEvent(final IdpPlayer victim, final IdpPlayer damager) {
        super(victim, InnEventType.PLAYER_DAMAGE_BY_PLAYER);
        this.damager = damager;
    }

    public IdpPlayer getDamager() {
        return damager;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }
    
}
