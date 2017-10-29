package net.innectis.innplugin.listeners.idp;

import net.innectis.innplugin.listeners.InnEventCancellable;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;

/**
 * Called when a player's food level changes
 *
 * @author AlphaBlend
 */
public class InnPlayerFoodLevelChangeEvent extends APlayerEvent implements InnEventCancellable {

    private int foodLevel;
    private boolean cancel;

    public InnPlayerFoodLevelChangeEvent(IdpPlayer player, int foodLevel) {
        super(player, InnEventType.PLAYER_FOOD_LEVEL_CHANGE);
        this.foodLevel = foodLevel;
    }

    /**
     * Gets the food level after the event finishes,
     * if not canceled
     * @return
     */
    public int getFoodLevel() {
        return foodLevel;
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
