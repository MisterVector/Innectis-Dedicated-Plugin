package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnBlockEvent;
import net.innectis.innplugin.listeners.InnEventType;
import org.bukkit.block.Block;

/**
 *
 * @author Hret
 *
 * The base for a block event.
 */
abstract class ABlockEvent extends AbstractInnEvent implements InnBlockEvent {

    private final Block block;

    public ABlockEvent(Block block, InnEventType type) {
        super(type);
        this.block = block;
    }

    /**
     * The block belonging to this event.
     * @return
     */
    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return "BlockEvent: [" + getBlock().toString() + "]";
    }

}
