package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.objects.owned.handlers.LotHandler;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;

/**
 *
 * @author Hret
 */
public class InnInteractEntityEvent extends APlayerEvent implements InnEventCancellable {

    private final Entity entity;
    private InnectisLot playerLot;
    private InnectisLot entityLot;
    private EquipmentSlot handSlot;
    private boolean cancel;

    public InnInteractEntityEvent(final IdpPlayer player, final EquipmentSlot handSlot, final Entity entity) {
        super(player, InnEventType.PLAYER_INTERACT_ENTITY);

        this.handSlot = handSlot;
        this.entity = entity;
        this.cancel = false;
    }

    /**
     * Returns the entity
     * @return
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Returns the lot the entity is on
     * @return
     */
    public InnectisLot getEntityLot() {
        if (entityLot == null && entity != null) {
            entityLot = LotHandler.getLot(entity.getLocation());
        }
        return entityLot;
    }

    /**
     * Gets the hand slot used in this event
     * @return
     */
    public EquipmentSlot getHandSlot() {
        return handSlot;
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
