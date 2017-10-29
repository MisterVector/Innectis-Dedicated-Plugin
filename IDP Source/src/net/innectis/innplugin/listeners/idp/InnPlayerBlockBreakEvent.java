package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnBlockEvent;
import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;

/**
 * @author Hret
 *
 * Event that gets called when a player breaks a block.
 * This event is both a player and a blockevent.
 */
public class InnPlayerBlockBreakEvent extends APlayerEvent implements InnBlockEvent, InnEventCancellable {

    private final Block block;
    private boolean cancel;

    public InnPlayerBlockBreakEvent(IdpPlayer player, Block block) {
        super(player, InnEventType.PLAYER_BLOCK_BREAK);
        this.block = block;
        cancel = false;
    }

    /**
     * Checks if the event was cancelled.
     * @return
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the event to cancelled.
     * @param cancelled
     */
    @Override
    public void setCancelled(boolean cancelled) {
        cancel = cancelled;
    }

    /**
     * The block that was broken.
     * @return
     */
    @Override
    public Block getBlock() {
        return block;
    }
    
}
