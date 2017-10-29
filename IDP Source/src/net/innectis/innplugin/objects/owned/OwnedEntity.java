package net.innectis.innplugin.objects.owned;

import net.innectis.innplugin.player.PlayerCredentials;
import org.bukkit.entity.EntityType;

/**
 * A class to manage a single owned entity
 *
 * @author AlphaBlend
 */
public class OwnedEntity {

    private PlayerCredentials ownerCredentials;
    private EntityType type;

    public OwnedEntity(PlayerCredentials ownerCredentials, EntityType type) {
        this.ownerCredentials = ownerCredentials;
        this.type = type;
    }

    /**
     * Gets the credentials of the player that owns this entity
     * @return
     */
    public PlayerCredentials getOwnerCredentials() {
        return ownerCredentials;
    }

    /**
     * Returns the owner of this entity
     * @return
     */
    public String getOwner() {
        return ownerCredentials.getName();
    }

    /**
     * Gets the type of this entity
     * @return
     */
    public EntityType getType() {
        return type;
    }
    
}
