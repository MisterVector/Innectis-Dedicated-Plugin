package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author Hret
 */
public class InnPlayerInteractEvent extends APlayerEvent implements InnEventCancellable {

    private final Block block;
    private final IdpItemStack item;
    private final BlockFace blockface;
    private final Action action;
    private InnectisLot playerLot;
    private InnectisLot blockLot;
    private EquipmentSlot handSlot;
    private boolean cancel;

    public InnPlayerInteractEvent(IdpPlayer player, EquipmentSlot handSlot, Block block, IdpItemStack item, BlockFace face, Action action) {
        super(player, InnEventType.PLAYER_INTERACT);

        this.handSlot = handSlot;
        this.block = block;
        this.action = action;
        this.item = item;
        this.blockface = face;
        this.cancel = false;
    }

    /**
     * Returns the hand slot used
     * @return
     */
    public EquipmentSlot getHandSlot() {
        return handSlot;
    }

    /**
     * Returns the block
     * @return
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Returns the lot the block is on
     * @return
     */
    public InnectisLot getBlockLot() {
        if (blockLot == null && block != null) {
            blockLot = LotHandler.getLot(block.getLocation());
        }
        return blockLot;
    }

    /**
     * Returns the lot the player is located on
     * @return
     */
    public InnectisLot getPlayerLot() {
        if (playerLot == null) {
            playerLot = LotHandler.getLot(getPlayer().getLocation());
        }
        return playerLot;
    }

    /** *
     * Returns the material of the block or null if no block
     * @return
     */
    public IdpMaterial getBlockMaterial() {
        if (getBlock() == null) {
            return IdpMaterial.AIR;
        } else {
            return IdpMaterial.fromBlock(getBlock());
        }
    }

    public IdpItemStack getItem() {
        return item;
    }

    public Action getAction() {
        return action;
    }

    public BlockFace getBlockFace() {
        return blockface;
    }

    /**
     * Checks if the event was cancelled.
     * <p/>
     * Note: This is only used in IdpGameManager.
     * @return
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the event to cancelled.
     * <p/>
     * Note: This is only used in IdpGameManager.
     * @param cancelled
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }
    
}
