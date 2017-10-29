package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

/**
 *
 * Event for handling player damage by projectiles
 */
public class InnPlayerDamageByProjectileEvent extends APlayerEvent implements InnEventCancellable {

    private final Projectile projectile;
    private boolean cancel;

    public InnPlayerDamageByProjectileEvent(IdpPlayer victim, Projectile projectile) {
        super(victim, InnEventType.PLAYER_DAMAGE_BY_PROJECTILE);
        this.projectile = projectile;
    }

    /**
     * The projectile that got shot
     * @return
     */
    public Projectile getProjectile() {
        return projectile;
    }

    /**
     * Will check the projectile if the damager is a player and returns a new IdpPlayer object.
     * If the shooter is not a player null be returned.
     * @return the player or null if shooter was not a player.
     */
    public IdpPlayer getDamager() {
        if (getProjectile().getShooter() instanceof Player) {
            return InnPlugin.getPlugin().getPlayer((Player) getProjectile().getShooter());
        }
        return null;
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
