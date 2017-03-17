
package net.innectis.innplugin.listeners;

import org.bukkit.entity.Entity;

/**
 *
 * @author RMH
 */
public interface InnEntityEvent {

    /**
     * The entity belong to this event.
     * @return
     */
    Entity getEntity();
    
}
