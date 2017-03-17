package net.innectis.innplugin.listeners;

/**
 *
 * @author Nosliw
 *
 * Interface for events that can be cancelled in bukkit.
 */
public interface InnEventCancellable {

    /**
     * Boolean that shows if the given event has been cancelled or not.
     * @return true means cancelled
     */
    public boolean isCancelled();

    /**
     * Set the event to cancel the actual bukkit event
     * @param cancel - set to true to cancel
     */
    public void setCancelled(boolean cancel);

}
