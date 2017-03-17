package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.system.warps.WarpHandler;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.Location;

/**
 *
 * @author Nosliw
 */
public class InnPlayerRespawnEvent extends APlayerEvent {

    private Location respawnLocation;

    public InnPlayerRespawnEvent(IdpPlayer player, Location respawnLocation) {
        super(player, InnEventType.PLAYER_RESPAWN);

        this.respawnLocation = respawnLocation;
    }

    public InnPlayerRespawnEvent(IdpPlayer player) {
        this(player, WarpHandler.getSpawn(player.getGroup()));
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = respawnLocation;
    }
    
}
