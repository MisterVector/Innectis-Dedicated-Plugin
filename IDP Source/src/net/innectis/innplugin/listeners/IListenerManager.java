package net.innectis.innplugin.listeners;

import java.util.List;
import java.util.UUID;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.listeners.bukkit.BukkitPlayerActionListener;
import net.innectis.innplugin.listeners.bukkit.IdpChatListener;

/**
 *
 * @author Hret
 *
 * The interface which describes what methods the listener manager needs to support.
 */
public interface IListenerManager {

    /**
     * This method will register the main BUKKIT listeners of the IDP.
     * The method should only be called once, concurrent calling will cause in an Exception!
     * @param plugin
     */
    void registerMainListeners(InnPlugin plugin);

    /**
     * This will check if the given event has any listeners
     * @param eventtype
     * @return true if there are listeners for this eventtype.
     */
    boolean hasListeners(InnEventType eventtype);

    /**
     * This will add the given listener to the list.
     * Events that are used by this lister will be send as they occur.
     * @param listener
     * @return
     */
    UUID registerSecListener(ISecondairyListener listener);

    /**
     * Removes the secondairy listener for the given event.
     * @param id
     */
    void removeSecListener(UUID id);

    /**
     * This will return the listener with the given ID.
     * @param id
     * @return the listener or null if none with the given id.
     */
    ISecondairyListener getListener(UUID id);

    /**
     * This will return a list of the UUID's for all active listeners.
     * @return
     */
    List<UUID> getListenerList();

    /**
     * Fires the given event and sends it to all of the secondairy listeners.
     * @param event
     */
    void fireEvent(InnEvent event);

    /**
     * TEMPORARY
     * Allow any object to get the chatlistener
     *
     * TODO: To be removed
     */
    IdpChatListener getChatListener();

    /**
     * TEMPORARY
     * Allow any object to get the PlayerActionListener
     *
     * TODO: To be removed
     */
    BukkitPlayerActionListener getPlayerActionListener();
    
}
