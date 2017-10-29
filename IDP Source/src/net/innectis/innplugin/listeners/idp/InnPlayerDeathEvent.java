package net.innectis.innplugin.listeners.idp;

import java.util.List;
import net.innectis.innplugin.listeners.InnEventType;
import net.innectis.innplugin.player.IdpPlayer;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hret
 *
 * Event for handling death events for a player.
 */
public class InnPlayerDeathEvent extends APlayerEvent {

    private String deathmessage;
    private List<ItemStack> drops;
    private boolean showDeathMessage = true;

    public InnPlayerDeathEvent(IdpPlayer player, List<ItemStack> drops, String deathmessage) {
        super(player, InnEventType.PLAYER_DEATH);
        this.deathmessage = deathmessage;
        this.drops = drops;
    }

    /**
     * Gets the deathmessage that will be broadcasted
     *
     * @return the deathmessage
     */
    public String getDeathMessage() {
        return deathmessage;
    }

    /**
     * Sets the deathmessage that will be broadcasted
     *
     * @param deathmessage
     */
    public void setDeathmessage(String deathmessage) {
        this.deathmessage = deathmessage;
    }

    /**
     * Returns if death message should be shown.
     *
     * @param drops
     */
    public boolean getShowDeathMessage() {
        return showDeathMessage;
    }

    /**
     * Sets if death message is shown.
     *
     * @param drops
     */
    public void setShowDeathMessage(boolean showDeathMessage) {
        this.showDeathMessage = showDeathMessage;
    }

    /**
     * Sets the drops of this death event
     *
     * @param drops
     */
    public void setDrops(List<ItemStack> drops) {
        this.drops = drops;
    }

    /**
     * '
     * Gets the drops of this death event
     *
     * @return
     */
    public List<ItemStack> getDrops() {
        return drops;
    }

    /**
     * Clears the drops of this death event
     */
    public void clearDrops() {
        drops.clear();
    }
    
}
