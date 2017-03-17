package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnBlockEvent;
import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;

/**
 *
 * @author Nosliw
 */
public class InnPlayerBlockPlaceEvent extends APlayerEvent implements InnBlockEvent, InnEventCancellable {

    private final Block block;
    private final Block blockAgainst;
    private boolean cancel;
    private boolean build;

    public InnPlayerBlockPlaceEvent(IdpPlayer player, Block block, Block blockAgainst, boolean build) {
        super(player, InnEventType.PLAYER_BLOCK_PLACE);

        this.block = block;
        this.blockAgainst = blockAgainst;
        this.build = build;
        this.cancel = false;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    public boolean canBuild() {
        return build;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }

    public Block getBlockAgainst() {
        return blockAgainst;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
