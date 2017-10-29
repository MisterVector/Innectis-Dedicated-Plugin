package net.innectis.innplugin.listeners;

import org.bukkit.block.Block;

/**
 *
 * @author Hret
 *
 * Interface for events that are linked to a block
 */
public interface InnBlockEvent {

    /**
     * The block belonging to this event.
     * @return
     */
    Block getBlock();
    
}
