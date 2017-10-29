package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEntityEvent;
import net.innectis.innplugin.listeners.InnEventType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 *
 * @author Hret
 *
 * The base for Entity bases events in the IDP.
 */
abstract class AEntityEvent extends AbstractInnEvent implements InnEntityEvent {

    private final Entity entity;

    public AEntityEvent(Entity entity, InnEventType type) {
        super(type);
        this.entity = entity;
    }

    /**
     * The entity belong to this event.
     * @return
     */
    @Override
    public Entity getEntity() {
        return entity;
    }

    /**
     * THe type of the Entity in this event
     * @return
     */
    public EntityType getEntityType() {
        return (entity == null ? EntityType.UNKNOWN : entity.getType());
    }
    
}
